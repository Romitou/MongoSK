package fr.romitou.mongosk.adapters;

import com.mongodb.MongoClientSettings;
import fr.romitou.mongosk.LoggerHelper;
import fr.romitou.mongosk.MongoSK;
import fr.romitou.mongosk.adapters.codecs.*;
import fr.romitou.mongosk.elements.MongoSKDocument;
import fr.romitou.mongosk.utils.BinaryUtils;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.configuration.CodecConfigurationException;

import javax.annotation.Nullable;
import java.io.StreamCorruptedException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MongoSKAdapter {

    public final static String DOCUMENT_FIELD = MongoSK.getInstance().getConfig().getString("skript-adapters.document-field", "__MongoSK__");
    public final static Boolean ADAPTERS_ENABLED = MongoSK.getInstance().getConfig().getBoolean("skript-adapters.enabled", false);
    public final static Boolean SAFE_DESERIALIZATION = MongoSK.getInstance().getConfig().getBoolean("skript-adapters.safe-data", true);
    public final static List<String> DISABLED_CODECS = MongoSK.getInstance().getConfig().getStringList("skript-adapters.disabled");

    private final static Codec<Document> DOCUMENT_CODEC = MongoClientSettings.getDefaultCodecRegistry().get(Document.class);
    private final static DecoderContext DECODER_CONTEXT = DecoderContext.builder().build();

    private final static List<MongoSKCodec<?>> loadedCodecs = new ArrayList<>();
    private final static List<String> loadedCodecNames = new ArrayList<>();
    private final static Map<String, MongoSKCodec<?>> loadedCodecsMap = new HashMap<>();
    private final static Map<Class<?>, Optional<MongoSKCodec<?>>> loadedCodecsByClassMap = new ConcurrentHashMap<>();
    private final static List<Class<? extends MongoSKCodec<?>>> availableCodecs = Arrays.asList(
        BiomeCodec.class,
        BlockCodec.class,
        ChunkCodec.class,
        DamageCauseCodec.class,
        DateCodec.class,
        EntityCodec.class,
        ExperienceCodec.class,
        GameModeCodec.class,
        ItemStackCodec.class,
        ItemTypeCodec.class,
        LocationCodec.class,
        MaterialCodec.class,
        MoneyCodec.class,
        PlayerCodec.class,
        PotionEffectCodec.class,
        PotionEffectTypeCodec.class,
        SlotCodec.class,
        TimeCodec.class,
        TimePeriodCodec.class,
        TimespanCodec.class,
        VectorCodec.class,
        WeatherTypeCodec.class,
        WorldCodec.class
    );

    public static void loadCodecs() {
        if (!ADAPTERS_ENABLED)
            return;
        availableCodecs.forEach(MongoSKAdapter::loadCodec);
        List<String> codecs = MongoSKAdapter.getCodecNames();
        LoggerHelper.info("Loaded " + codecs.size() + " codecs!",
            "Name of the loaded codecs: " + String.join(", ", codecs),
            "If you have problems with these, do not hesitate to report them."
        );
    }

    public static void loadCodec(Class<? extends MongoSKCodec<?>> codec) {
        try {
            MongoSKCodec<?> codecInstance = codec.getConstructor().newInstance();
            if (DISABLED_CODECS.contains(codecInstance.getName())) {
                LoggerHelper.info("Codec " + codecInstance.getName() + " is disabled, skipping registration.");
                return;
            }
            Class.forName(codecInstance.getReturnType().getCanonicalName());
            loadedCodecs.add(codecInstance);
            loadedCodecNames.add(codecInstance.getName());
            loadedCodecsMap.putIfAbsent(codecInstance.getName(), codecInstance);
            loadedCodecsByClassMap.clear();
        } catch (NoClassDefFoundError | ClassNotFoundException e) {
            LoggerHelper.severe("Oops, the return class of the " + codec.getName() + " codec doesn't exists! Skipping registration.");
        } catch (ReflectiveOperationException e) {
            LoggerHelper.severe("Oops, cannot load the " + codec.getName() + " codec!", e);
        }
    }

    public static List<String> getCodecNames() {
        return new ArrayList<>(loadedCodecNames);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T> MongoSKCodec<T> getCodecByName(String name) {
        return (MongoSKCodec<T>) loadedCodecsMap.get(name);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T> MongoSKCodec<T> getCodecByClass(Class<? extends T> clazz) {
        return (MongoSKCodec<T>) loadedCodecsByClassMap.computeIfAbsent(clazz, c -> {
            for (MongoSKCodec<?> codec : loadedCodecs) {
                if (codec.getReturnType().isAssignableFrom(c)) {
                    return Optional.of(codec);
                }
            }
            return Optional.empty();
        }).orElse(null);
    }

    public static Object deserializeValue(Object value) {
        if (!(value instanceof Document))
            return value;
        Document doc = (Document) value;
        if (!doc.containsKey(DOCUMENT_FIELD))
            return new MongoSKDocument(doc, null);
        String codecName = doc.getString(DOCUMENT_FIELD);
        MongoSKCodec<?> codec = MongoSKAdapter.getCodecByName(codecName);
        if (codec == null) {
            LoggerHelper.severe("No codec found for " + codecName + "!",
                "Loaded codecs: " + String.join(", ", MongoSKAdapter.getCodecNames()),
                "Requested codec: " + codecName
            );
            return new MongoSKDocument(doc, null);
        }
        try {
            return codec.deserialize(doc);
        } catch (StreamCorruptedException ex) {
            LoggerHelper.severe("An error occurred during the deserialization of the document: " + ex.getMessage(),
                "Requested codec: " + codecName,
                "Original value class: " + doc.getClass().getName()
            );
            return new MongoSKDocument(doc, null);
        }
    }

    public static Object serializeObject(Object unsafeObject) {
        if (unsafeObject == null)
            return null;
        if (unsafeObject instanceof MongoSKDocument)
            return ((MongoSKDocument) unsafeObject).getBsonDocument();
        LoggerHelper.debug("Searching codec for " + unsafeObject.getClass() + " class...");
        MongoSKCodec<Object> codec = MongoSKAdapter.getCodecByClass(unsafeObject.getClass());
        if (codec == null) {
            try {
                MongoClientSettings.getDefaultCodecRegistry().get(unsafeObject.getClass());
            } catch (CodecConfigurationException ignore) {
                LoggerHelper.debug("No codec found for this class. " + (SAFE_DESERIALIZATION ? "It has been removed from the document." : "No changes have been made to it."));
                return SAFE_DESERIALIZATION ? null : unsafeObject;
            }
            LoggerHelper.debug("The Mongo driver directly supports this type. Next!");
            return unsafeObject; // We're safe!
        }
        LoggerHelper.debug("A codec has been found: " + codec.getName());
        Document serializedDocument = codec.serialize(unsafeObject);
        serializedDocument.put(DOCUMENT_FIELD, codec.getName());
        LoggerHelper.debug("Result of the serialization: ",
            "Initial object: " + unsafeObject,
            "Serialized document: " + serializedDocument.toJson());
        return serializedDocument;
    }

    public static Object[] serializeArray(Object[] unsafeArray) {
        if (!ADAPTERS_ENABLED)
            return unsafeArray;

        Object[] result = new Object[unsafeArray.length];
        for (int i = 0; i < unsafeArray.length; i++) {
            result[i] = MongoSKAdapter.serializeObject(unsafeArray[i]);
        }
        return result;
    }

    public static Object[] deserializeValues(Object[] unsafeValues) {
        if (!ADAPTERS_ENABLED)
            return unsafeValues;
        Object[] values = new Object[unsafeValues.length];
        for (int i = 0; i < unsafeValues.length; i++) {
            values[i] = MongoSKAdapter.deserializeValue(unsafeValues[i]);
        }
        return values;
    }

    public static byte[] getBinaryData(Object unsafeBinary) throws StreamCorruptedException {
        return BinaryUtils.getBinaryData(unsafeBinary);
    }

    public static Document bsonDocumentToDocument(BsonDocument bsonDocument) {
        return DOCUMENT_CODEC.decode(bsonDocument.asBsonReader(), DECODER_CONTEXT);
    }

}

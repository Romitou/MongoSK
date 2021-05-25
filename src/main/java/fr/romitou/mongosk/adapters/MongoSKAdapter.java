package fr.romitou.mongosk.adapters;

import com.mongodb.MongoClientSettings;
import fr.romitou.mongosk.Logger;
import fr.romitou.mongosk.MongoSK;
import fr.romitou.mongosk.adapters.codecs.*;
import fr.romitou.mongosk.elements.MongoSKDocument;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.configuration.CodecConfigurationException;
import org.bson.types.Binary;

import javax.annotation.Nullable;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MongoSKAdapter {

    public final static String DOCUMENT_FIELD = MongoSK.getConfiguration().getString("skript-adapters.document-field", "__MongoSK__");
    public final static Boolean ADAPTERS_ENABLED = MongoSK.getConfiguration().getBoolean("skript-adapters.enabled", false);
    public final static Boolean SAFE_DESERIALIZATION = MongoSK.getConfiguration().getBoolean("skript-adapters.safe-data", true);

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
        PlayerCodec.class,
        PotionEffectCodec.class,
        PotionEffectTypeCodec.class,
        SlotCodec.class,
        TimeCodec.class,
        TimePeriodCodec.class,
        TimespanCodec.class,
        VectorCodec.class,
        VisualEffectCodec.class,
        WeatherTypeCodec.class,
        WorldCodec.class
    );
    public static List<MongoSKCodec<?>> loadedCodecs = new ArrayList<>();

    public static void loadCodecs() {
        if (!ADAPTERS_ENABLED)
            return;
        availableCodecs.forEach(MongoSKAdapter::loadCodec);
        List<String> codecs = MongoSKAdapter.getCodecNames();
        Logger.info("Loaded " + codecs.size() + " codecs!",
            "Name of the loaded codecs: " + String.join(", ", codecs),
            "If you have problems with these, do not hesitate to report them."
        );
    }

    public static void loadCodec(Class<? extends MongoSKCodec<?>> codec) {
        try {
            MongoSKCodec<?> codecInstance = codec.getConstructor().newInstance();
            Class.forName(codecInstance.getReturnType().getCanonicalName());
            loadedCodecs.add(codecInstance);
        } catch (NoClassDefFoundError | ClassNotFoundException e) {
            Logger.severe("Oops, the class of the " + codec.getName() + " codec doesn't exists! Try updating Skript?");
        } catch (ReflectiveOperationException e) {
            Logger.severe("Oops, cannot load the " + codec.getName() + " codec! Look at the console for more details.");
            e.printStackTrace();
        }
    }

    public static List<String> getCodecNames() {
        return loadedCodecs.stream()
            .map(MongoSKCodec::getName)
            .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T> MongoSKCodec<T> getCodecByName(String name) {
        return (MongoSKCodec<T>) loadedCodecs.stream()
            .filter(codec -> codec.getName().equals(name))
            .findFirst()
            .orElse(null);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T> MongoSKCodec<T> getCodecByClass(Class<? extends T> clazz) {
        return (MongoSKCodec<T>) loadedCodecs.stream()
            .filter(codec -> codec.getReturnType().isAssignableFrom(clazz))
            .findFirst()
            .orElse(null);
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
            Logger.severe("No codec found for " + codecName + "!",
                "Loaded codecs: " + String.join(", ", MongoSKAdapter.getCodecNames()),
                "Requested codec: " + codecName
            );
            return new MongoSKDocument(doc, null);
        }
        try {
            return codec.deserialize(doc);
        } catch (StreamCorruptedException ex) {
            Logger.severe("An error occurred during the deserialization of the document: " + ex.getMessage(),
                "Requested codec: " + codecName,
                "Original value class: " + doc,
                "Document JSON: " + doc.toJson()
            );
            return new MongoSKDocument(doc, null);
        }
    }

    public static Object serializeObject(Object unsafeObject) {
        if (unsafeObject == null)
            return null;
        if (unsafeObject instanceof MongoSKDocument)
            return ((MongoSKDocument) unsafeObject).getBsonDocument();
        Logger.debug("Searching codec for " + unsafeObject.getClass() + " class...");
        MongoSKCodec<Object> codec = MongoSKAdapter.getCodecByClass(unsafeObject.getClass());
        if (codec == null) {
            try {
                MongoClientSettings.getDefaultCodecRegistry().get(unsafeObject.getClass());
            } catch (CodecConfigurationException ignore) {
                Logger.debug("No codec found for this class. " + (SAFE_DESERIALIZATION ? "It has been removed from the document." : "No changes have been made to it."));
                return SAFE_DESERIALIZATION ? null : unsafeObject;
            }
            Logger.debug("The Mongo driver directly supports this type. Next!");
            return unsafeObject; // We're safe!
        }
        Logger.debug("A codec has been found: " + codec.getName());
        Document serializedDocument = codec.serialize(unsafeObject);
        serializedDocument.put(DOCUMENT_FIELD, codec.getName());
        Logger.debug("Result of the serialization: ",
            "Initial object: " + unsafeObject,
            "Serialized document: " + serializedDocument.toJson());
        return serializedDocument;
    }

    public static Object[] serializeArray(Object[] unsafeArray) {
        if (!ADAPTERS_ENABLED)
            return unsafeArray;
        return Arrays.stream(unsafeArray).map(MongoSKAdapter::serializeObject).toArray(Object[]::new);
    }

    public static Object[] deserializeValues(Object[] unsafeValues) {
        if (!ADAPTERS_ENABLED)
            return unsafeValues;
        return Arrays.stream(unsafeValues).map(MongoSKAdapter::deserializeValue).toArray(Object[]::new);
    }

    public static byte[] getBinaryData(Object unsafeBinary) throws StreamCorruptedException {
        if (unsafeBinary instanceof byte[])
            return (byte[]) unsafeBinary;
        else if (unsafeBinary instanceof Binary)
            return ((Binary) unsafeBinary).getData();
        throw new StreamCorruptedException("Cannot retrieve valid binary from document!");
    }

    public static Document bsonDocumentToDocument(BsonDocument bsonDocument) {
        return MongoClientSettings.getDefaultCodecRegistry()
            .get(Document.class)
            .decode(bsonDocument.asBsonReader(), DecoderContext.builder().build());
    }

}

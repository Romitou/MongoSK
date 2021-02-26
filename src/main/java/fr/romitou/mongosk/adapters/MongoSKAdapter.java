package fr.romitou.mongosk.adapters;

import fr.romitou.mongosk.Logger;
import fr.romitou.mongosk.MongoSK;
import fr.romitou.mongosk.adapters.codecs.*;
import org.bson.Document;

import javax.annotation.Nullable;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MongoSKAdapter {

    private final static String DOCUMENT_FIELD = MongoSK.getConfiguration().getString("skript-adapters.document-field", "__MongoSK__");
    private final static Boolean SAFE_DESERIALIZATION = MongoSK.getConfiguration().getBoolean("skript-adapters.safe-data", true);

    public static List<MongoSKCodec<?>> codecs = new ArrayList<>();

    public static List<String> loadCodecs() {
        codecs.add(new BiomeCodec());
        codecs.add(new BlockCodec());
        codecs.add(new ChunkCodec());
        codecs.add(new DamageCauseCodec());
        codecs.add(new DateCodec());
        codecs.add(new EntityCodec());
        codecs.add(new ExperienceCodec());
        codecs.add(new GameModeCodec());
        codecs.add(new ItemStackCodec());
        codecs.add(new ItemTypeCodec());
        codecs.add(new LocationCodec());
        codecs.add(new MaterialCodec());
        codecs.add(new PlayerCodec());
        codecs.add(new PotionEffectCodec());
        codecs.add(new PotionEffectTypeCodec());
        codecs.add(new SlotCodec());
        codecs.add(new TimeCodec());
        codecs.add(new TimePeriodCodec());
        codecs.add(new TimespanCodec());
        codecs.add(new VectorCodec());
        codecs.add(new VisualEffectCodec());
        codecs.add(new WeatherTypeCodec());
        codecs.add(new WorldCodec());
        return getCodecNames();
    }

    public static List<String> getCodecNames() {
        return codecs.stream()
            .map(MongoSKCodec::getName)
            .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T> MongoSKCodec<T> getCodecByName(String name) {
        return (MongoSKCodec<T>) codecs.stream()
            .filter(codec -> codec.getName().equals(name))
            .findFirst()
            .orElse(null);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T> MongoSKCodec<T> getCodecByClass(Class<? extends T> clazz) {
        return (MongoSKCodec<T>) codecs.stream()
            .filter(codec -> codec.getReturnType().isAssignableFrom(clazz))
            .findFirst()
            .orElse(null);
    }

    public static Object deserializeValue(Object value) {
        if (!(value instanceof Document))
            return value;
        Document doc = (Document) value;
        if (!doc.containsKey(DOCUMENT_FIELD))
            return value;
        String codecName = doc.getString(DOCUMENT_FIELD);
        MongoSKCodec<Object> codec = MongoSKAdapter.getCodecByName(codecName);
        if (codec == null) {
            Logger.severe("No codec found for " + codecName + "!",
                "Loaded codecs: " + String.join(", ", MongoSKAdapter.getCodecNames()),
                "Requested codec: " + codecName
            );
            return new Object[0];
        }
        try {
            return new Object[]{codec.deserialize(doc)};
        } catch (StreamCorruptedException ex) {
            Logger.severe("An error occurred during the deserialization of the document: " + ex.getMessage(),
                "Requested codec: " + codecName,
                "Original value class: " + doc.toString(),
                "Document JSON: " + doc.toJson()
            );
            return new Object[0];
        }
    }

    public static Object serializeObject(Object unsafeObject) {
        Logger.debug("Searching codec for " + unsafeObject.getClass() + " class...");
        MongoSKCodec<Object> codec = MongoSKAdapter.getCodecByClass(unsafeObject.getClass());
        if (codec == null) {
            Logger.debug("No codec found for this class. " + (SAFE_DESERIALIZATION ? "It has been removed from the document." : "No changes have been made to it."));
            return SAFE_DESERIALIZATION ? null : unsafeObject;
        }
        Logger.debug("A codec has been found: " + codec.getName());
        Document serializedDocument = codec.serialize(unsafeObject);
        serializedDocument.put(DOCUMENT_FIELD, codec.getName());
        Logger.debug("Result of the serialization: ",
            "Initial object: " + unsafeObject.toString(),
            "Serialized document: " + serializedDocument.toJson());
        return serializedDocument;
    }

    public static Object[] serializeArray(Object[] unsafeArray) {
        if (!MongoSK.getConfiguration().getBoolean("skript-adapters.enabled", false))
            return unsafeArray;
        return Arrays.stream(unsafeArray).map(MongoSKAdapter::serializeObject).toArray(Object[]::new);
    }

}

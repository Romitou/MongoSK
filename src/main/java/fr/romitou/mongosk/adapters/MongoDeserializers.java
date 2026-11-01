package fr.romitou.mongosk.adapters;

import org.bson.BsonValue;

import java.util.Arrays;

public class MongoDeserializers {

    public static Object[] deserializeBsonValues(BsonValue[] values) {
        return Arrays.stream(values).map(MongoDeserializers::deserializeBsonValue).toArray();
    }

    public static Object deserializeBsonValue(BsonValue value) {
        return switch (value.getBsonType()) {
            case ARRAY -> value.asArray().stream().map(MongoDeserializers::deserializeBsonValue).toArray();
            case INT32 -> value.asInt32().getValue();
            case INT64 -> value.asInt64().getValue();
            case BINARY -> value.asBinary().getData();
            case DOUBLE -> value.asDouble().getValue();
            case STRING -> value.asString().getValue();
            case SYMBOL -> value.asSymbol().getSymbol();
            case BOOLEAN -> value.asBoolean().getValue();
            case DOCUMENT -> MongoSKAdapter.deserializeValue(MongoSKAdapter.bsonDocumentToDocument(value.asDocument()));
            case DATE_TIME -> value.asDateTime().getValue();
            case TIMESTAMP -> value.asTimestamp().getValue();
            case OBJECT_ID -> value.asObjectId().getValue();
            case DECIMAL128 -> value.asDecimal128().doubleValue();
            case REGULAR_EXPRESSION -> value.asRegularExpression().getPattern();
            default -> null;
        };
    }

}

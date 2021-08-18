package fr.romitou.mongosk.adapters;

import org.bson.BsonValue;

import java.util.Arrays;

public class MongoDeserializers {

    public static Object[] deserializeBsonValues(BsonValue[] values) {
        return Arrays.stream(values).map(MongoDeserializers::deserializeBsonValue).toArray();
    }

    public static Object deserializeBsonValue(BsonValue value) {
        switch (value.getBsonType()) {
            case ARRAY:
                return value.asArray().toArray();
            case INT32:
                return value.asInt32().getValue();
            case INT64:
                return value.asInt64().getValue();
            case BINARY:
                return value.asBinary().getData();
            case DOUBLE:
                return value.asDouble().getValue();
            case STRING:
                return value.asString().getValue();
            case SYMBOL:
                return value.asSymbol().getSymbol();
            case BOOLEAN:
                return value.asBoolean().getValue();
            case DOCUMENT:
                return MongoSKAdapter.deserializeValue(MongoSKAdapter.bsonDocumentToDocument(value.asDocument()));
            case DATE_TIME:
                return value.asDateTime().getValue();
            case TIMESTAMP:
                return value.asTimestamp().getValue();
            case OBJECT_ID:
                return value.asObjectId().getValue();
            case DECIMAL128:
                return value.asDecimal128().intValue();
            case REGULAR_EXPRESSION:
                return value.asRegularExpression().getPattern();
            default:
                return null;
        }
    }

}

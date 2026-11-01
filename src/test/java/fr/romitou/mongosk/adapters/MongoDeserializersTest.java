package fr.romitou.mongosk.adapters;

import org.bson.BsonArray;
import org.bson.BsonDecimal128;
import org.bson.BsonInt32;
import org.bson.BsonString;
import org.bson.types.Decimal128;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class MongoDeserializersTest {

    @Test
    public void testDecimal128KeepsDecimals() {
        Object value = MongoDeserializers.deserializeBsonValue(new BsonDecimal128(new Decimal128(new BigDecimal("12.75"))));
        assertEquals(12.75, value);
    }

    @Test
    public void testArrayValuesAreDeserialized() {
        BsonArray array = new BsonArray(Arrays.asList(new BsonInt32(1), new BsonString("two")));
        Object value = MongoDeserializers.deserializeBsonValue(array);
        assertArrayEquals(new Object[]{1, "two"}, (Object[]) value);
    }
}

package fr.romitou.mongosk.objects;

import org.bson.conversions.Bson;

public class MongoFilter {

    private final Bson filter;
    private final String comparator;

    public MongoFilter(Bson filter, String comparator) {
        this.filter = filter;
        this.comparator = comparator;
    }

    public Bson get() {
        return this.filter;
    }

    public String toString() {
        return this.comparator;
    }

}

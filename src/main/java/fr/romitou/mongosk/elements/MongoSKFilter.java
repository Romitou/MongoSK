package fr.romitou.mongosk.elements;

import org.bson.conversions.Bson;

import java.util.Objects;

public class MongoSKFilter {

    private final Bson filter;
    private final String comparator;

    public MongoSKFilter(Bson filter, String comparator) {
        this.filter = filter;
        this.comparator = comparator;
    }

    public Bson getFilter() {
        return this.filter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MongoSKFilter that = (MongoSKFilter) o;
        return Objects.equals(filter, that.filter) && Objects.equals(comparator, that.comparator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filter, comparator);
    }

    @Override
    public String toString() {
        return "MongoSKFilter{" +
            "filter=" + filter +
            ", comparator='" + comparator + '\'' +
            '}';
    }

}

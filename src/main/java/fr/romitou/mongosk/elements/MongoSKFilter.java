package fr.romitou.mongosk.elements;

import fr.romitou.mongosk.Logger;
import org.bson.conversions.Bson;

import java.util.Objects;

public class MongoSKFilter {

    private final Bson filter;
    private final String display;

    public MongoSKFilter(Bson filter, String display) {
        this.filter = filter;
        this.display = display;
    }

    public Bson getFilter() {
        return this.filter;
    }

    public String getDisplay() {
        return display;
    }

    public void printDebug() {
        Logger.debug("Informations about this MongoSK filter:",
            "BSON filter: " + this.filter.toBsonDocument(),
            "Display: " + this.display
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MongoSKFilter that = (MongoSKFilter) o;
        return Objects.equals(filter, that.filter) && Objects.equals(display, that.display);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filter, display);
    }

    @Override
    public String toString() {
        return "MongoSKFilter{" +
            "filter=" + filter +
            ", display='" + display + '\'' +
            '}';
    }

}

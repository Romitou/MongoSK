package fr.romitou.mongosk.elements;

import fr.romitou.mongosk.Logger;
import org.bson.conversions.Bson;

import java.util.Objects;

public class MongoSKSort {

    private final Bson sort;
    private final String display;

    public MongoSKSort(Bson sort, String display) {
        this.sort = sort;
        this.display = display;
        this.printDebug();
    }

    public Bson getSort() {
        return this.sort;
    }

    public String getDisplay() {
        return display;
    }

    public void printDebug() {
        Logger.debug("Informations about this MongoSK sort:",
            "BSON filter: " + this.sort.toBsonDocument(),
            "Display: " + this.display
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MongoSKSort that = (MongoSKSort) o;
        return Objects.equals(sort, that.sort) && Objects.equals(display, that.display);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sort, display);
    }

    @Override
    public String toString() {
        return "MongoSKSorting{" +
            "sort=" + sort +
            ", comparator='" + display + '\'' +
            '}';
    }
}

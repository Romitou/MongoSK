package fr.romitou.mongosk.elements;

import fr.romitou.mongosk.LoggerHelper;
import org.bson.conversions.Bson;

import java.util.Objects;

public class MongoSKProjection {

    private final Bson projection;
    private final String display;

    public MongoSKProjection(Bson projection, String display) {
        this.projection = projection;
        this.display = display;
    }

    public Bson getProjection() {
        return this.projection;
    }

    public String getDisplay() {
        return display;
    }

    public void printDebug() {
        LoggerHelper.debug("Informations about this MongoSK projection:",
            "BSON projection: " + this.projection.toBsonDocument(),
            "Display: " + this.display
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MongoSKProjection that = (MongoSKProjection) o;
        return Objects.equals(projection, that.projection) && Objects.equals(display, that.display);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projection, display);
    }

    @Override
    public String toString() {
        return "MongoSKProjection{" +
            "projection=" + projection +
            ", display='" + display + '\'' +
            '}';
    }
}

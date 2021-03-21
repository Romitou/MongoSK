package fr.romitou.mongosk.elements;

import com.mongodb.reactivestreams.client.MongoCollection;
import fr.romitou.mongosk.Logger;
import org.bson.Document;

import java.util.Objects;

public class MongoSKCollection {

    private final MongoCollection<Document> mongoCollection;

    public MongoSKCollection(MongoCollection<Document> mongoCollection) {
        this.mongoCollection = mongoCollection;
        this.printDebug();
    }

    public MongoCollection<Document> getMongoCollection() {
        return mongoCollection;
    }

    public void printDebug() {
        Logger.debug("Informations about this MongoSK collection:",
            "Mongo Collection: " + this.mongoCollection.getNamespace()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MongoSKCollection that = (MongoSKCollection) o;
        return Objects.equals(mongoCollection, that.mongoCollection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mongoCollection);
    }

    @Override
    public String toString() {
        return "MongoSKCollection{" +
            "mongoCollection=" + mongoCollection +
            '}';
    }

}

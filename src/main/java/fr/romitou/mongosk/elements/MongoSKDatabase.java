package fr.romitou.mongosk.elements;

import com.mongodb.reactivestreams.client.MongoDatabase;
import fr.romitou.mongosk.LoggerHelper;

import java.util.Objects;

public class MongoSKDatabase {

    private final MongoDatabase mongoDatabase;

    public MongoSKDatabase(MongoDatabase mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
    }

    public MongoDatabase getMongoDatabase() {
        return mongoDatabase;
    }

    public void printDebug() {
        LoggerHelper.debug("Informations about this MongoSK database:",
            "Mongo Database: " + this.mongoDatabase.getName()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MongoSKDatabase that = (MongoSKDatabase) o;
        return Objects.equals(mongoDatabase, that.mongoDatabase);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mongoDatabase);
    }

    @Override
    public String toString() {
        return "MongoSKDatabase{" +
            "mongoDatabase=" + mongoDatabase +
            '}';
    }

}

package fr.romitou.mongosk.elements;

import com.mongodb.reactivestreams.client.MongoClient;
import fr.romitou.mongosk.LoggerHelper;
import fr.romitou.mongosk.MongoSK;

import java.util.Objects;

public class MongoSKServer {

    private final String displayedName;
    private final MongoClient mongoClient;

    public MongoSKServer(String displayedName, MongoClient mongoClient) {
        this.displayedName = displayedName;
        this.mongoClient = mongoClient;
        MongoSK.registerServer(this);
    }

    public String getDisplayedName() {
        return displayedName;
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public void printDebug() {
        LoggerHelper.debug("Informations about " + this.displayedName + " MongoSK server:",
            "Cluster description: " + this.mongoClient.getClusterDescription(),
            "Is cluster compatible with Mongo driver: " + this.mongoClient.getClusterDescription().isCompatibleWithDriver()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MongoSKServer that = (MongoSKServer) o;
        return Objects.equals(displayedName, that.displayedName) && Objects.equals(mongoClient, that.mongoClient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(displayedName, mongoClient);
    }

    @Override
    public String toString() {
        return "MongoSKServer{" +
            "displayedName='" + displayedName + '\'' +
            ", mongoClient=" + mongoClient +
            '}';
    }

}

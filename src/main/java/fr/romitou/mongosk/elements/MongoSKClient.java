package fr.romitou.mongosk.elements;

import com.mongodb.reactivestreams.client.MongoClient;

import java.util.Objects;

public class MongoSKClient {

    private final String clientName;
    private final MongoClient mongoClient;

    public MongoSKClient(String clientName, MongoClient mongoClient) {
        this.clientName = clientName;
        this.mongoClient = mongoClient;
    }

    public String getClientName() {
        return clientName;
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MongoSKClient that = (MongoSKClient) o;
        return Objects.equals(clientName, that.clientName) && Objects.equals(mongoClient, that.mongoClient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientName, mongoClient);
    }

    @Override
    public String toString() {
        return "MongoSKClient{" +
                "clientName='" + clientName + '\'' +
                ", mongoClient=" + mongoClient +
                '}';
    }

}

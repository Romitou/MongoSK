package fr.romitou.mongosk.objects;

import com.mongodb.client.MongoIterable;
import org.bson.Document;

public class MongoQuery {

    private MongoIterable<Document> iterable;
    private String query;

    public MongoQuery(MongoIterable<Document> iterable, String query) {
        this.iterable = iterable;
        setQuery(query);
    }

    public void setIterable(MongoIterable<Document> iterable) {
        this.iterable = iterable;
    }

    public MongoIterable<Document> getIterable() {
        return iterable;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String toString() {
        return query;
    }

}

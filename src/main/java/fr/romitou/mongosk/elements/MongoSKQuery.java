package fr.romitou.mongosk.elements;

import com.mongodb.reactivestreams.client.FindPublisher;
import com.mongodb.reactivestreams.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

public class MongoSKQuery {

    private MongoSKCollection mongoSKCollection;
    private MongoSKFilter mongoSKFilter;
    private MongoSKSort mongoSKSort;

    public MongoSKQuery() {
    }

    public MongoSKCollection getMongoSKCollection() {
        return mongoSKCollection;
    }

    public void setMongoSKCollection(MongoSKCollection mongoSKCollection) {
        this.mongoSKCollection = mongoSKCollection;
    }

    public MongoSKFilter getMongoSKFilter() {
        return mongoSKFilter;
    }

    public void setMongoSKFilter(MongoSKFilter mongoSKFilter) {
        this.mongoSKFilter = mongoSKFilter;
    }

    public MongoSKSort getMongoSKSort() {
        return mongoSKSort;
    }

    public void setMongoSKSort(MongoSKSort mongoSKSort) {
        this.mongoSKSort = mongoSKSort;
    }

    public FindPublisher<Document> buildIterable() {
        MongoCollection<Document> mongoCollection = getMongoSKCollection().getMongoCollection();
        FindPublisher<Document> findPublisher = mongoCollection.find(getMongoSKFilter().getFilter());
        if (getMongoSKSort() == null)
            return findPublisher;
        return findPublisher.sort(getMongoSKSort().getSort());
    }

    public String getDisplay() {
        List<String> stringList = new ArrayList<>();
        stringList.add("mongo document");
        if (mongoSKCollection != null)
            stringList.add("of " + mongoSKCollection.getMongoCollection().getNamespace().getCollectionName() + " collection");
        if (mongoSKFilter != null)
            stringList.add("with filter " + mongoSKFilter.getDisplay());
        if (mongoSKSort != null)
            stringList.add("sorted by " + mongoSKSort.getDisplay());
        return String.join(" ", stringList);
    }

    @Override
    public String toString() {
        return "MongoSKQuery{" +
            "mongoSKCollection=" + mongoSKCollection +
            ", mongoSKFilter=" + mongoSKFilter +
            ", mongoSKSort=" + mongoSKSort +
            '}';
    }
}

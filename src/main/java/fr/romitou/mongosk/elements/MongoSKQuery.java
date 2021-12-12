package fr.romitou.mongosk.elements;

import com.mongodb.client.model.Filters;
import com.mongodb.reactivestreams.client.FindPublisher;
import com.mongodb.reactivestreams.client.MongoCollection;
import fr.romitou.mongosk.LoggerHelper;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class MongoSKQuery {

    private MongoSKCollection mongoSKCollection;
    private MongoSKFilter mongoSKFilter;
    private MongoSKSort mongoSKSort;
    private Boolean diskUsage;
    private String comment;
    private Integer limit;
    private Integer skip;

    public MongoSKQuery() {
    }

    public MongoSKCollection getMongoSKCollection() {
        return mongoSKCollection;
    }

    public void setMongoSKCollection(MongoSKCollection mongoSKCollection) {
        this.mongoSKCollection = mongoSKCollection;
    }

    public MongoSKFilter getMongoSKFilter() {
        if (mongoSKFilter == null)
            return new MongoSKFilter(Filters.empty(), "empty filter");
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

    public Boolean getDiskUsage() {
        return diskUsage;
    }

    public void setDiskUsage(Boolean diskUsage) {
        this.diskUsage = diskUsage;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getSkip() {
        return skip;
    }

    public void setSkip(Integer skip) {
        this.skip = skip;
    }

    public void printDebug() {
        LoggerHelper.debug("Informations about this MongoSK query:",
            "MongoSK Filter: " + this.mongoSKFilter,
            "MongoSK Sort: " + this.mongoSKSort,
            "Disk usage: " + this.diskUsage,
            "Comment: " + this.comment,
            "Limit: " + this.limit,
            "Skip: " + this.skip
        );
    }


    public FindPublisher<Document> buildFindPublisher() {
        MongoCollection<Document> mongoCollection = getMongoSKCollection().getMongoCollection();
        FindPublisher<Document> findPublisher;
        findPublisher = (getMongoSKFilter() == null) ? mongoCollection.find() : mongoCollection.find(getMongoSKFilter().getFilter());
        if (getLimit() != null)
            findPublisher = findPublisher.limit(getLimit());
        if (getSkip() != null)
            findPublisher = findPublisher.skip(getSkip());
        if (getDiskUsage() != null)
            findPublisher = findPublisher.allowDiskUse(getDiskUsage());
        if (getComment() != null)
            findPublisher = findPublisher.comment(getComment());
        if (getMongoSKSort() != null)
            findPublisher = findPublisher.sort(getMongoSKSort().getSort());
        return findPublisher;
    }

    public String getDisplay() {
        List<String> stringList = new ArrayList<>();
        stringList.add("mongo query");
        if (mongoSKCollection != null)
            stringList.add("of " + mongoSKCollection.getMongoCollection().getNamespace().getCollectionName() + " collection");
        if (mongoSKFilter != null)
            stringList.add("with " + mongoSKFilter.getDisplay());
        if (mongoSKSort != null)
            stringList.add("sorted by " + mongoSKSort.getDisplay());
        if (getComment() != null)
            stringList.add("with comment \"" + getComment() + "\"");
        if (getDiskUsage() != null && !getDiskUsage())
            stringList.add("without disk usage");
        if (limit != null)
            stringList.add("with limit of " + limit + " document(s)");
        if (skip != null)
            stringList.add("with skip of " + limit + " document(s)");
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

package fr.romitou.mongosk.elements;

import java.util.Objects;

public class MongoQueryElement {
    private final String key;
    private final Integer index;

    public MongoQueryElement(String key) {
        this.key = key;
        this.index = null;
    }

    public MongoQueryElement(int index) {
        this.key = null;
        this.index = index;
    }

    public boolean isIndex() {
        return index != null;
    }

    public String getKey() {
        return key;
    }

    public Integer getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return isIndex() ? "[" + index + "]" : key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MongoQueryElement that = (MongoQueryElement) o;
        return Objects.equals(key, that.key) && Objects.equals(index, that.index);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, index);
    }
}

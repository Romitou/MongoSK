package fr.romitou.mongosk.adapters.codecs;

import fr.romitou.mongosk.adapters.MongoSKCodec;
import org.bson.Document;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.io.StreamCorruptedException;

public class VectorCodec implements MongoSKCodec<Vector> {
    @NotNull
    @Override
    public Vector deserialize(Document document) throws StreamCorruptedException {
        Integer x = document.getInteger("x"),
            y = document.getInteger("y"),
            z = document.getInteger("z");
        if (x == null || y == null || z == null)
            throw new StreamCorruptedException("Cannot retrieve x, y and z fields from document!");
        return new Vector(x, y, z);
    }

    @NotNull
    @Override
    public Document serialize(Vector vector) {
        Document document = new Document();
        document.put("x", vector.getX());
        document.put("y", vector.getY());
        document.put("z", vector.getZ());
        return document;
    }

    @NotNull
    @Override
    public Class<? extends Vector> getReturnType() {
        return Vector.class;
    }

    @NotNull
    @Override
    public String getName() {
        return "vector";
    }
}

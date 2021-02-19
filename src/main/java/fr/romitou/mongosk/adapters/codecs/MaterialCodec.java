package fr.romitou.mongosk.adapters.codecs;

import fr.romitou.mongosk.adapters.MongoSKCodec;
import org.bson.Document;
import org.bukkit.Material;

import javax.annotation.Nonnull;
import java.io.StreamCorruptedException;

public class MaterialCodec implements MongoSKCodec<Material> {

    @Nonnull
    @Override
    public Material deserialize(Document document) throws StreamCorruptedException {
        String name = document.getString("name");
        if (name == null)
            throw new StreamCorruptedException("Cannot retrieve material name from document!");
        Material material = Material.matchMaterial(name);
        if (material == null)
            throw new StreamCorruptedException("No material with the given name was found!");
        return material;
    }

    @Nonnull
    @Override
    public Document serialize(Material material) {
        Document document = new Document();
        document.put("name", material.name());
        return document;
    }

    @Override
    @Nonnull
    public Class<? extends Material> getReturnType() {
        return Material.class;
    }

    @Override
    @Nonnull
    public String getName() {
        return "material";
    }

}

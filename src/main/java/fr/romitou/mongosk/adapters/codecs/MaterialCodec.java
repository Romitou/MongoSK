package fr.romitou.mongosk.adapters.codecs;

import fr.romitou.mongosk.adapters.MongoSKCodec;
import org.bson.Document;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.io.StreamCorruptedException;

public class MaterialCodec implements MongoSKCodec<Material> {

    @NotNull
    @Override
    public Material deserialize(Document document) throws StreamCorruptedException {
        String name = document.getString("name");
        if (name == null)
            throw new StreamCorruptedException("Cannot retrieve name field from document!");
        Material material = Material.matchMaterial(name);
        if (material == null)
            throw new StreamCorruptedException("Cannot parse given material name!");
        return material;
    }

    @NotNull
    @Override
    public Document serialize(Material material) {
        Document document = new Document();
        document.put("name", material.name());
        return document;
    }

    @Override
    @NotNull
    public Class<? extends Material> getReturnType() {
        return Material.class;
    }

    @Override
    @NotNull
    public String getName() {
        return "material";
    }

}

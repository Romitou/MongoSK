package fr.romitou.mongosk.adapters.codecs;

import fr.romitou.mongosk.adapters.MongoSKCodec;
import org.bson.Document;
import org.bukkit.Material;

import javax.annotation.Nonnull;

public class MaterialCodec implements MongoSKCodec<Material> {

    @Override
    @Nonnull
    public Material deserialize(Document document) {
        int ordinal = document.get("ordinal", Integer.class);
        return Material.values()[ordinal];
    }

    @Nonnull
    @Override
    public Document serialize(Material material) {
        Document document = new Document();
        document.put("ordinal", material.ordinal());
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

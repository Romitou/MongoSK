package fr.romitou.mongosk.adapters.codecs;

import ch.njol.skript.Skript;
import fr.romitou.mongosk.adapters.MongoSKCodec;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import javax.annotation.Nonnull;
import java.io.StreamCorruptedException;
import java.util.Arrays;
import java.util.List;

public class ItemStackCodec implements MongoSKCodec<ItemStack> {

    private static final Boolean IS_LEGACY = !Skript.isRunningMinecraft(1, 13);

    @Nonnull
    @Override
    public ItemStack deserialize(Document document) throws StreamCorruptedException {
        Integer amount = document.getInteger("amount"),
            customModelData = document.getInteger("customModelData"),
            damage = document.getInteger("damage");
        String displayName = document.getString("displayName"),
            materialName = document.getString("material");
        List<String> lore = document.getList("lore", String.class);
        if (materialName == null)
            throw new StreamCorruptedException("Cannot deserialize material from document!");
        System.out.println(Arrays.toString(Material.values()));
        Material material;
        if (IS_LEGACY) {
            material = Material.valueOf(materialName);
        } else {
           material = Material.getMaterial(materialName);
           if (material == null) // Try to get the legacy material...
               material = Material.getMaterial(materialName, true);
        }
        if (material == null)
            throw new StreamCorruptedException("Given material name is invalid!");
        ItemStack baseItemStack = new ItemStack(material, amount);
        if (customModelData != null)
            baseItemStack.getItemMeta().setCustomModelData(customModelData);
        if (damage != null)
            ((Damageable) baseItemStack.getItemMeta()).setDamage(damage);
        if (displayName != null)
            baseItemStack.getItemMeta().setDisplayName(displayName);
        if (lore != null)
            baseItemStack.getItemMeta().setLore(lore);
        return baseItemStack;
    }

    @Nonnull
    @Override
    public Document serialize(ItemStack itemStack) {
        Document document = new Document();
        document.put("material", itemStack.getType().name());
        document.put("amount", itemStack.getAmount());
        if (itemStack instanceof Damageable)
            document.put("damage", ((Damageable) itemStack.getItemMeta()).getDamage());
        if (itemStack.getItemMeta().hasCustomModelData())
            document.put("customModelData", itemStack.getItemMeta().getCustomModelData());
        document.put("displayName", itemStack.getItemMeta().getDisplayName());
        document.put("lore", itemStack.getLore());
        return document;
    }

    @Nonnull
    @Override
    public Class<? extends ItemStack> getReturnType() {
        return ItemStack.class;
    }

    @Nonnull
    @Override
    public String getName() {
        return "itemStack";
    }
}

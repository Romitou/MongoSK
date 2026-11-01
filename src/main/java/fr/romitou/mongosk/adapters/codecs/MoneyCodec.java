package fr.romitou.mongosk.adapters.codecs;

import ch.njol.skript.hooks.economy.classes.Money;
import fr.romitou.mongosk.adapters.MongoSKCodec;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.io.StreamCorruptedException;

public class MoneyCodec implements MongoSKCodec<Money> {

    @NotNull
    @Override
    public Money deserialize(Document document) throws StreamCorruptedException {
        Number amount = document.get("amount", Number.class);
        if (amount == null)
            throw new StreamCorruptedException("Cannot retrieve amount field from document!");
        return new Money(amount.doubleValue());
    }

    @NotNull
    @Override
    public Document serialize(Money money) {
        Document document = new Document();
        document.put("amount", money.getAmount());
        return document;
    }

    @Override
    @NotNull
    public Class<? extends Money> getReturnType() {
        return Money.class;
    }

    @Override
    @NotNull
    public String getName() {
        return "money";
    }

}

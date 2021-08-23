package fr.romitou.mongosk.adapters.codecs;

import ch.njol.skript.hooks.economy.classes.Money;
import fr.romitou.mongosk.adapters.MongoSKCodec;
import org.bson.Document;

import javax.annotation.Nonnull;
import java.io.StreamCorruptedException;

public class MoneyCodec implements MongoSKCodec<Money> {

    @Nonnull
    @Override
    public Money deserialize(Document document) throws StreamCorruptedException {
        Double amount = document.getDouble("amount");
        if (amount == null)
            throw new StreamCorruptedException("Cannot retrieve amount field from document!");
        return new Money(amount);
    }

    @Nonnull
    @Override
    public Document serialize(Money money) {
        Document document = new Document();
        document.put("amount", money.getAmount());
        return document;
    }

    @Override
    @Nonnull
    public Class<? extends Money> getReturnType() {
        return Money.class;
    }

    @Override
    @Nonnull
    public String getName() {
        return "money";
    }

}

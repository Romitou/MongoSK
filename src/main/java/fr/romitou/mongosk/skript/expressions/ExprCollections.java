package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.mongodb.client.MongoDatabase;
import org.bukkit.event.Event;

import java.util.ArrayList;

@Name("Mongo Collections")
@Description("This expression allows you to retrieve collection names from a Mongo database.")
@Examples({"loop all collections from database \"mongosk\" with client \"test\":" +
        "\tbroadcast loop-value" +
        "" +
        "set {_collections::*} to all collections from database \"mongosk\" with client \"test\""})
@Since("1.0.0")
public class ExprCollections extends SimpleExpression<String> {

    static {
        Skript.registerExpression(ExprCollections.class, String.class, ExpressionType.SIMPLE, "[all] [mongo[db]] collections [name[s]] (of|from) %mongodatabase%");
    }

    private Expression<MongoDatabase> exprDatabase;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        exprDatabase = (Expression<MongoDatabase>) exprs[0];
        return true;
    }

    @Override
    protected String[] get(Event e) {
        MongoDatabase database = exprDatabase.getSingle(e);
        if (database == null)
            return new String[0];
        ArrayList<String> list = new ArrayList<>();
        database.listCollectionNames().forEach(list::add);
        return list.toArray(new String[0]);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "all mongo collections from " + exprDatabase.toString(e, debug);
    }

}

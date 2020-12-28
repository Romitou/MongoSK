package fr.romitou.mongosk.skript.expressions.databases;

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
import com.mongodb.client.MongoClient;
import org.bukkit.event.Event;

import java.util.ArrayList;

@Name("Mongo Databases")
@Description("This expression allows you to retrieve the names of the databases of a Mongo client.")
@Examples({"loop all databases from client named \"test\":",
        "\tbroadcast \"%loop-value\"",
        "",
        "set {_databases::*} to  all databases from client named \"test\""})
@Since("1.0.0")
public class ExprDatabases extends SimpleExpression<String> {

    static {
        Skript.registerExpression(ExprDatabases.class, String.class, ExpressionType.SIMPLE, "[all] [mongo[db]] databases (of|from) %mongoclient%");
    }

    private Expression<MongoClient> exprClient;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        exprClient = (Expression<MongoClient>) exprs[0];
        return true;
    }

    @Override
    protected String[] get(Event e) {
        MongoClient client = exprClient.getSingle(e);
        if (client == null)
            return new String[0];
        ArrayList<String> list = new ArrayList<>();
        client.listDatabaseNames().forEach(list::add);
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
        return "all mongo databases from " + exprClient.toString(e, debug);
    }

}

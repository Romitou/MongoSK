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
import com.mongodb.client.MongoClient;
import fr.romitou.mongosk.skript.MongoManager;
import org.bukkit.event.Event;

@Name("Mongo Client")
@Description("This expression allows you to retrieve a Mongo client by name, initially defined in the creation effect.")
@Examples({"create a new mongo client to host \"mongodb://127.0.0.1\" as \"mongosk\"",
        "set {_client} to client named \"mongosk\""})
@Since("1.0.0")
public class ExprClient extends SimpleExpression<MongoClient> {

    static {
        Skript.registerExpression(ExprClient.class, MongoClient.class, ExpressionType.SIMPLE, "[mongo[db]] client [of host] [(named|with name|called)] %string%");
    }

    private Expression<String> exprName;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        exprName = (Expression<String>) exprs[0];
        return true;
    }

    @Override
    protected MongoClient[] get(Event e) {
        String name = exprName.getSingle(e);
        return (name == null)
                ? new MongoClient[0]
                : new MongoClient[]{MongoManager.getClient(name)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends MongoClient> getReturnType() {
        return MongoClient.class;
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "mongo client named " + exprName.toString(e, debug);
    }

}

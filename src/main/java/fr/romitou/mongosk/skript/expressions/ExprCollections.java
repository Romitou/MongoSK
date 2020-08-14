package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import fr.romitou.mongosk.utils.MongoManager;
import org.bukkit.event.Event;

public class ExprCollections extends SimpleExpression<String> {

    static {
        Skript.registerExpression(ExprCollections.class, String.class, ExpressionType.SIMPLE, "[all] mongo[db] collections of database [named] %string%");
    }

    private Expression<String> database;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        database = (Expression<String>) exprs[0];
        return true;
    }

    @Override
    protected String[] get(Event e) {
        return MongoManager.getCollectionsFromDatabase(database.getSingle(e)).toArray(new String[0]);
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
        return "all mongodb collections of database " + database.toString(e, debug);
    }

}

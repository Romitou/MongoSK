package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bson.Document;
import org.bukkit.event.Event;

public class ExprMongoValue extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(ExprMongoValue.class, Object.class, ExpressionType.SIMPLE, "[mongo[db]] value %string% (of|from) %mongodocument%");
    }

    private Expression<String> value;
    private Expression<Document> document;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        value = (Expression<String>) exprs[0];
        document = (Expression<Document>) exprs[1];
        return true;
    }

    @Override
    protected Object[] get(Event e) {
        String v = value.getSingle(e);
        Document d = document.getSingle(e);
        if (v == null || d == null)
            return null;
        return new Object[]{d.get(v)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Object> getReturnType() {
        return Object.class;
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "mongo value " + value.toString(e, debug) + " of " + document.toString(e, debug);
    }

}

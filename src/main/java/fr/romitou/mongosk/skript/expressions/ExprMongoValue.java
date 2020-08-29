package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bson.Document;
import org.bukkit.event.Event;

import java.util.Arrays;

public class ExprMongoValue extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(ExprMongoValue.class, Object.class, ExpressionType.SIMPLE, "[mongo[db]] (1¦value|2¦list) %string% (of|from) %mongodocument%");
    }

    private Expression<String> exprValue;
    private Expression<Document> exprDocument;
    private int parseMark;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        exprValue = (Expression<String>) exprs[0];
        exprDocument = (Expression<Document>) exprs[1];
        parseMark = parseResult.mark;
        return true;
    }

    @Override
    protected Object[] get(Event e) {
        String value = exprValue.getSingle(e);
        Document document = exprDocument.getSingle(e);
        if (value == null || document == null)
            return null;
        return new Object[]{document.get(value)};
    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.REMOVE_ALL)
            return null;
        if (parseMark == 1 && (mode == Changer.ChangeMode.REMOVE || mode == Changer.ChangeMode.ADD))
            return null;
        return CollectionUtils.array(Object.class);
    }

    @Override
    public void change(Event e, Object[] delta, Changer.ChangeMode mode) {
        String value = exprValue.getSingle(e);
        Document document = exprDocument.getSingle(e);
        if (value == null || document == null)
            return;
        switch (mode) {
            case SET:
                document.put(value, delta[0]);
                break;
            case DELETE:
                document.put(value, null);
                break;
            case RESET:
                document.clear();
                break;
            case ADD:
                document.getList(value, Object.class).add(Arrays.stream(delta).toArray());
                break;
            case REMOVE:
                document.getList(value, Object.class).remove(Arrays.stream(delta).toArray());
                break;
            default:
                break;
        }
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<?> getReturnType() {
        return Object.class;
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "mongo value " + exprValue.toString(e, debug) + " of " + exprDocument.toString(e, debug);
    }

}

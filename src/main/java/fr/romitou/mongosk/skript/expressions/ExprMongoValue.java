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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        if (parseMark == 1) {
            return new Object[]{document.get(value)};
        } else {
            List<Object> list = document.getList(value, Object.class);
            return (list != null) ? list.toArray() : null;
        }
    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        if ((mode == Changer.ChangeMode.REMOVE_ALL || mode == Changer.ChangeMode.RESET) || (parseMark == 1 && (mode == Changer.ChangeMode.REMOVE || mode == Changer.ChangeMode.ADD)))
            return null;
        return CollectionUtils.array((parseMark == 1) ? Object.class : Object[].class);
    }

    @Override
    public void change(Event e, Object[] delta, Changer.ChangeMode mode) {
        String value = exprValue.getSingle(e);
        Document document = exprDocument.getSingle(e);
        List<Object> deltaList = delta != null ? Arrays.asList(delta) : new ArrayList<>();
        if (value == null || document == null)
            return;
        switch (mode) {
            case SET:
                document.put(value, deltaList);
                break;
            case DELETE:
                document.put(value, null);
                break;
            case ADD:
                List<Object> addList = document.getList(value, Object.class);
                if (addList == null) {
                    document.put(value, deltaList);
                    return;
                }
                addList.addAll(deltaList);
                break;
            case REMOVE:
                List<Object> removeList = document.getList(value, Object.class);
                if (removeList == null)
                    return;
                deltaList.forEach(removeList::remove);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean isSingle() {
        return parseMark == 1;
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

package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
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

@Name("Mongo Value")
@Description("This expression allows you to retrieve and modify certain values of a document. If you define an already existing entry, it will be replaced. Lists are supported.")
@Examples({"set {_document} to first document where \"points\" is \"10\" in {_collection}",
        "set {test::%value \"test\" of {_document}%} to true",
        "loop list \"example\" of {_document}:",
        "\tbroadcast \"%loop-value%\"",
        "set list \"example\" of {_document} to 10, 54 and 203",
        "add 41 to list \"example\" of {_document}",
        "save {_document} in collection named \"example\" from database named \"mongosk\" with client named \"default\""})
@Since("1.0.0")
public class ExprValue extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(ExprValue.class, Object.class, ExpressionType.SIMPLE, "[mongo[db]] (1¦value|2¦list) %string% (of|from) %mongodocument%");
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
            return new Object[0];
        if (parseMark == 1) {
            try {
                return new Object[]{document.get(value)};
            } catch (NullPointerException ex) {
                return null; // That document value doesn't exist;
            }
        } else {
            try {
                List<Object> list = document.getList(value, Object.class);
                return (list == null) ? new Object[0] : list.toArray();

            } catch (ClassCastException ex) {
                Skript.error("The mongodb document value is not a list!"); // I'm not sure whether or not this should return an error
                return null;
            }
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
                document.put(value, deltaList.size() == 1 ? delta[0] : deltaList);
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

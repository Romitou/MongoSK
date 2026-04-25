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
import fr.romitou.mongosk.LoggerHelper;
import fr.romitou.mongosk.adapters.MongoSKAdapter;
import fr.romitou.mongosk.elements.MongoQueryElement;
import fr.romitou.mongosk.elements.MongoSKDocument;
import fr.romitou.mongosk.utils.MongoPathParser;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Name("Mongo Embedded Value")
@Description({
    "It is sometimes redundant to extract a document from a document repeatedly and undertake several data manipulations on multiple lines.",
    "To remedy this, this expression will ask you for the path (using MongoSK specific syntax) as follows: \"foo.bar\", \"foo[0].bar\", or \"foo.bar[2]\".",
    "Embedded documents must be separated by a dot, and you must specify in brackets the index of the array (starting from 0) if it is a list."
})
@Examples({
    "# Given the following JSON document in {doc}:",
    "# { \"doc\": { \"foo\": \"bar\" }, \"list\": [ { \"numbers\": [ \"1\", \"2\" ], \"hello\": \"world\" } ] }",
    "set {_docfoo} to mongo embedded value with path \"doc.foo\" of {doc}",
    "broadcast \"%{_docfoo}%\" # Output: bar",
    "set {_listnumbers} to mongo embedded value with path \"list[0].numbers[1]\" of {doc}",
    "broadcast \"%{_listnumbers}%\" # Output: 2",
    "set {_listhello} to mongo embedded value with path \"list[0].hello\" of {doc}",
    "broadcast \"%{_listhello}%\"  # Output: world"
})
@Since("2.3.0")
public class ExprMongoEmbeddedValue extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(
            ExprMongoEmbeddedValue.class,
            Object.class,
            ExpressionType.COMBINED,
            "mongo[(sk|db)] embedded (1¦(field|value)|2¦(array|list)) [with path] %string% of %mongoskdocument%"
        );
    }

    private Expression<String> exprFieldName;
    private Expression<MongoSKDocument> exprMongoSKDocument;
    private Boolean isSingle;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @Nonnull Kleenean isDelayed, @Nonnull SkriptParser.ParseResult parseResult) {
        exprFieldName = (Expression<String>) exprs[0];
        exprMongoSKDocument = (Expression<MongoSKDocument>) exprs[1];
        isSingle = parseResult.mark == 1;
        return true;
    }

    @Override
    protected Object[] get(@Nonnull final Event e) {
        String fieldName = exprFieldName.getSingle(e);
        MongoSKDocument mongoSKDocument = exprMongoSKDocument.getSingle(e);
        if (fieldName == null || mongoSKDocument == null || mongoSKDocument.getBsonDocument() == null)
            return new Object[0];
        ArrayList<MongoQueryElement> mongoQueryElements = MongoPathParser.parse(fieldName);
        try {
            Object value = mongoSKDocument.getEmbeddedValue(mongoQueryElements);
            if (isSingle) {
                return new Object[]{MongoSKAdapter.deserializeValue(value)};
            } else {
                if (value instanceof List) {
                    return MongoSKAdapter.deserializeValues(((List<Object>) value).toArray());
                } else if (value instanceof Object[]) {
                    return MongoSKAdapter.deserializeValues((Object[]) value);
                } else {
                    return new Object[0];
                }
            }
        } catch (ClassCastException ex) {
            LoggerHelper.severe("The type of item you are querying is not correct. " +
                    "This can happen if you want to retrieve a list, but it is a single value.",
                "Document: " + mongoSKDocument.getBsonDocument().toJson(),
                "Exception: " + ex.getMessage());
            return new Object[0];
        }
    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.RESET)
            return null;
        if (isSingle && (mode == Changer.ChangeMode.ADD || mode == Changer.ChangeMode.REMOVE || mode == Changer.ChangeMode.REMOVE_ALL))
            return null;
        return CollectionUtils.array(Object[].class);
    }

    @Override
    public void change(Event e, Object[] delta, Changer.ChangeMode mode) {
        String fieldName = exprFieldName.getSingle(e);
        MongoSKDocument mongoSKDocument = exprMongoSKDocument.getSingle(e);
        List<?> omega = new ArrayList<>();
        if (delta != null)
            omega = Arrays.asList(MongoSKAdapter.serializeArray(delta));
        if (fieldName == null || mongoSKDocument == null || mongoSKDocument.getBsonDocument() == null)
            return;
        ArrayList<MongoQueryElement> mongoQueryElements = MongoPathParser.parse(fieldName);
        switch (mode) {
            case SET:
                mongoSKDocument.setEmbeddedValue(mongoQueryElements, isSingle ? omega.get(0) : omega);
                break;
            case DELETE:
                mongoSKDocument.setEmbeddedValue(mongoQueryElements, null);
                break;
            case ADD:
                Object addValue = mongoSKDocument.getEmbeddedValue(mongoQueryElements);
                if (addValue instanceof List) {
                    List<Object> list = new ArrayList<>((List<Object>) addValue);
                    list.addAll(omega);
                    mongoSKDocument.setEmbeddedValue(mongoQueryElements, list);
                } else if (addValue instanceof Object[]) {
                    List<Object> list = new ArrayList<>(Arrays.asList((Object[]) addValue));
                    list.add(omega);
                    mongoSKDocument.setEmbeddedValue(mongoQueryElements, list);
                } else {
                    mongoSKDocument.setEmbeddedValue(mongoQueryElements, omega);
                }
                break;
            case REMOVE_ALL:
            case REMOVE:
                Object removeValue = mongoSKDocument.getEmbeddedValue(mongoQueryElements);
                if (removeValue instanceof List) {
                    List<Object> list = new ArrayList<>((List<Object>) removeValue);
                    list.removeAll(omega);
                    mongoSKDocument.setEmbeddedValue(mongoQueryElements, list);
                } else if (removeValue instanceof Object[]) {
                    List<Object> list = new ArrayList<>(Arrays.asList((Object[]) removeValue));
                    list.removeAll(omega);
                    mongoSKDocument.setEmbeddedValue(mongoQueryElements, list);
                } else {
                    mongoSKDocument.setEmbeddedValue(mongoQueryElements, null);
                }
            break;
        }
    }

    @Override
    public boolean isSingle() {
        return isSingle;
    }

    @Override
    @Nonnull
    public Class<?> getReturnType() {
        return Object.class;
    }

    @Override
    @Nonnull
    public String toString(@Nullable Event e, boolean debug) {
        return "mongo " + (isSingle ? "value" : "list") + " named " + exprFieldName.toString(e, debug) + " of " + exprMongoSKDocument.toString(e, debug);
    }

}

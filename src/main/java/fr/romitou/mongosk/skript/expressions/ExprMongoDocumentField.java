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
import fr.romitou.mongosk.Logger;
import fr.romitou.mongosk.adapters.MongoSKAdapter;
import fr.romitou.mongosk.elements.MongoSKDocument;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

@Name("Mongo document field")
@Description("This expression is extremely important because it allows you to manage the values and lists of your documents. " +
    "You can define lists, add items to lists, set values, or delete them. If Skript adapters are enabled, you can " +
    "specify any type of data, and they will be automatically transformed by MongoSK so that they can be registered. " +
    "So you can store players, blocks, items for example.")
@Examples({"command test:",
    "\ttrigger:",
    "\t\tset {_doc} to first mongo document from {mycollection}",
    "\t\tif mongo value \"player\" of {_doc} is not set:",
    "\t\t\tset mongo value \"player\" of {_doc} to player",
    "\t\tset {_player} to mongo value \"player\" of {_doc}",
    "\t\tsend \"test\" to {_player}"})
@Since("2.0.0")
public class ExprMongoDocumentField extends SimpleExpression<Object> {


    static {
        Skript.registerExpression(
            ExprMongoDocumentField.class,
            Object.class,
            ExpressionType.COMBINED,
            "mongo[(sk|db)] (1¦(field|value)|2¦(array|list)) [named] %string% of %mongoskdocument%"
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
        if (!mongoSKDocument.getBsonDocument().containsKey(fieldName)) {
            Logger.severe("The specified field does not exist in the document.",
                "Document: " + mongoSKDocument.getBsonDocument().toJson(),
                "Keys: " + mongoSKDocument.getBsonDocument().keySet());
            return new Object[0];
        }
        try {
            if (isSingle) {
                Object value = mongoSKDocument.getBsonDocument().get(fieldName);
                return new Object[]{MongoSKAdapter.deserializeValue(value)};
            }
            List<Object> values = mongoSKDocument.getBsonDocument().getList(fieldName, Object.class);
            return MongoSKAdapter.deserializeValues(values.toArray());
        } catch (ClassCastException ex) {
            Logger.severe("The type of item you are querying is not correct. " +
                "This can happen if you want to retrieve a list, but it is a single value.",
                "Document: " + mongoSKDocument.getBsonDocument().toJson(),
                "Exception: " + ex.getMessage());
            return new Object[0];
        }
    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        switch (mode) {
            case ADD:
                if (!isSingle)
                    return CollectionUtils.array(Object[].class);
            case SET:
            case REMOVE:
            case DELETE:
                return CollectionUtils.array(isSingle ? Object.class : Object[].class);
            default:
                return new Class[0];
        }
    }

    @Override
    public void change(@Nonnull final Event e, Object[] delta, @Nonnull Changer.ChangeMode mode) {
        String fieldName = exprFieldName.getSingle(e);
        MongoSKDocument mongoSKDocument = exprMongoSKDocument.getSingle(e);
        List<Object> omega = Arrays.asList(MongoSKAdapter.serializeArray(delta));
        if (fieldName == null || mongoSKDocument == null || mongoSKDocument.getBsonDocument() == null || omega.size() == 0)
            return;
        switch (mode) {
            case ADD:
                try {
                    List<Object> addList = mongoSKDocument.getBsonDocument().getList(fieldName, Object.class);
                    addList.addAll(omega);
                } catch (RuntimeException ex) {
                    Logger.severe("An error occurred during adding objects to Mongo document: " + ex.getMessage(),
                        "Field name: " + fieldName,
                        "Document: " + mongoSKDocument.getBsonDocument().toJson(),
                        "Omega: " + omega
                    );
                }
                break;
            case SET:
                try {
                    mongoSKDocument.getBsonDocument().put(fieldName, isSingle ? omega.get(0) : omega);
                } catch (RuntimeException ex) {
                    Logger.severe("An error occurred during setting Mongo document field: " + ex.getMessage(),
                        "Field name: " + fieldName,
                        "Document: " + mongoSKDocument.getBsonDocument().toJson(),
                        "Omega: " + omega
                    );
                }
                break;
            case REMOVE:
                try {
                    List<Object> removeList = mongoSKDocument.getBsonDocument().getList(fieldName, Object.class);
                    removeList.removeAll(omega);
                } catch (RuntimeException ex) {
                    Logger.severe("An error occurred during removing objects from Mongo document: " + ex.getMessage(),
                        "Field name: " + fieldName,
                        "Document: " + mongoSKDocument.getBsonDocument().toJson(),
                        "Omega: " + omega
                    );
                }
                break;
            case DELETE:
                try {

                    mongoSKDocument.getBsonDocument().remove(fieldName);
                } catch (RuntimeException ex) {
                    Logger.severe("An error occurred during deleting field of Mongo document: " + ex.getMessage(),
                        "Field name: " + fieldName,
                        "Document: " + mongoSKDocument.getBsonDocument().toJson(),
                        "Omega: " + omega
                    );
                }
                break;
            default:
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

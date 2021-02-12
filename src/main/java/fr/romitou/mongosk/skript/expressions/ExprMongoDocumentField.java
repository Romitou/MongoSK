package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import fr.romitou.mongosk.elements.MongoSKDocument;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

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
        if (isSingle)
            return new Object[]{mongoSKDocument.getBsonDocument().get(fieldName)};
        return mongoSKDocument.getBsonDocument().getList(fieldName, Object.class).toArray();
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
        List<Object> omega = Arrays.asList(delta);
        if (fieldName == null || mongoSKDocument == null || mongoSKDocument.getBsonDocument() == null || omega.size() == 0)
            return;
        switch (mode) {
            case ADD:
                List<Object> addList = mongoSKDocument.getBsonDocument().getList(fieldName, Object.class);
                addList.addAll(omega);
                break;
            case SET:
                mongoSKDocument.getBsonDocument().put(fieldName, isSingle ? omega.get(0) : omega);
                break;
            case REMOVE:
                List<Object> removeList = mongoSKDocument.getBsonDocument().getList(fieldName, Object.class);
                removeList.removeAll(omega);
                break;
            case DELETE:
                mongoSKDocument.getBsonDocument().remove(fieldName);
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

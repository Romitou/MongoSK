package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import fr.romitou.mongosk.elements.MongoSKDocument;
import org.bukkit.event.Event;

@Name("Mongo document values")
@Description("With this expression, retrieve all the values of a document. The expression returns a list of different data types.")
@Examples({"set {_values::*} to mongo values of {_document}"})
public class ExprMongoValues extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(
            ExprMongoValues.class,
            Object.class,
            ExpressionType.SIMPLE,
            "mongo[(sk|db)] values of [document] %mongoskdocument%"
        );
    }

    private Expression<MongoSKDocument> exprDocument;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        exprDocument = (Expression<MongoSKDocument>) exprs[0];
        return true;
    }

    @Override
    protected Object[] get(Event e) {
        MongoSKDocument document = exprDocument.getSingle(e);
        if (document == null)
            return new Object[0];
        return document.getBsonDocument().values().toArray();
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<?> getReturnType() {
        return Object.class;
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "mongo values of document " + exprDocument.toString(e, debug);
    }
}

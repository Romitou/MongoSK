package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import fr.romitou.mongosk.elements.MongoSKDocument;
import org.bukkit.event.Event;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Mongo document keys")
@Description({
    "With this expression, retrieve all the keys (field names) of a document.",
    "The expression returns a list of text strings representing the keys."
})
@Examples({
    "set {_keys::*} to mongo keys of {_document}"
})
public class ExprMongoKeys extends SimpleExpression<String> {

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EXPRESSION, SyntaxInfo.Expression.builder(ExprMongoKeys.class, String.class)
            .addPatterns("mongo[(sk|db)] keys of [document] %mongoskdocument%")
            .priority(SyntaxInfo.SIMPLE)
            .build());
    }

    private Expression<MongoSKDocument> exprDocument;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        exprDocument = (Expression<MongoSKDocument>) exprs[0];
        return true;
    }

    @Override
    protected String[] get(Event e) {
        MongoSKDocument document = exprDocument.getSingle(e);
        if (document == null)
            return new String[0];
        return document.getBsonDocument().keySet().toArray(new String[0]);
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
        return "mongo keys of document " + exprDocument.toString(e, debug);
    }
}

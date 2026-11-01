package fr.romitou.mongosk.skript.expressions;

import org.skriptlang.skript.registration.DefaultSyntaxInfos;
import ch.njol.skript.lang.ExpressionType;
import org.skriptlang.skript.registration.SyntaxRegistry;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import fr.romitou.mongosk.elements.MongoSKDocument;
import org.bukkit.event.Event;

@Name("Mongo document keys")
@Description({
    "With this expression, retrieve all the keys (field names) of a document.",
    "The expression returns a list of text strings representing the keys."
})
@Examples({
    "set {_keys::*} to mongo keys of {_document}"
})
public class ExprMongoKeys extends SimpleExpression<String> {

    static {
        fr.romitou.mongosk.MongoSK.getAddonInstance().syntaxRegistry().register(SyntaxRegistry.EXPRESSION, DefaultSyntaxInfos.Expression.builder(ExprMongoKeys.class, String.class).addPatterns("mongo[(sk|db)] keys of [document] %mongoskdocument%").priority(ExpressionType.SIMPLE.priority()).build());
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

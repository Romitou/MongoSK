package fr.romitou.mongosk.skript.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import fr.romitou.mongosk.elements.MongoSKDocument;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;

@Name("Mongo document has key?")
@Description("Checks if the documents contains the specified key.")
@Examples({"set {_doc} to a new mongo document",
    "set mongo value \"foo\" of {_doc} to \"bar\"",
    "if mongo document {_doc} has key \"foo\":",
    "\tbroadcast \"The 'foo' key exists!\"",
    "else:",
    "\tbroadcast \"The 'foo' key doesn't exist :(\""
})
@Since("2.1.0")
public class CondMongoDocHasKey extends Condition {

    static {
        Skript.registerCondition(
            CondMongoDocHasKey.class,
            "mongo[(sk|db)] document[s] %mongoskdocuments% (1¦(has|contains)|2¦doesn't (have|contain)) key [named] %string%"
        );
    }

    private Expression<MongoSKDocument> exprMongoSKDocument;
    private Expression<String> exprKey;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @Nonnull Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        exprMongoSKDocument = (Expression<MongoSKDocument>) exprs[0];
        exprKey = (Expression<String>) exprs[1];
        setNegated(parseResult.mark == 2);
        return true;
    }

    @Override
    public boolean check(@Nonnull Event e) {
        String key = exprKey.getSingle(e);
        if (key == null)
            return false;
        return exprMongoSKDocument.check(e, doc -> doc.getBsonDocument().containsKey(key), isNegated());
    }

    @Nonnull
    @Override
    public String toString(Event e, boolean debug) {
        return exprMongoSKDocument.toString(e, debug) + (isNegated() ? " doesn't have" : " has") + " key " + exprKey.toString(e, debug);
    }
}

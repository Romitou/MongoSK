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

@Name("Mongo document is empty?")
@Description("Checks if the document is empty (if it contains no keys or values).")
@Examples({"set {_doc} to a new mongo document",
    "if mongo document {_doc} is empty:",
    "\tbroadcast \"The document is empty!\""})
@Since("2.1.0")
public class CondMongoDocIsEmpty extends Condition {

    static {
        Skript.registerCondition(
            CondMongoDocIsEmpty.class,
            "mongo[(sk|db)] document[s] %mongoskdocuments% (is|are)[(1¦(n't| not))] empty"
        );
    }

    private Expression<MongoSKDocument> exprMongoSKDocument;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @Nonnull Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        exprMongoSKDocument = (Expression<MongoSKDocument>) exprs[0];
        setNegated(parseResult.mark == 1);
        return true;
    }

    @Override
    public boolean check(@Nonnull Event e) {
        return exprMongoSKDocument.check(e, doc -> doc.getBsonDocument().isEmpty(), isNegated());
    }

    @Nonnull
    @Override
    public String toString(Event e, boolean debug) {
        return exprMongoSKDocument.toString(e, debug) + (isNegated() ? " is" : " isn't") + " empty ";
    }
}

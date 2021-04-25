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

@Name("Mongo document has value?")
@Description("Checks if the documents contains the specified value.")
@Examples({"set {_doc} to a new mongo document",
    "set mongo value \"foo\" of {_doc} to \"bar\"",
    "if mongo document {_doc} has value \"bar\":",
    "\tbroadcast \"The 'bar' value exists!\"",
    "else:",
    "\tbroadcast \"The 'bar' value doesn't exist :(\""
})
@Since("2.1.0")
public class CondMongoDocHasValue extends Condition {

    static {
        Skript.registerCondition(
            CondMongoDocHasValue.class,
            "mongo[(sk|db)] document[s] %mongoskdocuments% (1¦(has|contains)|2¦doesn't (have|contain)) value %integer/number/string/boolean/mongoskdocument%"
        );
    }

    private Expression<MongoSKDocument> exprMongoSKDocument;
    private Expression<?> exprValue;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @Nonnull Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        exprMongoSKDocument = (Expression<MongoSKDocument>) exprs[0];
        exprValue = exprs[1];
        setNegated(parseResult.mark == 2);
        return true;
    }

    @Override
    public boolean check(@Nonnull Event e) {
        Object value = exprValue.getSingle(e);
        if (value == null)
            return false;
        return exprMongoSKDocument.check(e, doc -> doc.getBsonDocument().containsValue(value), isNegated());
    }

    @Nonnull
    @Override
    public String toString(Event e, boolean debug) {
        return exprMongoSKDocument.toString(e, debug) + (isNegated() ? " doesn't have" : " has") + " key " + exprValue.toString(e, debug);
    }
}

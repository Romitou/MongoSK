package fr.romitou.mongosk.skript.conditions;

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
import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

@Name("Mongo document is empty?")
@Description({
    "Checks if the document is completely empty, meaning it contains no keys or values.",
    "Useful for verifying document initialization or checking if an update removed all fields."
})
@Examples({
    "set {_doc} to a new mongo document",
    "if mongo document {_doc} is empty:",
    "	broadcast \"The document is empty!\""
})
@Since("2.1.0")
public class CondMongoDocIsEmpty extends Condition {

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.CONDITION, SyntaxInfo.builder(CondMongoDocIsEmpty.class)
            .addPatterns("mongo[(sk|db)] document[s] %mongoskdocuments% (is|are)[(1:(n't| not))] empty")
            .build());
    }

    private Expression<MongoSKDocument> exprMongoSKDocument;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        exprMongoSKDocument = (Expression<MongoSKDocument>) exprs[0];
        setNegated(parseResult.mark == 1);
        return true;
    }

    @Override
    public boolean check(@NotNull Event e) {
        return exprMongoSKDocument.check(e, doc -> doc.getBsonDocument().isEmpty(), isNegated());
    }

    @NotNull
    @Override
    public String toString(Event e, boolean debug) {
        return exprMongoSKDocument.toString(e, debug) + (isNegated() ? " is" : " isn't") + " empty ";
    }
}

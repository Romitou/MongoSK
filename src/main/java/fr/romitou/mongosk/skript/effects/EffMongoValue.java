package fr.romitou.mongosk.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import fr.romitou.mongosk.EffectSection;
import fr.romitou.mongosk.adapters.MongoSKAdapter;
import fr.romitou.mongosk.skript.sections.SectMongoDocument;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;

@Name("Mongo section document value")
@Description("These syntaxes can only be used in a Mongo section of a document creation. " +
    "They are useful when you want to create simple documents quickly. " +
    "If your document is complex, you should use the classic expressions.")
@Examples({"set {_nested} to a new mongo document with:",
    "\tmongo \"number\": 100",
    "\tmongo \"boolean\": false",
    "set {_doc} to a new mongo document with:",
    "\tmongo \"simpleField\": \"Hello!\"",
    "\tmongo \"nestedObject\": {_nested}",
    "broadcast {_doc}'s mongo json"})
@Since("2.1.0")
public class EffMongoValue extends Effect {

    static {
        Skript.registerEffect(EffMongoValue.class, "mongo[(sk|db)] %string%: %objects%");
    }

    private Expression<String> exprKey;
    private Expression<?> exprValue;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(@Nonnull Expression<?>[] exprs, int matchedPattern, @Nonnull Kleenean isDelayed, @Nonnull SkriptParser.ParseResult parseResult) {
        if (!EffectSection.isCurrentSection(SectMongoDocument.class))
            return false;
        exprKey = (Expression<String>) exprs[0];
        exprValue = exprs[1].getConvertedExpression(Object.class);
        return exprValue != null;
    }

    @Override
    protected void execute(@Nonnull Event e) {
        String key = exprKey.getSingle(e);
        Object[] value = exprValue.getArray(e);
        SectMongoDocument.mongoSKDocument.getBsonDocument().put(key, value.length == 1
            ? MongoSKAdapter.serializeObject(value[0])
            : MongoSKAdapter.serializeArray(value)
        );
    }

    @Nonnull
    @Override
    public String toString(Event e, boolean debug) {
        return "mongo " + exprKey.toString(e, debug) + ": " + exprValue.toString(e, debug);
    }
}

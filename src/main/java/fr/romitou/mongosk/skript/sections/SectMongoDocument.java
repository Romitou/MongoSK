package fr.romitou.mongosk.skript.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Variable;
import ch.njol.util.Kleenean;
import fr.romitou.mongosk.Logger;
import fr.romitou.mongosk.MongoSection;
import fr.romitou.mongosk.elements.MongoSKDocument;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;

@Name("Mongo section document")
@Description("This section can be used to create documents quickly and easily. " +
    "To know how to use this section completely, see the \"Mongo section document value\" effect. " +
    "Please note: you must specify a variable that will be affected by this section, as in the example.")
@Examples({"set {_doc} to a new mongo document with:",
    "\tmongo \"key\": \"value\"",
    "send {_doc}"})
@Since("2.1.0")
public class SectMongoDocument extends MongoSection<MongoSKDocument> {

    public static MongoSKDocument mongoSKDocument = new MongoSKDocument();

    static {
        Skript.registerCondition(SectMongoDocument.class, "set %object% to [a] new mongo[(db|sk)] document with");
    }

    @Override
    public boolean init(@Nonnull Expression<?>[] exprs, int matchedPattern, @Nonnull Kleenean isDelayed, @Nonnull SkriptParser.ParseResult parseResult) {

        if (isConditional()) {
            Logger.severe("You cannot use conditions with this section.",
                "Use the section like this: set {_variable} to a new mongo document with:");
            return false;
        }

        Expression<?> variable = exprs[0];
        if (!(variable instanceof Variable<?>)) {
            Logger.severe("In order to use this section, you must provide a variable as the first argument.",
                "Use the section like this: set {_variable} to a new mongo document with:");
            return false;
        }

        loadSection();
        setVariable((Variable<?>) variable);
        return true;
    }

    @Override
    protected MongoSKDocument provideValue() {
        return mongoSKDocument;
    }

    @Override
    protected void endSection() {
        mongoSKDocument = new MongoSKDocument();
    }


    @Nonnull
    @Override
    public String toString(Event e, boolean debug) {
        return "set " + getVariable().toString(e, debug) + " to a new mongo document with";
    }
}

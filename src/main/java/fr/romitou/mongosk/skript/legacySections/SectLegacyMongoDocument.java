package fr.romitou.mongosk.skript.legacySections;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Variable;
import ch.njol.util.Kleenean;
import fr.romitou.mongosk.LoggerHelper;
import fr.romitou.mongosk.MongoSK;
import fr.romitou.mongosk.MongoSection;
import fr.romitou.mongosk.elements.MongoSKDocument;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;

public class SectLegacyMongoDocument extends MongoSection<MongoSKDocument> {

    public static MongoSKDocument mongoSKDocument = new MongoSKDocument();

    static {
        if (!MongoSK.isUsingNewSections())
            Skript.registerCondition(
                SectLegacyMongoDocument.class,
                "set %object% to [a] new mongo[(db|sk)] document with"
            );
    }

    @Override
    public boolean init(@Nonnull Expression<?>[] exprs, int matchedPattern, @Nonnull Kleenean isDelayed, @Nonnull SkriptParser.ParseResult parseResult) {

        if (isConditional()) {
            LoggerHelper.severe("You cannot use conditions with this section.",
                "Use the section like this: set {_variable} to a new mongo document with:");
            return false;
        }

        Expression<?> variable = exprs[0];
        if (!(variable instanceof Variable<?>)) {
            LoggerHelper.severe("In order to use this section, you must provide a variable as the first argument.",
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

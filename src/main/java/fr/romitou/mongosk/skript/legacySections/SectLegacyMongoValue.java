package fr.romitou.mongosk.skript.legacySections;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import fr.romitou.mongosk.LegacyEffectSection;
import fr.romitou.mongosk.MongoSK;
import fr.romitou.mongosk.adapters.MongoSKAdapter;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class SectLegacyMongoValue extends Effect {

    static {
        if (!MongoSK.isUsingNewSections()) {
            Skript.registerEffect(
                SectLegacyMongoValue.class,
                "mongo[(sk|db)] [(1¦(field|value)|2¦(array|list))] %string%: %objects%"
            );
        }
    }

    private Expression<String> exprKey;
    private Expression<?> exprValue;
    private Kleenean isSingle;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(@Nonnull Expression<?>[] exprs, int matchedPattern, @Nonnull Kleenean isDelayed, @Nonnull SkriptParser.ParseResult parseResult) {
        if (!LegacyEffectSection.isCurrentSection(SectLegacyMongoDocument.class))
            return false;
        isSingle = parseResult.mark == 1
            ? Kleenean.TRUE
            : parseResult.mark == 2
            ? Kleenean.FALSE
            : Kleenean.UNKNOWN;
        exprKey = (Expression<String>) exprs[0];
        exprValue = exprs[1].getConvertedExpression(Object.class);
        return exprValue != null;
    }

    @Override
    protected void execute(@Nonnull Event e) {
        String key = exprKey.getSingle(e);
        List<?> omega = Arrays.asList(MongoSKAdapter.serializeArray(exprValue.getArray(e)));
        switch (isSingle) {
            case UNKNOWN:
                SectLegacyMongoDocument.mongoSKDocument.getBsonDocument().put(key, omega.size() == 1 ? omega.get(0) : omega);
                break;
            case TRUE:
            case FALSE:
                SectLegacyMongoDocument.mongoSKDocument.getBsonDocument().put(key, isSingle == Kleenean.TRUE ? omega.get(0) : omega);
                break;
        }
    }

    @Nonnull
    @Override
    public String toString(Event e, boolean debug) {
        return "mongo " + exprKey.toString(e, debug) + ": " + exprValue.toString(e, debug);
    }
}

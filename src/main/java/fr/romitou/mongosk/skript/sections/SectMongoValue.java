package fr.romitou.mongosk.skript.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.EffectSection;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import fr.romitou.mongosk.MongoSK;
import fr.romitou.mongosk.adapters.MongoSKAdapter;
import org.bukkit.event.Event;

import java.util.Arrays;
import java.util.List;
@Name("Mongo section document value")
@Description("This effect can only be used in a Mongo section document, and allows quick assignment of fields to a given document. " +
    "Be aware that this syntax is only available for basic data manipulation. If your manipulation is not possible via this section, you can perfectly well use the classic field definition expression in addition to this section.")
@Examples({"set {_nested} to a new mongo document with:",
    "\tmongo \"number\": 100",
    "\tmongo \"boolean\": false",
    "set {_doc} to a new mongo document with:",
    "\tmongo \"simpleField\": \"Hello!\"",
    "\tmongo \"nestedObject\": {_nested}",
    "broadcast {_doc}'s mongo json"})
@Since("2.1.0") // New class from 2.3.0, but section backported for older versions :)
public class SectMongoValue extends EffectSection {

    static {
        if (MongoSK.isUsingNewSections())
            Skript.registerSection(
                SectMongoValue.class,
                "mongo[(sk|db)] [(1¦(field|value)|2¦(array|list))] %string%\\: %objects%"
            );
    }

    private Expression<String> exprKey;
    private Expression<?> exprValue;
    private Kleenean isSingle;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        if (!getParser().isCurrentSection(SectMongoDocument.class))
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
    protected TriggerItem walk(Event e) {
        String key = exprKey.getSingle(e);
        List<?> omega = Arrays.asList(MongoSKAdapter.serializeArray(exprValue.getArray(e)));
        switch (isSingle) {
            case UNKNOWN:
                SectMongoDocument.mongoSKDocument.getBsonDocument().put(key, omega.size() == 1 ? omega.get(0) : omega);
                break;
            case TRUE:
            case FALSE:
                SectMongoDocument.mongoSKDocument.getBsonDocument().put(key, isSingle == Kleenean.TRUE ? omega.get(0) : omega);
                break;
        }
        return walk(e, true);
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "mongo " + exprKey.toString(e, debug) + ": " + exprValue.toString(e, debug);
    }
}

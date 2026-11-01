package fr.romitou.mongosk.skript.sections;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.*;
import ch.njol.util.Kleenean;
import fr.romitou.mongosk.adapters.MongoSKAdapter;
import org.bukkit.event.Event;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.Arrays;
import java.util.List;
@Name("Mongo section document value")
@Description({
    "This effect can only be used within a Mongo section document, and allows quick assignment of fields to the given document.",
    "Be aware that this syntax is intended for basic data manipulation.",
    "For complex scenarios, you can use the classic field definition expressions alongside this section."
})
@Examples({
    "set {_nested} to a new mongo document with:",
    "	mongo \"number\": 100",
    "	mongo \"boolean\": false",
    "set {_doc} to a new mongo document with:",
    "	mongo \"simpleField\": \"Hello!\"",
    "	mongo \"nestedObject\": {_nested}",
    "broadcast {_doc}'s mongo json"
})
@Since("2.1.0")
public class SectMongoValue extends Effect {

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EFFECT, SyntaxInfo.builder(SectMongoValue.class)
            .addPatterns("mongo[(sk|db)] [(1:(field|value)|2:(array|list))] %string%\\: %objects%")
            .build());
    }

    private Expression<String> exprKey;
    private Expression<?> exprValue;
    private Kleenean isSingle;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if (!getParser().isCurrentEvent(SectMongoDocument.EvtCreateMongoDocument.class))
            return false;
        isSingle = switch (parseResult.mark) {
            case 1 -> Kleenean.TRUE;
            case 2 -> Kleenean.FALSE;
            default -> Kleenean.UNKNOWN;
        };
        exprKey = (Expression<String>) exprs[0];
        exprValue = exprs[1].getConvertedExpression(Object.class);
        return exprValue != null;
    }

    @Override
    protected void execute(Event event) {
        SectMongoDocument.EvtCreateMongoDocument mongoDocumentEvent = (SectMongoDocument.EvtCreateMongoDocument) event;
        String key = exprKey.getSingle(event);
        List<?> values = Arrays.asList(MongoSKAdapter.serializeArray(exprValue.getArray(event)));
        Object value = switch (isSingle) {
            case TRUE -> values.get(0);
            case FALSE -> values;
            case UNKNOWN -> values.size() == 1 ? values.get(0) : values;
        };
        mongoDocumentEvent.getMongoDocument().getBsonDocument().put(key, value);
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "mongo " + exprKey.toString(e, debug) + ": " + exprValue.toString(e, debug);
    }

}

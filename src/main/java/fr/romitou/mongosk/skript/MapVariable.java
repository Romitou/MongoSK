package fr.romitou.mongosk.skript;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.*;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import fr.romitou.mongosk.Logger;
import fr.romitou.mongosk.objects.MongoSKDocument;
import org.bukkit.event.Event;

import java.util.Arrays;
import java.util.Map;

public class MapVariable extends SimpleExpression<MongoSKDocument> {

    static {
        Skript.registerExpression(MapVariable.class, MongoSKDocument.class, ExpressionType.SIMPLE, "mapped %objects%");
    }

    private Variable<?> variable;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        Expression<?> variable = exprs[0];

        if ((variable instanceof Variable<?>)) {
            this.variable = (Variable<?>) variable;
            return this.variable.isList();
        }

        System.out.println(variable.getClass());
        Logger.severe("The expression must be used with a list variable!");
        return false;
    }

    @Override
    protected MongoSKDocument[] get(Event e) {
        System.out.println("get");
        Object variableMap = Variables.getVariable(getVariableName(e), e, false);
        System.out.println("variableMap: " + variableMap);
        if (variableMap == null)
            return new MongoSKDocument[0];
        if (variableMap instanceof Map<?,?>) {
            Map<String, Object> map = (Map<String, Object>) variableMap;
            // Print map keys
            System.out.println(Arrays.toString(map.keySet().toArray()));
        }

        return new MongoSKDocument[0];
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends MongoSKDocument> getReturnType() {
        return MongoSKDocument.class;
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "avecfdfsq";
    }

    public String getVariableName(Event e) {
        String variableName = variable.toString(e, false);
        variableName = variableName.substring(1, variableName.length() - 1);
        return variableName;
    }
}

package fr.romitou.mongosk;

import ch.njol.skript.lang.Variable;
import ch.njol.skript.variables.Variables;
import org.bukkit.event.Event;

/**
 * Class to create sections using variables, like this:
 * <code>
 *     set {_variable} to a new mongo document:
 *         mongo value "test": 1245
 *         mongo list "lol": "foo" and "bar"
 *     broadcast {_variable}'s mongo json
 * </code>
 *
 * @param <T> The value associated with this section
 */
public abstract class MongoSection<T> extends LegacyEffectSection {

    private Variable<?> variable;

    @Override
    protected void execute(Event e) {
        runSection(e);
        Variables.setVariable(variable.getName().toString(e), provideValue(), e, variable.isLocal());
        endSection();
    }

    protected Variable<?> getVariable() {
        return variable;
    }

    protected void setVariable(Variable<?> variable) {
        this.variable = variable;
    }

    protected abstract T provideValue();

    protected abstract void endSection();

}

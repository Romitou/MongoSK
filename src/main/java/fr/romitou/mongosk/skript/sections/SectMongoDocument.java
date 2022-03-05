package fr.romitou.mongosk.skript.sections;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.*;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import fr.romitou.mongosk.LoggerHelper;
import fr.romitou.mongosk.MongoSK;
import fr.romitou.mongosk.elements.MongoSKDocument;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.List;

public class SectMongoDocument extends EffectSection {

    static class MongoTriggerItem extends TriggerItem {

        private final Variable<?> variable;

        public MongoTriggerItem(Variable<?> variable) {
            this.variable = variable;
        }

        @Override
        protected boolean run(Event e) {
            Variables.setVariable(variable.getName().getDefaultVariableName(), SectMongoDocument.mongoSKDocument, e, variable.isLocal());
            SectMongoDocument.mongoSKDocument = new MongoSKDocument();
            return true;
        }

        @Override
        public String toString(Event e, boolean debug) {
            return "[MongoSK internal: mongo document creation final item]";
        }
    }

    static {
        if (MongoSK.isUsingNewSections())
            Skript.registerSection(
                SectMongoDocument.class,
                "set %object% to [a] new mongo[(db|sk)] document with"
            );
    }

    public static MongoSKDocument mongoSKDocument = new MongoSKDocument();
    private Variable<?> localVariable;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        Expression<?> variable = exprs[0];
        if (!(variable instanceof Variable<?>)) {
            LoggerHelper.severe("In order to use this section, you must provide a variable as the first argument.",
                "Use the section like this: set {_variable} to a new mongo document with:");
            return false;
        }

        localVariable = (Variable<?>) variable;

        if (hasSection() && sectionNode != null) {
            List<TriggerSection> currentSections = getParser().getCurrentSections();
            currentSections.add(this);
            try {
                ArrayList<TriggerItem> items = ScriptLoader.loadItems(sectionNode);
                MongoTriggerItem mongoTriggerItem = new MongoTriggerItem(localVariable);
                TriggerItem last = items.get(items.size() - 1).setNext(mongoTriggerItem);
                items.set(items.size() - 1, last);
                items.add(mongoTriggerItem);
                setTriggerItems(items);
            } finally {
                currentSections.remove(currentSections.size() - 1);
            }
        }

        return true;
    }

    @Override
    protected TriggerItem walk(Event e) {
        return walk(e, true);
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "set " + localVariable.toString(e, debug) + " to a new mongo document with";
    }
}

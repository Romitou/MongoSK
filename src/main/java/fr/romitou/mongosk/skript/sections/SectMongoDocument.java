package fr.romitou.mongosk.skript.sections;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.*;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import fr.romitou.mongosk.LoggerHelper;
import fr.romitou.mongosk.MongoSK;
import fr.romitou.mongosk.elements.MongoSKDocument;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.List;

@Name("Mongo section document")
@Description("The Mongo section document allows the initialization of a new document and allows to define its fields in a quick and intuitive way with the expression \"Mongo section document value\". " +
    "The variable you specify in the first line represents the variable that will contain the document built in the section. " +
    "You can then use this variable after the end of your section.")
@Examples({"set {_doc} to a new mongo document with:",
    "\tmongo \"a\": 1, 2 and 3",
    "\tmongo list \"b\": 4, 5 and 6",
    "\tmongo list \"c\": 1",
    "\tmongo \"d\": 7",
    "\tmongo value \"e\": 8",
    "\tmongo value \"f\": 9 and 10"})
@Since("2.1.0") // New class from 2.3.0, but section backported for older versions :)
public class SectMongoDocument extends EffectSection {

    static class MongoTriggerItem extends TriggerItem {

        private final Variable<?> variable;

        public MongoTriggerItem(Variable<?> variable) {
            this.variable = variable;
        }

        @Override
        protected boolean run(Event e) {
            Variables.setVariable(variable.getName().toString(e), SectMongoDocument.mongoSKDocument, e, variable.isLocal());
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
                if (items.size() != sectionNode.size()) { // Some items didn't parse
                    return false;
                }
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

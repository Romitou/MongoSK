package fr.romitou.mongosk.skript.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SectionExpression;
import ch.njol.skript.lang.*;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import fr.romitou.mongosk.MongoSK;
import fr.romitou.mongosk.elements.MongoSKDocument;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
public class SectMongoDocument extends SectionExpression<MongoSKDocument> {
    static {
        if (MongoSK.isUsingNewSections())
            Skript.registerExpression(
                SectMongoDocument.class,
                MongoSKDocument.class,
                ExpressionType.SIMPLE,
                "[a] new mongo[(db|sk)] document with"
            );
    }

    protected static class EvtCreateMongoDocument extends Event {
        public MongoSKDocument mongoSKDocument = new MongoSKDocument();

        @Override
        public @NotNull HandlerList getHandlers() {
            return new HandlerList();
        }

        public MongoSKDocument getMongoDocument() {
            return mongoSKDocument;
        }
    }

    private Trigger trigger;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
        System.out.println("Initiated Mongo section document");
        if (sectionNode == null) {
            return false;
        }

        this.trigger = this.loadCode(
            sectionNode,
            "create mongo document",
            () -> {},
            EvtCreateMongoDocument.class
        );
        return true;
    }

    @Override
    protected MongoSKDocument @Nullable [] get(Event event) {
        EvtCreateMongoDocument contextEvent = new EvtCreateMongoDocument();
        Variables.withLocalVariables(event, contextEvent, () ->
            TriggerItem.walk(trigger, contextEvent)
        );
        return new MongoSKDocument[]{contextEvent.getMongoDocument()};
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
        return "new mongo document with";
    }
}

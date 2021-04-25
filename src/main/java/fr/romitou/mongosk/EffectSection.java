package fr.romitou.mongosk;

import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.TriggerSection;
import ch.njol.skript.log.*;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.*;

/**
 * The original idea and work for the sections comes from Tuke_Nuke/TuSKe, available here:
 * https://github.com/Tuke-Nuke/TuSKe/blob/master/src/main/java/com/github/tukenuke/tuske/util/EffectSection.java
 *
 * @author Tuke_Nuke on 29/03/2017
 * @author Romitou on 25/04/2021
 */
public abstract class EffectSection extends Condition {

    public static HashMap<Class<? extends EffectSection>, EffectSection> effectSections = new HashMap<>();

    private final Node node;
    private SectionNode sectionNode;
    private TriggerSection triggerSection;
    private Boolean shouldExecuteNext = true;

    /**
     * Initialization of the section.
     * To use sections, your class must extend EffectSection.
     */
    @SuppressWarnings("unchecked")
    public EffectSection() {
        // Get the current node.
        this.node = SkriptLogger.getNode();

        // Do some null-checks before we initialise our new section.
        if (!(this.node instanceof SectionNode))
            return;
        if (this.node.getKey() == null || this.node.getParent() == null)
            return;

        // Since the "comment" field is protected, we have to do reflection.
        String comment;
        try {
            Field commentField = Node.class.getDeclaredField("comment");
            commentField.setAccessible(true);
            comment = (String) commentField.get(this.node);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            comment = "";
        }

        // Construction of our new section.
        this.sectionNode = new SectionNode(this.node.getKey(), comment, this.node.getParent(), this.node.getLine());

        // Since the Node#move method causes a ConcurrentModificationException, we have to do reflection (again).
        try {
            Field nodeField = SectionNode.class.getDeclaredField("nodes");
            nodeField.setAccessible(true);

            // Get nodes of this.node and move them to this.sectionNode.
            ArrayList<Node> nodes = (ArrayList<Node>) nodeField.get(this.node);
            nodeField.set(this.sectionNode, nodes);

            // Remove all nodes from this.node, since they are moved.
            nodeField.set(this.node, new ArrayList<>());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the class is the current section.
     *
     * @param classes Classes to check
     * @return Whether the class is the current section
     */
    @Nonnull
    @SafeVarargs
    public static Boolean isCurrentSection(Class<? extends EffectSection>... classes) {
        return getCurrentSection(classes).isPresent();
    }

    /**
     * Retrieve a specific section by the class that extends it.
     *
     * @param classes Classes to check
     * @return An optional containing the current section
     */
    @Nonnull
    @SafeVarargs
    public static Optional<EffectSection> getCurrentSection(Class<? extends EffectSection>... classes) {
        return Arrays.stream(classes)
            .filter(EffectSection.effectSections::containsKey)
            .map(EffectSection.effectSections::get)
            .findFirst();
    }

    /**
     * Initializes the section's trigger, so that the section can then be executed.
     * It is mandatory to initialise it first before using {@link EffectSection#runSection(Event)}.
     */
    protected void loadSection() {
        // If section node is null, this means that a trigger has already been created. Aborting.
        if (this.sectionNode == null)
            return;

        // Start log handler.
        RetainingLogHandler logHandler = SkriptLogger.startRetainingLog();

        // Store the created section.
        effectSections.put(this.getClass(), this);

        // Create a trigger for the section we have created.
        this.triggerSection = new TriggerSection(this.sectionNode) {
            @Override
            protected TriggerItem walk(@Nonnull Event e) {
                // Call the TriggerSection#walk method of TriggerSection.
                return this.walk(e, true);
            }

            @Nonnull
            @Override
            public String toString(Event e, boolean debug) {
                // Call the Debuggable#toString method of EffectSection (and not of the TriggerSection).
                return EffectSection.this.toString(e, debug);
            }
        };

        // Stop long handler.
        stopRetainingLogHandler(logHandler);

        // Our trigger has been created, delete our section node.
        this.sectionNode = null;
    }

    /**
     * Method from TuSKe, to correctly display parsing errors encountered during the parsing of the section.
     * https://github.com/Tuke-Nuke/TuSKe/blob/master/src/main/java/com/github/tukenuke/tuske/util/EffectSection.java#L174-L201
     *
     * @param logger The logger to stop
     */
    private void stopRetainingLogHandler(RetainingLogHandler logger) {

        // Stop the current logger
        logger.stop();

        // Use reflection to get current handlers from SkriptLogger
        HandlerList handlerList = new HandlerList();
        try {
            Field field = SkriptLogger.class.getDeclaredField("handlers");
            field.setAccessible(true);
            handlerList = (HandlerList) field.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        // List of LogHandler that will be stopped
        List<LogHandler> toStop = new ArrayList<>();

        for (LogHandler logHandler : handlerList) {
            if (!(logHandler instanceof ParseLogHandler))
                break;
            toStop.add(logHandler);
        }

        // Stop the handlers
        toStop.forEach(LogHandler::stop);

        // Print logs from the original logger
        logger.printLog();
    }

    protected abstract void execute(Event e);

    /**
     * Adaptation of Condition#check, which asks to return whether the condition has been met.
     *
     * @param e The event to check
     * @return Whether the section should continue to run
     */
    @Override
    public boolean check(@Nonnull Event e) {
        execute(e);
        if (this.shouldExecuteNext && this.triggerSection != null)
            setNext(this.triggerSection.getNext());
        return !hasSection();
    }

    /**
     * Executes the section, with the event passed as a parameter.
     * It is mandatory to have initialized the section first, via {@link EffectSection#loadSection()}.
     *
     * @param e The event
     */
    protected void runSection(Event e) {
        this.shouldExecuteNext = false;
        TriggerItem.walk(this.triggerSection, e);
    }

    /**
     * Checks if the section has not been used as a condition.
     * To do this, we check whether the node contains the keywords "if", or "else if".
     *
     * @return whether the section is conditional
     */
    protected Boolean isConditional() {
        // Get the key of our node.
        String nodeKey = this.node.getKey();
        if (nodeKey == null)
            return true;

        // We need to make substrings in order to use String#equalsIgnoreCase.
        return nodeKey.substring(0, 2).equalsIgnoreCase("if")
            || nodeKey.substring(0, 4).equalsIgnoreCase("else");
    }

    /**
     * Checks if a section exists: if the section has been created, or if a trigger exists.
     *
     * @return Whether there is a section
     */
    protected Boolean hasSection() {
        return this.sectionNode != null || this.triggerSection != null;
    }

    /**
     * Checks that the {@link EffectSection#loadSection()} method has been called beforehand.
     *
     * @return Whether the section is initialized
     */
    protected Boolean isInitialized() {
        return this.sectionNode == null && this.triggerSection != null;
    }

    /**
     * Useful for recovering the original node.
     *
     * @return The original node
     */
    public Node getNode() {
        return node;
    }

    /**
     * Useful for recovering the section node.
     *
     * @return The section node
     */
    public SectionNode getSectionNode() {
        return sectionNode;
    }

    /**
     * Useful for recovering the trigger section.
     *
     * @return The trigger section
     */
    public TriggerSection getTriggerSection() {
        return triggerSection;
    }

    /**
     * Useful for recovering the trigger execution state.
     *
     * @return Whether the next trigger item should be executed
     */
    public Boolean shouldExecuteNext() {
        return shouldExecuteNext;
    }
}

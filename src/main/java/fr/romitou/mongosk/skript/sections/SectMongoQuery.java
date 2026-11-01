package fr.romitou.mongosk.skript.sections;

import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SectionExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import fr.romitou.mongosk.elements.MongoSKCollection;
import fr.romitou.mongosk.elements.MongoSKFilter;
import fr.romitou.mongosk.elements.MongoSKProjection;
import fr.romitou.mongosk.elements.MongoSKQuery;
import fr.romitou.mongosk.elements.MongoSKSort;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.entry.util.ExpressionEntryData;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.List;
import java.util.function.Consumer;

@Name("Mongo section query")
@Description({
    "Builds a Mongo query in a section. Nothing is sent to MongoDB until the query is executed, for example with the Execute Mongo query effect.",
    "The collection is required. The filter, sort, projection, limit, skip, comment and disk usage are optional."
})
@Examples({
    "set {_query} to a new mongo query with:",
    "\tcollection: {mycollection}",
    "\tfilter: a new mongosk filter where field \"coins\" is greater than 100",
    "\tsort: mongo descending sort by field \"coins\"",
    "\tlimit: 10",
    "execute mongo query {_query} and store the results in {_docs::*}"
})
@Since("3.0.0")
public class SectMongoQuery extends SectionExpression<MongoSKQuery> {

    private static final EntryValidator VALIDATOR = EntryValidator.builder()
        .addEntryData(new ExpressionEntryData<>("collection", null, false, MongoSKCollection.class))
        .addEntryData(new ExpressionEntryData<>("filter", null, true, MongoSKFilter.class))
        .addEntryData(new ExpressionEntryData<>("sort", null, true, MongoSKSort.class))
        .addEntryData(new ExpressionEntryData<>("projection", null, true, MongoSKProjection.class))
        .addEntryData(new ExpressionEntryData<>("limit", null, true, Number.class))
        .addEntryData(new ExpressionEntryData<>("skip", null, true, Number.class))
        .addEntryData(new ExpressionEntryData<>("comment", null, true, String.class))
        .addEntryData(new ExpressionEntryData<>("disk usage", null, true, Boolean.class))
        .build();

    public static void register(SyntaxRegistry registry) {
        registry.register(SyntaxRegistry.EXPRESSION, SyntaxInfo.Expression.builder(SectMongoQuery.class, MongoSKQuery.class)
            .addPatterns("[a] new mongo[(db|sk)] (query|request) with")
            .priority(SyntaxInfo.SIMPLE)
            .build());
    }

    private EntryContainer entries;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult,
                        @Nullable SectionNode sectionNode, @Nullable List<TriggerItem> triggerItems) {
        if (sectionNode == null)
            return false;
        entries = VALIDATOR.validate(sectionNode);
        return entries != null;
    }

    @Override
    protected MongoSKQuery[] get(Event event) {
        MongoSKQuery query = new MongoSKQuery();
        apply(event, "collection", MongoSKCollection.class, query::setMongoSKCollection);
        apply(event, "filter", MongoSKFilter.class, query::setMongoSKFilter);
        apply(event, "sort", MongoSKSort.class, query::setMongoSKSort);
        apply(event, "projection", MongoSKProjection.class, query::setMongoSKProjection);
        apply(event, "limit", Number.class, limit -> query.setLimit(limit.intValue()));
        apply(event, "skip", Number.class, skip -> query.setSkip(skip.intValue()));
        apply(event, "comment", String.class, query::setComment);
        apply(event, "disk usage", Boolean.class, query::setDiskUsage);
        return new MongoSKQuery[]{query};
    }

    @SuppressWarnings("unchecked")
    private <T> void apply(Event event, String key, Class<T> type, Consumer<T> setter) {
        Expression<? extends T> expression = entries.getOptional(key, Expression.class, false);
        if (expression == null)
            return;
        T value = expression.getSingle(event);
        if (value != null)
            setter.accept(value);
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends MongoSKQuery> getReturnType() {
        return MongoSKQuery.class;
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return "new mongo query with";
    }
}

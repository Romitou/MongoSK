package fr.romitou.mongosk.skript;

import fr.romitou.mongosk.skript.conditions.CondMongoDocHasKey;
import fr.romitou.mongosk.skript.conditions.CondMongoDocHasValue;
import fr.romitou.mongosk.skript.conditions.CondMongoDocIsEmpty;
import fr.romitou.mongosk.skript.effects.EffCloseMongoServer;
import fr.romitou.mongosk.skript.effects.EffCreateMongoCollection;
import fr.romitou.mongosk.skript.effects.EffDropMongoCollection;
import fr.romitou.mongosk.skript.effects.EffDropMongoDatabase;
import fr.romitou.mongosk.skript.effects.EffManageMongoDocument;
import fr.romitou.mongosk.skript.effects.EffMongoCommand;
import fr.romitou.mongosk.skript.effects.EffMongoCount;
import fr.romitou.mongosk.skript.effects.EffMongoDistinct;
import fr.romitou.mongosk.skript.effects.EffMongoFind;
import fr.romitou.mongosk.skript.effects.EffMongoList;
import fr.romitou.mongosk.skript.effects.EffMongoModifyQuery;
import fr.romitou.mongosk.skript.events.MongoCommandFailed;
import fr.romitou.mongosk.skript.events.MongoCommandStarted;
import fr.romitou.mongosk.skript.events.MongoCommandSucceeded;
import fr.romitou.mongosk.skript.expressions.*;
import fr.romitou.mongosk.skript.sections.SectMongoDocument;
import fr.romitou.mongosk.skript.sections.SectMongoQuery;
import fr.romitou.mongosk.skript.sections.SectMongoValue;
import org.skriptlang.skript.addon.AddonModule;
import org.skriptlang.skript.addon.SkriptAddon;
import org.skriptlang.skript.bukkit.lang.eventvalue.EventValueRegistry;
import org.skriptlang.skript.registration.SyntaxRegistry;

/**
 * Registers the Skript types and syntaxes of MongoSK.
 */
public final class MongoSKModule implements AddonModule {

    @Override
    public void init(SkriptAddon addon) {
        // The types must be registered before the syntaxes which use them.
        SkriptTypes.register();
    }

    @Override
    public void load(SkriptAddon addon) {
        register(addon,
            // Conditions
            CondMongoDocHasKey::register,
            CondMongoDocHasValue::register,
            CondMongoDocIsEmpty::register,
            // Effects
            EffCloseMongoServer::register,
            EffCreateMongoCollection::register,
            EffDropMongoCollection::register,
            EffDropMongoDatabase::register,
            EffManageMongoDocument::register,
            EffMongoCommand::register,
            EffMongoCount::register,
            EffMongoDistinct::register,
            EffMongoFind::register,
            EffMongoList::register,
            EffMongoModifyQuery::register,
            // Expressions
            ExprLastMongoError::register,
            ExprMongoCollection::register,
            ExprMongoCollectionDatabase::register,
            ExprMongoDatabase::register,
            ExprMongoDocumentField::register,
            ExprMongoDocumentJson::register,
            ExprMongoEmbeddedValue::register,
            ExprMongoFilter::register,
            ExprMongoJoinedFilters::register,
            ExprMongoJoinedSorts::register,
            ExprMongoKeys::register,
            ExprMongoName::register,
            ExprMongoProjection::register,
            ExprMongoQueryCollection::register,
            ExprMongoQueryComment::register,
            ExprMongoQueryDiskUsage::register,
            ExprMongoQueryFilter::register,
            ExprMongoQueryLimit::register,
            ExprMongoQueryProjection::register,
            ExprMongoQuerySkip::register,
            ExprMongoQuerySort::register,
            ExprMongoServer::register,
            ExprMongoSort::register,
            ExprMongoValues::register,
            ExprNewMongoDocument::register,
            ExprNewMongoQuery::register,
            // Sections
            SectMongoDocument::register,
            SectMongoQuery::register,
            SectMongoValue::register
        );

        SyntaxRegistry registry = moduleRegistry(addon);
        EventValueRegistry eventValues = addon.registry(EventValueRegistry.class);
        MongoCommandFailed.register(registry, eventValues);
        MongoCommandStarted.register(registry, eventValues);
        MongoCommandSucceeded.register(registry, eventValues);
    }

    @Override
    public String name() {
        return "mongosk";
    }

}

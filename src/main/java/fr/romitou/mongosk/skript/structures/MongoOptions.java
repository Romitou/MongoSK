package fr.romitou.mongosk.skript.structures;

import ch.njol.skript.Skript;
import ch.njol.skript.config.EntryNode;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Variable;
import ch.njol.skript.variables.Variables;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import org.bukkit.event.Event;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.structure.Structure;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MongoOptions extends Structure {
    private static final Pattern DATABASE_PATTERN =
            Pattern.compile("database \"(.+)\"");
    private static final Pattern COLLECTION_PATTERN =
            Pattern.compile("collection \"(.+)\"");

    static {
        Skript.registerStructure(MongoOptions.class,
                "mongo options"
        );
    }

    private MongoClient mongoClient = null;

    @Override
    public boolean init(Literal<?>[] args, int matchedPattern, SkriptParser.ParseResult parseResult, EntryContainer entryContainer) {
        SectionNode node = entryContainer.getSource();
        node.convertToEntries(-1);
        return loadOptions(node);
    }

    private Boolean loadOptions(SectionNode sectionNode) {
        int i = 0;
        for (Node node : sectionNode) {
            if (i == 0) {
                if (!(node instanceof EntryNode) || !node.getKey().equals("connection string")) {
                    Skript.error("The first line of the options must be the connection string");
                    return false;
                }
                String connectionString = ((EntryNode) node).getValue();
                if (!connectionString.startsWith("\"") || !connectionString.endsWith("\"")) {
                    Skript.error("Invalid connection string. Must be in the form of '\"<connection string>\"'");
                    return false;
                }
                connectionString = connectionString.substring(1, connectionString.length() - 1);

                try {
                    ConnectionString connString = new ConnectionString(connectionString);
                    mongoClient = MongoClients.create(
                            MongoClientSettings.builder()
                                    .applyConnectionString(connString)
                                    .build()
                    );
                } catch (IllegalArgumentException e) {
                    Skript.error("Invalid connection string: " + e.getMessage());
                    return false;
                }
            } else {
                if (node instanceof SectionNode && node.getKey() != null) {
                    Matcher matcher = DATABASE_PATTERN.matcher(node.getKey());
                    if (matcher.matches()) {
                        if (!(loadCollections((SectionNode) node, matcher.group(1))))
                            return false;
                    } else {
                        Skript.error("Invalid database definition. Must be in the form of 'database \"<name>\":'");
                        return false;
                    }
                } else {
                    Skript.error("Invalid line in options");
                    return false;
                }
            }
            i++;
        }
        return true;
    }

    private Boolean loadCollections(SectionNode sectionNode, String databaseName) {
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        for (Node node : sectionNode) {
            if (node instanceof EntryNode) {
                String collectionName = node.getKey();
                if (collectionName == null) {
                    Skript.error("Invalid collection definition. Must be in the form of '<collection name>: <variable>'");
                    return false;
                }
                Matcher matcher = COLLECTION_PATTERN.matcher(collectionName);
                if (matcher.matches()) {
                    collectionName = matcher.group(1);
                } else {
                    Skript.error("Invalid collection definition. Must be in the form of '<collection name>: <variable>'");
                    return false;
                }
                String rawVariable = ((EntryNode) node).getValue();

                String name = rawVariable.toLowerCase();
                if (name.startsWith("{") && name.endsWith("}")) {
                    name = name.substring(1, name.length() - 1);
                } else {
                    Skript.error("Invalid variable definition. Must be in the form of '{<variable name>}'");
                }

                if (name.startsWith(Variable.LOCAL_VARIABLE_TOKEN)) {
                    Skript.error("'" + name + "' cannot be a local variable");
                    return false;
                }

                if (name.contains("<") || name.contains(">")) {
                    Skript.error("'" + name + "' cannot have symbol '<' or '>' within the definition");
                    return false;
                }

                if (name.contains("%")) {
                    Skript.error("Invalid use of percent signs in variable name");
                    return false;
                }

                if (name.contains("::*")) {
                    Skript.error("Variable cannot be a list");
                    return false;
                }

                MongoCollection<?> collection = database.getCollection(collectionName);
                // TODO: handle collection creation
                // TODO: handle duplicate name
                Variables.setVariable(name, collection, null, false);
            } else {
                Skript.error("Invalid collection definition. Must be in the form of '<collection name>: <variable>'");
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean load() {
        return true;
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "mongo options";
    }
}

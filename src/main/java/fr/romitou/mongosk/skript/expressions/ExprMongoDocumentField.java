package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import fr.romitou.mongosk.Logger;
import fr.romitou.mongosk.MongoSK;
import fr.romitou.mongosk.adapters.MongoSKAdapter;
import fr.romitou.mongosk.adapters.MongoSKCodec;
import fr.romitou.mongosk.elements.MongoSKDocument;
import org.bson.Document;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.StreamCorruptedException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ExprMongoDocumentField extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(
            ExprMongoDocumentField.class,
            Object.class,
            ExpressionType.COMBINED,
            "mongo[(sk|db)] (1¦(field|value)|2¦(array|list)) [named] %string% of %mongoskdocument%"
        );
    }

    private final static String documentField = MongoSK.getConfiguration().getString("document-field", "__MongoSK__");

    private Expression<String> exprFieldName;
    private Expression<MongoSKDocument> exprMongoSKDocument;
    private Boolean isSingle;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, @Nonnull Kleenean isDelayed, @Nonnull SkriptParser.ParseResult parseResult) {
        exprFieldName = (Expression<String>) exprs[0];
        exprMongoSKDocument = (Expression<MongoSKDocument>) exprs[1];
        isSingle = parseResult.mark == 1;
        return true;
    }
    @Override
    protected Object[] get(@Nonnull final Event e) {
        String fieldName = exprFieldName.getSingle(e);
        MongoSKDocument mongoSKDocument = exprMongoSKDocument.getSingle(e);
        if (fieldName == null || mongoSKDocument == null || mongoSKDocument.getBsonDocument() == null)
            return new Object[0];
        if (isSingle) {
            Object value = mongoSKDocument.getBsonDocument().get(fieldName);
            if (value instanceof Document) {
                Document doc = (Document) value;
                if (doc.containsKey(documentField)) {
                    String codecName = doc.getString(documentField);
                    MongoSKCodec<Object> codec = MongoSKAdapter.getCodecByName(codecName);
                    if (codec == null) {
                        Logger.severe("No codec found for " + codecName + "!",
                            "Loaded codecs: " + String.join(", ", MongoSKAdapter.getCodecNames()),
                            "Requested codec: " + codecName
                        );
                        return new Object[0];
                    }
                    try {
                        return new Object[]{codec.deserialize(doc)};
                    } catch (StreamCorruptedException ex) {
                        Logger.severe("An error occurred during the deserialization of the " + doc.toJson() + " document!",
                            "Requested codec: " + codecName,
                            "Original value class: " + doc.toString(),
                            "Document JSON: " + doc.toJson()
                        );
                        return new Object[0];
                    }
                }
            }
            return new Object[]{value};
        }
        return mongoSKDocument.getBsonDocument().getList(fieldName, Object.class).toArray();
    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        switch (mode) {
            case ADD:
                if (!isSingle)
                    return CollectionUtils.array(Object[].class);
            case SET:
            case REMOVE:
            case DELETE:
                return CollectionUtils.array(isSingle ? Object.class : Object[].class);
            default:
                return new Class[0];
        }
    }

    @Override
    public void change(@Nonnull final Event e, Object[] delta, @Nonnull Changer.ChangeMode mode) {
        String fieldName = exprFieldName.getSingle(e);
        MongoSKDocument mongoSKDocument = exprMongoSKDocument.getSingle(e);
        List<Object> omega = Arrays.asList(delta);
        if (MongoSK.getConfiguration().getBoolean("skript-adapters", false)) {
            omega = omega.stream()
                .map(o -> {
                    Logger.debug("Searching codec for " + o.getClass() + "...");
                    MongoSKCodec<Object> codec = MongoSKAdapter.getCodecByClass(o.getClass());
                    if (codec == null) return o;
                    Logger.debug("Codec found: " + codec.getName());
                    Document serializedDocument = codec.serialize(o);
                    serializedDocument.put(documentField, codec.getName());
                    return serializedDocument;
                })
                .collect(Collectors.toList());
        }
        if (fieldName == null || mongoSKDocument == null || mongoSKDocument.getBsonDocument() == null || omega.size() == 0)
            return;
        switch (mode) {
            case ADD:
                try {
                    List<Object> addList = mongoSKDocument.getBsonDocument().getList(fieldName, Object.class);
                    addList.addAll(omega);
                } catch (RuntimeException ex) {
                    Logger.severe("An error occurred during adding objects to Mongo document: " + ex.getMessage(),
                        "Field name: " + fieldName,
                        "Document: " + mongoSKDocument.getBsonDocument().toJson(),
                        "Omega: " + omega
                    );
                }
                break;
            case SET:
                try {
                    mongoSKDocument.getBsonDocument().put(fieldName, isSingle ? omega.get(0) : omega);
                } catch (RuntimeException ex) {
                    Logger.severe("An error occurred during setting Mongo document field: " + ex.getMessage(),
                        "Field name: " + fieldName,
                        "Document: " + mongoSKDocument.getBsonDocument().toJson(),
                        "Omega: " + omega
                    );
                }
                break;
            case REMOVE:
                try {
                    List<Object> removeList = mongoSKDocument.getBsonDocument().getList(fieldName, Object.class);
                    removeList.removeAll(omega);
                } catch (RuntimeException ex) {
                    Logger.severe("An error occurred during removing objects from Mongo document: " + ex.getMessage(),
                        "Field name: " + fieldName,
                        "Document: " + mongoSKDocument.getBsonDocument().toJson(),
                        "Omega: " + omega
                    );
                }
                break;
            case DELETE:
                try {

                    mongoSKDocument.getBsonDocument().remove(fieldName);
                } catch (RuntimeException ex) {
                    Logger.severe("An error occurred during deleting field of Mongo document: " + ex.getMessage(),
                        "Field name: " + fieldName,
                        "Document: " + mongoSKDocument.getBsonDocument().toJson(),
                        "Omega: " + omega
                    );
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean isSingle() {
        return isSingle;
    }

    @Override
    @Nonnull
    public Class<?> getReturnType() {
        return Object.class;
    }

    @Override
    @Nonnull
    public String toString(@Nullable Event e, boolean debug) {
        return "mongo " + (isSingle ? "value" : "list") + " named " + exprFieldName.toString(e, debug) + " of " + exprMongoSKDocument.toString(e, debug);
    }
}

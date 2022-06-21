package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import fr.romitou.mongosk.LoggerHelper;
import fr.romitou.mongosk.adapters.MongoSKAdapter;
import fr.romitou.mongosk.elements.MongoSKDocument;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Name("Mongo Embedded Value")
@Description("It is sometimes redundant to extract a document from a document from a document etc... and undertake several data manipulations on several lines. " +
    "To remedy this, this expression will only ask you the path (MongoSK specific syntax, be careful) as follows: \"foo.bar\", or \"foo[0].bar\" or \"foo.bar[2]\" for example. " +
    "Embedded documents must be separated by a dot, and you must specify in brackets the index of the array (starting from 0) if it is a list.")
@Examples({"{",
    "   \"doc\": {",
    "      \".foo\": \"bar\"",
    "   },",
    "   \"list\": [",
    "      {",
    "         \"numbers\": [",
    "            \"1\",",
    "            \"2\"",
    "         ],",
    "         \"hello\": \"world\"",
    "      }",
    "   ]",
    "}" +
    "" +
    "set {_docfoo} to mongo embedded value with path \"doc.\\.foo\" of {doc}",
    "broadcast \"%{_docfoo}%\" # Output: bar",
    "",
    "set {_listnumbers} to mongo embedded value with path \"list[0].numbers[1]\" of {doc}",
    "broadcast \"%{_listnumbers}%\" # Output: 2",
    "",
    "set {_listhello} to mongo embedded value with path \"list[0].hello\" of {doc}",
    "broadcast \"%{_listhello}%\"  # Output: world"
})
@Since("2.3.0")
public class ExprMongoEmbeddedValue extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(
            ExprMongoEmbeddedValue.class,
            Object.class,
            ExpressionType.COMBINED,
            "mongo[(sk|db)] embedded (1¦(field|value)|2¦(array|list)) [with path] %string% of %mongoskdocument%"
        );
    }

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
        MongoQueryElement[] mongoQueryElements = buildQueryElementsFromString(fieldName);
        try {
            Object value = mongoSKDocument.getEmbeddedValue(mongoQueryElements);
            if (isSingle) {
                return new Object[]{MongoSKAdapter.deserializeValue(value)};
            } else {
                if (value instanceof List) {
                    return MongoSKAdapter.deserializeValues(((List<Object>) value).toArray());
                } else if (value instanceof Object[]) {
                    return MongoSKAdapter.deserializeValues((Object[]) value);
                } else {
                    return new Object[0];
                }
            }
        } catch (ClassCastException ex) {
            LoggerHelper.severe("The type of item you are querying is not correct. " +
                    "This can happen if you want to retrieve a list, but it is a single value.",
                "Document: " + mongoSKDocument.getBsonDocument().toJson(),
                "Exception: " + ex.getMessage());
            return new Object[0];
        }
    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        return new Class[0];
    }

//    @Override
//    public void change(@Nonnull final Event e, Object[] delta, @Nonnull Changer.ChangeMode mode) {
//        String fieldName = exprFieldName.getSingle(e);
//        MongoSKDocument mongoSKDocument = exprMongoSKDocument.getSingle(e);
//        List<?> omega = new ArrayList<>();
//        if (delta != null)
//            omega = Arrays.asList(MongoSKAdapter.serializeArray(delta));
//        if (fieldName == null || mongoSKDocument == null || mongoSKDocument.getBsonDocument() == null)
//            return;
//        switch (mode) {
//            case ADD:
//                try {
//                    ArrayList<Object> addList = new ArrayList<>(mongoSKDocument.getBsonDocument().getList(fieldName, Object.class));
//                    addList.addAll(omega);
//                    mongoSKDocument.getBsonDocument().put(fieldName, addList);
//                } catch (NullPointerException ex) {
//                    mongoSKDocument.getBsonDocument().put(fieldName, omega);
//                } catch (RuntimeException ex) {
//                    reportException("adding objects", fieldName, mongoSKDocument, omega, ex);
//                }
//                break;
//            case SET:
//                try {
//                    mongoSKDocument.getBsonDocument().put(fieldName, isSingle ? omega.get(0) : omega);
//                } catch (RuntimeException ex) {
//                    reportException("setting field", fieldName, mongoSKDocument, omega, ex);
//                }
//                break;
//            case REMOVE:
//                try {
//                    ArrayList<Object> removeList = new ArrayList<>(mongoSKDocument.getBsonDocument().getList(fieldName, Object.class));
//                    removeList.removeAll(omega);
//                    mongoSKDocument.getBsonDocument().put(fieldName, removeList);
//                } catch (RuntimeException ex) {
//                    reportException("removing objects", fieldName, mongoSKDocument, omega, ex);
//                }
//                break;
//            case DELETE:
//                try {
//                    mongoSKDocument.getBsonDocument().remove(fieldName);
//                } catch (RuntimeException ex) {
//                    reportException("deleting field", fieldName, mongoSKDocument, omega, ex);
//                }
//                break;
//            default:
//                break;
//        }
//    }

//    private void reportException(String action, String fieldName, MongoSKDocument document, List<?> omega, Exception ex) {
//        LoggerHelper.severe("An error occurred during " + action + " of Mongo document: " + ex.getMessage(),
//            "Field name: " + fieldName,
//            "Document: " + document.getBsonDocument().toJson(),
//            "Omega: " + omega
//        );
//    }

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

    public class MongoQueryElement {
        public String path;
        public Integer index;

        MongoQueryElement(String path, Integer index) {
            this.path = path;
            this.index = index;
        }

        MongoQueryElement(String path) {
            this(path, null);
        }

        MongoQueryElement(Integer index) {
            this(null, index);
        }

        @Override
        public String toString() {
            return "MongoQueryElement{" +
                "path='" + path + '\'' +
                ", index=" + index +
                '}';
        }
    }

    public MongoQueryElement[] buildQueryElementsFromString(String string) {
        ArrayList<MongoQueryElement> mongoQueryElements = new ArrayList<>();
        Integer arrayStartIndex = null;
        Integer textStartIndex = null;
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            boolean lastIteration = i == string.length() - 1;
            boolean isArrayStart = c == '[';
            if (((c == '.' || isArrayStart) && (i >= 1 && string.charAt(i - 1) != '\\')) || (lastIteration && arrayStartIndex == null)) {
                if (isArrayStart)
                    arrayStartIndex = i;
                Integer textIndex = textStartIndex;
                textStartIndex = null;
                if (textIndex == null) {
                    LoggerHelper.severe("The path is not valid. Expected a field name, but found a single dot.",
                        "Path: " + string,
                        "Index: " + i);
                    continue;
                }
                String queryString = string.substring(textIndex, lastIteration ? i + 1 : i);
                mongoQueryElements.add(new MongoQueryElement(queryString.replace("\\.", ".")));
            } else if (arrayStartIndex == null && textStartIndex == null) {
                textStartIndex = i;
            } else if (c == ']') {
                Integer startIndex = arrayStartIndex;
                arrayStartIndex = null;
                if (startIndex == null) {
                    LoggerHelper.severe("The path is not valid. Expected an array index, but found a closing bracket.",
                        "Path: " + string,
                        "Index: " + i);
                    continue;
                }
                Integer index = null;
                try {
                    index = Integer.parseInt(string.substring(startIndex + 1, i));
                } catch (NumberFormatException ex) {
                    LoggerHelper.severe("The path is not valid. Expected an integer, but found a non-integer value within the array brackets.",
                        "String: " + string,
                        "Index: " + i);
                }
                mongoQueryElements.add(new MongoQueryElement(index));
                if (string.length() > i + 1 && string.charAt(i + 1) == '.')
                    i++;
            }
        }
        return mongoQueryElements.toArray(new MongoQueryElement[0]);
    }

}

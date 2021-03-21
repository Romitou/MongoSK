package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.util.coll.CollectionUtils;
import fr.romitou.mongosk.Logger;
import fr.romitou.mongosk.elements.MongoSKDocument;
import org.bson.BSONException;
import org.bson.Document;
import org.bson.json.JsonParseException;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Name("Mongo document JSON")
@Description("Retrieve the JSON representation of the document. This expression can be useful to use it with " +
    "skript-json and convert the document to a Skript variable, and vice versa. " +
    "You can also define the JSON of the document.")
@Examples({"set {_json} do {_document}'s mongo json",
    "set mongo json of {_document} to \"{\"\"example\"\": true\"}"})
@Since("2.0.0")
public class ExprMongoDocumentJson extends SimplePropertyExpression<MongoSKDocument, String> {

    static {
        register(
            ExprMongoDocumentJson.class,
            String.class,
            "mongo[(db|sk)] json",
            "mongoskdocuments"
        );
    }

    @Override
    @Nonnull
    public String convert(MongoSKDocument document) {
        return document.getBsonDocument().toJson();
    }

    @Override
    public Class<?>[] acceptChange(@Nonnull Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET)
            return CollectionUtils.array(String.class);
        return null;
    }

    @Override
    public void change(@Nonnull final Event e, Object[] delta, @Nullable Changer.ChangeMode mode) {
        if (delta == null)
            return;
        MongoSKDocument mongoSKDocument = getExpr().getSingle(e);
        String jsonString = (String) delta[0];
        if (mongoSKDocument == null || jsonString == null)
            return;
        try {
            mongoSKDocument.setBsonDocument(Document.parse(jsonString));
        } catch (BSONException | JsonParseException ex) {
            Logger.severe("An error occurred when changing the document's JSON: " + ex.getMessage(),
                "Original document: " + mongoSKDocument.getBsonDocument().toJson(),
                "Provided JSON: " + jsonString,
                "Please check its validity on https://jsonlint.com."
            );
        }

    }

    @Override
    @Nonnull
    protected String getPropertyName() {
        return "mongosk json";
    }

    @Override
    @Nonnull
    public Class<? extends String> getReturnType() {
        return String.class;
    }
}

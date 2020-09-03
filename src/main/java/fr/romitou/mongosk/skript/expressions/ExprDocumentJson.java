package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.bson.Document;

@Name("Mongo Document Json")
@Description("This expression allows to get the JSON content of a Mongo document.")
@Examples({"set {_document} to first document where \"points\" is \"10\" in {_collection}" +
        "broadcast {_document}'s json"})
@Since("1.0.1")
public class ExprDocumentJson extends SimplePropertyExpression<Document, String> {

    static {
        register(ExprDocumentJson.class, String.class, "[mongo[db]] json", "mongodocument");
    }

    @Override
    public String convert(Document document) {
        return document.toJson();
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    protected String getPropertyName() {
        return "mongo json";
    }

}

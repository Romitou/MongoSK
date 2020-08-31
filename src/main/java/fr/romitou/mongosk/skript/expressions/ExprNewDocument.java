package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bson.Document;
import org.bukkit.event.Event;

@Name("New Mongo Document")
@Description("This expression allows you to create a new Mongo document. You will then be able to edit it and save it in the collection you want.")
@Examples({"set {_new} to new mongo document" +
        "set value \"player\" of {_new} to name of player" +
        "add random integer between 1 and 10 to list \"random\" of {_new}" +
        "save {_new} in collection named \"example\" from database named \"mongosk\" with client named \"default\""})
@Since("1.0.0")
public class ExprNewDocument extends SimpleExpression<Document> {

    static {
        Skript.registerExpression(ExprNewDocument.class, Document.class, ExpressionType.SIMPLE, "new [empty] [mongo[db]] document");
    }

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    protected Document[] get(Event e) {
        return new Document[]{new Document()};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Document> getReturnType() {
        return Document.class;
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "new mongo document";
    }

}

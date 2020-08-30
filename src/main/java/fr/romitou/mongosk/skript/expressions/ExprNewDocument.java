package fr.romitou.mongosk.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bson.Document;
import org.bukkit.event.Event;

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

package fr.romitou.mongosk.skript;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser;
import org.bukkit.event.Event;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;
import org.skriptlang.skript.lang.structure.Structure;

public class MongoServerStruct extends Structure {

    static {
        EntryValidator ev = EntryValidator.builder().addEntry("test", "undefined", false).build();

        Skript.registerStructure(MongoServerStruct.class, ev, "define [a] [new] mongo server");
    }

    @Override
    public boolean init(Literal<?>[] args, int matchedPattern, SkriptParser.ParseResult parseResult, EntryContainer entryContainer) {
        System.out.println("init");
        return true;
    }

    @Override
    public boolean load() {
        System.out.println("load");
        return true;
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "abcdef";
    }
}

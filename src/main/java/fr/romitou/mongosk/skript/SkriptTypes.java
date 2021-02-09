package fr.romitou.mongosk.skript;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import fr.romitou.mongosk.elements.MongoSKServer;

import javax.annotation.Nonnull;

public class SkriptTypes {
    static {
        Classes.registerClass(new ClassInfo<>(MongoSKServer.class, "mongoskserver")
            .user("mongo(db|sk)?( |-)?servers?")
            .name("MongoSK Server")
            .description("Represents a remote MongoDB server, with which you can interact. To create this type, use the Mongo Server expression.")
            .since("2.0.0")
            .parser(new Parser<MongoSKServer>() {
                @Override
                public boolean canParse(@Nonnull ParseContext context) {
                    return false;
                }

                @Override
                @Nonnull
                public String toString(@Nonnull MongoSKServer server, int flags) {
                    return server.getDisplayedName() + " server";
                }

                @Override
                @Nonnull
                public String toVariableNameString(@Nonnull MongoSKServer server) {
                    return "mongoskserver:" + server.getDisplayedName();
                }

                @Override
                @Nonnull
                public String getVariableNamePattern() {
                    return "mongoskserver:.+";
                }
            })
        );
    }
}

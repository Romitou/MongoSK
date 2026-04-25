package fr.romitou.mongosk.skript;

import java.util.Locale;

public enum MongoSKComparator {

    GREATER_THAN_OR_EQUAL("is ((greater|more|higher|bigger|larger|above) [than] or (equal to|the same as)|\\>=) %integer/number%"),
    GREATER_THAN("is (((greater|more|higher|bigger|larger) than|above)|\\>) %integer/number%"),
    LESS_THAN_OR_EQUAL("is ((less|smaller|below) [than] or (equal to|the same as)|\\<=) %integer/number%"),
    LESS_THAN("is (((less|smaller) than|below)|\\<) %integer/number%"),
    NOT_EQUAL("is ((not|neither)|isn't|!=) [equal to] %object%"),
    EQUALS("(is|equals|=) %object%"),
    NOT_EXIST("(does(n't| not) exist|is not (set|defined))"),
    EXISTS("(exists|is (set|defined))");

    private final String pattern;

    MongoSKComparator(String pattern) {
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }

    public String toString() {
        return this.name().replace("_", " ").toLowerCase(Locale.ROOT);
    }

}

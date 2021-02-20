package fr.romitou.mongosk.skript;

import java.util.Locale;

public enum MongoSKComparator {

    /*
     * Objects in patterns cannot be supported for the simple reason that this would add complexity to the code and
     * the management of UnparsedLiterals, which is a potential source of new bugs. However, if this brings any problems
     * or you want to change the way things are done here, feel free to visit GitHub for more informations.
     */
    GREATER_THAN_OR_EQUAL("is ((greater|more|higher|bigger|larger|above) [than] or (equal to|the same as)|\\>=) %integer/number%"),
    GREATER_THAN("is (((greater|more|higher|bigger|larger) than|above)|\\>) %integer/number%"),
    LESS_THAN_OR_EQUAL("is ((less|smaller|below) [than] or (equal to|the same as)|\\<=) %integer/number%"),
    LESS_THAN("is (((less|smaller) than|below)|\\<) %integer/number%"),
    NOT_EQUAL("is ((not|neither)|isn't|!=) [equal to] %integer/number/string/boolean%"),
    EQUALS("(is|equals|=) %integer/number/string/boolean%"),
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

package fr.romitou.mongosk.utils;

import fr.romitou.mongosk.LoggerHelper;
import fr.romitou.mongosk.elements.MongoQueryElement;

import java.util.ArrayList;

public class MongoPathParser {

    public static ArrayList<MongoQueryElement> parse(String path) {
        ArrayList<MongoQueryElement> elements = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inBracket = false;
        boolean escapeNext = false;

        for (int i = 0; i < path.length(); i++) {
            char c = path.charAt(i);

            if (escapeNext) {
                current.append(c);
                escapeNext = false;
            } else if (c == '\\') {
                escapeNext = true;
            } else if (c == '.' && !inBracket) {
                if (current.length() > 0) {
                    elements.add(new MongoQueryElement(current.toString()));
                    current.setLength(0);
                }
//                else {
//                    LoggerHelper.severe("Empty field name found between dots",
//                        "Path: " + path,
//                        "Index: " + i
//                    );
//                }
            } else if (c == '[') {
                if (current.length() > 0) {
                    elements.add(new MongoQueryElement(current.toString()));
                    current.setLength(0);
                }
                inBracket = true;
            } else if (c == ']') {
                if (!inBracket) {
                    LoggerHelper.warn("Unexpected closing bracket",
                        "Path: " + path,
                        "Index: " + i
                    );
                    continue;
                }
                inBracket = false;
                try {
                    int index = Integer.parseInt(current.toString());
                    elements.add(new MongoQueryElement(index));
                } catch (NumberFormatException e) {
                    LoggerHelper.warn("Expected integer inside brackets",
                        "Path: " + path,
                        "Content: " + current
                    );
                }
                current.setLength(0);
            } else {
                current.append(c);
            }
        }

        if (current.length() > 0 && !inBracket) {
            elements.add(new MongoQueryElement(current.toString()));
        } else if (inBracket) {
            LoggerHelper.warn("Unclosed bracket in path",
                "Path: " + path
            );
        }

        return elements;
    }
}

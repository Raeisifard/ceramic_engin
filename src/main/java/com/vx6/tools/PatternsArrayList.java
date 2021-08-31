package com.vx6.tools;

import io.vertx.core.json.JsonArray;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class PatternsArrayList extends ArrayList<Pattern> {
    protected ArrayList<Pattern> allowPhoneNoPatterns;
    protected ArrayList<Pattern> exceptPhoneNoPatterns;

    public PatternsArrayList(JsonArray allowPatterns, JsonArray exceptPatterns) {
        allowPhoneNoPatterns = new ArrayList<Pattern>() {
            @Override
            public boolean contains(Object o) {
                for (int i = 0; i < size(); i++) {
                    if (get(i).matcher((String) o).matches())
                        return true;
                }
                return false;
            }
        };
        exceptPhoneNoPatterns = new ArrayList<Pattern>() {
            @Override
            public boolean contains(Object o) {
                for (int i = 0; i < size(); i++) {
                    if (get(i).matcher((String) o).matches())
                        return true;
                }
                return false;
            }
        };
        allowPatterns.forEach(allow -> {
            String allowPattern = (String) allow;
            if ((allowPattern != null) && (!allowPattern.isEmpty()))
                allowPhoneNoPatterns.add(Pattern.compile(allowPattern));

        });
        exceptPatterns.forEach(except -> {
            String exceptPattern = (String) except;
            if ((exceptPattern != null) && (!exceptPattern.isEmpty()))
                exceptPhoneNoPatterns.add(Pattern.compile(exceptPattern));
        });
    }

    public boolean allow(String destPhoneNo) {
        return (allowPhoneNoPatterns.isEmpty() || allowPhoneNoPatterns.contains(destPhoneNo)) &&
                (exceptPhoneNoPatterns.isEmpty() || !exceptPhoneNoPatterns.contains(destPhoneNo));
    }
}

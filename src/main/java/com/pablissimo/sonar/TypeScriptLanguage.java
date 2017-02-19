package com.pablissimo.sonar;

import org.sonar.api.resources.AbstractLanguage;

public class TypeScriptLanguage extends AbstractLanguage {
    public static final String LANGUAGE_NAME = "TypeScript";
    public static final String LANGUAGE_KEY = "ts";
    protected static final String[] LANGUAGE_EXTENSIONS = { "ts", "tsx" };
    protected static final String LANGUAGE_DEFINITION_EXTENSION = "d.ts";

    public TypeScriptLanguage(){
        super(LANGUAGE_KEY, LANGUAGE_NAME);
    }

    @Override
    public String[] getFileSuffixes() {
        return LANGUAGE_EXTENSIONS;
    }
}

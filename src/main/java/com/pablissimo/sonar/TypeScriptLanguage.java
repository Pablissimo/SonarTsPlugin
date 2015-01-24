package com.pablissimo.sonar;

import org.sonar.api.resources.AbstractLanguage;

public class TypeScriptLanguage extends AbstractLanguage {
	public static final String LANGUAGE_NAME = "TypeScript";
	public static final String LANGUAGE_EXTENSION = "ts";
	public static final String LANGUAGE_DEFINITION_EXTENSION = "d.ts";
	
	public TypeScriptLanguage(){
		super(LANGUAGE_EXTENSION, LANGUAGE_NAME);
	}

	public String[] getFileSuffixes() {
		return new String[]{ LANGUAGE_EXTENSION };
	}
}

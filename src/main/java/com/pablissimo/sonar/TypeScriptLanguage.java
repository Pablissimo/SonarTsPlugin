package com.pablissimo.sonar;

import org.sonar.api.resources.AbstractLanguage;

public class TypeScriptLanguage extends AbstractLanguage {
	public TypeScriptLanguage(){
		super("ts", "TypeScript");
	}

	public String[] getFileSuffixes() {
		return new String[]{ "ts" };
	}
}

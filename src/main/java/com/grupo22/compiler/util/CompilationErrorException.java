package com.grupo22.compiler.util;

public class CompilationErrorException extends Exception {
	
	public CompilationErrorException() {
		super("Can't continue analyzing, there was a compilation error.");
	}

}

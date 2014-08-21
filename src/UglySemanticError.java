
public final class UglySemanticError {
	
	private static boolean hasErrors = false;
	
	private UglySemanticError() {
		return;
	}
	
	public static void complain(String message) {
		UglySemanticError.hasErrors = true;
		System.err.print("Semantic error: ");
		System.err.println(message);
	}

	public static void complain(int line, int column, String message) {
		UglySemanticError.hasErrors = true;
		System.err.print(String.format("(L: %d C: %d) Semantic error: ", line, column));
		System.err.println(message);
	}

	public static void complain(SimpleNode node, String message) {
		UglySemanticError.hasErrors = true;
		System.err.print(String.format("(L: %d C: %d) Semantic error: ", node.line, node.column));
		System.err.println(message);
	}
	
	public static boolean hadErrors() {
		return UglySemanticError.hasErrors;
	}
	
}

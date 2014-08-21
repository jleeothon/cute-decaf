import java.util.HashMap;

public class UglySymbolTable extends HashMap<String, UglySymbol> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private UglySymbolTable parent;

	public UglySymbolTable(String name) {
		super();
		this.name = name;
	}

	public UglySymbolTable(String name, UglySymbolTable parent) {
		super();
		this.name = name;
		this.parent = parent;
	}
	
	public String getName() {
		return this.name;
	}
	
	public UglySymbolTable getParent() {
		return this.parent;
	}
	
	@Override
	public String toString() {
		StringBuilder builder;
		if (this.name != null) {
			builder = new StringBuilder(this.name);
		} else {
			builder = new StringBuilder("nameless block");
		}
		builder.append(" {");
		boolean added = false;
		for (UglySymbol symbol : this.values()) {
			builder.append(String.format("%s, ", symbol.toString()));
			added = true;
		}
		if (added) {
			builder.delete(builder.length() - 2, builder.length());
		}
		builder.append("}");
		return builder.toString();
	}


}

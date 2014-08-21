import java.util.LinkedList;
import java.util.List;

public class UglySymbolType {

	private String name;
	private int size;

	public static final UglySymbolType INTEGER = new UglySymbolType("int", 4);
	public static final UglySymbolType BOOLEAN = new UglySymbolType("boolean", 4);
	public static final UglySymbolType CHARACTER = new UglySymbolType("char", 4);
	public static final UglySymbolType VOID = new UglySymbolType("void", 0);
	
	public static UglySymbolType getUglySymbolType(String name) {
		if (name.equals("int")) {
			return UglySymbolType.INTEGER;
		} else if (name.equals("boolean")) {
			return UglySymbolType.BOOLEAN;
		} else if (name.equals("void")) {
			return UglySymbolType.VOID;
		} else if (name.equals("char")) {
			return UglySymbolType.CHARACTER;
		} else {
			return null;
		}
	}
	
	private UglySymbolType(String name, int size) {
		this.name = name;
		this.size = size;
	}
	
	public String getName() {
		return this.name;
	}

	public int getSize() {
		return this.size;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	public static class Method extends UglySymbolType {
		List<UglySymbolType> params;
		UglySymbolType returnType;
		
		public Method(String name, UglySymbolType returnType) {
			super(name, returnType.getSize());
			this.returnType = returnType;
			this.params = new LinkedList<UglySymbolType>();
		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder("(");
			boolean hasArgs = false;
			for (UglySymbolType type : this.params) {
				hasArgs = true;
				builder.append(type.toString());
				builder.append(", ");
			}
			if (hasArgs) {
				builder.delete(builder.length() - 2, builder.length());
			}
			builder.append(") => ");
			builder.append(this.returnType.toString());
			return builder.toString();
		}
		
	}

}

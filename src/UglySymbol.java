
public class UglySymbol {
	private String name;
	private UglySymbolType type;
	private boolean isArray;
	private int size;
	private Object value;
	
	public UglySymbol(String name, UglySymbolType type, boolean isArray, int size) {
		this.name = name;
		this.type = type;
		this.size = size;
		this.isArray = isArray;
	}
	
	public UglySymbol(String name, UglySymbolType type) {
		this.name = name;
		this.type = type;
		this.size = 0;
		this.isArray = false;
	}
	
	public String getName() {
		return this.name;
	}

	public UglySymbolType getType() {
		return this.type;
	}

	public int getSize() {
		return this.size;
	}

	public boolean isArray() {
		return this.isArray;
	}
	
	public Object getValue() {
		return this.value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	
	@Override
	public boolean equals(Object o) {
		try {
			UglySymbol s = (UglySymbol)o;
			return this.name.equals(s.name);
		} catch (Exception e) {
			return false;
		}
	}

	public enum CuteType {
		INTEGER, BOOLEAN
	}
	
	@Override
	public String toString() {
		if (this.size == -1) {
			return String.format("<%s, %s>", this.name, this.type);
		} else {
			return String.format("<%s, %s, %d>", this.name, this.type, this.size);
		}
	}
	
}

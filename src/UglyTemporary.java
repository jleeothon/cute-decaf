import java.util.concurrent.atomic.AtomicInteger;

public class UglyTemporary {

	private static final AtomicInteger nextIdentifier = new AtomicInteger(0);
	private final int identifier;

	public static int currentIdentifier() {
		return nextIdentifier.get();
	}

	public UglyTemporary() {
		this.identifier = nextIdentifier.getAndIncrement();
	}

	@Override
	public String toString() {
		return String.format("$%d", this.identifier);
	}

}
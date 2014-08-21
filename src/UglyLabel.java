import java.util.concurrent.atomic.AtomicInteger;

/**
  * Could we make room for a crazy-daisy design pattern to reuse code
  * together with <c>UglyTemporary</c>? Plis =3.
  */
public class UglyLabel {	
	
	private static final AtomicInteger nextIdentifier = new AtomicInteger(0);
	private final int identifier;

	public static int currentIdentifier() {
		return nextIdentifier.get();
	}

	public UglyLabel() {
		this.identifier = nextIdentifier.getAndIncrement();
	}

	@Override
	public String toString() {
		return String.format("<L%d>", this.identifier);
	}

}
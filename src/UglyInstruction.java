

public abstract class UglyInstruction {

	/** three-address code, well, duh */
	Object addr0;
	/** three-address code, well, duh */
	Object addr1;
	/** three-address code, well, duh */
	Object addr2;

	public UglyInstruction(Object addr0, Object addr1, Object addr2) {
		this.addr0 = addr0;
		this.addr1 = addr1;
		this.addr2 = addr2;
	}

	public abstract void translate();
	
	private static String normalize(Object o) {
		if (o instanceof String) {
			return "\"" + o + "\"";
		} else if (o instanceof Number || o instanceof Boolean) {
			return "#" + o;
		} else if (o instanceof UglySymbol) {
			return ((UglySymbol)o).getName();
		} else {
			if (o != null) {
				return o.toString();
			} else {
				return "(null)";
			}
		}
	}
	
	public static class Assignment extends UglyInstruction {

		public final String operator;

		public Assignment(Object addr0, Object addr1) {
			this(addr0, addr1, "=");
		}

		public Assignment(Object addr0, Object addr1, String operator) {
			super(addr0, addr1, null);
			this.operator = operator;
		}

		@Override
		public void translate() {
			// do stuff
		}

		@Override
		public String toString() {
			return String.format(
				"%s %s %s;",
				UglyInstruction.normalize(this.addr0),
				this.operator,
				UglyInstruction.normalize(this.addr1)
				);
		}
	}

	public static class Unary extends UglyInstruction {
		
		public final String operator;

		public Unary(Object temporary, String operator, Object operand) {
			super(temporary, operand, null);
			this.operator = operator;
		}

		@Override
		public void translate() {
			// do stuff
		}

		@Override
		public String toString() {
			return String.format(
					"%s = %s %s;",
					UglyInstruction.normalize(this.addr0), 
					this.operator, 
					UglyInstruction.normalize(this.addr1)
					);
		}
	}

	public static class Binary extends UglyInstruction {
		
		public final String operator;

		public Binary(Object temporary, Object leftOperand, Object rightOperand, String operator) {
			super(temporary, leftOperand, rightOperand);
			this.operator = operator;
		}

		@Override
		public void translate() {
			// do stuff
		}

		@Override
		public String toString() {
			return String.format(
					"%s = %s %s %s;",
					UglyInstruction.normalize(this.addr0),
					UglyInstruction.normalize(this.addr1),
					this.operator,
					UglyInstruction.normalize(this.addr2)
					);
		}

	}

	public static abstract class Branching extends UglyInstruction {
		
		public Branching(Object o, Object p, Object q) {
			super(o, p, q);
		}

		public static class Label extends UglyInstruction {
			
			public Label(UglyLabel label) {
				super(label, null, null);
			}

			@Override
			public void translate() {
				// do stuff
			}

			@Override
			public String toString() {
				return "\t" + this.addr0 + ":";
			}

		}

		public static class Goto extends UglyInstruction {

			public Goto(UglyLabel label) {
				super(label, null, null);
			}

			@Override
			public void translate() {
				// do stuff
			}

			@Override
			public String toString() {
				return String.format(
					"Goto %s;",
					this.addr0
					);
			}

		}

		public static class Ifz extends UglyInstruction {
			
			public Ifz(Object condition, UglyLabel label) {
				super(condition, label, null);
			}

			@Override
			public void translate() {
				// do stuff
			}

			@Override
			public String toString() {
				return String.format(
					"Ifz %s Goto %s;",
					this.addr0,
					this.addr1
					);
			}

		}
	}

	public static abstract class ParameterHandle extends UglyInstruction {
		
		public ParameterHandle(Object o, Object p, Object q) {
			super(o, p, q);
		}
		
		public static class Push extends ParameterHandle {
			public Push(Object argument) {
				super(argument, null, null);
			}

			@Override
			public void translate() {
				// do stuff
			}

			@Override
			public String toString() {
				return String.format("PushParam %s;", UglyInstruction.normalize(this.addr0));
			}
		}
		public static class Pop extends ParameterHandle {
			public Pop(int size) {
				super(new Integer(size), null, null);
			}

			public int getSize() {
				return ((Integer)this.addr0).intValue();
			}

			@Override
			public void translate() {
				// do stuff
			}

			@Override
			public String toString() {
				return String.format("PopParams %d;", this.getSize());
			}
		}
	}

	public static abstract class MethodCall extends UglyInstruction {

		public MethodCall(Object o, Object p, Object q) {
			super(o, p, q);
		}

		public static class Simple extends MethodCall {
			public Simple(UglyInstruction.Branching.Label label) {
				super(label, null, null);
			}

			public Simple(String label) {
				super(label, null, null);
			}

			@Override
			public void translate() {
				// do stuff
			}

			@Override
			public String toString() {
				return String.format("LCall %s;", this.addr0);
			}
		}
		public static class Expression extends MethodCall {
			public Expression(UglyTemporary temporary, UglyInstruction.Branching.Label label) {
				super(temporary, label, null);
			}

			public Expression(UglyTemporary temporary, String label) {
				super(temporary, label, null);
			}

			@Override
			public void translate() {
				// do stuff
			}

			@Override
			public String toString() {
				return String.format("%s = LCall %s;", this.addr0, this.addr1);
			}
		}

	}

	public static abstract class MethodDeclaration extends UglyInstruction {

		public MethodDeclaration(Object o, Object p, Object q) {
			super(o, p, q);
		}

		public static class Begin extends MethodDeclaration {
			public Begin(int size) {
				super(new Integer(size), null, null);
			}

			@Override
			public void translate() {
				// do stuff
			}

			@Override
			public String toString() {
				return String.format("\tBeginFunc %s;", this.addr0);
			}
		}

		public static class End extends MethodDeclaration {
			public End() {
				super(null, null, null);
			}

			@Override
			public void translate() {
				// do stuff;
			}

			@Override
			public String toString() {
				return "\tEndFunc;";
			}
		}

		public static class Return extends MethodDeclaration {
			public Return() {
				super(null, null, null);
			}

			public Return(Object o) {
				super(o, null, null);
			}

			@Override
			public void translate() {
				// do stuff
			}

			@Override
			public String toString() {
				if (this.addr0 == null) {
					return "Return;";
				} else {
					return String.format(
						"Return %s;",
						UglyInstruction.normalize(this.addr0)
						);
				}
			}
		}


	}

	public static class MemoryReference extends UglyInstruction.Assignment {
		public final boolean hasLeftDereference;
		public final int leftOffset;
		public final int rightOffset;

		public MemoryReference(boolean hasLeftDereference, Object left, Object right) {
			this(hasLeftDereference, left, right, "=");
		}

		public MemoryReference(boolean hasLeftDereference, Object left, Object right, String operator) {
			this(hasLeftDereference, left, 0, right, 0, operator);
		}

		public MemoryReference(
				boolean hasLeftDereference,
				Object left,
				int leftOffset,
				Object right,
				int rightOffset,
				String operator
				) {
			super(left, right, operator);
			this.hasLeftDereference = hasLeftDereference;
			this.leftOffset = leftOffset;
			this.rightOffset = rightOffset;
		}

		@Override
		public void translate() {

		}

		@Override
		public String toString() {
			String leftSide = UglyInstruction.normalize(this.addr0);
			if (this.leftOffset != 0) {
				leftSide = String.format("%s + %d", leftSide, this.leftOffset);
			}
			String rightSide = UglyInstruction.normalize(this.addr1);
			if (this.rightOffset != 0) {
				rightSide = String.format("%s + %d", rightSide, this.rightOffset);
			}
			if (this.hasLeftDereference) {
				return String.format("*(%s) %s %s", leftSide, this.operator, rightSide);
			} else {
				return String.format("%s %s *(%s)", leftSide, this.operator, rightSide);
			}
		}

	}

}
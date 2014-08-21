public class UglyTACVisitor implements DecafVisitor {

	private static final boolean DEBUG = true;
	
	@Override
	public Object visit(SimpleNode node, UglySymbolTable data) {
		System.err.printf(
				"Visiting %s in %s (too broad)",
				node.getClass().getName(),
				this.getClass().getName()
				);
		return null;
	}

	@Override
	public Object visit(CuteProgram node, UglySymbolTable data) {
		node.childrenAccept(this, data);
		return null;
	}

	@Override
	public Object visit(CuteFieldDeclarationUnit node, UglySymbolTable data) {
		
		return null;
	}

	@Override
	public Object visit(CuteFieldDeclaration node, UglySymbolTable data) {
		// Field declaration does pretty much nothing
		return null;
	}

	@Override
	public Object visit(CuteMethodDeclarationParameter node, UglySymbolTable data) {
		return new Integer(node.type.getSize());
	}

	/**
	 * Will only accept 
	 * @param node
	 * @param data
	 * @return
	 */
	@Override
	public Object visit(CuteMethodDeclaration node, UglySymbolTable data) {
		int size = 0;
		int temporaries = UglyTemporary.currentIdentifier();
		UglyInstruction label = new UglyInstruction.Branching.Label(node.label);
		UglyInstruction begin = new UglyInstruction.MethodDeclaration.Begin(0);
		if (DEBUG) {
			System.out.println(label);
			System.out.print(begin);
			System.out.println(" (size at the end)");
		}
		node.instructions.add(label);
		node.instructions.add(begin);
		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			size += (Integer)node.jjtGetChild(i).jjtAccept(this, data);
		}
		temporaries = UglyTemporary.currentIdentifier() - temporaries;
		size += temporaries * UglySymbolType.INTEGER.getSize();
		begin.addr0 = new Integer(size);
		UglyInstruction end = new UglyInstruction.MethodDeclaration.End();
		node.closeInstructions.add(end);
		if (DEBUG) {
			System.out.print(end);
			System.out.println(" (size was " + size + ")");
		}
		return null;
	}

	@Override
	public Object visit(CuteBlock node, UglySymbolTable data) {
		int size = 0;
		for (int i = 0; i < node.jjtGetNumChildren(); i++) {
			size += (Integer)node.jjtGetChild(i).jjtAccept(this, data);
		}
		return new Integer(size);
	}

	@Override
	public Object visit(CuteVariableDeclaration node, UglySymbolTable data) {
		int size = node.type.getSize();
		size *= node.names.size();
		return size;
	}

	@Override
	public Object visit(CuteType node, UglySymbolTable data) {
		return 0;
	}

	/**
	  * Has instructions in the fashion:
	  * (1)
	  * ifz t_expression goto endIfLabel; (instructions)
	  *     blah blah;
	  * endIfLabel:	(closeInstructions)
	  * blah blah
	  * (2)
	  * ifz t_expression goto elseLabel; (instructions)
	  *     blah blah;
	  * goto endIfLabel; (middleInstructions)
	  * elseLabel: (middleInstructions)
	  *     blah blah;
	  * endIfLabel: (closeInstructions)
	  * blah blah;
	  * @return the size of all locals created in both the if-block and the else-block.
	  */
	@Override
	public Object visit(CuteIfStatement node, UglySymbolTable data) {
		int size = 0;
		int childCount = node.jjtGetNumChildren();
		boolean hasElse = childCount == 3;
		if (!hasElse) {
			assert childCount == 2;
		}

		Node expression = node.jjtGetChild(0);
		assert expression instanceof CuteExpression;
		Object expressionResult = expression.jjtAccept(this, data);

		if (!hasElse) {
			assert node.elseLabel == null;
			UglyInstruction gotoEndIf = new UglyInstruction.Branching.Ifz(expressionResult, node.endIfLabel);
			node.instructions.add(gotoEndIf);
			if (DEBUG) {
				System.out.println(gotoEndIf);
			}
		} else {
			assert childCount == 3;
			assert node.elseLabel != null;
			UglyInstruction gotoElse = new UglyInstruction.Branching.Ifz(expressionResult, node.elseLabel);
			node.instructions.add(gotoElse);
			if (DEBUG) {
				System.out.println(gotoElse);
			}
		}

		Node ifBlock = node.jjtGetChild(1);
		size += (Integer)ifBlock.jjtAccept(this, data);

		if (hasElse) {
			assert node.elseLabel != null;
			UglyInstruction gotoEndIf = new UglyInstruction.Branching.Goto(node.endIfLabel);
			UglyInstruction elseLabel = new UglyInstruction.Branching.Label(node.elseLabel);
			node.middleInstructions.add(gotoEndIf);
			node.middleInstructions.add(elseLabel);
			if (DEBUG) {
				System.out.println(gotoEndIf);
				System.out.println(elseLabel);
			}

			Node elseBlock = node.jjtGetChild(2);
			assert elseBlock instanceof CuteBlock;
			size += (Integer)elseBlock.jjtAccept(this, data);
		}

		UglyInstruction endIfLabel = new UglyInstruction.Branching.Label(node.endIfLabel);
		node.closeInstructions.add(endIfLabel);
		if (DEBUG) {
			System.out.println(endIfLabel);
		}

		return size;
	}

	/**
	  * i = (visit initial expression); (instructions)
	  * t0 = (visit end_expression); (instructions)
	  * 	forLabel: (instructions)
	  * t1 = i != t0; (instructions
	  * ifz t1 goto endForLabel; (instructions)
	  * blah blah (visit block)
	  * 	incrementLabel:
	  * t2 = #1; // here be funny optimization techniques (close instructions)
	  *** (not writing this instruction before because it might not be true anymore
	  *** that each temporary is used once
	  *** [maybe won't be able to use the aho&alii algorithm])
	  * i += t2; (close instructions)
	  * goto forLabel; (close instructions)
	  * 	endForLabel;
	  * @param node the usual crap
	  * @param data the usual crap
	  * @return the size of all locals created.
	  */
	@Override
	public Object visit(CuteForStatement node, UglySymbolTable data) {
		int size = UglySymbolType.INTEGER.getSize();
		UglyInstruction instruction;
		
		// instructions
		
		Node initialExpression = node.jjtGetChild(0);
		Object initialValue = initialExpression.jjtAccept(this, data);
		instruction = new UglyInstruction.Assignment(node.iEntry, initialValue);
		node.instructions.add(instruction);
		if (DEBUG) System.out.println(instruction);
		
		Node endExpression = node.jjtGetChild(1);
		Object endValue = endExpression.jjtAccept(this, data);
		UglyTemporary t0 = new UglyTemporary();
		instruction = new UglyInstruction.Assignment(t0, endValue);
		node.instructions.add(instruction);
		if (DEBUG) System.out.println(instruction);
		
		instruction = new UglyInstruction.Branching.Label(node.forLabel);
		node.instructions.add(instruction);
		if (DEBUG) System.out.println(instruction);
		
		UglyTemporary t1 = new UglyTemporary();
		instruction = new UglyInstruction.Binary(t1, node.iEntry, t0, "<");
		node.instructions.add(instruction);
		if (DEBUG) System.out.println(instruction);
		
		instruction = new UglyInstruction.Branching.Ifz(t1, node.endForLabel);
		node.instructions.add(instruction);
		if (DEBUG) System.out.println(instruction);
		
		// do the block
		
		size += (Integer)node.jjtGetChild(2).jjtAccept(this, data);
		
		// close instructions
		
		instruction = new UglyInstruction.Branching.Label(node.incrementLabel);
		node.closeInstructions.add(instruction);
		if (DEBUG) System.out.println(instruction);
		
		UglyTemporary t2 = new UglyTemporary();
		instruction = new UglyInstruction.Assignment(t2, 1);
		node.closeInstructions.add(instruction);
		if (DEBUG) System.out.println(instruction);
		
		instruction = new UglyInstruction.Assignment(node.iEntry, t2, "+=");
		node.closeInstructions.add(instruction);
		if (DEBUG) System.out.println(instruction);
		
		instruction = new UglyInstruction.Branching.Goto(node.forLabel);
		node.closeInstructions.add(instruction);
		if (DEBUG) System.out.println(instruction);
		
		instruction = new UglyInstruction.Branching.Label(node.endForLabel);
		node.closeInstructions.add(instruction);
		if (DEBUG) System.out.println(instruction);
		
		return size;
	}

	/**
	 * @return 0 because no locals are created
	 */
	@Override
	public Object visit(CuteReturnStatement node, UglySymbolTable data) {
		UglyInstruction instruction;
		if (node.jjtGetNumChildren() == 1) {
			Node expression = node.jjtGetChild(0);
			assert expression instanceof CuteExpression;
			Object o = expression.jjtAccept(this, data);
			instruction = new UglyInstruction.MethodDeclaration.Return(o); 
		} else {
			assert node.jjtGetNumChildren() == 0;
			instruction = new UglyInstruction.MethodDeclaration.Return();
		}
		node.instructions.add(instruction);
		if (DEBUG) {
			System.out.println(instruction);
		}
		return 0;
	}

	/**
	 * @return 0 because no locals are created
	 */
	@Override
	public Object visit(CuteBreakStatement node, UglySymbolTable data) {
		Node n = node;
		while (!(n instanceof CuteForStatement)) {
			n = ((SimpleNode)n).parent;
		}
		CuteForStatement forParent = (CuteForStatement)n;
		UglyInstruction gotoEndFor = new UglyInstruction.Branching.Goto(forParent.endForLabel);
		node.instructions.add(gotoEndFor);
		if (DEBUG) {
			System.out.println(gotoEndFor);
		}
		return 0;
	}

	/**
	 * @return 0 because no locals are created
	 */
	@Override
	public Object visit(CuteContinueStatement node, UglySymbolTable data) {
		Node n = node;
		while (!(n instanceof CuteForStatement)) {
			n = ((SimpleNode)n).parent;
		}
		CuteForStatement forParent = (CuteForStatement)n;
		UglyInstruction gotoIncrement = new UglyInstruction.Branching.Goto(forParent.incrementLabel);
		node.instructions.add(gotoIncrement);
		if (DEBUG) {
			System.out.println(gotoIncrement);
		}
		return 0;
	}

	@Override
	public Object visit(CuteStatement node, UglySymbolTable data) {
		assert node.jjtGetNumChildren() == 1 : node.jjtGetNumChildren();
		node.jjtGetChild(0).jjtAccept(this, data);
		return 0;
	}

	@Override
	public Object visit(CuteAssignment node, UglySymbolTable data) {
		CuteLocation location = (CuteLocation)node.jjtGetChild(0);
		Object leftSide = location.jjtAccept(this, data);
		Object rightSide = node.jjtGetChild(1).jjtAccept(this, data);
		
		UglyInstruction assignment;
		if (location.isArrayAccess) {
			assert leftSide instanceof UglyTemporary;
			assignment = new UglyInstruction.MemoryReference(true, leftSide, rightSide, node.value.toString());
		} else {
			assert leftSide instanceof UglySymbol;
			assert node.value instanceof String;
			assignment = new UglyInstruction.Assignment(leftSide, rightSide, node.value.toString());
		}
		node.closeInstructions.add(assignment);
		if (DEBUG) {
			System.out.println(assignment);
		}
		
		return 0;
	}

	@Override
	public Object visit(CuteLocation node, UglySymbolTable data) {
		if (node.isArrayAccess) {
			Object index = node.jjtGetChild(0).jjtAccept(this, data);
			UglyTemporary t0 = new UglyTemporary();
			UglyTemporary t1 = new UglyTemporary();
			UglyTemporary t2 = new UglyTemporary();
			UglyTemporary t3 = new UglyTemporary();
			UglyInstruction assignIndex = new UglyInstruction.Assignment(t0, index);
			UglyInstruction assignSize = new UglyInstruction.Assignment(t1, node.entry.getType().getSize());
			UglyInstruction displacement = new UglyInstruction.Binary(t2, t0, t1, "*");
			UglyInstruction address = new UglyInstruction.Binary(t3, node.entry, t2, "+");
			node.closeInstructions.add(assignIndex);
			node.closeInstructions.add(assignSize);
			node.closeInstructions.add(displacement);
			node.closeInstructions.add(address);
			if (DEBUG) {
				System.out.println(assignIndex);
				System.out.println(assignSize);
				System.out.println(displacement);
				System.out.println(address);
			}
			return t3;
		} else {
			return node.entry;
		}
	}

	/**
	 * 
	 */
	@Override
	public Object visit(CuteMethodCall node, UglySymbolTable data) {
		int paramSize = 0;
		for (int i = node.jjtGetNumChildren() - 1; i > 0; i--) {
			CuteExpression expression = (CuteExpression)node.jjtGetChild(i);
			Object o = expression.jjtAccept(this, data);
			UglyInstruction instruction = new UglyInstruction.ParameterHandle.Push(o);
			node.instructions.add(instruction);
			try {
				// TODO why NPE :( ?
				paramSize += expression.type.getSize();
			} catch (NullPointerException e) {
				paramSize += UglySymbolType.INTEGER.getSize();
			}
			if (DEBUG) {
				System.out.println(instruction);
			}
		}
		UglyTemporary t = null;
		UglyInstruction callInstruction;
		if (node.entry.getType() == UglySymbolType.VOID) {
			callInstruction = new UglyInstruction.MethodCall.Simple(node.entry.getName());
		} else {
			t = new UglyTemporary();
			callInstruction = new UglyInstruction.MethodCall.Expression(t, node.entry.getName());
		}
		node.closeInstructions.add(callInstruction);
		if (DEBUG) {
			System.out.println(callInstruction);
		}
		UglyInstruction popInstruction = new UglyInstruction.ParameterHandle.Pop(paramSize);
		node.closeInstructions.add(popInstruction);
		if (DEBUG) {
			System.out.println(popInstruction);
		}
		if (node.entry.getType() == UglySymbolType.VOID) {
			return 0;
		} else {
			return t;
		}
	}

	@Override
	public Object visit(CuteCalloutStatement node, UglySymbolTable data) {
		int paramSize = 0;
		for (int i = node.jjtGetNumChildren() - 1; i > 0; i--) {
			Node argument = node.jjtGetChild(i);
			Object o = argument.jjtAccept(this, data);
			UglyInstruction instruction = new UglyInstruction.ParameterHandle.Push(o);
			node.instructions.add(instruction);
			paramSize += UglySymbolType.INTEGER.getSize(); // TODO separate types
			// paramSize += 
			if (DEBUG) {
				System.out.println(instruction);
			}
		}
		{
			// push string
			String name = (String)node.value;
			UglyTemporary t = new UglyTemporary();
			UglyInstruction tString = new UglyInstruction.Assignment(t, name);
			node.instructions.add(tString);
			if (DEBUG) {
				System.out.println(tString);
			}
			UglyInstruction instruction = new UglyInstruction.ParameterHandle.Push(t);
			node.instructions.add(instruction);
			paramSize += UglySymbolType.INTEGER.getSize(); // TODO separate types
			if (DEBUG) {
				System.out.println(instruction);
			}
		}
		
		UglyInstruction callInstruction;
		UglyTemporary t = new UglyTemporary();
		callInstruction = new UglyInstruction.MethodCall.Expression(t, "callout");
		node.closeInstructions.add(callInstruction);
		if (DEBUG) {
			System.out.println(callInstruction);
		}
		UglyInstruction popInstruction = new UglyInstruction.ParameterHandle.Pop(paramSize);
		node.closeInstructions.add(popInstruction);
		if (DEBUG) {
			System.out.println(popInstruction);
		}
		return t;
	}

	@Override
	public Object visit(CuteMethodName node, UglySymbolTable data) {
		node.childrenAccept(this, data);
		return 0;
	}

	@Override
	public Object visit(CuteExpression node, UglySymbolTable data) {
		assert node.jjtGetNumChildren() == 1;
		return node.jjtGetChild(0).jjtAccept(this, data);
	}

	@Override
	public Object visit(CuteDisjunctionExpression node, UglySymbolTable data) {
		if (node.jjtGetNumChildren() == 1) {
			return node.jjtGetChild(0).jjtAccept(this, data);
		} else {
			Object tLeft = node.jjtGetChild(0).jjtAccept(this, data);
			UglyTemporary newTemp = null;
			for (int i = 1; i < node.jjtGetNumChildren(); i++) {
				Object tRight = node.jjtGetChild(i).jjtAccept(this, data);
				newTemp = new UglyTemporary();
				String operator = (String)((CuteExpression)node.jjtGetChild(i)).operator;
				assert operator.equals("||");
				UglyInstruction.Binary binary = new UglyInstruction.Binary(
					newTemp,
					tLeft,
					tRight,
					operator
					);
				tLeft = newTemp;
				node.instructions.add(binary);
				if (DEBUG) {
					System.out.println(binary);
				}
			}
			return newTemp;
		}
	}

	@Override
	public Object visit(CuteConjunctionExpression node, UglySymbolTable data) {
		if (node.jjtGetNumChildren() == 1) {
			return node.jjtGetChild(0).jjtAccept(this, data);
		} else {
			Object tLeft = node.jjtGetChild(0).jjtAccept(this, data);
			UglyTemporary newTemp = null;
			for (int i = 1; i < node.jjtGetNumChildren(); i++) {
				Object tRight = node.jjtGetChild(i).jjtAccept(this, data);
				newTemp = new UglyTemporary();
				String operator = (String)((CuteExpression)node.jjtGetChild(i)).operator;
				assert operator.equals("||");
				UglyInstruction.Binary binary = new UglyInstruction.Binary(
					newTemp,
					tLeft,
					tRight,
					operator
					);
				tLeft = newTemp;
				node.instructions.add(binary);
				if (DEBUG) {
					System.out.println(binary);
				}
			}
			return newTemp;
		}
	}

	@Override
	public Object visit(CuteEqualityExpression node, UglySymbolTable data) {
		if (node.jjtGetNumChildren() == 1) {
			return node.jjtGetChild(0).jjtAccept(this, data);
		} else {
			assert node.jjtGetNumChildren() == 2;
			Object tLeft = node.jjtGetChild(0).jjtAccept(this, data);
			Object tRight = node.jjtGetChild(1).jjtAccept(this, data);
			String operator = (String)((CuteExpression)node.jjtGetChild(1)).operator;
			assert operator.equals("==") || operator.equals("!=") : operator;
			UglyTemporary newTemp = new UglyTemporary();
			UglyInstruction.Binary binary = new UglyInstruction.Binary(newTemp, tLeft, tRight, operator);
			node.instructions.add(binary);
			if (DEBUG) {
				System.out.println(binary);
			}
			return newTemp;
		}
	}

	@Override
	public Object visit(CuteRelationExpression node, UglySymbolTable data) {
		if (node.jjtGetNumChildren() == 1) {
			return node.jjtGetChild(0).jjtAccept(this, data);
		} else {
			assert node.jjtGetNumChildren() == 2;
			Object tLeft = node.jjtGetChild(0).jjtAccept(this, data);
			Object tRight = node.jjtGetChild(1).jjtAccept(this, data);
			String operator = (String)((CuteExpression)node.jjtGetChild(1)).operator;
			UglyTemporary newTemp = new UglyTemporary();
			UglyInstruction.Binary binary = new UglyInstruction.Binary(newTemp, tLeft, tRight, operator);
			node.instructions.add(binary);
			{ // is this really a friggin' operator?
				String[] ops = {"<", ">", "<=", ">="};
				boolean b = false;
				for (String s : ops) {
					b = b || s.equals(operator);
				}
				assert b : operator;
			}
			if (DEBUG) {
				System.out.println(binary);
			}
			return newTemp;
		}
	}

	@Override
	public Object visit(CuteTermExpression node, UglySymbolTable data) {
		if (node.jjtGetNumChildren() == 1) {
			return node.jjtGetChild(0).jjtAccept(this, data);
		} else {
			Object tLeft = node.jjtGetChild(0).jjtAccept(this, data);
			UglyTemporary newTemp = null;
			for (int i = 1; i < node.jjtGetNumChildren(); i++) {
				Object tRight = node.jjtGetChild(i).jjtAccept(this, data);
				newTemp = new UglyTemporary();
				String operator = ((CuteExpression)node.jjtGetChild(i)).operator;
				assert operator.equals("+") || operator.equals("-") : operator;
				UglyInstruction.Binary binary = new UglyInstruction.Binary(
					newTemp,
					tLeft,
					tRight,
					operator
					);
				tLeft = newTemp;
				node.instructions.add(binary);
				if (DEBUG) {
					System.out.println(binary);
				}
			}
			return newTemp;
		}
	}

	@Override
	public Object visit(CuteFactorExpression node, UglySymbolTable data) {
		if (node.jjtGetNumChildren() == 1) {
			return node.jjtGetChild(0).jjtAccept(this, data);
		} else {
			Object tLeft = node.jjtGetChild(0).jjtAccept(this, data);
			UglyTemporary newTemp = null;
			for (int i = 1; i < node.jjtGetNumChildren(); i++) {
				Object tRight = node.jjtGetChild(i).jjtAccept(this, data);
				newTemp = new UglyTemporary();
				String operator = ((CuteExpression)node.jjtGetChild(i)).operator;
				assert operator.equals("*") || operator.equals("/") || operator.equals("%") : operator;
				UglyInstruction.Binary binary = new UglyInstruction.Binary(
					newTemp,
					tLeft,
					tRight,
					operator
					);
				tLeft = newTemp;
				node.instructions.add(binary);
				if (DEBUG) {
					System.out.println(binary);
				}
			}
			return newTemp;
		}
	}

	@Override
	public Object visit(CuteUnaryExpression node, UglySymbolTable data) {
		assert node.jjtGetNumChildren() == 1;
		if (node.isArithmetic == null) {
			return node.jjtGetChild(0).jjtAccept(this, data);
		} else {
			Object operand = node.jjtGetChild(0).jjtAccept(this, data);
			UglyTemporary t = new UglyTemporary();
			assert node.operator.equals("-") || node.operator.equals("+") || node.operator.equals("!") : node.operator; 
 			UglyInstruction.Unary unary = new UglyInstruction.Unary(t, node.operator, operand);
			node.closeInstructions.add(unary);
			if (DEBUG) {
				System.out.println(unary);
			}
			return t;
		}
	}

	@Override
	public Object visit(CuteExpressionUnit node, UglySymbolTable data) {
		assert node.jjtGetNumChildren() == 1;
		Node child = node.jjtGetChild(0);
		if (child instanceof CuteMethodCall) {
			Object o = child.jjtAccept(this, data);
			return o;
		} else if (child instanceof CuteCalloutStatement) {
			Object o = child.jjtAccept(this, data);
			return o;
		} else if (child instanceof CuteLocation) {
			CuteLocation location = (CuteLocation)child;
			if (location.isArrayAccess) {
				UglyTemporary index = (UglyTemporary)location.jjtAccept(this, data);
				UglyTemporary t = new UglyTemporary();
				UglyInstruction dereference = new UglyInstruction.MemoryReference(false, t, index);
				node.closeInstructions.add(dereference);
				if (DEBUG) {
					System.out.println(dereference);
				}
				return t;
			}
		}
		return child.jjtAccept(this, data);
	}

	@Override
	public Object visit(CuteCalloutArgument node, UglySymbolTable data) {
		assert node.jjtGetNumChildren() == 1;
		Node argument = node.jjtGetChild(0);
		Object o = node.jjtGetChild(0).jjtAccept(this, data);
		return o;
	}

	@Override
	public Object visit(CuteLiteral node, UglySymbolTable data) {
		UglyTemporary t = new UglyTemporary();
		UglyInstruction.Assignment load = new UglyInstruction.Assignment(t, node.value);
		node.instructions.add(load);
		if (DEBUG) {
			System.out.println(load);
		}
		return t;
	}

	@Override
	public Object visit(CuteCharLiteral node, UglySymbolTable data) {
		assert "this code should not be reached" == null;
		return 0;
	}

	@Override
	public Object visit(CuteIntLiteral node, UglySymbolTable data) {
		assert "this code should not be reached" == null;
		return 0;
	}

	@Override
	public Object visit(CuteBooleanLiteral node, UglySymbolTable data) {
		assert "this code should not be reached" == null;
		return 0;
	}

	@Override
	public Object visit(CuteStringLiteral node, UglySymbolTable data) {
		UglyTemporary t = new UglyTemporary();
		UglyInstruction assignment = new UglyInstruction.Assignment(t, node.value);
		node.instructions.add(assignment);
		if (DEBUG) {
			System.out.println(assignment);
		}
		return t;
	}
	
}
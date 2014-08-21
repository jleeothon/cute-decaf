
public class UglyTypeCheckVisitor implements DecafVisitor {

	private final static boolean DEBUG = false;

	public static void UglyTypeCheckVisitor() {
		if (DEBUG) {
			System.out.println("Printing debug information for UglyTypeCheckVisitor");
		}
	}

	private static void printVisitorPrefix() {
		System.out.print("[DEBUG] Ugly Type Check Visitor: ");
	}

	@Override
	public Object visit(SimpleNode node, UglySymbolTable data) {
		return node.childrenAccept(this, data);
	}

	@Override
	public Object visit(CuteProgram node, UglySymbolTable data) {
		return node.childrenAccept(this, data);
	}

	@Override
	public Object visit(CuteFieldDeclarationUnit node, UglySymbolTable data) {
		return node.childrenAccept(this, data);
	}

	@Override
	public Object visit(CuteFieldDeclaration node, UglySymbolTable data) {
		return node.childrenAccept(this, data);
	}

	@Override
	public Object visit(CuteMethodDeclarationParameter node,
			UglySymbolTable data) {
		return node.childrenAccept(this, data);
	}

	@Override
	public Object visit(CuteMethodDeclaration node, UglySymbolTable data) {
		return node.childrenAccept(this, data);
	}

	@Override
	public Object visit(CuteBlock node, UglySymbolTable data) {
		return node.childrenAccept(this, data);
	}

	@Override
	public Object visit(CuteVariableDeclaration node, UglySymbolTable data) {
		return node.childrenAccept(this, data);
	}

	@Override
	public Object visit(CuteType node, UglySymbolTable data) {
		return node.childrenAccept(this, data);
	}

	@Override
	public Object visit(CuteIfStatement node, UglySymbolTable data) {
		CuteExpression condition = (CuteExpression)node.jjtGetChild(0);
		UglySymbolType conditionType = (UglySymbolType)condition.jjtAccept(this, data);
		if (conditionType != UglySymbolType.BOOLEAN) {
			UglySemanticError.complain(
				node,
				"Expression in if-clause condition near should be of boolean type"
				);
		}
		CuteBlock ifBlock = (CuteBlock)node.jjtGetChild(1);
		ifBlock.jjtAccept(this, data);
		if (node.jjtGetNumChildren() == 3) {
			CuteBlock elseBlock = (CuteBlock)node.jjtGetChild(2);
			elseBlock.jjtAccept(this, data);
		}
		return null;
	}

	@Override
	public Object visit(CuteForStatement node, UglySymbolTable data) {
		CuteExpression startExpression = (CuteExpression)node.jjtGetChild(0);
		CuteExpression endExpression = (CuteExpression)node.jjtGetChild(1);
		UglySymbolType startType = (UglySymbolType)startExpression.jjtAccept(this, data);
		UglySymbolType endType = (UglySymbolType)endExpression.jjtAccept(this, data);
		if (startType != UglySymbolType.INTEGER) {
			UglySemanticError.complain(
				node,
				"start value of for-statement should be of int type"
				);
		}
		if (endType != UglySymbolType.INTEGER) {
			UglySemanticError.complain(
				node,
				"end value of for-statement should be of int type"
				);
		}
		return node.childrenAccept(this, data);
	}

	@Override
	public Object visit(CuteReturnStatement node, UglySymbolTable data) {
		SimpleNode parent = node;
		UglySymbolType parentMethodType = null;
		while (parent != null) {
			parent = (SimpleNode)parent.parent;
			if (parent instanceof CuteMethodDeclaration) {
				parentMethodType = ((CuteMethodDeclaration)parent).type;
				parentMethodType = ((UglySymbolType.Method)parentMethodType).returnType;
				break;
			}
		}
		assert parentMethodType != null;
		if (node.jjtGetNumChildren() > 0) {
			assert node.jjtGetNumChildren() == 1;
			CuteExpression expression = (CuteExpression)node.jjtGetChild(0);
			UglySymbolType expressionType = (UglySymbolType)expression.jjtAccept(this, data);
			if (parentMethodType != expressionType) {
				UglySemanticError.complain(
					node,
					String.format("Returning %s type in method of %s type", expressionType, parentMethodType)
					);
			}
		} else {
			if (parentMethodType != UglySymbolType.VOID) {
				UglySemanticError.complain(
					node,
					String.format("No return value for method of type %s", parentMethodType)
					);
			}
		}
		return null;
	}

	@Override
	public Object visit(CuteBreakStatement node, UglySymbolTable data) {
		return node.childrenAccept(this, data);
	}

	@Override
	public Object visit(CuteContinueStatement node, UglySymbolTable data) {
		return node.childrenAccept(this, data);
	}

	@Override
	public Object visit(CuteStatement node, UglySymbolTable data) {
		return node.childrenAccept(this, data);
	}

	@Override
	public Object visit(CuteAssignment node, UglySymbolTable data) {
		CuteLocation location = (CuteLocation)node.jjtGetChild(0);
		UglySymbolType locationType = (UglySymbolType)location.jjtAccept(this, data);
		CuteExpression expression = (CuteExpression)node.jjtGetChild(1);
		UglySymbolType expressionType = (UglySymbolType)expression.jjtAccept(this, data);
		if (node.isShortcut) {
			if (locationType != UglySymbolType.INTEGER && locationType != null) {
				UglySemanticError.complain(
					node,
					"Left side of asignment shortcut must be of int type"
					);	
			}
			if (expressionType != UglySymbolType.INTEGER && expressionType != null) {
				UglySemanticError.complain(
					node,
					"Right side of asignment shortcut must be of int type"
					);	
			}
		} else {
			if (locationType != expressionType && locationType != null && expressionType != null) {
				UglySemanticError.complain(
					node,
					String.format("Assigning into %s type of %s type", locationType, expressionType)
					);
			}
		}
		return null;
	}

	/**
	  * Returns the type of the method variables.
	  */
	@Override
	public Object visit(CuteLocation node, UglySymbolTable data) {
		UglySymbol entry = node.entry;
		if (DEBUG) {
			printVisitorPrefix();
			System.out.printf("location of symbol: %s\n", entry);
		}
		UglySymbolType type = null;
		if (entry != null) {
			type = entry.getType();
		}

		if (node.jjtGetNumChildren() == 1) {
			CuteExpression indexExpression = (CuteExpression)node.jjtGetChild(0);
			UglySymbolType indexType = (UglySymbolType)indexExpression.jjtAccept(this, data);
			if (indexType != null && indexType != UglySymbolType.INTEGER) {
				UglySemanticError.complain(
					node,
					String.format(
						"Index in location %s should be of int type",
						node.name
						)
					);
			}
		}

		return type;
	}

	/**
	  * Returns the type of the method call.
	  */
	@Override
	public Object visit(CuteMethodCall node, UglySymbolTable data) {
		// node.childrenAccept(this, data);
		assert node.jjtGetChild(0) instanceof CuteMethodName;

		// check args of method call
		int argCount = node.jjtGetNumChildren() - 1; // because of method name
		UglySymbol entry = node.entry;
		if (entry == null) {
			return null;
		}
		
		int paramCount = ((UglySymbolType.Method)entry.getType()).params.size();
		if (argCount != paramCount) {
			UglySemanticError.complain(
				node,
				String.format(
					"provided %d arguments in method with %d parameters",
					argCount,
					paramCount
					)
				);
		} else {
			Object[] paramTypes = ((UglySymbolType.Method)node.entry.getType()).params.toArray();
			for (int i = 1; i < paramCount; i++) {
				CuteExpression argument = (CuteExpression)node.jjtGetChild(i);
				Object argumentType = argument.jjtAccept(this, data);
				Object parameterType = paramTypes[i - 1];
				if (argumentType != parameterType && argumentType != null) {
					UglySemanticError.complain(
						argument,
						String.format(
							"provided %s argument where %s was expected (argument number %d)",
							argumentType,
							parameterType,
							i
							)
						);
				}
			}
		} // end if argCount != paramCount

		UglySymbolType.Method methodType = (UglySymbolType.Method)entry.getType();
		UglySymbolType returnType = methodType.returnType;
		if (DEBUG) {
			printVisitorPrefix();
			System.out.printf("method call of symbol: %s, ", entry);
			System.out.printf("return type is: %s\n", returnType);
		}

		return returnType;
	}

	/**
	  * Returns the type of the expression.
	  */
	@Override
	public Object visit(CuteMethodName node, UglySymbolTable data) {
		return null;
		//return node.childrenAccept(this, data);
	}

	/**
	  * Returns the type of the expression.
	  */
	@Override
	public Object visit(CuteExpression node, UglySymbolTable data) {
		UglySymbolType type = (UglySymbolType)node.jjtGetChild(0).jjtAccept(this, data);
		node.type = type;
		return type;
	}

	/**
	  * Returns the type of the expression.
	  */
	@Override
	public Object visit(CuteDisjunctionExpression node, UglySymbolTable data) {
		if (node.jjtGetNumChildren() == 1) {
			return node.jjtGetChild(0).jjtAccept(this, data);
		} else {
			for (int i = 0; i < node.jjtGetNumChildren(); i++) {
				UglySymbolType childType = (UglySymbolType)node.jjtGetChild(i).jjtAccept(this, data);
				if (childType != UglySymbolType.BOOLEAN && childType != null) {
					UglySemanticError.complain(
						node,
						String.format("Disjunction operand is not of int type, found %s.", childType)
						);
					break;
				}
			}
			return UglySymbolType.BOOLEAN;
		}
	}

	/**
	  * Returns the type of the expression.
	  */
	@Override
	public Object visit(CuteConjunctionExpression node, UglySymbolTable data) {
		if (node.jjtGetNumChildren() == 1) {
			return node.jjtGetChild(0).jjtAccept(this, data);
		} else {
			for (int i = 0; i < node.jjtGetNumChildren(); i++) {
				UglySymbolType childType = (UglySymbolType)node.jjtGetChild(i).jjtAccept(this, data);
				if (childType != UglySymbolType.BOOLEAN && childType != null) {
					UglySemanticError.complain(
						node,
						String.format("Conjunction operand is not of int type, found %s.", childType)
						);
					break;
				}
			}
			return UglySymbolType.BOOLEAN;
		}
	}

	/**
	  * Returns the type of the expression.
	  */
	@Override
	public Object visit(CuteEqualityExpression node, UglySymbolTable data) {
		CuteExpression child0 = (CuteExpression)node.jjtGetChild(0);
		UglySymbolType child0Type = (UglySymbolType)child0.jjtAccept(this, data);
		if (node.jjtGetNumChildren() == 1) {
			return child0Type;
		} else {
			assert node.jjtGetNumChildren() == 2 : node.jjtGetNumChildren();
			CuteExpression leftChild = child0;
			UglySymbolType leftType = child0Type;
			CuteExpression rightChild = (CuteExpression)node.jjtGetChild(1);
			UglySymbolType rightType = (UglySymbolType)rightChild.jjtAccept(this, data);
			if (leftType != rightType) {
				UglySemanticError.complain(
					node.line,
					node.column,
					String.format(
						"Both sides of equality operator should be of same type, found %s and %s",
						leftType,
						rightType
						)
					);
			}
			return UglySymbolType.BOOLEAN;
		}
	}

	/**
	  * Returns the type of the expression.
	  */
	@Override
	public Object visit(CuteRelationExpression node, UglySymbolTable data) {
		CuteExpression child0 = (CuteExpression)node.jjtGetChild(0);
		UglySymbolType child0Type = (UglySymbolType)child0.jjtAccept(this, data);
		if (node.jjtGetNumChildren() == 1) {
			return child0Type;
		} else {
			assert node.jjtGetNumChildren() == 2 : node.jjtGetNumChildren();
			CuteExpression leftChild = child0;
			UglySymbolType leftType = child0Type;
			CuteExpression rightChild = (CuteExpression)node.jjtGetChild(1);
			UglySymbolType rightType = (UglySymbolType)rightChild.jjtAccept(this, data);
			if (leftType != UglySymbolType.INTEGER) {
				UglySemanticError.complain(
					node,
					String.format(
						"Left side of relation operator should be of int type, found %s.",
						leftType
						)
					);
			}
			if (rightType != UglySymbolType.INTEGER) {
				UglySemanticError.complain(
					node,
					String.format(
						"Right side of relation operator should be of int type, found %s.",
						leftType
						)
					);
			}
			return UglySymbolType.BOOLEAN;
		}
	}

	/**
	  * Returns the type of the expression.
	  */
	@Override
	public Object visit(CuteTermExpression node, UglySymbolTable data) {
		if (node.jjtGetNumChildren() == 1) {
			return node.jjtGetChild(0).jjtAccept(this, data);
		} else {
			for (int i = 0; i < node.jjtGetNumChildren(); i++) {
				UglySymbolType childType = (UglySymbolType)node.jjtGetChild(i).jjtAccept(this, data);
				if (childType != UglySymbolType.INTEGER && childType != null) {
					UglySemanticError.complain(
						node,
						String.format("Term operand is not of int type, found %s.", childType)
						);
					break;
				}
			}
			return UglySymbolType.INTEGER;
		}
	}

	@Override
	public Object visit(CuteFactorExpression node, UglySymbolTable data) {
		if (node.jjtGetNumChildren() == 1) {
			return node.jjtGetChild(0).jjtAccept(this, data);
		} else {
			for (int i = 0; i < node.jjtGetNumChildren(); i++) {
				UglySymbolType childType = (UglySymbolType)(node.jjtGetChild(i).jjtAccept(this, data));
				if (childType != UglySymbolType.INTEGER && childType != null) {
					UglySemanticError.complain(
						node,
						String.format("Factor operand is not of int type, found %s.", childType)
						);
					break;
				}
			}
			return UglySymbolType.INTEGER;
		}
	}

	@Override
	public Object visit(CuteUnaryExpression node, UglySymbolTable data) {
		UglySymbolType operandType = (UglySymbolType)node.jjtGetChild(0).jjtAccept(this, data);
		if (node.isArithmetic == null) {
			return operandType;
		} else if (operandType != null) {
			if (node.isArithmetic == true) {
				if (operandType != UglySymbolType.INTEGER) {
					UglySemanticError.complain("Unary sign operator should receive an int type");
				}
				return UglySymbolType.INTEGER;
			} else {
				if (operandType != UglySymbolType.BOOLEAN) {
					UglySemanticError.complain("Unary not operator should receive a boolean type");
				}
				return UglySymbolType.BOOLEAN;
			}
		} else {
			if (node.isArithmetic == true) {
				return UglySymbolType.INTEGER;
			} else {
				return UglySymbolType.BOOLEAN;
			}
		}
	}

	/**
	  * Returns the type of the expression.
	  */
	@Override
	public Object visit(CuteExpressionUnit node, UglySymbolTable data) {
		Object o = node.jjtGetChild(0).jjtAccept(this, data);
		if (o == UglySymbolType.VOID) {
			UglySemanticError.complain(
				node,
				"Using void method inside expression"
				);
		}
		return o;
	}

	@Override
	public Object visit(CuteCalloutArgument node, UglySymbolTable data) {
		return UglySymbolType.INTEGER;
	}

	/**
	  * Returns the type of the expression.
	  */
	@Override
	public Object visit(CuteLiteral node, UglySymbolTable data) {
		CuteLiteral childNode = (CuteLiteral)node.jjtGetChild(0);
		Object o = childNode.jjtAccept(this, data);
		assert o instanceof UglySymbolType : o.getClass().getName();
		if (DEBUG) {
			printVisitorPrefix();
			System.out.printf(
				"literal type: %s in line %d, column %d\n",
				o,
				childNode.line,
				childNode.column
				);
		}
		return o;
	}

	@Override
	public Object visit(CuteCharLiteral node, UglySymbolTable data) {
		// CAUTION
		return UglySymbolType.INTEGER;
	}

	@Override
	public Object visit(CuteIntLiteral node, UglySymbolTable data) {
		return UglySymbolType.INTEGER;
	}

	@Override
	public Object visit(CuteBooleanLiteral node, UglySymbolTable data) {
		return UglySymbolType.BOOLEAN;
	}

	@Override
	public Object visit(CuteCalloutStatement node, UglySymbolTable data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(CuteStringLiteral node, UglySymbolTable data) {
		// TODO Auto-generated method stub
		return null;
	}

}

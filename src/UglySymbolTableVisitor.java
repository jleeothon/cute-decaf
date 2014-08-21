
/**
  * Actually, it does more than just putting symbols into the table (bad naming, my bad),
  * it also does some control flow checking: (1) ensuring that breaks and continue's
  * are inside a for-loop, and (2) adding setting label references in for-loops and if-loops
  * and method definitions.
  *
  */
public class UglySymbolTableVisitor implements DecafVisitor {
	
	private final static boolean DEBUG = false;
	private static void printVisitorPrefix() {
		System.out.print("Ugly Symbol Table Visitor: ");
	}

	@Override
	public Object visit(SimpleNode node, UglySymbolTable data) {
		return data;
	}

	@Override
	public Object visit(CuteProgram node, UglySymbolTable data) {
		Object scope = node.childrenAccept(this, data);
		UglySymbol main = data.get("main");
		if (main != null) {
			try {
				UglySymbolType.Method mainMethodType = (UglySymbolType.Method)main.getType();
				if (DEBUG) {
					printVisitorPrefix();
					System.out.println("main type: " + mainMethodType);
				}
				// if (mainMethodType.returnType != UglySymbolType.VOID) {
				// 	UglySemanticError.complain("main method should be of void type.");
				// }
				if (!mainMethodType.params.isEmpty()) {
					UglySemanticError.complain("main method takes no parameters.");
				}
			} catch (ClassCastException e) {
				UglySemanticError.complain("Symbol \"main\" should be a method with signature () => void.");
			}
		} else {
			UglySemanticError.complain("There is no main method.");
		}
		if (DEBUG) {
			printVisitorPrefix();
			System.out.println("GLOBAL TABLE OVERVIEW");
			System.out.println(data);
		}
		return scope;
	}

	@Override
	public Object visit(CuteFieldDeclarationUnit node, UglySymbolTable data) {
		assert "This code should not be reached" == null; 
		return node.childrenAccept(this, data);
	}

	@Override
	public Object visit(CuteFieldDeclaration node, UglySymbolTable data) {
		if (DEBUG) {
			printVisitorPrefix();
			System.out.println(node.toString());
		}
		for (UglyTriplet<String, Boolean, Integer> info : node.infos) {
			if (data.containsKey(info.v)) {
				UglySemanticError.complain(
					String.format("Redefinition of %s in scope %s", info.v, data.getName())
					);
			} else {
				UglySymbol symbol = new UglySymbol(info.v, node.type, info.w, info.x);
				data.put(info.v, symbol);
				UglySymbol symbolgot = data.get(info.v);
			}
			if (info.w && info.x < 1) {
				UglySemanticError.complain(
					String.format("%s is a non-positive-sized array", info.v)
					);
			}
		}
		return node.childrenAccept(this, data);
	}

	@Override
	public Object visit(CuteMethodDeclarationParameter node, UglySymbolTable data) {
		// TODO trim this tree
		return node.childrenAccept(this, data);
	}

	@Override
	public Object visit(CuteMethodDeclaration node, UglySymbolTable data) {
		UglySymbol method = null;
		UglySymbolType.Method methodType;
		node.label = new UglyLabel();
		if (data.containsKey(node.name)) {
			UglySemanticError.complain(String.format(
					"Redeclaration of method %s in scope %s",
					node.name,
					data.getName()
					));
			return node.childrenAccept(this, data);
		}
		method = new UglySymbol(node.name, node.type);
		if (DEBUG) {
			printVisitorPrefix();
			System.out.println("node type" + node.type);
			System.out.printf("adding method: %s\n", method);
		}
		data.put(node.name, method);
		UglySymbolTable scope = new UglySymbolTable(node.name, data);
		if (DEBUG) {
			printVisitorPrefix();
			System.out.printf("Adding parameters to method %s... ", node.name);
		}
		for (UglyPair<UglySymbolType, String> pair : node.params) {
			UglySymbol symbol = new UglySymbol(pair.w, pair.v);
			if (DEBUG) {
				printVisitorPrefix();
				System.out.print(symbol);
				System.out.print(", ");
			}
			if (scope.containsKey(pair.w)) {
				UglySemanticError.complain(
						String.format(
								"Redeclaration of parameter %s in method %s",
								pair.w,
								node.name
								)
						);
			} else {
				scope.put(symbol.getName(), symbol);
			}
		}
		if (DEBUG) {
			printVisitorPrefix();
			System.out.printf("Type for method %s is %s\n", node.name, node.type);
		}
		if (DEBUG) {
			printVisitorPrefix();
			System.out.println();
			System.out.println(scope);
		}
		return node.childrenAccept(this, scope);
	}

	@Override
	public Object visit(CuteBlock node, UglySymbolTable data) {
		if (node.shouldPushScope) {
			UglySymbolTable scope = new UglySymbolTable(node.name, data);
			return node.childrenAccept(this, scope);
		} else {
			return node.childrenAccept(this, data);
		}
	}

	@Override
	public Object visit(CuteVariableDeclaration node, UglySymbolTable data) {
		if (DEBUG) {
			printVisitorPrefix();
			System.out.println("Adding variables to local scope.");
		}
		for (String name : node.names) {
			if (data.containsKey(name)) {
				UglySemanticError.complain(String.format("Redeclaration of %s in scope %s.", name, data.getName()));
			} else {
				data.put(name, new UglySymbol(name, node.type));
			}
		}
		if (DEBUG) {
			printVisitorPrefix();
			System.out.println(data.toString());
		}
		return node.childrenAccept(this, data);
	}

	@Override
	public Object visit(CuteType node, UglySymbolTable data) {
		return node.childrenAccept(this, data);
	}

	@Override
	public Object visit(CuteIfStatement node, UglySymbolTable data) {
		if (DEBUG) {
			printVisitorPrefix();
			System.out.println("if Statement");
		}
		Node expression = node.jjtGetChild(0);
		assert expression instanceof CuteExpression;
		expression.jjtAccept(this, data);

		Node ifBlock = node.jjtGetChild(1);
		assert ifBlock instanceof CuteBlock;
		ifBlock.jjtAccept(this, data);
		
		if (node.jjtGetNumChildren() == 3) {
			Node elseBlock = node.jjtGetChild(2);
			assert elseBlock instanceof CuteBlock;
			node.elseLabel = new UglyLabel();
			elseBlock.jjtAccept(this, data);
		}

		node.endIfLabel = new UglyLabel();

		assert node.jjtGetNumChildren() <= 3;
		// node.childrenAccept(this, data);
		return null;
	}

	@Override
	public Object visit(CuteForStatement node, UglySymbolTable data) {
		if (DEBUG) {
			printVisitorPrefix();
			System.out.println("for Statement");
		}
		UglySymbolTable scope = new UglySymbolTable("for-block", data);
		UglySymbol iEntry = new UglySymbol(node.iName, UglySymbolType.INTEGER);
		scope.put(node.iName, iEntry);
		node.iEntry = iEntry;
		if (DEBUG) {
			printVisitorPrefix();
			System.out.println("Adding iterator to for-loop.");
			System.out.println(scope);
		}


		node.forLabel = new UglyLabel();
		node.childrenAccept(this, scope);
		node.incrementLabel = new UglyLabel();
		node.endForLabel = new UglyLabel();

		return null;
	}

	@Override
	public Object visit(CuteStatement node, UglySymbolTable data) {
		return node.childrenAccept(this, data);
	}

	@Override
	public Object visit(CuteAssignment node, UglySymbolTable data) {
		return node.childrenAccept(this, data);
	}

	@Override
	public Object visit(CuteLocation node, UglySymbolTable data) {
		UglySymbol symbol;
		for (
				UglySymbolTable scope = data;
				scope != null;
				scope = scope.getParent()
			)
		{
			if (DEBUG) {
				printVisitorPrefix();
				System.out.printf("Looking for <%s> in scope %s\n", node.name, scope.getName());
			}
			symbol = scope.get(node.name);
			if (symbol != null) {
				if (DEBUG) {
					printVisitorPrefix();
					System.out.printf(
						"Found (location) symbol <%s>[%s] in scope %s.\n",
						node.name, symbol.isArray(), scope.getName()
						);
					System.out.printf(
						"<%s> in input isArrayAccess %s, size in table: %d\n",
						node.name,
						node.isArrayAccess,
						symbol.getSize()
						);
					System.out.printf("visiting location, add entry: %s\n", symbol);
				}
				node.entry = symbol;
				if (node.isArrayAccess && !symbol.isArray()) {
					UglySemanticError.complain(
						node.line,
						node.column,
						String.format("Variable %s was accessed as array.", node.name)
						);
				} else if (!node.isArrayAccess && symbol.isArray()) {
					UglySemanticError.complain(
						node.line,
						node.column,
						String.format("Array %s was not accessed as array.", node.name)
						);
				} else {
					//
				}
				return node.childrenAccept(this, data);
			}
		}
		UglySemanticError.complain(
			node.line,
			node.column,
			String.format("Could not find symbol %s", node.name)
		);
		return node.childrenAccept(this, data);
	}

	@Override
	public Object visit(CuteMethodCall node, UglySymbolTable data) {
		if (DEBUG) {
			printVisitorPrefix();
			StringBuilder builder = new StringBuilder("Calling method ");
			builder.append(node.name);
			builder.append("(");
			builder.append(")");
		}
		for (UglySymbolTable scope = data; scope != null; scope = scope.getParent()) {
			UglySymbol symbol = scope.get(node.name);
			if (symbol != null) {
				node.entry = symbol;
				if (DEBUG) {
					printVisitorPrefix();
					System.out.printf(
						"Found method name <%s> in scope %s\n",
						node.name,
						scope.getName()
						);
				}
				return node.childrenAccept(this, data);
			}
		}		
		UglySemanticError.complain(
			String.format(
				"Could not find (method) symbol %s near line %d, column %d.",
				node.name, node.line, node.column
				)
			);
		return node.childrenAccept(this, data);
	}

	@Override
	public Object visit(CuteMethodName node, UglySymbolTable data) {
		return node.childrenAccept(this, data);
	}

	@Override
	public Object visit(CuteExpression node, UglySymbolTable data) {
		return node.childrenAccept(this, data);
	}

	@Override
	public Object visit(CuteCalloutArgument node, UglySymbolTable data) {
		return node.childrenAccept(this, data);
	}

	@Override
	public Object visit(CuteLiteral node, UglySymbolTable data) {
		return node.childrenAccept(this, data);
	}

	@Override
	public Object visit(CuteCharLiteral node, UglySymbolTable data) {
		return node.childrenAccept(this, data);
	}

	@Override
	public Object visit(CuteIntLiteral node, UglySymbolTable data) {
		return node.childrenAccept(this, data);
	}

	@Override
	public Object visit(CuteBooleanLiteral node, UglySymbolTable data) {
		return node.childrenAccept(this, data);
	}

	@Override
	public Object visit(CuteDisjunctionExpression node, UglySymbolTable data) {
		return node.childrenAccept(this, data);
	}

	@Override
	public Object visit(CuteConjunctionExpression node, UglySymbolTable data) {
		return node.childrenAccept(this, data);
	}

	@Override
	public Object visit(CuteEqualityExpression node, UglySymbolTable data) {
		return node.childrenAccept(this, data);
	}

	@Override
	public Object visit(CuteRelationExpression node, UglySymbolTable data) {
		return node.childrenAccept(this, data);
	}

	@Override
	public Object visit(CuteTermExpression node, UglySymbolTable data) {
		return node.childrenAccept(this, data);
	}

	@Override
	public Object visit(CuteFactorExpression node, UglySymbolTable data) {
		return node.childrenAccept(this, data);
	}

	@Override
	public Object visit(CuteUnaryExpression node, UglySymbolTable data) {
		return node.childrenAccept(this, data);
	}

	@Override
	public Object visit(CuteExpressionUnit node, UglySymbolTable data) {
		return node.childrenAccept(this, data);
	}

	@Override
	public Object visit(CuteReturnStatement node, UglySymbolTable data) {
		return node.childrenAccept(this, data);
	}

	@Override
	public Object visit(CuteBreakStatement node, UglySymbolTable data) {
		this.checkContainedInFor("break", node);
		return node.childrenAccept(this, data);
	}

	@Override
	public Object visit(CuteContinueStatement node, UglySymbolTable data) {
		this.checkContainedInFor("continue", node);
		return node.childrenAccept(this, data);
	}

	private void checkContainedInFor(String statement, SimpleNode node) {
		boolean isContainedInForScope = false;
		for (
				SimpleNode currentNode = (SimpleNode)node.parent;
				currentNode != null;
				currentNode = (SimpleNode)currentNode.parent
			)
		{
			if (DEBUG) {
				printVisitorPrefix();
				System.out.printf("looking for a for-loop in node %s\n", currentNode);
			}
			if (currentNode instanceof CuteForStatement) {
				isContainedInForScope = true;
				break;
			}
		}
		if (!isContainedInForScope) {
			UglySemanticError.complain(
				String.format("%s statement must be contained within a for loop.", statement)
				);
		}
		
	}

	@Override
	public Object visit(CuteCalloutStatement node, UglySymbolTable data) {
		return node.childrenAccept(this, data);
	}

	@Override
	public Object visit(CuteStringLiteral node, UglySymbolTable data) {
		// TODO Auto-generated method stub
		return null;
	}

}

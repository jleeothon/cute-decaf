/* Generated By:JavaCC: Do not edit this line. DecafVisitor.java Version 5.0 */
public interface DecafVisitor
{
  public Object visit(SimpleNode node, UglySymbolTable data);
  public Object visit(CuteProgram node, UglySymbolTable data);
  public Object visit(CuteFieldDeclarationUnit node, UglySymbolTable data);
  public Object visit(CuteFieldDeclaration node, UglySymbolTable data);
  public Object visit(CuteMethodDeclarationParameter node, UglySymbolTable data);
  public Object visit(CuteMethodDeclaration node, UglySymbolTable data);
  public Object visit(CuteBlock node, UglySymbolTable data);
  public Object visit(CuteVariableDeclaration node, UglySymbolTable data);
  public Object visit(CuteType node, UglySymbolTable data);
  public Object visit(CuteIfStatement node, UglySymbolTable data);
  public Object visit(CuteForStatement node, UglySymbolTable data);
  public Object visit(CuteReturnStatement node, UglySymbolTable data);
  public Object visit(CuteBreakStatement node, UglySymbolTable data);
  public Object visit(CuteContinueStatement node, UglySymbolTable data);
  public Object visit(CuteStatement node, UglySymbolTable data);
  public Object visit(CuteAssignment node, UglySymbolTable data);
  public Object visit(CuteLocation node, UglySymbolTable data);
  public Object visit(CuteMethodCall node, UglySymbolTable data);
  public Object visit(CuteCalloutStatement node, UglySymbolTable data);
  public Object visit(CuteMethodName node, UglySymbolTable data);
  public Object visit(CuteExpression node, UglySymbolTable data);
  public Object visit(CuteDisjunctionExpression node, UglySymbolTable data);
  public Object visit(CuteConjunctionExpression node, UglySymbolTable data);
  public Object visit(CuteEqualityExpression node, UglySymbolTable data);
  public Object visit(CuteRelationExpression node, UglySymbolTable data);
  public Object visit(CuteTermExpression node, UglySymbolTable data);
  public Object visit(CuteFactorExpression node, UglySymbolTable data);
  public Object visit(CuteUnaryExpression node, UglySymbolTable data);
  public Object visit(CuteExpressionUnit node, UglySymbolTable data);
  public Object visit(CuteCalloutArgument node, UglySymbolTable data);
  public Object visit(CuteLiteral node, UglySymbolTable data);
  public Object visit(CuteCharLiteral node, UglySymbolTable data);
  public Object visit(CuteIntLiteral node, UglySymbolTable data);
  public Object visit(CuteBooleanLiteral node, UglySymbolTable data);
  public Object visit(CuteStringLiteral node, UglySymbolTable data);
}
/* JavaCC - OriginalChecksum=15e7d0092de792479d9e0acea0e08530 (do not edit this line) */
/* Generated By:JJTree: Do not edit this line. ASTmethodDecl.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTmethodDecl extends SimpleNode {
  public ASTmethodDecl(int id) {
    super(id);
  }

  public ASTmethodDecl(DecafParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(DecafParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=49361a296a8cace33b487a85dc6e8ae9 (do not edit this line) */

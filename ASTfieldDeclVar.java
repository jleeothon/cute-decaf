/* Generated By:JJTree: Do not edit this line. ASTfieldDeclVar.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTfieldDeclVar extends SimpleNode {
  public ASTfieldDeclVar(int id) {
    super(id);
  }

  public ASTfieldDeclVar(DecafParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(DecafParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=f77435ef1014249d28b9052389e6c434 (do not edit this line) */
/* Generated By:JJTree: Do not edit this line. ASTprogram.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTprogram extends SimpleNode {
  public ASTprogram(int id) {
    super(id);
  }

  public ASTprogram(DecafParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(DecafParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=22b79c76acc37ba6e5b3d1c66af8071e (do not edit this line) */

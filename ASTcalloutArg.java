/* Generated By:JJTree: Do not edit this line. ASTcalloutArg.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTcalloutArg extends SimpleNode {
  public ASTcalloutArg(int id) {
    super(id);
  }

  public ASTcalloutArg(DecafParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(DecafParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=667e259c015ddf3ce03f03d8107fda65 (do not edit this line) */
/* Generated By:JJTree: Do not edit this line. CuteReturnStatement.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=Cute,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class CuteReturnStatement extends SimpleNode {
  public CuteReturnStatement(int id) {
    super(id);
  }

  public CuteReturnStatement(Decaf p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(DecafVisitor visitor, UglySymbolTable data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=d70059944b63272fb29cb098b80d48ce (do not edit this line) */

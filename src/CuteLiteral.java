/* Generated By:JJTree: Do not edit this line. CuteLiteral.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=Cute,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public class CuteLiteral extends SimpleNode {

  public CuteLiteral(int id) {
    super(id);
  }

  public CuteLiteral(Decaf p, int id) {
    super(p, id);
  }

  /** Accept the visitor. **/
  public Object jjtAccept(DecafVisitor visitor, UglySymbolTable data) {
    return visitor.visit(this, data);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder(super.toString());
    builder.append(": ");
    if (this.value == null) {
      builder.append("(null ");
      builder.append(this.getClass().getName());
      builder.append(")");
    } else {
      builder.append(this.value.toString());
    }
    return builder.toString();
  }

}
/* JavaCC - OriginalChecksum=b1e585bbfb3e120196bf21cd99b47287 (do not edit this line) */

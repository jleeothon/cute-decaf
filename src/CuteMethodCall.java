import java.util.List;
import java.util.LinkedList;

/* Generated By:JJTree: Do not edit this line. CuteMethodCall.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=Cute,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class CuteMethodCall extends SimpleNode {

  String name;
  int line;
  int column;

  /**
   * To be used only when visiting.
   */
  UglySymbol entry;


  public CuteMethodCall(int id) {
    super(id);
  }

  public CuteMethodCall(Decaf p, int id) {
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
    builder.append(this.name);
    return builder.toString();
  }

}
/* JavaCC - OriginalChecksum=1c81d45bec905defeff741ebf9c66c3f (do not edit this line) */
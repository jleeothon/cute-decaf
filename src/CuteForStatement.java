/* Generated By:JJTree: Do not edit this line. CuteForStatement.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=Cute,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class CuteForStatement extends SimpleNode {

  String iName;
  UglySymbol iEntry;
  int initialValue;
  int finalValue;
  UglyLabel forLabel;
  UglyLabel endForLabel;
  UglyLabel incrementLabel;

  public CuteForStatement(int id) {
    super(id);
  }

  public CuteForStatement(Decaf p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(DecafVisitor visitor, UglySymbolTable data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=d272cea2474cf894356cb2fe318cb3ea (do not edit this line) */

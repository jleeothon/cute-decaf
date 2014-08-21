import java.util.ArrayList;
import java.util.List;

/* Generated By:JJTree: Do not edit this line. CuteVariableDeclaration.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=Cute,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public class CuteVariableDeclaration extends SimpleNode {

  public UglySymbolType type;
  public List<String> names;
	
  public CuteVariableDeclaration(int id) {
    super(id);
    names = new ArrayList<String>();
  }

  public CuteVariableDeclaration(Decaf p, int id) {
    super(p, id);
    names = new ArrayList<String>();
  }

  /** Accept the visitor. **/
  public Object jjtAccept(DecafVisitor visitor, UglySymbolTable data) {
    return visitor.visit(this, data);
  }
  
  @Override
  public String toString() {
	  StringBuilder builder = new StringBuilder(super.toString());
    builder.append(": ");
    builder.append(type.toString());
    builder.append(": [");
	  for (String name : this.names) {
      builder.append(name);
		  builder.append(", ");
	  }
    builder.delete(builder.length() - 2, builder.length());
    builder.append("]");
	  return builder.toString();
  }
  
}
/* JavaCC - OriginalChecksum=d61031d3cbb6c0f8fd21b27969cee91c (do not edit this line) */

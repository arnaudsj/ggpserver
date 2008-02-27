package tud.gamecontroller.term;

public abstract class AbstractTerm implements TermInterface {

	private String kifForm=null;
	private String prefixForm=null;
	
	public boolean isCompound() {
		return !isConstant() && !isVariable();
	}
	
	public String getKIFForm() {
		if(kifForm==null){
			StringBuilder s=new StringBuilder();
			if(!isCompound()){
				s.append(getName().toUpperCase());
			}else{
				s.append("(");
				s.append(getName().toUpperCase());
				for(TermInterface arg:getArgs()){
					s.append(" ");
					s.append(arg.getKIFForm());
				}
				s.append(")");
			}
			kifForm=s.toString();
		}
		return kifForm;
	}

	public String getPrefixForm() {
		if(prefixForm==null){
			StringBuilder s=new StringBuilder();
			if(!isCompound()){
				s.append(getName().toLowerCase());
			}else{
				s.append(getName().toLowerCase());
				s.append("(");
				boolean first=true;
				for(TermInterface arg:getArgs()){
					if(!first){
						s.append(",");
					}else{
						first=false;
					}
					s.append(arg.getPrefixForm());
				}
				s.append(")");
			}
			prefixForm=s.toString();
		}
		return prefixForm;
	}

	public boolean equals(Object obj) {
		if (obj instanceof TermInterface) {
			TermInterface t = (TermInterface) obj;
			return getKIFForm().equals(t.getKIFForm());
		}else{
			return false;
		}
	}

	public int hashCode() {
		return getKIFForm().hashCode();
	}

}

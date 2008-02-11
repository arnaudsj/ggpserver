package tud.gamecontroller.game;

public abstract class AbstractTerm implements TermInterface {

	public boolean isCompound() {
		return !isConstant() && !isVariable();
	}
	
	public String getKIFForm() {
		String s=null;
		if(!isCompound()){
			s=getName().toUpperCase();
		}else{
			s="("+getName().toUpperCase();
			for(TermInterface arg:getArgs()){
				s+=" "+arg.getKIFForm();
			}
			s+=")";
		}
		return s;
	}

	public String getPrefixForm() {
		String s=null;
		if(!isCompound()){
			s=getName().toLowerCase();
		}else{
			s=getName().toLowerCase()+"(";
			boolean first=true;
			for(TermInterface arg:getArgs()){
				if(!first){
					s+=",";
				}else{
					first=false;
				}
				s+=arg.getPrefixForm();
			}
			s+=")";
		}
		return s;
	}

}

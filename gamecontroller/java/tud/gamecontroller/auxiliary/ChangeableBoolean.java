package tud.gamecontroller.auxiliary;

public class ChangeableBoolean{
	private boolean value;
	public ChangeableBoolean(boolean value){
		this.value=value;
	}
	public void setTrue(){
		this.value=true;
	}
	public void setFalse(){
		this.value=false;
	}
	public boolean isTrue(){
		return value;
	}
}
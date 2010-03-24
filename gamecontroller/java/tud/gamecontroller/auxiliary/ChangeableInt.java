package tud.gamecontroller.auxiliary;

public class ChangeableInt{
	private int value;
	public ChangeableInt(int value){
		this.value=value;
	}
	public void setValue(int value){
		this.value=value;
	}
	public int getValue(){
		return value;
	}
}
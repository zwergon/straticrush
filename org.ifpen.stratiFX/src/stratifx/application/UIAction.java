package stratifx.application;

public class UIAction {
	
	final public static int ZoomOneOne = 1;
	final public static int DummyDraw  = 2;
	
	int type;
	
	public UIAction( int type ) {
		this.type = type;
	}
	
	public int getType(){
		return type;
	}
}

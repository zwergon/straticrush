package stratifx.application;

public class UIAction {
	
	final public static int ZoomOneOne = 1;
	final public static int Open       = 2;
	
	
	final public static int Last       = Open + 1;
	
	int type;
	
	public UIAction( int type ) {
		this.type = type;
	}
	
	public int getType(){
		return type;
	}
}

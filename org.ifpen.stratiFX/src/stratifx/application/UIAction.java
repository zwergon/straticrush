package stratifx.application;

public class UIAction {
	
	final public static int ZoomOneOne = 1;
	final public static int Open       = 2;
	
	
	final public static int Last       = Open + 1;
	
	public static final int Interaction = UIAction.Last+1;
	public static final int Properties  = UIAction.Last+2;
	

	
	int type;
	
	public UIAction( int type ) {
		this.type = type;
	}
	
	public int getType(){
		return type;
	}
}

package no.geosoft.cc.graphics;

public class GEvent {
	
	public static final int NONE                 = 0;
	public static final int ABORT                = 1;
	
	public static final int FOCUS_IN             = 15;
	public static final int FOCUS_OUT            = 16;    
	
	public int type;

	public GEvent( int type ){
		this.type = type;
	}

	public GEvent(){
		this.type = NONE;

	}
}

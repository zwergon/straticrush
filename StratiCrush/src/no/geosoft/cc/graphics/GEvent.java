package no.geosoft.cc.graphics;

public class GEvent {
	
	public static final int NONE                 = 0;
	public static final int ABORT                = 1;
	public static final int MOTION               = 2;

	public static final int BUTTON1_DOWN         = 3;
	public static final int BUTTON1_DRAG         = 4;
	public static final int BUTTON1_UP           = 5;
	public static final int BUTTON1_DOUBLE_CLICK = 6;   // TODO

	public static final int BUTTON2_DOWN         = 7;
	public static final int BUTTON2_DRAG         = 8;
	public static final int BUTTON2_UP           = 9;
	public static final int BUTTON2_DOUBLE_CLICK = 10;  // TODO

	public static final int BUTTON3_DOWN         = 11;
	public static final int BUTTON3_DRAG         = 12;
	public static final int BUTTON3_UP           = 13;
	public static final int BUTTON3_DOUBLE_CLICK = 14;  // TODO

	public static final int FOCUS_IN             = 15;
	public static final int FOCUS_OUT            = 16;    

	public static final int WHEEL_MOUSE_UP       = 17;
	public static final int WHEEL_MOUSE_DOWN     = 18;
	
	public static final int CONTEXT_MENU         = 19;
	
	public static final int ALT_DOWN_MASK        = 0x1;
	public static final int CTRL_DOWN_MASK       = 0x2;
	public static final int META_DOWN_MASK       = 0x4;
	public static final int ALTGR_DOWN_MASK      = 0x8;
	
	
	public int type;
	public int modifier;
	public int x;
	public int y;
	
	public void setModifier( int MASK, boolean set ){
		if (set){
			modifier |= MASK;
		}
		else {
			modifier = modifier & ~MASK;
		}
	}
	
	public boolean isAltDown(){
		return ( modifier & ALT_DOWN_MASK ) == ALT_DOWN_MASK;
	}
	
	public boolean isCtrlDown(){
		return ( modifier & CTRL_DOWN_MASK ) == CTRL_DOWN_MASK;
	}
	
	public boolean isMetaDown(){
		return ( modifier & META_DOWN_MASK ) == META_DOWN_MASK;
	}
	
	public GEvent( int type, int x, int y ){
		this.type = type;
		this.modifier = NONE;
		this.x = x;
		this.y = y;
	}
	
	public GEvent( int x, int y ){
		this.type = NONE;
		this.modifier = NONE;
		this.x = x;
		this.y = y;
	}


}

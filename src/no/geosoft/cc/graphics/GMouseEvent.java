package no.geosoft.cc.graphics;

public class GMouseEvent extends GEvent {
	
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

	public static final int WHEEL_MOUSE_UP       = 17;
	public static final int WHEEL_MOUSE_DOWN     = 18;
	
	public static final int CONTEXT_MENU         = 19;
	
	public static final int ALT_DOWN_MASK        = 0x1;
	public static final int CTRL_DOWN_MASK       = 0x2;
	public static final int SHIFT_DOWN_MASK       = 0x4;
	public static final int ALTGR_DOWN_MASK      = 0x8;
	
	

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
		return ( modifier & SHIFT_DOWN_MASK ) == SHIFT_DOWN_MASK;
	}
	
	public GMouseEvent( int type, int x, int y ){
		super( type );
		this.modifier = NONE;
		this.x = x;
		this.y = y;
	}
	
	public GMouseEvent( int x, int y ){
		super( NONE );
		this.modifier = NONE;
		this.x = x;
		this.y = y;
	}


}

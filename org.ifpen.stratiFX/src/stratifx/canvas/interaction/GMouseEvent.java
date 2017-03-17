/* 
 * Copyright 2017 lecomtje.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package stratifx.canvas.interaction;

public class GMouseEvent extends GEvent {
	
	public static final int MOTION               = 2;

	public static final int BUTTON_DOWN         = 3;
	public static final int BUTTON_DRAG         = 4;
	public static final int BUTTON_UP           = 5;
	public static final int BUTTON_DOUBLE_CLICK = 6;   // TODO

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
	public int button;
	
	public static final int BUTTON_1 = 1;
	public static final int BUTTON_2 = 2;
	public static final int BUTTON_3 = 3;
	
	
	
	public void setModifier( int MASK, boolean set ){
		if (set){
			modifier |= MASK;
		}
		else {
			modifier = modifier & ~MASK;
		}
	}
	
	public int getButton() {
		return button;
	}

	public void setButton(int button) {
		this.button = button;
	}

	public boolean isAltDown(){
		return ( modifier & ALT_DOWN_MASK ) == ALT_DOWN_MASK;
	}
	
	public boolean isCtrlDown(){
		return ( modifier & CTRL_DOWN_MASK ) == CTRL_DOWN_MASK;
	}
	
	public boolean isShiftDown(){
		return ( modifier & SHIFT_DOWN_MASK ) == SHIFT_DOWN_MASK;
	}
	
	public GMouseEvent( int type, int button, int x, int y, int modifier ){
		super( type );
		this.modifier = NONE;
		this.button = button;
		this.x = x;
		this.y = y;
	}
	
	public GMouseEvent( int type, int x, int y ){
		this( type, BUTTON_1, x, y, 0 );
	}
	
	public GMouseEvent( int x, int y ){
		this( NONE, BUTTON_1, x, y, 0 );
	}
	
	@Override
	public String toString(){
		final StringBuilder sb = new StringBuilder("MouseEvent [");

        sb.append("eventType = ").append(getType());

        sb.append(", x = ").append(x).append(", y = ").append(y);

        
        sb.append(", button = ").append(getButton());
        
        if (isShiftDown()) {
            sb.append(", shiftDown");
        }
        if (isCtrlDown()) {
            sb.append(", controlDown");
        }
        if (isAltDown()) {
            sb.append(", altDown");
        }
       
        return sb.append("]").toString();
	}


}

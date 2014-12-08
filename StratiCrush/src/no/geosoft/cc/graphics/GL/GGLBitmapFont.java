package no.geosoft.cc.graphics.GL;

import no.geosoft.cc.interfaces.IFontImpl;

import com.jogamp.opengl.util.gl2.GLUT;

public class GGLBitmapFont implements IFontImpl {
	
	
	private int glut_type_;
	
	
	public GGLBitmapFont() {
		this( GLUT.BITMAP_8_BY_13 );
	}


	public GGLBitmapFont( int type ) {
		glut_type_ = type;
		switch( type ){
		default:
		case GLUT.BITMAP_8_BY_13:
			break;
		case GLUT.BITMAP_9_BY_15:
			break;
		case GLUT.BITMAP_HELVETICA_10:
			break;
		case GLUT.BITMAP_HELVETICA_12:
			break;
		case GLUT.BITMAP_HELVETICA_18:
			break;
		}
	}
	
	public int getType(){
		return glut_type_;
	}

	

}

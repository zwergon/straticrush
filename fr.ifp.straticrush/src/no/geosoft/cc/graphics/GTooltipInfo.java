package no.geosoft.cc.graphics;

public class GTooltipInfo {
	
	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
	
	public void setXY( int x, int y ){
		this.x = x;
		this.y = y;
	}
	
	public int getX(){
		return x;
	}

	public int getY(){
		return y;
	}
	
	
	String info;
	
	int x,y;

}

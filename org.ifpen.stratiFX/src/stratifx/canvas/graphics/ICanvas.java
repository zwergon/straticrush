package stratifx.canvas.graphics;

public interface ICanvas {
	
	public void refresh();
	
	public void setClipArea(GRegion damageRegion);
	public void clear(GRect extent);
		
	public void render ( GSegment segment, GStyle style );
	public void render (GText text, GStyle style);
	public void render (GImage image);
	public void render (int[] x, int[] y, GImage image);
	
	public GRect getStringBox (String string, GFont font);
	
	
	  
}

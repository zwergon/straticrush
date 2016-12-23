package stratifx.application.views;

import java.awt.Color;
import java.util.Iterator;
import java.util.Random;

import fr.ifp.kronosflow.controllers.events.IControllerEvent;
import fr.ifp.kronosflow.geology.BodyFeature;
import fr.ifp.kronosflow.geology.StratigraphicUnit;
import fr.ifp.kronosflow.mesh.Cell;
import fr.ifp.kronosflow.mesh.IMeshProvider;
import fr.ifp.kronosflow.mesh.Mesh2D;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.polyline.ICurviPoint;
import fr.ifp.kronosflow.polyline.PolyLine;
import fr.ifp.kronosflow.uids.IHandle;
import stratifx.canvas.graphics.GColor;
import stratifx.canvas.graphics.GImage;
import stratifx.canvas.graphics.GObject;
import stratifx.canvas.graphics.GPosition;
import stratifx.canvas.graphics.GSegment;
import stratifx.canvas.graphics.GStyle;
import stratifx.canvas.graphics.GText;


public class GPatchView extends GView {
	
	GSegment border = null;
	Mesh2D mesh = null;
	
	public GPatchView(){
	}
	
	@Override
	public void setModel (Object object)
	{
		setUserData( object );
	
		Patch patch = (Patch)object;
		
		createGSegments(patch);
		
		if ( patch instanceof IMeshProvider ){
			
			GColor fgColor = getPatchColor();
			mesh = ((IMeshProvider)patch).getMesh();
			
			for( IHandle handle : mesh.getCells() ){
				addCell( (Cell)handle, fgColor );
			}
		}
		
	}
	
	public GSegment getBorder(){
		return border;
	}
	
	
	public GColor getPatchColor() {

		Patch patch = (Patch)getObject();

		GColor patchColor = null;
		BodyFeature feature = patch.getGeologicFeaturesByClass(StratigraphicUnit.class);

		if (feature != null) {
			Color acolor = feature.getColor();
			patchColor = new GColor(acolor.getRed(), acolor.getGreen(), acolor.getBlue(), acolor.getAlpha() );
		}
		else {
			patchColor = getRandomPastelColor();
		}

		return patchColor;
	}
	
	private static GColor getRandomPastelColor() {
		Random r = new Random();
		return GColor.getHSBColor(r.nextFloat(), (float) (0.1 + 0.2 * r.nextFloat()),
				(float) (0.3 + 0.5 * r.nextFloat()));
	}


	protected void createGSegments(Patch patch) {
		
		if ( null != patch.getBorder() ){
			
			GStyle style = new GStyle();
			style.setForegroundColor ( GColor.black );
			GColor color = getPatchColor();
			style.setBackgroundColor(  color );
			style.setLineWidth (1);
			setStyle( style );
			
			border = addPolyLine( patch.getBorder()  );
			
			GStyle textStyle = new GStyle();
			textStyle.setForegroundColor (new GColor (100, 100, 150));
			textStyle.setBackgroundColor (null);
			int offset = 0;
			
			Iterator<ICurviPoint> itr = patch.getBorder().iterator();
			while( itr.hasNext() ){
				itr.next();
				GText text = new GText (String.valueOf(offset), GPosition.FIRST | GPosition.STATIC );
				text.setPositionOffset( offset++ );
				
				text.setStyle (textStyle);
				border.addText(text);
			}
			
			GStyle symbolStyle = new GStyle();
			symbolStyle.setForegroundColor (new GColor (0, 0, 255));
			symbolStyle.setBackgroundColor (new GColor (0, 0, 255));
			GImage square = new GImage (GImage.SYMBOL_SQUARE1);
			square.setStyle (symbolStyle);

			border.setVertexImage (square);
		}
		
		setVisibility( GObject.DATA_VISIBLE | GObject.ANNOTATION_INVISIBLE | GObject.SYMBOLS_INVISIBLE );
	}
	
	protected GSegment addPolyLine( PolyLine line) {
		
		GSegment gline = new GPolyline( line );
		addSegment(gline);
		
		return gline;
	}
	
	
	public void draw()
	{
		updateGeometry();
	}


	public void updateGeometry() {
		
		if ( border == null ){
			return;
		}
	
		for( Object segment : getSegments() ){
			if ( segment instanceof IUpdateGeometry ){
				((IUpdateGeometry)segment).updateGeometry();
			}
		}
	}
	
	public Patch getObject(){
		return (Patch)getUserData();
	}
	
	@Override
	public void modelChanged( IControllerEvent<?> event ) {	
		 updateGeometry();
	}

	private GSegment addCell(Cell cell, GColor fgColor ) {
		
		GSegment gcell = new GCell( mesh, cell );
		
		GStyle style = new GStyle();
		style.setForegroundColor ( GColor.black );
		style.setBackgroundColor ( null );
		//style.setFillPattern(GStyle.FILL_NONE);
		style.setLineWidth (1);
		gcell.setStyle (style);
		
		addSegment(gcell);
		
		return gcell;
	}
	
	
	

	

}

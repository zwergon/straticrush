package straticrush.view;

import java.awt.Color;
import java.util.Iterator;
import java.util.Random;

import no.geosoft.cc.graphics.GColor;
import no.geosoft.cc.graphics.GImage;
import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GPosition;
import no.geosoft.cc.graphics.GScene;
import no.geosoft.cc.graphics.GSegment;
import no.geosoft.cc.graphics.GStyle;
import no.geosoft.cc.graphics.GText;
import fr.ifp.kronosflow.controller.IControllerEvent;
import fr.ifp.kronosflow.geology.BodyFeature;
import fr.ifp.kronosflow.geology.StratigraphicUnit;
import fr.ifp.kronosflow.mesh.Cell;
import fr.ifp.kronosflow.mesh.IMeshProvider;
import fr.ifp.kronosflow.mesh.Mesh2D;
import fr.ifp.kronosflow.mesh.Node;
import fr.ifp.kronosflow.model.CompositePatch;
import fr.ifp.kronosflow.model.ICurviPoint;
import fr.ifp.kronosflow.model.IHandle;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PolyLine;
import fr.ifp.kronosflow.utils.LOGGER;
import fr.ifp.kronosflow.utils.UID;


public class PatchView extends View {
	
	GSegment border = null;
	Mesh2D mesh = null;
	
	public PatchView(){
		
	}
	
	@Override
	public void setUserData (Object object)
	{
		super.setUserData( object );
	
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
		
		/*for( PolyLine line : patch.getLines() ){
			addPolyLine( line, GColor.green );
		}*/
		
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
			if ( segment instanceof GPolyline ){
				((GPolyline)segment).updateGeometry();
			}
			else if ( segment instanceof GCell ){

				GCell gcell = (GCell)segment;
				Cell cell = gcell.getCell();

				UID[] nodes = cell.getNodeIds();

				int npts = nodes.length+1;
				double[] xpts = new double[npts];
				double[] ypts = new double[npts];

				for( int i=0; i<npts; i++){
					Node node = (Node) mesh.getNode( cell.getNodeId(i % nodes.length) );
					xpts[i] = node.x();
					ypts[i] = node.y();
				}	
				gcell.setGeometry(xpts, ypts);
			}
		}
		
		
	}
	
	public Patch getObject(){
		return (Patch)getUserData();
	}
	
	@Override
	public void objectChanged( IControllerEvent<?> event ) {
		
		GScene scene = getScene();
		
		synchronized(scene) {
			if ( event.getObject() instanceof CompositePatch ){
				CompositePatch composite = (CompositePatch)event.getObject();
				for( Patch patch : composite.getPatchs() ){
					if ( patch == getObject() ){
						updateGeometry();
						return;
					}
				}
			}
			if ( event.getObject() == getObject() ){
				updateGeometry();
			}
		}
		
	}
	
	
	private class GCell extends GSegment {
		public GCell( Cell cell ){
			cell_ = cell;
		}
		
		public Cell getCell(){
			return cell_;
		}
		
		private Cell cell_;
	};
	
	

	private GSegment addCell(Cell cell, GColor fgColor ) {
		
		GSegment gcell = new GCell( cell );
		
		
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

package stratifx.application.views;

import java.awt.Color;
import java.util.Random;

import fr.ifp.kronosflow.controllers.events.IControllerEvent;
import fr.ifp.kronosflow.geology.BodyFeature;
import fr.ifp.kronosflow.geology.StratigraphicUnit;
import fr.ifp.kronosflow.mesh.Cell;
import fr.ifp.kronosflow.mesh.IMeshProvider;
import fr.ifp.kronosflow.mesh.Mesh2D;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.uids.IHandle;
import stratifx.canvas.graphics.GColor;
import stratifx.canvas.graphics.GObject;
import stratifx.canvas.graphics.GSegment;
import stratifx.canvas.graphics.GStyle;


public class GPatchView extends GView {
	
	GObject border = null;
	Mesh2D mesh = null;
	
	public GPatchView(){
	}
	
	@Override
	public void setModel (Object object)
	{
		setUserData( object );
	
		Patch patch = (Patch)object;
		
		createBorder(patch);
		
		if ( patch instanceof IMeshProvider ){
			
			GColor fgColor = getPatchColor();
			mesh = ((IMeshProvider)patch).getMesh();
			
			for( IHandle handle : mesh.getCells() ){
				addCell( (Cell)handle, fgColor );
			}
		}
		
	}
	
	public GObject getBorder(){
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


	protected void createBorder( Patch patch ) {
		
		if ( null != patch.getBorder() ){
			border = new GPolyline( patch.getBorder()  );
			
			GStyle style = new GStyle();
			style.setBackgroundColor( getPatchColor() );
			style.setForegroundColor( GColor.black );
			border.setStyle( style );
			
			add(border);
		}
			
	}
	
	public Patch getObject(){
		return (Patch)getUserData();
	}
	
	@Override
	public void modelChanged( IControllerEvent<?> event ) {	
		 redraw();
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

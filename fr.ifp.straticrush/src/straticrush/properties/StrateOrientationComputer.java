package straticrush.properties;

import java.util.ArrayList;
import java.util.List;

import fr.ifp.kronosflow.controllers.property.PropertyComputer;
import fr.ifp.kronosflow.geology.StratigraphicEvent;
import fr.ifp.kronosflow.geometry.Point2D;
import fr.ifp.kronosflow.mesh.Cell;
import fr.ifp.kronosflow.mesh.IMeshProvider;
import fr.ifp.kronosflow.mesh.Mesh2D;
import fr.ifp.kronosflow.model.AngularSector;
import fr.ifp.kronosflow.model.FeatureGeolInterval;
import fr.ifp.kronosflow.model.ICurviPoint;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchLibrary;
import fr.ifp.kronosflow.model.PolyLine;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.property.IPropertyAccessor;
import fr.ifp.kronosflow.property.Property;
import fr.ifp.kronosflow.property.PropertyDB;
import fr.ifp.kronosflow.property.PropertyDouble;
import fr.ifp.kronosflow.property.PropertyInfo;
import fr.ifp.kronosflow.property.PropertyStyle;
import fr.ifp.kronosflow.property.PropertyInfo.Kind;
import fr.ifp.kronosflow.property.PropertyInfo.Support;
import fr.ifp.kronosflow.utils.UID;
import fr.ifp.kronosflow.warp.Displacement;
import fr.ifp.kronosflow.warp.MLSAffineWarp;
import fr.ifp.kronosflow.warp.Warp;

public class StrateOrientationComputer extends PropertyComputer {
	
		Warp warp; 
	
	protected StrateOrientationComputer(Section section) {
		super(section);
		
	}
	
	static public class Builder implements PropertyComputer.Builder {
		@Override
		public PropertyComputer create( Section section ) {
			return new StrateOrientationComputer(section);
		}
	}
	
	@Override
	public Property compute() {
		PatchLibrary patchLib = section.getPatchLibrary();
		
		PropertyDB propertyDB = section.getPropertyDB();
		
		
		
		PropertyInfo pinfo =  new PropertyInfo( "Strate Orientation", Support.CellProperty, Kind.Real );
		
		Property surfaceProp = propertyDB.findProperty( pinfo );
		if ( null == surfaceProp ){
			surfaceProp = new Property(pinfo);
			propertyDB.addProperty(surfaceProp);
		}
		
		IPropertyAccessor accessor = surfaceProp.getAccessor();
		warp=new MLSAffineWarp();
		warp.setDisplacements(computeDisplacement(patchLib.getPatches()));
		for( Patch patch : patchLib.getPatches() ){
			if ( patch instanceof IMeshProvider ){
				computeUsingMesh( ((IMeshProvider)patch).getMesh(), accessor );
			}
			else {
				computeUsingPatch( patch, accessor );
			}
		}
		
		PropertyStyle propStyle = new PropertyStyle(section.getStyle());
		propStyle.setCurrent(surfaceProp);
		
		
		return surfaceProp;
		
	}
	
	
	private List<Displacement> computeDisplacement(List<Patch> patchList) {
		Displacement disp;
		List<Displacement> displacements=new ArrayList<Displacement>();
		for (Patch patch : patchList) {
			List<FeatureGeolInterval> features = patch.findObjects(FeatureGeolInterval.class);
			for (FeatureGeolInterval featureGeolInterval : features) {
				if(featureGeolInterval.getInterval().getFeature() instanceof StratigraphicEvent){
					List<Point2D> points = featureGeolInterval.getInterval().getPoints2D();
					for (int i = 0; i < points.size()-1; i++) {
						Point2D pA=points.get(i);
						Point2D pB=points.get(i+1);
						
						if(pA.x()<pB.x()){
							 disp=new Displacement(pA.getPosition(), pB.getPosition());
						}else{
							disp=new Displacement(pB.getPosition(), pA.getPosition());
						}
						 displacements.add(disp);
					} 
				}
			}
		}	
		return displacements;
	}


	private void computeUsingPatch( Patch patch, IPropertyAccessor accessor ) {
		
		PolyLine border = patch.getBorder();
	
		for( ICurviPoint cp : border.getPoints() ){
			
			double phi = 0;//porosity.getPorosity( border.getPosition(cp) );
			
			accessor.setValue( cp.getUID(), new PropertyDouble(  phi ) );	
		}
		
	}


	private void computeUsingMesh( Mesh2D mesh, IPropertyAccessor accessor ) {	
		Point2D pt=new Point2D();
		for( UID uid : mesh.getCellIds() ){
			
			Cell cell = (Cell)mesh.getCell(uid);
			double[] src = cell.barycenter(mesh);
			double[] dst=new double[2];
			warp.getDeformed(src, dst);
			pt.setPosition(dst);
			pt.substract(src);
			double angle=AngularSector.angle(pt) ;
			
			accessor.setValue( uid, new PropertyDouble( angle ) );	
		}
	}
	


}

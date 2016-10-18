package straticrush.properties;

import fr.ifp.kronosflow.controllers.property.PropertyComputer;
import fr.ifp.kronosflow.geometry.Point2D;
import fr.ifp.kronosflow.mesh.IGeometryProvider;
import fr.ifp.kronosflow.mesh.IMeshProvider;
import fr.ifp.kronosflow.mesh.Mesh2D;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchCell;
import fr.ifp.kronosflow.model.PatchLibrary;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.polyline.ICurviPoint;
import fr.ifp.kronosflow.polyline.PolyLine;
import fr.ifp.kronosflow.property.IPropertyAccessor;
import fr.ifp.kronosflow.property.Property;
import fr.ifp.kronosflow.property.PropertyDB;
import fr.ifp.kronosflow.property.PropertyInfo;
import fr.ifp.kronosflow.property.PropertyInfo.Kind;
import fr.ifp.kronosflow.property.PropertyInfo.Support;
import fr.ifp.kronosflow.property.PropertyStyle;
import fr.ifp.kronosflow.property.PropertyVector;
import fr.ifp.kronosflow.uids.UID;

public class XYPropertyComputer extends PropertyComputer {

	protected XYPropertyComputer(Section section) {
		super(section);
	}
	
	static public class Builder implements PropertyComputer.Builder {
		@Override
		public PropertyComputer create( Section section ) {
			return new XYPropertyComputer(section);
		}
	}
	
	@Override
	public Property compute() {
		PatchLibrary patchLib = section.getPatchLibrary();
		
		PropertyDB propertyDB = section.getPropertyDB();
		
		PropertyInfo pinfo =  new PropertyInfo( "XY", Support.NodeProperty, Kind.Vector );
		
		Property surfaceProp = propertyDB.findProperty( pinfo );
		if ( null == surfaceProp ){
			surfaceProp = new Property(pinfo);
			propertyDB.addProperty(surfaceProp);
		}
		
		IPropertyAccessor accessor = surfaceProp.getAccessor();
		
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
	
	
	private void computeUsingPatch( Patch patch, IPropertyAccessor accessor ) {
		
		accessor.addHandle( new PatchCell(patch) );
		PolyLine border = patch.getBorder();
	
		for( ICurviPoint cp : border.getPoints() ){
			Point2D pt = border.getPosition(cp);
			accessor.setValue( cp.getUID(), new PropertyVector(pt.getPosition()));
		}
		
	}


	private void computeUsingMesh( Mesh2D mesh, IPropertyAccessor accessor ) {	
		
		accessor.addMesh( mesh );
		
		IGeometryProvider provider = mesh.getGeometryProvider();
		
		for( UID uid : mesh.getNodeIds() ){
			accessor.setValue( uid, new PropertyVector( provider.getPosition(uid)) );	
		}
	}


	@Override
	public Property compute(Patch patchToCompute) {
		
		PropertyDB propertyDB = section.getPropertyDB();
		
		PropertyInfo pinfo =  new PropertyInfo( "XY", Support.NodeProperty, Kind.Vector );
		
		Property surfaceProp = propertyDB.findProperty( pinfo );
		if ( null == surfaceProp ){
			surfaceProp = new Property(pinfo);
			propertyDB.addProperty(surfaceProp);
		}
		
		IPropertyAccessor accessor = surfaceProp.getAccessor();
		
		if ( patchToCompute instanceof IMeshProvider ){
			computeUsingMesh( ((IMeshProvider)patchToCompute).getMesh(), accessor );
		}
		else {
			computeUsingPatch( patchToCompute, accessor );
		}
		
		
		PropertyStyle propStyle = new PropertyStyle(section.getStyle());
		propStyle.setCurrent(surfaceProp);
		
		
		return surfaceProp;
	}
	


}

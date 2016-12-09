package straticrush.properties;

import fr.ifp.kronosflow.controllers.property.PropertyComputer;
import fr.ifp.kronosflow.geometry.Point2D;
import fr.ifp.kronosflow.mesh.IGeometryProvider;
import fr.ifp.kronosflow.mesh.IMeshProvider;
import fr.ifp.kronosflow.mesh.Mesh2D;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchLibrary;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.model.style.PropertyStyle;
import fr.ifp.kronosflow.polyline.ICurviPoint;
import fr.ifp.kronosflow.polyline.Node;
import fr.ifp.kronosflow.polyline.PolyLine;
import fr.ifp.kronosflow.property.IPropertyAccessor;
import fr.ifp.kronosflow.property.Property;
import fr.ifp.kronosflow.property.PropertyDB;
import fr.ifp.kronosflow.property.PropertyInfo;
import fr.ifp.kronosflow.property.PropertyInfo.Kind;
import fr.ifp.kronosflow.property.PropertyInfo.Support;
import fr.ifp.kronosflow.property.PropertyLocation;
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
			surfaceProp = propertyDB.createProperty(pinfo);
		}
		
		IPropertyAccessor accessor = surfaceProp.getAccessor();
		
		for( Patch patch : patchLib.getPatches() ){
			if ( patch instanceof IMeshProvider ){
				computeUsingMesh( patch, accessor );
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
		
		PolyLine border = patch.getBorder();
	
		for( ICurviPoint cp : border.getPoints() ){
			Point2D pt = border.getPosition(cp);
			
			PropertyLocation location = new PropertyLocation( patch, pt.getPosition() );
			accessor.setValue( location, pt.getPosition() );
		}
		
		
	}


	private void computeUsingMesh( Patch patch, IPropertyAccessor accessor ) {	
		
		
		Mesh2D mesh = ((IMeshProvider)patch).getMesh();
		
		IGeometryProvider provider = mesh.getGeometryProvider();
		
		for( UID uid : mesh.getNodeIds() ){
			Node node = (Node)mesh.getNode(uid);
			node.setPropertyDomain(patch);
			double[] xy = provider.getPosition(uid);
			PropertyLocation location = new PropertyLocation( node.getPropertyDomain(), xy );
			accessor.setValue( location,  xy  );	
		}
		
	}


	@Override
	public Property compute(Patch patchToCompute) {
		
		PropertyDB propertyDB = section.getPropertyDB();
		
		PropertyInfo pinfo =  new PropertyInfo( "XY", Support.NodeProperty, Kind.Vector );
		
		Property surfaceProp = propertyDB.findProperty( pinfo );
		if ( null == surfaceProp ){
			surfaceProp = propertyDB.createProperty(pinfo);
		}
		
		IPropertyAccessor accessor = surfaceProp.getAccessor();
		
		if ( patchToCompute instanceof IMeshProvider ){
			computeUsingMesh( patchToCompute, accessor );
		}
		else {
			computeUsingPatch( patchToCompute, accessor );
		}
		
		
		PropertyStyle propStyle = new PropertyStyle(section.getStyle());
		propStyle.setCurrent(surfaceProp);
		
		
		return surfaceProp;
	}
	


}

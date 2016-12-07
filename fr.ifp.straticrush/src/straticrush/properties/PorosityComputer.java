package straticrush.properties;

import fr.ifp.jdeform.decompaction.Porosity;
import fr.ifp.kronosflow.controllers.property.PropertyComputer;
import fr.ifp.kronosflow.geometry.Point2D;
import fr.ifp.kronosflow.mesh.IGeometryProvider;
import fr.ifp.kronosflow.mesh.IMeshProvider;
import fr.ifp.kronosflow.mesh.Mesh2D;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchLibrary;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.polyline.ICurviPoint;
import fr.ifp.kronosflow.polyline.Node;
import fr.ifp.kronosflow.polyline.PolyLine;
import fr.ifp.kronosflow.property.IPropertyAccessor;
import fr.ifp.kronosflow.property.Property;
import fr.ifp.kronosflow.property.PropertyDB;
import fr.ifp.kronosflow.property.PropertyInfo;
import fr.ifp.kronosflow.property.PropertyInfo.Kind;
import fr.ifp.kronosflow.property.PropertyInfo.Support;
import fr.ifp.kronosflow.property.PropertyStyle;
import fr.ifp.kronosflow.property.PropertyValue;
import fr.ifp.kronosflow.uids.UID;

public class PorosityComputer extends PropertyComputer {
	
	
	Porosity porosity;
	
	
	protected PorosityComputer(Section section) {
		super(section);
		PatchLibrary patchLib = section.getPatchLibrary();
		porosity = new Porosity( patchLib.getPaleobathymetry() );
		
	}
	
	static public class Builder implements PropertyComputer.Builder {
		@Override
		public PropertyComputer create( Section section ) {
			return new PorosityComputer(section);
		}
	}
	
	@Override
	public Property compute() {
		PatchLibrary patchLib = section.getPatchLibrary();
		
		PropertyDB propertyDB = section.getPropertyDB();
		
		PropertyInfo pinfo =  new PropertyInfo( "Porosity", Support.NodeProperty, Kind.Real );
		
		Property surfaceProp = propertyDB.findProperty( pinfo );
		if ( null == surfaceProp ){
			surfaceProp = propertyDB.createProperty(pinfo);
		}
		
		IPropertyAccessor accessor = surfaceProp.getAccessor();
		
		for( Patch patch : patchLib.getPatches() ){
			if ( patch instanceof IMeshProvider ){
				Mesh2D mesh = ((IMeshProvider)patch).getMesh();
				computeUsingMesh( mesh, accessor );
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
			
			Point2D xy = border.getPosition(cp);
			
			double phi = porosity.getPorosity( xy.getPosition() );
			
			accessor.setValue( xy.getPosition(), new PropertyValue(  phi ) );	
		}
		
		
	}


	private void computeUsingMesh( Mesh2D mesh, IPropertyAccessor accessor ) {	
		
		IGeometryProvider provider = mesh.getGeometryProvider();
		
		for( UID uid : mesh.getNodeIds() ){
			double phi = porosity.getPorosity( provider.getPosition(uid) );		
			Node node = (Node)mesh.getNode(uid);
			accessor.setValue( node.getPosition(), new PropertyValue( phi ) );	
		}
	}


	@Override
	public Property compute(Patch patchToCompute) {
		PropertyDB propertyDB = section.getPropertyDB();
		
		
		
		PropertyInfo pinfo =  new PropertyInfo( "Porosity", Support.NodeProperty, Kind.Real );
		
		Property surfaceProp = propertyDB.findProperty( pinfo );
		if ( null == surfaceProp ){
			surfaceProp = propertyDB.createProperty(pinfo);
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

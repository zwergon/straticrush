package straticrush.properties;

import fr.ifp.jdeform.decompaction.Porosity;
import fr.ifp.kronosflow.controllers.property.PropertyComputer;
import fr.ifp.kronosflow.mesh.IGeometryProvider;
import fr.ifp.kronosflow.mesh.IMeshProvider;
import fr.ifp.kronosflow.mesh.Mesh2D;
import fr.ifp.kronosflow.model.ICurviPoint;
import fr.ifp.kronosflow.model.Node;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchCell;
import fr.ifp.kronosflow.model.PatchLibrary;
import fr.ifp.kronosflow.model.PolyLine;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.property.IPropertyAccessor;
import fr.ifp.kronosflow.property.Property;
import fr.ifp.kronosflow.property.PropertyDB;
import fr.ifp.kronosflow.property.PropertyDouble;
import fr.ifp.kronosflow.property.PropertyInfo;
import fr.ifp.kronosflow.property.PropertyInfo.Kind;
import fr.ifp.kronosflow.property.PropertyInfo.Support;
import fr.ifp.kronosflow.property.PropertyStyle;
import fr.ifp.kronosflow.utils.UID;

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
			surfaceProp = new Property(pinfo);
			propertyDB.addProperty(surfaceProp);
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
		
		accessor.addHandle( new PatchCell(patch) );
		
		PolyLine border = patch.getBorder();
	
		for( ICurviPoint cp : border.getPoints() ){
			
			double phi = porosity.getPorosity( border.getPosition(cp) );
			
			accessor.setValue( cp.getUID(), new PropertyDouble(  phi ) );	
		}
		
	}


	private void computeUsingMesh( Mesh2D mesh, IPropertyAccessor accessor ) {	
		
		accessor.addMesh(mesh);
		
		IGeometryProvider provider = mesh.getCurrentProvider();
		
		for( UID uid : mesh.getNodeIds() ){
			double phi = porosity.getPorosity( provider.getPosition(uid) );			
			accessor.setValue( uid, new PropertyDouble( phi ) );	
		}
	}
	


}

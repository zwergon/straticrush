package straticrush.properties;

import fr.ifp.fem2d.elements.IFunction;
import fr.ifp.fem2d.elements.nodes.NodesIntegrate;
import fr.ifp.jdeform.decompaction.Porosity;
import fr.ifp.kronosflow.controllers.property.PropertyComputer;
import fr.ifp.kronosflow.mesh.Cell;
import fr.ifp.kronosflow.mesh.IMeshProvider;
import fr.ifp.kronosflow.mesh.Mesh2D;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchLibrary;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.model.style.PropertyStyle;
import fr.ifp.kronosflow.property.IPropertyAccessor;
import fr.ifp.kronosflow.property.Property;
import fr.ifp.kronosflow.property.PropertyDB;
import fr.ifp.kronosflow.property.PropertyInfo;
import fr.ifp.kronosflow.property.PropertyLocation;
import fr.ifp.kronosflow.uids.UID;
import fr.ifp.kronosflow.utils.LOGGER;

public class SolidSurfaceComputer extends PropertyComputer{
	
	Porosity porosity;
	
	
	static public class Builder implements PropertyComputer.Builder {
		@Override
		public PropertyComputer create( Section section ) {
			return new SolidSurfaceComputer(section);
		}
	}

	protected SolidSurfaceComputer(Section section) {
		super(section);
		PatchLibrary patchLib = section.getPatchLibrary();
		porosity = new Porosity( patchLib.getPaleobathymetry() );
	}

	@Override
	public Property compute() {
		PatchLibrary patchLib = section.getPatchLibrary();
		
		PropertyDB propertyDB = section.getPropertyDB();
		
		
		PropertyInfo pinfo =  new PropertyInfo( "SolidSurface" );
		
		Property surfaceProp = propertyDB.findProperty( pinfo );
		if ( null == surfaceProp ){
			surfaceProp = propertyDB.createProperty(pinfo);
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
	
	
	private void computeUsingPatch(Patch patch, IPropertyAccessor accessor) {
		LOGGER.error("not yet implemented", getClass() );
	}
	
	private IFunction porosityFn = new  IFunction() {
		@Override
		public double value(double[] xy) {
			return (1 - porosity.getPorosity(xy) );
		}	
	};

	private void computeUsingMesh( Mesh2D mesh, IPropertyAccessor accessor ) {	
		
		
		for( UID uid : mesh.getCellIds() ){
			
			Cell cell = (Cell)mesh.getCell(uid);
			
			NodesIntegrate integrate = new NodesIntegrate(mesh, cell);
			
			double surface = Math.abs(integrate.compute( porosityFn ));
			
			double[] xy = cell.barycenter( mesh.getGeometryProvider() );
			
			PropertyLocation location = new PropertyLocation( cell.getPropertyDomain(), xy );
			accessor.setValue( location, surface );
		}
		
	}
		
	
	@Override
	public Property compute(Patch patchToCompute) {
		return compute();
	}
}

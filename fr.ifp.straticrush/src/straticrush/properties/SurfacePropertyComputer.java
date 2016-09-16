package straticrush.properties;

import fr.ifp.fem2d.elements.IFunction;
import fr.ifp.fem2d.elements.nodes.NodesIntegrate;
import fr.ifp.kronosflow.controllers.property.PropertyComputer;
import fr.ifp.kronosflow.mesh.Cell;
import fr.ifp.kronosflow.mesh.IMeshProvider;
import fr.ifp.kronosflow.mesh.Mesh2D;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchCell;
import fr.ifp.kronosflow.model.PatchLibrary;
import fr.ifp.kronosflow.model.PolyLineGeometry;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.property.IPropertyAccessor;
import fr.ifp.kronosflow.property.Property;
import fr.ifp.kronosflow.property.PropertyDB;
import fr.ifp.kronosflow.property.PropertyDouble;
import fr.ifp.kronosflow.property.PropertyInfo;
import fr.ifp.kronosflow.property.PropertyStyle;
import fr.ifp.kronosflow.utils.UID;

public class SurfacePropertyComputer extends PropertyComputer {
	
	
	static public class Builder implements PropertyComputer.Builder {
		@Override
		public PropertyComputer create( Section section ) {
			return new SurfacePropertyComputer(section);
		}
	}
	
	public SurfacePropertyComputer(Section section) {
		super(section);
	}

	@Override
	public Property compute() {
		PatchLibrary patchLib = section.getPatchLibrary();
		
		PropertyDB propertyDB = section.getPropertyDB();
		
		
		PropertyInfo pinfo =  new PropertyInfo( "Surface" );
		
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
	
	
	private void computeUsingPatch(Patch patch, IPropertyAccessor accessor) {
	
		accessor.addHandle( new PatchCell(patch) );
		PolyLineGeometry geometry = new PolyLineGeometry(patch.getBorder());
		double surface = geometry.computeArea();
		
		accessor.setValue( patch.getUID(), new PropertyDouble(surface) );
		
	}
	
	private IFunction cstFn = new  IFunction() {
		@Override
		public double value(double[] xy) {
			return 1;
		}	
	};

	private void computeUsingMesh( Mesh2D mesh, IPropertyAccessor accessor ) {	
		
		accessor.addMesh(mesh);
		for( UID uid : mesh.getCellIds() ){
			
			Cell cell = (Cell)mesh.getCell(uid);
			
			NodesIntegrate integrate = new NodesIntegrate(mesh, cell);
			
			double surface = Math.abs(integrate.compute( cstFn ) );
			
			accessor.setValue( uid, new PropertyDouble(surface) );	
		}
	}
	
	
	
	

}

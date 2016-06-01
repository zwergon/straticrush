package straticrush.properties;

import java.util.List;

import fr.ifp.fem2d.elements.IFunction;
import fr.ifp.fem2d.solve.FEMSolver;
import fr.ifp.fem2d.solve.HeatConductionSolver;
import fr.ifp.fem2d.solve.ImposedTemperatureFunction;
import fr.ifp.fem2d.solve.InternalLoad;
import fr.ifp.fem2d.solve.LoadCondition;
import fr.ifp.kronosflow.mesh.IMeshProvider;
import fr.ifp.kronosflow.mesh.Mesh2D;
import fr.ifp.kronosflow.mesh.regions.Region;
import fr.ifp.kronosflow.mesh.regions.RegionDB;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchLibrary;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.property.IPropertyAccessor;
import fr.ifp.kronosflow.property.Property;
import fr.ifp.kronosflow.property.PropertyDB;
import fr.ifp.kronosflow.property.PropertyInfo;
import fr.ifp.kronosflow.property.PropertyInfo.Kind;
import fr.ifp.kronosflow.property.PropertyInfo.Support;
import fr.ifp.kronosflow.property.PropertyStyle;
import fr.ifp.kronosflow.property.computers.PropertyComputer;

public class PoissonComputer extends PropertyComputer {
	
	static public class Builder implements PropertyComputer.Builder {
		@Override
		public PropertyComputer create( Section section ) {
			return new PoissonComputer(section);
		}
	}

	protected PoissonComputer(Section section) {
		super(section);
	}

	@Override
	public Property compute() {
		
		if ( null == section ){
			return null;
		}
		
		PatchLibrary patchLib = section.getPatchLibrary();
		
		PropertyDB propertyDB = section.getPropertyDB();
		
		PropertyInfo pinfo =  new PropertyInfo( "Poisson", Support.NodeProperty, Kind.Real );
		
		Property surfaceProp = propertyDB.findProperty( pinfo );
		if ( null == surfaceProp ){
			surfaceProp = new Property(pinfo);
			propertyDB.addProperty(surfaceProp);
		}
		
		IPropertyAccessor accessor = surfaceProp.getAccessor();
		
		List<Patch> patches = patchLib.getPatches();
		if ( !patches.isEmpty() ){
			Patch patch = patches.get(0);
			solvePoisson( patch );
		}
		
		
		PropertyStyle propStyle = new PropertyStyle(section.getStyle());
		propStyle.setCurrent(surfaceProp);
		
		
		return surfaceProp;
	}
	
	
	class CstFunction implements IFunction {
		@Override
		public double value(double[] xy) {
			return 1;
		}
		
	}
	

	private void solvePoisson(Patch patch) {
		
		if ( patch instanceof IMeshProvider ){
			
			Mesh2D mesh = ((IMeshProvider)patch).getMesh();
			
			FEMSolver solver = new HeatConductionSolver( mesh ) ;
			
			LoadCondition load = new InternalLoad( new CstFunction() );
			solver.addLoadCondition( load );
			
			
			RegionDB regionDB = mesh.getRegionDB();
			Region region = regionDB.findByName("boundary");
			if ( region != null ){
				load = new ImposedTemperatureFunction(region, new CstFunction() ); 
				solver.addLoadCondition(load);
			}
			
			solver.compute();
			
			
			
		}
		
	}

}

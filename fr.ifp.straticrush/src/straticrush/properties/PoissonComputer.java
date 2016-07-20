package straticrush.properties;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import fr.ifp.fem2d.elements.IFunction;
import fr.ifp.fem2d.heat.HeatConductionSolver;
import fr.ifp.fem2d.heat.ImposedTemperatureFunction;
import fr.ifp.fem2d.solve.FEMSolver;
import fr.ifp.fem2d.solve.InternalLoad;
import fr.ifp.fem2d.solve.LoadCondition;
import fr.ifp.kronosflow.controllers.property.PropertyComputer;
import fr.ifp.kronosflow.geometry.Point2D;
import fr.ifp.kronosflow.mesh.CellPoint;
import fr.ifp.kronosflow.mesh.CompositeMesh2D;
import fr.ifp.kronosflow.mesh.Mesh2D;
import fr.ifp.kronosflow.mesh.MeshAccessor;
import fr.ifp.kronosflow.mesh.NodeLink;
import fr.ifp.kronosflow.mesh.regions.Region;
import fr.ifp.kronosflow.mesh.regions.RegionDB;
import fr.ifp.kronosflow.mesh.regions.RegionZeroD;
import fr.ifp.kronosflow.model.CurviPoint;
import fr.ifp.kronosflow.model.ICurviPoint;
import fr.ifp.kronosflow.model.ICurviPoint.CoordType;
import fr.ifp.kronosflow.model.Interval;
import fr.ifp.kronosflow.model.Node;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchInterval;
import fr.ifp.kronosflow.model.PatchLibrary;
import fr.ifp.kronosflow.model.PolyLineGeometry;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.model.implicit.MeshPatch;
import fr.ifp.kronosflow.property.IPropertyAccessor;
import fr.ifp.kronosflow.property.Property;
import fr.ifp.kronosflow.property.PropertyDB;
import fr.ifp.kronosflow.property.PropertyDouble;
import fr.ifp.kronosflow.property.PropertyInfo;
import fr.ifp.kronosflow.property.PropertyInfo.Kind;
import fr.ifp.kronosflow.property.PropertyInfo.Support;
import fr.ifp.kronosflow.property.PropertyStyle;
import fr.ifp.kronosflow.topology.Border;
import fr.ifp.kronosflow.topology.Contact;
import fr.ifp.kronosflow.topology.PartitionLine;
import fr.ifp.kronosflow.utils.UID;

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
		
		CompositeMesh2D mesh = new CompositeMesh2D();
		for( Patch patch : patchLib.getPatches() ){
			if ( patch instanceof MeshPatch ){
				MeshPatch mPatch = (MeshPatch)patch;
				mesh.addMesh( mPatch.getMesh() );
			}
		}
		
		MeshAccessor meshAccessor = new MeshAccessor(mesh);
		
		RegionDB regionDB = mesh.getRegionDB();
		RegionZeroD region = new RegionZeroD("boundary");

		Set<Node> nodes = new HashSet<Node>();
		for( PartitionLine pLine : patchLib.getPartitionLines() ){
			if ( pLine instanceof Border ){
				Interval interval = pLine.getPatchInterval().getInterval();
				for( ICurviPoint cp : interval.getPoints()){
					UID uid = getNode( meshAccessor, (CellPoint)cp );
					nodes.add( (Node)mesh.getNode(uid) );
				}
			}
			else if ( pLine instanceof Contact ){
				createMeshLinks( (Contact)pLine, mesh);
			}
		}
		region.setNodes( nodes );
		regionDB.addRegion( region );
		
		
		solvePoisson(mesh, accessor);
		
		
		PropertyStyle propStyle = new PropertyStyle(section.getStyle());
		propStyle.setCurrent(surfaceProp);
		
		
		return surfaceProp;
	}

	private UID getNode(MeshAccessor meshAccessor, CellPoint cp) {
		NodeLink nodeLink = meshAccessor.computeNodeLink(cp);
		Collection<UID> nodes = nodeLink.getLinkedNodes();
		return nodes.iterator().next();
	}
	
	
	class CstFunction implements IFunction {
		
		double value;
		
		public CstFunction() {
			this.value = 0.0;
		}
		
		public CstFunction( double value ) {
			this.value = value;
		}
		
		@Override
		public double value(double[] xy) {
			return value;
		}
		
	}
	
	
	int pair = 0;
	
	private void createMeshLinks( Contact contact, Mesh2D mesh ) {

		PatchInterval master;
		PatchInterval slave;
		if ( pair % 2 ==  0 ){
			master = contact.getPatchInterval();
			slave  = contact.getMate();
		}
		else
		{
			slave = contact.getPatchInterval();
			master  = contact.getMate();
		}
		pair++;
			
		Interval interval = master.getInterval();
		MeshPatch patch = (MeshPatch)master.getPatch();
		MeshAccessor accessor = new MeshAccessor(patch.getMesh());

		Interval mateInterval = slave.getInterval();
		MeshPatch matePatch = (MeshPatch)slave.getPatch();
		MeshAccessor mateAccessor = new MeshAccessor(matePatch.getMesh());
		PolyLineGeometry mateGeometry = new PolyLineGeometry(mateInterval);

		int npts = Math.min( interval.size(), mateInterval.size() );

		double s1 = interval.first().getCurvilinear();
		double s2 = interval.last().getCurvilinear();
		double secart = (s2 - s1)/(double)(npts-1);

		double s = s1;
		/*for( int i=0; i< npts;i++ ){
	        	CurviPoint cp = new CurviPoint(CoordType.INTERPOLATED);
	        	cp.setCurvilinear(s);*/

		for( ICurviPoint cp : interval.getPoints() ){

	
			Point2D p = interval.getPosition(cp);
			
			

			System.out.println("create link for " + p.x() + "," + p.y() );

			NodeLink link = accessor.computeNodeLink(p.getPosition());



			Point2D proj = new Point2D();
			mateGeometry.projectEuclidian(p, proj);

			NodeLink mateLink = mateAccessor.computeNodeLink(proj.getPosition());


			for (UID uid : mateLink.getLinkedNodes()) {

				double w = mateLink.getWeight(uid);
				System.out.println(". " + uid + " ( " + w + ") " );
				link.addWeightedNode(uid, -mateLink.getWeight(uid));
			}

			mesh.addLink(link);

			s+=secart;


		}




	}


	private boolean solvePoisson( Mesh2D mesh, IPropertyAccessor accessor ) {
			
		FEMSolver solver = new HeatConductionSolver( mesh ) ;

		LoadCondition load = new InternalLoad( solver.getMapping(), new CstFunction(2.) );
		solver.addLoadCondition( load );


		RegionDB regionDB = mesh.getRegionDB();
		Region region = regionDB.findByName("boundary");
		if ( region != null ){
			load = new ImposedTemperatureFunction( solver.getMapping(), region, new CstFunction(5) ); 
			solver.addLoadCondition(load);
		}

		solver.compute();

		for( UID uid : mesh.getNodeIds() ){
			double temperature = solver.getValue( uid );
			accessor.setValue( uid, new PropertyDouble(temperature) );
		}

		return true;
			
	
		
	}

}

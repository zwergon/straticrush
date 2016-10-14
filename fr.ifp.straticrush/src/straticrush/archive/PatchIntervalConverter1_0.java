package straticrush.archive;

import fr.ifp.kronosflow.model.KinObject;
import fr.ifp.kronosflow.model.PatchInterval;
import fr.ifp.kronosflow.polyline.IPolyline;

public class PatchIntervalConverter1_0 implements IConverter {

	@Override
	public void write(DBArchiver dbArchiver, DBStub stub) {
		
		PatchInterval patchInterval = (PatchInterval)stub.getObject();

		dbArchiver.write( stub, KinObject.class );
		
		dbArchiver.write( new DBStub(patchInterval.getInterval(), patchInterval), IPolyline.class );
		
	}

	

	@Override
	public void read(DBArchiver dbArchiver, DBStub stub) {
		// TODO Auto-generated method stub
		
	}
	
	
	

}

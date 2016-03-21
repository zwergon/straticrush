package straticrush.archive;

import fr.ifp.kronosflow.model.KinObject;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PolyLine;
import fr.ifp.kronosflow.utils.LOGGER;

public class PatchConverter1_0 implements IConverter {

	@Override
	public void write(DBArchiver dbArchiver, DBStub stub) {
		
		Patch patch = (Patch)stub.getObject();

		//LOGGER.debug("write Patch " + patch.getName(), getClass() );
		
		dbArchiver.write( stub, KinObject.class );
		
		dbArchiver.write( new DBStub(patch.getBorder(), patch) );
	}

	@Override
	public void read(DBArchiver dbArchiver, DBStub stub) {
		// TODO Auto-generated method stub

	}

}

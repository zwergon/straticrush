package straticrush.archive;

import fr.ifp.kronosflow.model.KinObject;
import fr.ifp.kronosflow.model.PatchLibrary;
import fr.ifp.kronosflow.utils.LOGGER;

public class PatchLibraryConverter1_0 implements IConverter {
	
	@Override
	public void write(DBArchiver dbArchiver, DBStub stub) {
		
		PatchLibrary patchLibrary = (PatchLibrary)stub.getObject();
		
		LOGGER.debug("write PatchLibrary " + patchLibrary.getName(), getClass() );
		
		dbArchiver.write( stub, KinObject.class );
		
	}

	@Override
	public void read(DBArchiver dbArchiver, DBStub stub) {
		// TODO Auto-generated method stub
		
	}
}

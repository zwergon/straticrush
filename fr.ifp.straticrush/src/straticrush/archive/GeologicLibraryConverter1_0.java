package straticrush.archive;

import fr.ifp.kronosflow.model.KinObject;
import fr.ifp.kronosflow.model.geology.GeologicLibrary;
import fr.ifp.kronosflow.utils.LOGGER;

public class GeologicLibraryConverter1_0 implements IConverter {

	@Override
	public void write(DBArchiver dbArchiver, DBStub stub) {
		GeologicLibrary geologicLib = (GeologicLibrary)stub.getObject();

		LOGGER.debug("write GeologicLibrary " + geologicLib.getName(), getClass() );
		
		dbArchiver.write( stub, KinObject.class );
		
	}

	@Override
	public void read(DBArchiver dbArchiver, DBStub stub) {
		// TODO Auto-generated method stub
		
	}

}

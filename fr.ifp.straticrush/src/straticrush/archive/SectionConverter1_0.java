package straticrush.archive;

import fr.ifp.kronosflow.model.KinObject;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.utils.LOGGER;

public class SectionConverter1_0 implements IConverter {

	@Override
	public void write(DBArchiver dbArchiver, DBStub stub) {
		Section section = (Section)stub.getObject();

		LOGGER.debug("write Section " + section.getName(), getClass() );

		dbArchiver.write( stub, KinObject.class );

	}

	@Override
	public void read(DBArchiver dbArchiver, DBStub stub) {
		// TODO Auto-generated method stub

	}

}

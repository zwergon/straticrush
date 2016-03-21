package straticrush.archive;

import fr.ifp.kronosflow.geology.GeologicFeature;
import fr.ifp.kronosflow.utils.LOGGER;

public class GeologicFeatureConverter1_0 implements IConverter {

	@Override
	public void write(DBArchiver dbArchiver, DBStub stub) {
		GeologicFeature feature = (GeologicFeature)stub.getObject();

		LOGGER.debug("write GeologicFeature " + feature.getName() + "(" + feature.getClass().getSimpleName() + ")" , getClass());

	}

	@Override
	public void read(DBArchiver dbArchiver, DBStub stub) {
		// TODO Auto-generated method stub

	}

}

package straticrush.view;

import fr.ifp.kronosflow.model.PatchInterval;
import fr.ifp.kronosflow.topology.Contact;

public class PartitionLineView extends PatchIntervalView {
	
	@Override
	PatchInterval getPatchInterval(){
		return ((Contact)getUserData()).getPatchInterval();
	}

}

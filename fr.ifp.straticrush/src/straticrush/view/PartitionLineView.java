package straticrush.view;

import fr.ifp.kronosflow.model.PatchInterval;
import fr.ifp.kronosflow.topology.PartitionLine;

public class PartitionLineView extends PatchIntervalView {
	
	@Override
	PatchInterval getPatchInterval(){
		return ((PartitionLine)getUserData()).getPatchInterval();
	}

}

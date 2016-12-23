package stratifx.application.views;

import fr.ifp.kronosflow.model.PatchInterval;
import fr.ifp.kronosflow.topology.PartitionLine;

public class GPartitionLineView extends GPatchIntervalView {
	
	@Override
	PatchInterval getPatchInterval(){
		return ((PartitionLine)getUserData()).getPatchInterval();
	}

}

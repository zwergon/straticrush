package straticrush.archive;

import fr.ifp.kronosflow.model.KinObject;
import fr.ifp.kronosflow.uids.IHandle;

public class DBStub {

	Object object;
	
	IHandle parent;
	

	public DBStub( Object object ) {
		this.object = object;
		if ( object instanceof KinObject ){
			this.parent = ((KinObject)object).getParent();
		}
	}
	
	public DBStub(Object object, IHandle parent ) {
		this.object = object;
		this.parent = parent;
	}

	public Object getObject() {
		return object;
	}

	public IHandle getParent(){
		return parent;
	}

	
	
	
}

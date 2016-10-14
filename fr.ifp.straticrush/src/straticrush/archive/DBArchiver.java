package straticrush.archive;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.ifp.kronosflow.geology.BorderFeature;
import fr.ifp.kronosflow.geology.ErosionFeature;
import fr.ifp.kronosflow.geology.FaultFeature;
import fr.ifp.kronosflow.geology.GeologicLibrary;
import fr.ifp.kronosflow.geology.StratigraphicColumn;
import fr.ifp.kronosflow.geology.StratigraphicEvent;
import fr.ifp.kronosflow.geology.StratigraphicUnit;
import fr.ifp.kronosflow.geoscheduler.GeoschedulerSection;
import fr.ifp.kronosflow.mesh.CellPoint;
import fr.ifp.kronosflow.mesh.GridNode;
import fr.ifp.kronosflow.model.FeatureGeolInterval;
import fr.ifp.kronosflow.model.FeatureInterval;
import fr.ifp.kronosflow.model.KinObject;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchInterval;
import fr.ifp.kronosflow.model.PatchLibrary;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.model.explicit.ExplicitPatch;
import fr.ifp.kronosflow.model.explicit.ExplicitPoint;
import fr.ifp.kronosflow.model.explicit.ExplicitPolyLine;
import fr.ifp.kronosflow.model.explicit.InfinitePolyline;
import fr.ifp.kronosflow.model.implicit.MeshPatch;
import fr.ifp.kronosflow.model.implicit.MeshPolyLine;
import fr.ifp.kronosflow.polyline.CurviPoint;
import fr.ifp.kronosflow.polyline.ICurviPoint;
import fr.ifp.kronosflow.polyline.IPolyline;
import fr.ifp.kronosflow.polyline.Node;
import fr.ifp.kronosflow.polyline.PolyLine;
import fr.ifp.kronosflow.uids.IHandle;
import fr.ifp.kronosflow.uids.UID;
import fr.ifp.kronosflow.utils.LOGGER;

public class DBArchiver {
	
    Connection connection = null;

    String url = "jdbc:mysql://localhost:3306/testdb";
    String user = "testuser";
    String password = "test623";
    
    static Map<String, String> mapConverters = new HashMap<String, String>();
    
    static {
    	registerConverter(KinObject.class, KinObjectConverter1_0.class);
    	registerConverter(PatchLibrary.class, PatchLibraryConverter1_0.class);

    	registerConverter(Section.class, SectionConverter1_0.class );
    	registerConverter(GeoschedulerSection.class, SectionConverter1_0.class );	
    	registerConverter(PatchInterval.class, PatchIntervalConverter1_0.class );
    	registerConverter(FeatureGeolInterval.class, PatchIntervalConverter1_0.class);
    	
    	registerConverter(GeologicLibrary.class, GeologicLibraryConverter1_0.class );
    	registerConverter(StratigraphicColumn.class, StratigraphicColumnConverter1_0.class );
    	registerConverter(StratigraphicUnit.class, GeologicFeatureConverter1_0.class );
    	registerConverter(StratigraphicEvent.class, GeologicFeatureConverter1_0.class );
    	registerConverter(FaultFeature.class, GeologicFeatureConverter1_0.class );
    	registerConverter(ErosionFeature.class, GeologicFeatureConverter1_0.class );
    	registerConverter(BorderFeature.class, GeologicFeatureConverter1_0.class );
    	
    	    	
    	registerConverter(IPolyline.class, PolyLineConverter1_0.class );
    	registerConverter(PolyLine.class, PolyLineConverter1_0.class );
    	registerConverter(ExplicitPolyLine.class, PolyLineConverter1_0.class );
    	registerConverter(MeshPolyLine.class, PolyLineConverter1_0.class );
    	registerConverter(InfinitePolyline.class, PolyLineConverter1_0.class );
    	registerConverter(FeatureInterval.class, PolyLineConverter1_0.class );
    	
    	registerConverter(Patch.class, PatchConverter1_0.class );
    	registerConverter(ExplicitPatch.class, PatchConverter1_0.class );
    	registerConverter(MeshPatch.class, PatchConverter1_0.class );
    	registerConverter(Patch.class, PatchConverter1_0.class );
    	
    	registerConverter(ICurviPoint.class, ICurviPointConverter1_0.class );
    	registerConverter(CurviPoint.class, ICurviPointConverter1_0.class );
    	registerConverter(ExplicitPoint.class, ICurviPointConverter1_0.class );
    	registerConverter(CellPoint.class, ICurviPointConverter1_0.class );
    	
    	registerConverter(Node.class, INodesConverter1_0.class );
    	registerConverter(GridNode.class, INodesConverter1_0.class );
    }
    
    
	static public void registerConverter( Class<?> objectClass, Class<?> converterClass ){
		mapConverters.put( objectClass.getCanonicalName(), converterClass.getCanonicalName() );
	}
	
	
	public void open(){
		try {
    		connection = DriverManager.getConnection(url, user, password);
 
    	} catch (SQLException ex) {
    		Logger lgr = Logger.getLogger(getClass().getName());
    		lgr.log(Level.SEVERE, ex.getMessage(), ex);
    	}
	}
	
	public void close(){
		try {

			if ( null != connection ){
				connection.close();
			}

		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(getClass().getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
		}
	}

    Connection getConnection(){
    	return connection;
    }

    public void write( DBStub stub ){
    	Object object = stub.getObject();
    	if ( null == object ){
    		return;
    	}
    	IConverter converter = getConverter(object);
    	if ( converter != null ){
    		converter.write(this, stub );
    	}
    }

    public void write( DBStub stub, Class<?> classConverter ){
    	Object object = stub.getObject();
    	if ( null == object ){
    		return;
    	}
    	IConverter converter = getConverter(classConverter);
    	if ( converter != null ){
    		converter.write(this, stub );
    	}
    }
    

    private IConverter getConverter( Object object ){  	
    	return getConverter( object.getClass() );
    }


    private IConverter getConverter( Class<?> classConverter ){

    	String key = classConverter.getCanonicalName();

    	IConverter converter = null;
    	if ( !mapConverters.containsKey(key) ){
    		return null;
    	}

    	try {
    		Class<?> c1 = Class.forName( mapConverters.get(key) );
    		if ( c1 == null ){
    			return null;
    		}
    		converter = (IConverter)c1.newInstance();

    	} catch (Exception ex) {
    		Logger lgr = Logger.getLogger(getClass().getName());
    		lgr.log(Level.SEVERE, ex.getMessage(), ex);
    		return null;
    	}

    	return converter;
    }
    
    public  UID getUID( IHandle handle, String database ){

    	UID uid = null;
    	
    	Statement st = null;
    	try {

    		st = connection.createStatement();
    		ResultSet rs = st.executeQuery( "SELECT Id FROM "+ database+ " WHERE Id=" + handle.getUID().getId() );
    		if ( rs.next() ){
    			uid = new UID( rs.getString(1) );
    		}

    	} catch (SQLException ex) {
    		LOGGER.error( ex, getClass() );

    	} finally {

    		try {
    			if (st != null) {
    				st.close();
    			}
    		} catch (SQLException ex) {
    			LOGGER.error( ex, getClass() );
    		}
    	}

    	return uid;
    }
    
	public Set<UID> selectIds( IHandle parent, String database ){
		
		
		HashSet<UID> uids = new HashSet<UID>();
		PreparedStatement st = null;
		try {
			st = connection.prepareStatement(
					"SELECT Id,parentId FROM "+database+" WHERE parentId=?"
					);
			st.setString(1, parent.getUID().toString() );
			ResultSet rs = st.executeQuery();
			while( rs.next() ){
				uids.add( new UID( rs.getString(1)) );
			}
			

		}
		catch(SQLException ex ){
			LOGGER.error(ex, getClass());
		}
		finally {
			try {
				if (st != null) {
					st.close();
				}
			}
			catch(SQLException ex ){	
				LOGGER.error(ex, getClass());
			}
		}

		return uids;

	}


}
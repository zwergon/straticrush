package straticrush.archive;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import fr.ifp.kronosflow.model.FeatureInterval;
import fr.ifp.kronosflow.polyline.ICurviPoint;
import fr.ifp.kronosflow.polyline.Node;
import fr.ifp.kronosflow.polyline.PolyLine;
import fr.ifp.kronosflow.uids.IHandle;
import fr.ifp.kronosflow.uids.UID;
import fr.ifp.kronosflow.utils.LOGGER;

public class PolyLineConverter1_0 implements IConverter {

	@Override
	public void write(DBArchiver dbArchiver, DBStub stub) {

		Object object = stub.getObject();
		if ( object instanceof PolyLine ){
			
			writePolyLine( dbArchiver, stub.getParent(), (PolyLine)object );
			
			//write subparts.
			dbArchiver.write( stub, ICurviPoint.class );
			dbArchiver.write( stub, Node.class );
						
		}
		else if ( object instanceof FeatureInterval ){
			writeInterval( dbArchiver, stub.getParent(), (FeatureInterval)object );
		}


	}

	

	@Override
	public void read(DBArchiver dbArchiver, DBStub stub) {
		// TODO Auto-generated method stub

	}
	
	
	public void writePolyLine( DBArchiver archiver, IHandle parent, PolyLine polyline ){

		Connection con = archiver.getConnection();
		PreparedStatement st = null;

		try {

			PreparedStatement insertStatement = con.prepareStatement(
					"INSERT INTO IPolylines(Id, parentId, Class, closed ) VALUES( ?, ?, ?, ?)"
					);

			UID uid = archiver.getUID( polyline, "IPolylines" );
			if ( uid == null ){
				st = insertStatement;
				st.setLong( 1, polyline.getUID().getId() );
				if( parent == null ){
					st.setNull( 2, Types.BIGINT );
				}
				else {
					st.setLong( 2, parent.getUID().getId() );
				}
				st.setString( 3, polyline.getClass().getCanonicalName() );
				st.setBoolean( 4, polyline.isClosed() );
			}
			
			
			if ( st != null ){
				st.executeUpdate();
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
	}
	
	
	private void writeInterval(DBArchiver archiver, IHandle parent,	FeatureInterval interval) {
		
		Connection con = archiver.getConnection();
		PreparedStatement st = null;

		try {

			PreparedStatement insertStatement = con.prepareStatement(
					"INSERT INTO IPolylines(Id, parentId, Class, closed, s1, s2 ) VALUES( ?, ?, ?, ?, ?, ?)"
					);

			UID uid = archiver.getUID( interval, "IPolylines" );
			if ( uid == null ){
				st = insertStatement;
				st.setLong( 1, interval.getUID().getId() );
				if( parent == null ){
					st.setNull( 2, Types.BIGINT );
				}
				else {
					st.setLong( 2, parent.getUID().getId() );
				}
				st.setString( 3, interval.getClass().getCanonicalName() );
				st.setBoolean( 4, interval.isClosed() );
				st.setDouble(5, interval.getS1().getCurvilinear() );
				st.setDouble(6, interval.getS2().getCurvilinear() );
			}
			
			
			if ( st != null ){
				st.executeUpdate();
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
		
	}
	

}

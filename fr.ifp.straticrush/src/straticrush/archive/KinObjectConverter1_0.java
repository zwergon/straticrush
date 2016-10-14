package straticrush.archive;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import fr.ifp.kronosflow.model.KinObject;
import fr.ifp.kronosflow.uids.UID;
import fr.ifp.kronosflow.utils.LOGGER;

public class KinObjectConverter1_0 implements IConverter {

	@Override
	public void write(DBArchiver dbArchiver, DBStub stub) {

		KinObject kobject = (KinObject)stub.getObject();

		writeKinObject(dbArchiver, kobject);

		for( KinObject child : kobject.getChildren() ){
			dbArchiver.write( new DBStub(child) );
		}

	}

	@Override
	public void read(DBArchiver dbArchiver, DBStub stub) {
		// TODO Auto-generated method stub

	}


	public void writeKinObject( DBArchiver archiver, KinObject object ){

		Connection con = archiver.getConnection();
		PreparedStatement st = null;

		try {

			
			PreparedStatement insertStatement = con.prepareStatement(
					"INSERT INTO KinObjects(Id, parentId, Class, Style) VALUES( ?, ?, ?, ?)"
					);

			PreparedStatement updateStatement = con.prepareStatement(
					"UPDATE KinObjects " 
							+ "SET Style=?  "
							+ "WHERE Id = ?;"
					);

			UID uid = archiver.getUID( object, "KinObjects" );
			if ( uid == null ){
				st = insertStatement;
				st.setLong( 1, object.getUID().getId() );
				if( object.getParent() == null ){
					st.setNull( 2, Types.BIGINT );
				}
				else {
					st.setLong( 2, object.getParent().getUID().getId() );
				}
				st.setString( 3, object.getClass().getCanonicalName() );
				st.setNull( 4, Types.LONGVARCHAR );
			}
			else {

				st = updateStatement;
				st.setNull( 1, Types.LONGVARCHAR );
				st.setLong( 2, object.getUID().getId() );
			}
			
			st.executeUpdate();



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





package straticrush.archive;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.ifp.kronosflow.polyline.ICurviPoint;
import fr.ifp.kronosflow.polyline.PolyLine;
import fr.ifp.kronosflow.uids.IHandle;
import fr.ifp.kronosflow.uids.UID;
import fr.ifp.kronosflow.utils.LOGGER;

public class ICurviPointConverter1_0 implements IConverter {
	
	
	@Override
	public void write(DBArchiver dbArchiver, DBStub stub) {
		
		if ( stub.getObject() instanceof PolyLine ){
			PolyLine polyline = (PolyLine)stub.getObject();
			writeList( dbArchiver,  stub.getParent(), polyline );
		}
		
	}
	
	@Override
	public void read(DBArchiver dbArchiver, DBStub stub) {
		// TODO Auto-generated method stub

	}

	private void writeList(DBArchiver dbArchiver, IHandle parent, PolyLine polyline ) {
		
		List<ICurviPoint> icps = polyline.getPoints();
		
		
		Connection con = dbArchiver.getConnection();
		PreparedStatement st = null;

		try {
			con.setAutoCommit(false);
			PreparedStatement insertStatement = con.prepareStatement(
					"INSERT INTO ICurviPoints( Id, parentId, Class, Curvilinear, CoordType )"
							+ "VALUES (?,?,?,?,?);"
					);
			
			PreparedStatement updateStatement = con.prepareStatement(
					  "UPDATE ICurviPoints " 
					+ "SET Curvilinear= ?  "
				    + "WHERE Id = ?;"
					);
			
			Set<UID> uids = dbArchiver.selectIds(parent, "ICurviPoints");
			for( ICurviPoint pt : icps ){
				
				if ( uids.contains(pt.getUID() ) ){
					uids.remove(pt.getUID());
					st = updateStatement;
					st.setDouble( 1, pt.getCurvilinear() );
					st.setLong(   2, pt.getUID().getId() );
				}
				else {
					st = insertStatement;
					st.setLong(1, pt.getUID().getId() );
					st.setLong(2, parent.getUID().getId() );
					st.setString(3, pt.getClass().getCanonicalName() );
					st.setDouble( 4, pt.getCurvilinear() );
					st.setInt( 5, pt.getType().ordinal() );
				}

				st.addBatch();

			}

			st.executeBatch();
			con.commit();
			
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
	}
	
	




}

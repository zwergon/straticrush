package straticrush.archive;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Set;

import fr.ifp.kronosflow.mesh.GridNode;
import fr.ifp.kronosflow.polyline.Node;
import fr.ifp.kronosflow.polyline.PolyLine;
import fr.ifp.kronosflow.uids.IHandle;
import fr.ifp.kronosflow.uids.UID;
import fr.ifp.kronosflow.utils.LOGGER;

public class INodesConverter1_0 implements IConverter {

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
		
		Connection con = dbArchiver.getConnection();
		PreparedStatement st = null;

		try {
			con.setAutoCommit(false);
			PreparedStatement insertStatement = con.prepareStatement(
					"INSERT INTO Nodes ( Id, parentId, Class, OriX, OriZ, X, Z, Idx )"
							+ "VALUES (?,?,?,?,?,?,?,?);"
					);
			
			PreparedStatement updateStatement = con.prepareStatement(
					  "UPDATE Nodes " 
					+ "SET X=?, Z=?  "
				    + "WHERE Id = ?;"
					);
			
			Set<UID> uids = dbArchiver.selectIds(parent, "Nodes");
			
			for( IHandle ih : polyline.getNodes() ){
				Node node = (Node)ih;
				
				double[] pos = node.getPosition();
			
				
				if ( uids.contains(node.getUID() ) ){
					uids.remove(node.getUID());
					st = updateStatement;
					st.setDouble( 1, pos[0] );
					st.setDouble( 2, pos[1] );
					st.setLong( 3,  node.getUID().getId() );
				}
				else {
					double[] ori = node.getOriginalPos();
					st = insertStatement;
					st.setLong(1, node.getUID().getId() );
					st.setLong(2, parent.getUID().getId() );
					st.setString(3, node.getClass().getCanonicalName() );
					st.setDouble( 4, ori[0] );
					st.setDouble( 5, ori[1] );
					st.setDouble( 6, pos[0] );
					st.setDouble( 7, pos[1] );
					
					if ( node instanceof GridNode ){
						st.setInt(8, ((GridNode)node).getIdx() );
					}
					else {
						st.setNull(8, Types.INTEGER );
					}
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

package straticrush.archive;

public interface IConverter {
	
	public void write( DBArchiver dbArchiver, DBStub stub );

	public void read( DBArchiver dbArchiver, DBStub stub );

}

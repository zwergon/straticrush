package stratifx.model.wrappers;

public interface IPersisted {
    
    public long getUID();

 
    public void setUID(long uid);

 
    public void setPersistedClass(String className);

 
    public String getPersistedClass();
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stratifx.model.wrappers;


/**
 *
 * @author lecomtje
 */
public abstract class AbstractPersisted implements IPersisted {

    String className;

    long uid;

    @Override
    public long getUID() {
        return uid;
    }

    @Override
    public void setUID(long uid) {
        this.uid = uid;
    }

    @Override
    public void setPersistedClass(String className) {
        this.className = className;
    }

    @Override
    public String getPersistedClass() {
        return className;
    }

}

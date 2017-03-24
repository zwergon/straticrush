/*
 * Copyright (C) 2014-2017 by IFPEN
 * All rights reserved.
 * 
 * IFPEN Headquarters:
 * 1 & 4, avenue de Bois-Preau
 * 92852 Rueil-Malmaison Cedex - France
 */
package stratifx.model.wrappers;

import fr.ifp.kronosflow.model.geology.GeologicLibrary;
import fr.ifp.kronosflow.model.wrapper.IWrapper;

/**
 *
 * @author lecomtje
 */
public class GeologicLibraryWrapper implements IWrapper<GeologicLibrary> {
    
    
    PersistableGeologicLibrary persistedLibrary;

    @Override
    public void setPersisted(Object persisted) {
        persistedLibrary = (PersistableGeologicLibrary)persisted;
    }

    @Override
    public Object getPersisted() {
       return persistedLibrary;
    }

    @Override
    public boolean load(GeologicLibrary library) {
        library.removeAll();
        persistedLibrary.getFeatures().forEach((feature)->library.add(feature));
        return true;
    }

    @Override
    public boolean save(GeologicLibrary library) {
        persistedLibrary.setFeatures( library.getGeologicFeatures() );
        return true;
    }
    
}

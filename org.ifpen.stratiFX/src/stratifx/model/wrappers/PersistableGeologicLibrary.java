/*
 * Copyright (C) 2014-2017 by IFPEN
 * All rights reserved.
 * 
 * IFPEN Headquarters:
 * 1 & 4, avenue de Bois-Preau
 * 92852 Rueil-Malmaison Cedex - France
 */
package stratifx.model.wrappers;

import fr.ifp.kronosflow.model.geology.GeologicFeature;
import fr.ifp.kronosflow.model.geology.GeologicLibrary;
import java.util.List;

/**
 * {@link GeologicLibrary} is shared accros all times store a pointer to this
 * one.
 */
class PersistableGeologicLibrary extends AbstractPersisted {

    List<GeologicFeature> features;

    public void setFeatures(List<GeologicFeature> features) {
        this.features = features;
    }

    List<GeologicFeature>  getFeatures() {
        return features;
    }

}

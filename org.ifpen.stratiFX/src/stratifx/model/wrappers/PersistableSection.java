/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stratifx.model.wrappers;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author lecomtje
 */
class PersistableSection extends AbstractPersisted {

    Set<IPersisted> patches = new HashSet<>();

    IPersisted paleo;

    IPersisted reference;

    public Set<IPersisted> getPatches() {
        return patches;
    }

    public void setPatches(HashSet<IPersisted> patches) {
        this.patches = patches;
    }

    public IPersisted getPaleobathymetry() {
        return paleo;
    }

    public void setPaleobathymetry(PersistablePolyline paleo) {
        this.paleo = paleo;
    }

    public IPersisted getDomainReference() {
        return reference;
    }

    public void setDomainReference(PersistablePolyline reference) {
        this.reference = reference;
    }
    
    

}

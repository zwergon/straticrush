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
class PersistableSection {

    Set<PersistablePatch> patches = new HashSet<>();

    PersistablePolyline paleo = new PersistablePolyline();

    PersistablePolyline reference = new PersistablePolyline();

    long uid;

    public long getUID() {
        return uid;
    }

    public void setUID(long uid) {
        this.uid = uid;

    }

    public Set<PersistablePatch> getPatches() {
        return patches;
    }

    public void setPatches(HashSet<PersistablePatch> patches) {
        this.patches = patches;
    }

    public PersistablePolyline getPaleobathymetry() {
        return paleo;
    }

    public void setPaleobathymetry(PersistablePolyline paleo) {
        this.paleo = paleo;
    }

    public PersistablePolyline getDomainReference() {
        return reference;
    }

    public void setDomainReference(PersistablePolyline reference) {
        this.reference = reference;
    }

}

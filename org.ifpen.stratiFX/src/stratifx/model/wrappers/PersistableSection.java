/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stratifx.model.wrappers;

import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.model.wrapper.IPersisted;
import fr.ifp.kronosflow.polyline.PolyLine;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author lecomtje
 */
class PersistableSection implements IPersisted<Section> {

    Set<IPersisted<Patch>> patches = new HashSet<>();

    IPersisted<PolyLine> paleo = new PersistablePolyline();

    IPersisted<PolyLine> reference = new PersistablePolyline();

    long uid;

    @Override
    public long getUID() {
        return uid;
    }

    public void setUID(long uid) {
        this.uid = uid;

    }

    public Set<IPersisted<Patch>> getPatches() {
        return patches;
    }

    public void setPatches(HashSet<IPersisted<Patch>> patches) {
        this.patches = patches;
    }

    public IPersisted<PolyLine> getPaleobathymetry() {
        return paleo;
    }

    public void setPaleobathymetry(PersistablePolyline paleo) {
        this.paleo = paleo;
    }

    public IPersisted<PolyLine> getDomainReference() {
        return reference;
    }

    public void setDomainReference(PersistablePolyline reference) {
        this.reference = reference;
    }
    
     @Override
    public Section create() {
        return new Section();
    }

}

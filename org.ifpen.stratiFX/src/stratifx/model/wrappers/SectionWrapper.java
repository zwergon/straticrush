/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stratifx.model.wrappers;

import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchLibrary;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.model.explicit.ExplicitPatch;
import fr.ifp.kronosflow.model.geology.DomainReference;
import fr.ifp.kronosflow.model.geology.Paleobathymetry;
import fr.ifp.kronosflow.model.wrapper.IWrapper;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author lecomtje
 */
public class SectionWrapper implements IWrapper<Section> {

    PersistableSection persistedSection = new PersistableSection();

    public SectionWrapper() {
        super();
    }
   
    @Override
    public boolean load(Section wrapped) {
         
        // Save all the section information in the persistableSection
        PatchLibrary library = wrapped.getPatchLibrary();

        // search if all patches are in the PersistableSection
        List<Patch> patches = library.getPatches();

        Iterator<Patch> ite = patches.iterator();

        // Remove useless patch

        while (ite.hasNext()) {
            Patch patch = ite.next();
            boolean found = false;
            for (PersistablePatch p : persistedSection.getPatches()) {
                if (patch.getUID().getId() == p.getUID()) {
                    found = true;
                    // Wrap the patch
                    PatchWrapper wrapper = new PatchWrapper(p);
                    wrapper.load(patch);
                    break;
                }
            }

            if (!found) {
                library.remove(patch);
            }
        }

        // Reload the patches
        patches = library.getPatches();

        // Create new patches
        for (

        PersistablePatch patch : persistedSection.getPatches()) {
            boolean found = false;

            for (Patch p : patches) {
                if (p.getUID().getId() == patch.getUID()) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                Patch p = new ExplicitPatch();

                wrapped.getPatchLibrary().add(p);
                p.setPatchLibrary(wrapped.getPatchLibrary());
                PatchWrapper wrapper = new PatchWrapper(patch);
                wrapper.load(p);

            }
        }

        // Chargement des paleobathymetry

        Paleobathymetry paleo = wrapped.getPatchLibrary().getPaleobathymetry();
        if (paleo == null) {
            paleo = new Paleobathymetry();
            wrapped.getPatchLibrary().add(paleo);
        }

        new PolylineWrapper(persistedSection.getPaleobathymetry()).load(paleo.getPolyline());

        DomainReference reference = wrapped.getPatchLibrary().getDomainReference();
        if (reference == null) {
            reference = new DomainReference();
            wrapped.getPatchLibrary().add(reference);
        }

        new PolylineWrapper(persistedSection.getDomainReference())
                .load(reference.getPolyline());
        
        return true;

    }

    @Override
    public boolean save(Section wrapped) {
        // Save all the section information in the persistableSection
        PatchLibrary library = wrapped.getPatchLibrary();

        // search if all patches are in the PersistableSection
        List<Patch> patches = library.getPatches();

        // First, cleaning the PersistableSection

        Iterator<PersistablePatch> ite = persistedSection.getPatches().iterator();

        while (ite.hasNext()) {
            PersistablePatch patch = ite.next();
            boolean found = false;
            for (Patch p : patches) {
                if (p.getUID().getId() == patch.getUID()) {
                    found = true;
                    // Wrap the patch
                    PatchWrapper wrapper = new PatchWrapper(patch);
                    wrapper.save(p);
                    break;
                }
            }

            if (!found) ite.remove();
        }

        for (Patch p : patches) {
            boolean found = false;

            for (PersistablePatch patch : persistedSection.getPatches()) {
                if (p.getUID().getId() == patch.getUID()) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                PersistablePatch patch = new PersistablePatch();
                PatchWrapper wrapper = new PatchWrapper(patch);
                wrapper.save(p);
                persistedSection.getPatches().add(patch);
            }
        }

        // Chargement des paleobathymetry
        new PolylineWrapper(persistedSection.getPaleobathymetry())
                .save(wrapped.getPatchLibrary().getPaleobathymetry().getPolyline());

        new PolylineWrapper(persistedSection.getDomainReference())
                .save(wrapped.getPatchLibrary().getDomainReference().getPolyline());

        
        return true;
    }

    
}

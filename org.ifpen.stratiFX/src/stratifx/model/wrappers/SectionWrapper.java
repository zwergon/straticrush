/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stratifx.model.wrappers;

import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchLibrary;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.model.geology.DomainReference;
import fr.ifp.kronosflow.model.geology.Paleobathymetry;
import fr.ifp.kronosflow.model.wrapper.IPersisted;
import fr.ifp.kronosflow.model.wrapper.IWrapper;
import fr.ifp.kronosflow.model.wrapper.WrapperFactory;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author lecomtje
 */
public class SectionWrapper implements IWrapper<Section> {

    PersistableSection persistedSection = null;

    public SectionWrapper() {
        super();
    }

    @Override
    public boolean load(Section wrapped) {

        if (persistedSection == null) {
            return false;
        }

        // Save all the section information in the persistableSection
        PatchLibrary library = wrapped.getPatchLibrary();

        // search if all patches are in the PersistableSection
        List<Patch> patches = library.getPatches();

        Iterator<Patch> ite = patches.iterator();

        // Remove useless patch
        while (ite.hasNext()) {
            Patch patch = ite.next();
            boolean found = false;
            for (IPersisted persistedPatch : persistedSection.getPatches()) {

                if (patch.getUID().getId() == persistedPatch.getUID()) {
                    found = true;
                    WrapperFactory.load(patch, persistedPatch);
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
        for (IPersisted persistedPatch : persistedSection.getPatches()) {
            boolean found = false;

            for (Patch patch : patches) {
                if (patch.getUID().getId() == persistedPatch.getUID()) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                Patch patch = (Patch)WrapperFactory.build( persistedPatch.getPersistedClass() );
                wrapped.getPatchLibrary().add(patch);
                patch.setPatchLibrary(wrapped.getPatchLibrary());

                WrapperFactory.load(patch, persistedPatch);

            }
        }

        // Chargement des paleobathymetry
        Paleobathymetry paleo = wrapped.getPatchLibrary().getPaleobathymetry();
        if (paleo == null) {
            paleo = new Paleobathymetry();
            wrapped.getPatchLibrary().add(paleo);
        }

        WrapperFactory.load(
                paleo.getPolyline(),
                persistedSection.getPaleobathymetry()
        );

        DomainReference reference = wrapped.getPatchLibrary().getDomainReference();
        if (reference == null) {
            reference = new DomainReference();
            wrapped.getPatchLibrary().add(reference);
        }

        WrapperFactory.load(
                reference.getPolyline(),
                persistedSection.getDomainReference()
        );

        return true;

    }

    @Override
    public boolean save(Section wrapped) {

        if (null == persistedSection) {
            persistedSection = new PersistableSection();
        }

        // Save all the section information in the persistableSection
        PatchLibrary library = wrapped.getPatchLibrary();

        
        Set<IPersisted> persistedPatches = persistedSection.getPatches();
        persistedPatches.clear();
        for (Patch p : library.getPatches()) {
            PersistablePatch persistedPatch = new PersistablePatch();
            WrapperFactory.save(p, persistedPatch);
            persistedPatches.add(persistedPatch);
        }

        // Chargement des paleobathymetry
        PersistablePolyline bathy = new PersistablePolyline();
        WrapperFactory.save(
                wrapped.getPatchLibrary().getPaleobathymetry().getPolyline(),
                bathy
        );
        persistedSection.setPaleobathymetry(bathy);

        PersistablePolyline domain = new PersistablePolyline();
        WrapperFactory.save(
                wrapped.getPatchLibrary().getDomainReference().getPolyline(),
                domain
        );
        persistedSection.setDomainReference(domain);

        return true;
    }

    @Override
    public void setPersisted(Object persisted) {
        persistedSection = (PersistableSection) persisted;
    }

    @Override
    public Object getPersisted() {
        return persistedSection;
    }

}

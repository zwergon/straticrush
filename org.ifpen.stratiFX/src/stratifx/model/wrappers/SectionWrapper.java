/* 
 * Copyright 2017 lecomtje.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package stratifx.model.wrappers;

import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchLibrary;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.model.geology.*;
import fr.ifp.kronosflow.model.wrapper.IWrapper;
import fr.ifp.kronosflow.model.wrapper.WrapperFactory;
import fr.ifp.kronosflow.kernel.polyline.PolyLine;
import stratifx.model.persistable.*;

import java.util.Iterator;
import java.util.List;

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

        GeologicLibrary geologicLibrary =
                (GeologicLibrary)WrapperFactory.build(persistedSection.getGeologicalLibrary().getPersistedClass());
        WrapperFactory.load(
                geologicLibrary,
                persistedSection.getGeologicalLibrary()
        );
        wrapped.setFeatures( geologicLibrary );
        wrapped.setStratigraphicColumn( geologicLibrary.findObject(StratigraphicColumn.class) );

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

                if (patch.getUID().getId() == persistedPatch.getUid()) {
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
                if (patch.getUID().getId() == persistedPatch.getUid()) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                Patch patch = (Patch) WrapperFactory.build(persistedPatch.getPersistedClass());
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
            persistedSection = new PersistableSection(wrapped);
        }

        GeologicLibrary geologicLibrary = wrapped.getFeatures();

        PersistableGeologicLib persistableGeologicLib = new PersistableGeologicLib(geologicLibrary);
        WrapperFactory.save(geologicLibrary, persistableGeologicLib);
        persistedSection.setGeologicLibrary(persistableGeologicLib);


        // Save all the section information in the persistableSection
        PatchLibrary library = wrapped.getPatchLibrary();

        List<IPersisted> persistedPatches = persistedSection.getPatches();
        persistedPatches.clear();
        for (Patch p : library.getPatches()) {
            PersistablePatch persistedPatch = new PersistablePatch(p);
            WrapperFactory.save(p, persistedPatch);
            persistedPatches.add(persistedPatch);
        }

        // Chargement des paleobathymetry
        PolyLine bathymetry = wrapped.getPatchLibrary().getPaleobathymetry().getPolyline();
        PersistablePolyline bathy = new PersistablePolyline(bathymetry);
        WrapperFactory.save(bathymetry, bathy );
        persistedSection.setPaleobathymetry(bathy);


        PolyLine referenceDomain = wrapped.getPatchLibrary().getDomainReference().getPolyline();
        PersistablePolyline domain = new PersistablePolyline(referenceDomain);
        WrapperFactory.save(referenceDomain, domain );
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

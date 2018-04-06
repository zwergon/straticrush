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
package stratifx.model.persistable;

import fr.ifp.kronosflow.model.Section;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author lecomtje
 */
public class PersistableSection extends AbstractPersisted {

    List<IPersisted> patches = new ArrayList<>();

    IPersisted paleo;

    IPersisted reference;

    IPersisted geologicLibrary;

    public PersistableSection() {
    }

    public PersistableSection(Section section ){
        super(section, section.getName());
    }

    public List<IPersisted> getPatches() {
        return patches;
    }

    public void setPatches(List<IPersisted> patches) {
        this.patches = patches;
    }

    public IPersisted getPaleobathymetry() {
        return paleo;
    }

    public void setPaleobathymetry(IPersisted paleo) {
        this.paleo = paleo;
    }

    public IPersisted getDomainReference() {
        return reference;
    }

    public void setDomainReference(IPersisted reference) {
        this.reference = reference;
    }

    public void setGeologicLibrary( IPersisted library ){
        geologicLibrary = library;
    }

    public IPersisted getGeologicalLibrary(){
        return geologicLibrary;
    }
    
    

}

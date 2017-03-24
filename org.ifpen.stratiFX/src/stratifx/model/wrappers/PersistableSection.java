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
    
    IPersisted geologicLibrary;

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
    
    public void setGeologicLibrary( PersistableGeologicLibrary library ){
        geologicLibrary = library;
    }
    
    public IPersisted getGeologicalLibrary(){
        return geologicLibrary;
    }
    
    

}

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
package stratifx.application.griding.triangulation;

import fr.ifp.kronosflow.model.IPatchMeshBuilder;
import fr.ifp.kronosflow.model.builder.TrglPatchBuilder;
import stratifx.application.interaction.tools.AMesh2DInteraction;
import stratifx.canvas.graphics.GScene;

public class TriangulateInteraction extends AMesh2DInteraction {


    public TriangulateInteraction(GScene scene) {
        super(scene);
    }

    @Override
    public IPatchMeshBuilder createBuilder() {
        return new TrglPatchBuilder();
    }
}

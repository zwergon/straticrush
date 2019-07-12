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
package stratifx.application.triangulation;

import fr.ifp.kronosflow.deform.scene.Scene;
import java.util.Collection;
import java.util.List;

import fr.ifp.kronosflow.kernel.geometry.Point2D;
import fr.ifp.kronosflow.mesh.Triangle;
import fr.ifp.kronosflow.mesh.triangulation.Triangulation;
import fr.ifp.kronosflow.model.CompositePatch;
import fr.ifp.kronosflow.model.IPatchMeshBuilder;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.kernel.polyline.Node;
import fr.ifp.kronosflow.model.builder.TrglPatchBuilder;
import fr.ifp.kronosflow.uids.IHandle;
import stratifx.application.interaction.SectionInteraction;
import stratifx.application.interaction.tools.AMesh2DInteraction;
import stratifx.canvas.graphics.GColor;
import stratifx.canvas.graphics.GFXSymbol;
import stratifx.canvas.graphics.GImage;
import stratifx.canvas.graphics.GObject;
import stratifx.canvas.graphics.GScene;
import stratifx.canvas.graphics.GSegment;
import stratifx.canvas.graphics.GStyle;
import stratifx.canvas.interaction.GMouseEvent;

public class TriangulateInteraction extends AMesh2DInteraction {


    public TriangulateInteraction(GScene scene) {
        super(scene);
    }

    @Override
    public IPatchMeshBuilder createBuilder() {
        return new TrglPatchBuilder();
    }
}

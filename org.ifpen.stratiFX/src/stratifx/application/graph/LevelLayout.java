package stratifx.application.graph;

import fr.ifp.kronosflow.deform.scene.sequence.SequenceGraph;
import fr.ifp.kronosflow.deform.scene.sequence.SequenceNode;
import fr.ifp.kronosflow.utils.LOGGER;
import javafx.geometry.Bounds;
import javafx.scene.Node;

import java.util.List;

public class LevelLayout extends Layout {

    public LevelLayout(GraphFX graphFX){
        super(graphFX);
    }

    @Override
    public void execute() {

        SequenceGraph graph = (SequenceGraph) graphFX.getGraph();

        List<SequenceNode> orderedNodes = graph.getOrderedLevel(0);

        int i=0;
        for(SequenceNode node : orderedNodes){
            double x = 100 + i*70;
            double y = 100;

            CellFX cellFX = graphFX.getCell(node);

            Node n = cellFX.getView();
            Bounds bounds = n.getLayoutBounds();
            n.relocate(x + bounds.getMinX(), y+bounds.getMinY());
            i++;
        }

    }
}

package stratifx.application.graph;

import fr.ifp.kronosflow.model.graph.Graph;

public abstract class Layout {

    GraphFX graphFX;

    protected Layout(GraphFX graphFX){
        this.graphFX = graphFX;
    }

    public GraphFX getGraphFX() {
        return graphFX;
    }

    public abstract void execute();
}
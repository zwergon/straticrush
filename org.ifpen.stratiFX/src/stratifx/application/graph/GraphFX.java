package stratifx.application.graph;

import fr.ifp.kronosflow.deform.scene.sequence.SequenceNode;
import fr.ifp.kronosflow.model.graph.Graph;
import fr.ifp.kronosflow.model.graph.GraphEdge;
import fr.ifp.kronosflow.model.graph.Vertex;
import fr.ifp.kronosflow.uids.UID;

import java.util.*;

public class GraphFX {

    Graph graph;

    MouseGestures mouseGestures;

    CellFXLayer cellFXLayer;

    Map<UID, CellFX> vertexToCell;

    public GraphFX(Graph graph) {

        this.graph = graph;

        this.cellFXLayer = new CellFXLayer();

        this.vertexToCell = new HashMap<>();

        mouseGestures = new MouseGestures(this);
    }

    public CellFXLayer getCellLayer() {
        return this.cellFXLayer;
    }

    public Graph getGraph() {
        return graph;
    }

    public Collection<CellFX> getAllCells(){
        return vertexToCell.values();
    }

    public CellFX getCell(SequenceNode node){
        return vertexToCell.get(node.getUID());
    }

    public void initialize() {

        cellFXLayer.getChildren().removeAll();

        Collection<Vertex> vertices = graph.getVertices();
        Collection<CellFX> fxCells = new ArrayList<>(vertices.size());
        for (Vertex vertex : vertices) {
            fxCells.add(createCell(vertex));
        }

        List<GraphEdge> edges = graph.getEdges();
        Collection<EdgeFX> fxEdges = new ArrayList<>(edges.size());
        for (GraphEdge edge : edges) {
            fxEdges.add(createEdge(edge));
        }

        cellFXLayer.getChildren().addAll(fxEdges);
        cellFXLayer.addCells(fxCells);

        // enable dragging of cells
        for (CellFX cellFX : fxCells) {
            mouseGestures.makeDraggable(cellFX.getView());
        }

    }

    private CellFX createCell(Vertex vertex) {

        CellFX fxCell = new CircleCell(vertex);


        vertexToCell.put(vertex.getUID(), fxCell);

        return fxCell;
    }

    private EdgeFX createEdge(GraphEdge edge) {
        CellFX fxCell1 = vertexToCell.get(edge.getFrom().getUID());
        CellFX fxCell2 = vertexToCell.get(edge.getTo().getUID());
        return new EdgeFX(fxCell1, fxCell2);

    }

}
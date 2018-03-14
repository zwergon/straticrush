package stratifx.application.graph;

import javafx.scene.layout.Pane;

import java.util.Collection;

public class CellFXLayer extends Pane {

    public void addCells( Collection<CellFX> fxCells ){
        for( CellFX fxCell : fxCells ){
            getChildren().add(fxCell.getView());
        }
    }

}
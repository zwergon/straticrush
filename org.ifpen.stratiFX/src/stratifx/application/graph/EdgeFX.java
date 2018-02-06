package stratifx.application.graph;

import javafx.scene.Group;
import javafx.scene.shape.Line;

public class EdgeFX extends Group {

    protected CellFX source;
    protected CellFX target;

    Line line;

    public EdgeFX(CellFX source, CellFX target) {

        this.source = source;
        this.target = target;

        line = new Line();

        line.startXProperty().bind( source.layoutXProperty().add(source.getBoundsInParent().getWidth() / 2.0));
        line.startYProperty().bind( source.layoutYProperty().add(source.getBoundsInParent().getHeight() / 2.0));

        line.endXProperty().bind( target.layoutXProperty().add( target.getBoundsInParent().getWidth() / 2.0));
        line.endYProperty().bind( target.layoutYProperty().add( target.getBoundsInParent().getHeight() / 2.0));

        getChildren().add( line);

    }

    public CellFX getSource() {
        return source;
    }

    public CellFX getTarget() {
        return target;
    }

}
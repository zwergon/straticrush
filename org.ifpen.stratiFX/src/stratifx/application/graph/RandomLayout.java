package stratifx.application.graph;

import java.util.Collection;
import java.util.List;
import java.util.Random;

public class RandomLayout extends Layout {


    Random rnd = new Random();

    public RandomLayout(GraphFX graphFX) {
        super(graphFX);
    }

    @Override
    public void execute() {

        Collection<CellFX> cellFXES = graphFX.getAllCells();

        for (CellFX cellFX : cellFXES) {

            double x = rnd.nextDouble() * 500;
            double y = rnd.nextDouble() * 500;

            cellFX.getView().relocate(x, y);

        }

    }

}
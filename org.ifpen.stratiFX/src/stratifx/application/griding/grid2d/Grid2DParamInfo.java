package stratifx.application.griding.grid2d;

import stratifx.application.fxcontrollers.IGridingParameter;
import stratifx.application.fxcontrollers.MenuParamInfo;
import stratifx.application.interaction.InteractionFactory;
import stratifx.application.interaction.InteractionUIAction;

public class Grid2DParamInfo extends MenuParamInfo implements IGridingParameter {

    public Grid2DParamInfo() {
        key = "Grid2D";
        fxmlFile = "../griding/grid2d/Grid2DParamUI.fxml";
        menuId = "GridingMenu";
        uiAction = new InteractionUIAction(key, key);

        InteractionFactory.getInstance().register( key, Grid2DInteraction.class);
    }
}

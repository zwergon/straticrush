package stratifx.application.grid2d;

import stratifx.application.fxcontrollers.IGridingParameter;
import stratifx.application.fxcontrollers.MenuParamInfo;
import stratifx.application.interaction.InteractionFactory;
import stratifx.application.interaction.InteractionUIAction;

public class Grid2DParamInfo extends MenuParamInfo implements IGridingParameter {

    public Grid2DParamInfo() {
        key = "Grid2D";
        fxmlFile = null;
        menuId = "GridingMenu";
        uiAction = new InteractionUIAction(key, key);

        InteractionFactory.getInstance().register( key, Grid2DInteraction.class);
    }
}

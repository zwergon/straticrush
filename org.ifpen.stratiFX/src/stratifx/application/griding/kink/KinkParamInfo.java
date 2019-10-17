package stratifx.application.griding.kink;

import stratifx.application.fxcontrollers.IGridingParameter;
import stratifx.application.fxcontrollers.MenuParamInfo;
import stratifx.application.griding.grid2d.Grid2DInteraction;
import stratifx.application.interaction.InteractionFactory;
import stratifx.application.interaction.InteractionUIAction;

public class KinkParamInfo extends MenuParamInfo implements IGridingParameter {

    public KinkParamInfo() {
        key = "Kink";
        menuId = "GridingMenu";
        uiAction = new InteractionUIAction(key, key);

        InteractionFactory.getInstance().register( key, KinkInteraction.class);
    }
}

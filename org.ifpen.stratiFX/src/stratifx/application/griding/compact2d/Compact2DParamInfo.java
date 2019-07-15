package stratifx.application.griding.compact2d;

import stratifx.application.fxcontrollers.IGridingParameter;
import stratifx.application.fxcontrollers.MenuParamInfo;
import stratifx.application.interaction.InteractionFactory;
import stratifx.application.interaction.InteractionUIAction;

public class Compact2DParamInfo extends MenuParamInfo implements IGridingParameter {
    public Compact2DParamInfo(){
        key = "Compact2D";
        fxmlFile = null;
        menuId = "GridingMenu";
        uiAction = new InteractionUIAction(key, key);

        InteractionFactory.getInstance().register( key, Compact2DInteraction.class);
    }
}

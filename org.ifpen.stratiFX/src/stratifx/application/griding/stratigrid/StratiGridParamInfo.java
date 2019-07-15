package stratifx.application.griding.stratigrid;


import stratifx.application.fxcontrollers.IGridingParameter;
import stratifx.application.fxcontrollers.MenuParamInfo;
import stratifx.application.interaction.InteractionFactory;
import stratifx.application.interaction.InteractionUIAction;

public class StratiGridParamInfo extends MenuParamInfo implements IGridingParameter {

    public StratiGridParamInfo() {
        key = "StratiGrid";
        fxmlFile = null;
        menuId = "GridingMenu";
        uiAction = new InteractionUIAction(key, key);

        InteractionFactory.getInstance().register(key, StratiGridInteraction.class);
    }
}
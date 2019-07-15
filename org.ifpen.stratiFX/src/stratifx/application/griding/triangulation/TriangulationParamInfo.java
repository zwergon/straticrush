package stratifx.application.griding.triangulation;

import stratifx.application.fxcontrollers.IGridingParameter;
import stratifx.application.fxcontrollers.MenuParamInfo;
import stratifx.application.interaction.InteractionFactory;
import stratifx.application.interaction.InteractionUIAction;

public class TriangulationParamInfo extends MenuParamInfo implements IGridingParameter {

    public TriangulationParamInfo() {
        key = "Trgl";
        fxmlFile = null;
        menuId = "GridingMenu";
        uiAction = new InteractionUIAction(key, key);

        InteractionFactory.getInstance().register(key, TriangulateInteraction.class);
    }
}

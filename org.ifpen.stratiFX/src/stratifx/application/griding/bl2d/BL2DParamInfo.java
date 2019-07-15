package stratifx.application.griding.bl2d;

import stratifx.application.fxcontrollers.IGridingParameter;
import stratifx.application.fxcontrollers.MenuParamInfo;
import stratifx.application.interaction.InteractionFactory;
import stratifx.application.interaction.InteractionUIAction;

public class BL2DParamInfo extends MenuParamInfo implements IGridingParameter {

    public BL2DParamInfo(){
        key = "BL2DMesh";
        fxmlFile = "../griding/bl2d/BL2DEnvUI.fxml";
        menuId = "GridingMenu";
        uiAction = new InteractionUIAction(key, key);

        InteractionFactory.getInstance().register( key, BL2DMeshInteraction.class);
    }
}

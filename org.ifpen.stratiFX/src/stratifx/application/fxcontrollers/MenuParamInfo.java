package stratifx.application.fxcontrollers;

import stratifx.application.interaction.InteractionUIAction;

public class MenuParamInfo extends ParamInfo{

    protected String menuId;

    protected InteractionUIAction uiAction;

    public InteractionUIAction getUIAction() {
        return uiAction;
    }

    public String getMenuId() {
        return menuId;
    }
}

package stratifx.application.fxcontrollers;

import javafx.scene.layout.Pane;
import stratifx.application.interaction.InteractionUIAction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MenuParamInfo {

    protected String menuId;

    protected String fxmlFile;

    protected String key;

    protected Pane pane;

    protected InteractionUIAction uiAction;

    static private Map<String, MenuParamInfo> paramsMap = new HashMap<>();

    public String getFxmlFile() {
        return fxmlFile;
    }

    public String getKey(){
        return key;
    }

    public void setPane( Pane pane ){
        this.pane = pane;
    }

    public Pane getPane(){
        return pane;
    }


    public static Map<String, MenuParamInfo> getMenuParamInfos() {
        return paramsMap;
    }

    public static MenuParamInfo getMenuParamInfo( String key ){
        return paramsMap.get(key);
    }

    public static <T> Collection<T> getParameters( Class<T> clazz ){
        Collection<T> parameters = new ArrayList<>();
        for( MenuParamInfo mpi : paramsMap.values() ){
            if ( clazz.isInstance(mpi) ){
                parameters.add((T)mpi);
            }
        }

        return parameters;
    }


    public static void register( MenuParamInfo mpi ){
        paramsMap.put(mpi.key, mpi);
    }


    public InteractionUIAction getUIAction() {
        return uiAction;
    }

    public String getMenuId() {
        return menuId;
    }
}

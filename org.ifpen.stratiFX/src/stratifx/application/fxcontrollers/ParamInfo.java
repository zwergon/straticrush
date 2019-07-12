package stratifx.application.fxcontrollers;

import javafx.scene.layout.Pane;
import stratifx.application.interaction.InteractionUIAction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ParamInfo {

    protected String key;

    protected Pane pane;

    protected String fxmlFile;

    static private Map<String, ParamInfo> paramsMap = new HashMap<>();

    public String getKey(){
        return key;
    }

    public String getFxmlFile() {
        return fxmlFile;
    }

    public void setPane( Pane pane ){
        this.pane = pane;
    }

    public Pane getPane(){
        return pane;
    }


    public static Map<String, ParamInfo> getMenuParamInfos() {
        return paramsMap;
    }

    public static ParamInfo getParamInfo(String key ){
        return paramsMap.get(key);
    }

    public static <T> Collection<T> getParameters(Class<T> clazz ){
        Collection<T> parameters = new ArrayList<>();
        for( ParamInfo mpi : paramsMap.values() ){
            if ( clazz.isInstance(mpi) ){
                parameters.add((T)mpi);
            }
        }

        return parameters;
    }

    public static void register( ParamInfo mpi ){
        paramsMap.put(mpi.key, mpi);
    }

}

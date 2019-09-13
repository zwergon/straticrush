package stratifx.application.main;

public class ModelUIaction  extends UIAction<String> {

    public ModelUIaction( String modelType ) {
        super(MODEL, modelType);
    }

    public String getModelType() {
        return getData();
    }

}

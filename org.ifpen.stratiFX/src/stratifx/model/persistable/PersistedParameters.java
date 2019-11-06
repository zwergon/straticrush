package stratifx.model.persistable;

import fr.ifp.kronosflow.model.style.Style;
import stratifx.application.main.GParameters;

public class PersistedParameters extends AbstractPersisted {

    Style style;

    public PersistedParameters() {
    }

    public PersistedParameters(GParameters parameters) {
        super(parameters, "GParameters");
        this.style = parameters.getInstanceStyle();
    }

    public Style getStyle(){
        return style;
    }

    public void setStyle( Style style ){
        this.style = style;
    }
}

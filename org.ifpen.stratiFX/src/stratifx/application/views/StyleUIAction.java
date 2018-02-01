package stratifx.application.views;

import fr.ifp.kronosflow.model.style.Style;
import stratifx.application.main.UIAction;

public class StyleUIAction extends UIAction<Style> {

    public StyleUIAction(Style style) {
        super(STYLE, style);
    }

}

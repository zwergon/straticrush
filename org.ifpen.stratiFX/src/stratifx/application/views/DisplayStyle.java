package stratifx.application.views;

import fr.ifp.kronosflow.model.style.IStyleProvider;
import fr.ifp.kronosflow.model.style.Style;

public class DisplayStyle implements IStyleProvider {

    private Style style;

    public DisplayStyle(Style style) {
        this.style = style;
    }

    @Override
    public Style getStyle() {
        return style;
    }

    public void setWithLines(boolean withLines) {
        style.setAttributeB("withLines", withLines);
    }

    public boolean getWithLines(){
        return style.findAttributeB("withLines", true);
    }

    public void setWithSolid(boolean withSolid) {
        style.setAttributeB("withSolid", withSolid);
    }

    public boolean getWithSolid(){
        return style.findAttributeB("withSolid", true);
    }

    public void setWithSymbol(boolean withSymbol) {
        style.setAttributeB("withSymbol", withSymbol);
    }

    public boolean getWithSymbol(){
        return style.findAttributeB("withSymbol", false);
    }

    public void setWithAnnotation(boolean withAnnotation) {
        style.setAttributeB("withAnnotation", withAnnotation);
    }

    public boolean getWithAnnotation(){
        return style.findAttributeB("withAnnotation", false);
    }

}

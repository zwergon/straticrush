package stratifx.application.views;

import fr.ifp.kronosflow.model.style.IStyleProvider;
import fr.ifp.kronosflow.model.style.Style;

public class DisplayStyle implements IStyleProvider {

    private final String LINES = "*display*withLines";
    private final String SOLID = "*display*withSolid";
    private final String SYMBOL = "*display*withSymbol";
    private final String ANNOTATION = "*display*withAnnotation";

    private Style style;

    public DisplayStyle(Style style) {
        this.style = style;
    }

    @Override
    public Style getStyle() {
        return style;
    }

    public void setWithLines(boolean withLines) {
        style.setAttributeB( LINES,  withLines);
    }

    public boolean getWithLines(){
        return style.findAttributeB( LINES, true);
    }

    public void setWithSolid(boolean withSolid) {
        style.setAttributeB(SOLID, withSolid);
    }

    public boolean getWithSolid(){
        return style.findAttributeB(SOLID, true);
    }

    public void setWithSymbol(boolean withSymbol) {
        style.setAttributeB(SYMBOL, withSymbol);
    }

    public boolean getWithSymbol(){
        return style.findAttributeB(SYMBOL, false);
    }

    public void setWithAnnotation(boolean withAnnotation) {
        style.setAttributeB(ANNOTATION, withAnnotation);
    }

    public boolean getWithAnnotation(){
        return style.findAttributeB(ANNOTATION, false);
    }

}

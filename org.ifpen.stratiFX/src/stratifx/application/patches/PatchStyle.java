package stratifx.application.patches;

import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.style.IStyleProvider;
import fr.ifp.kronosflow.model.style.Style;

public class PatchStyle implements IStyleProvider {
    private Style style;

    public PatchStyle(Style style){
        this.style = style;
    }

    @Override
    public Style getStyle() {
        return this.style;
    }

    public void setPatch(String name, Boolean bool){
        style.setAttributeB(name,bool);
    }

    public Boolean getPatch(String name){
        return style.findAttributeB(name,true);
    }
}

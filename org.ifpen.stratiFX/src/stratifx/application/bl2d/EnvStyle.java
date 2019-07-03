package stratifx.application.bl2d;

import fr.ifp.kronosflow.model.style.IStyleProvider;
import fr.ifp.kronosflow.model.style.Style;

public class EnvStyle implements IStyleProvider {
    private Style style;

    public EnvStyle(Style style){
        this.style = style;
    }

    @Override
    public Style getStyle() {
        return this.style;
    }

    public void setEnvElement(String element){
        style.setAttribute("element",element);
    }

    public void setBorderPoints(String borderPoints){ style.setAttribute("border points",borderPoints);}

    public void setEnvVerb(Integer verb){
        style.setAttributeI("verb",verb);
    }

    public void setEnvHmin(String hmin){
        style.setAttribute("hmin",hmin);
    }

    public void setEnvHmax(String hmax){
        style.setAttribute("hmax",hmax);
    }

    public void removeEnvElement(){
        style.removeAttribute("element");
    }

    public void removeBorderPoints(){ style.removeAttribute("border points");}

    public void removeEnvVerb(){
        style.removeAttribute("verb");
    }

    public void removeEnvHmin(){
        style.removeAttribute("hmin");
    }

    public void removeEnvHmax(){
        style.removeAttribute("hmax");
    }

    public String getEnvElement(){return style.getAttribute("element");}

    public String getBorderPoints(){return style.getAttribute("border points");}

    public Integer getEnvVerb(){return style.getAttributeI("verb");}

    public String getEnvHmin(){return style.getAttribute("hmin");}

    public String getEnvHmax(){return style.getAttribute("hmax"); }

    public void setBL2DMesh(){style.setAttributeB("BL2DMesh",true);}

    public Boolean getBL2DMesh(){return style.getAttributeB("BL2DMesh");}

    public void removeBL2DMesh(){style.removeAttribute("BL2DMesh");}
}

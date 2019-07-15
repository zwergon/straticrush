package stratifx.application.bl2d;

import fr.ifp.kronosflow.model.style.IStyleProvider;
import fr.ifp.kronosflow.model.style.Style;

public class EnvStyle implements IStyleProvider {

    private Style style;

    private final String ELEMENT = "*bl2d*element";
    private final String VERB = "*bl2d*verb";
    private final String HMIN = "*bl2d*hmin";
    private final String HMAX = "*bl2d*hmax";
    private final String BORDERPOINTS = "*bl2d*borderpoints";
    private final String INNERCONTACTS = "*bl2d*innercontacts";

    public EnvStyle(Style style){
        this.style = style;
    }

    @Override
    public Style getStyle() {
        return this.style;
    }



    public String getEnvElement(){return style.findAttributeS(ELEMENT,"Triangular");}

    public String getEnvVerb(){return style.findAttributeS(VERB,null);}

    public String getEnvHmin(){return style.findAttributeS(HMIN,null);}

    public String getEnvHmax(){return style.findAttributeS(HMAX,null);}

    public String getBORDERPOINTS(){return style.findAttributeS(BORDERPOINTS,"No");}

    public String getINNERCONTACTS(){return style.findAttributeS(INNERCONTACTS,"None");}

    public void setEnvElement(String element){ style.setAttribute(ELEMENT,element);}

    public void setEnvVerb(String verb){ style.setAttribute(VERB,verb);}

    public void setEnvHmin(String hmin){ style.setAttribute(HMIN ,hmin);}

    public void setEnvHmax(String hmax){ style.setAttribute(HMAX,hmax);}

    public void setBORDERPOINTS(String borderpoints){ style.setAttribute(BORDERPOINTS,borderpoints);}

    public void setINNERCONTACTS(String innercontacts){ style.setAttribute(INNERCONTACTS,innercontacts);}

}

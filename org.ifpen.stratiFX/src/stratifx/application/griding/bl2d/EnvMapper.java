package stratifx.application.griding.bl2d;

import fr.ifpen.kine.BL2D.Env;
import stratifx.application.main.GParameters;

public class EnvMapper{

    public EnvMapper(){}

    public Env defaultEnv(){

        Env env = new Env();

        BL2DStyle BL2DStyle = new BL2DStyle(GParameters.getInstanceStyle());

        if(BL2DStyle.getEnvElement().equals("Triangular")){
            env.setElement("p1");
        }
        if(BL2DStyle.getEnvElement().equals("Quad-dominant")){
            env.setElement("q1.0");
        }
        if(BL2DStyle.getEnvElement().equals("Quadrangular")){
            env.setElement("q1.1");
        }

        String verb = BL2DStyle.getEnvVerb();
        String hmin = BL2DStyle.getEnvHmin();
        String hmax = BL2DStyle.getEnvHmax();

        if(verb!=null){
            env.setVerb(Integer.valueOf(verb));
        }
        if(hmin!=null){
            env.setHmin(Double.valueOf(hmin));
        }
        if(hmax!=null){
            env.setHmax(Double.valueOf(hmax));
        }

        return env;
    }

}

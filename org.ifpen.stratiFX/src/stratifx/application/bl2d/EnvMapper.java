package stratifx.application.bl2d;

import fr.ifpen.kine.BL2D.Env;
import stratifx.application.main.GParameters;

public class EnvMapper{

    public EnvMapper(){}

    public Env defaultEnv(){

        Env env = new Env();

        EnvStyle envStyle = new EnvStyle(GParameters.getStyle());

        if(envStyle.getEnvElement().equals("Triangular")){
            env.setElement("p1");
        }
        if(envStyle.getEnvElement().equals("Quad-dominant")){
            env.setElement("q1.0");
        }
        if(envStyle.getEnvElement().equals("Quadrangular")){
            env.setElement("q1.1");
        }

        String verb = envStyle.getEnvVerb();
        String hmin = envStyle.getEnvHmin();
        String hmax = envStyle.getEnvHmax();

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

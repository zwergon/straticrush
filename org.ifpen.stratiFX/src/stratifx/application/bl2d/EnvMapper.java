package stratifx.application.bl2d;

import fr.ifpen.kine.BL2D.Env;
import stratifx.application.main.GParameters;

public class EnvMapper{

    public EnvMapper(){}

    public Env defaultEnv(){
        Env env = new Env();
        EnvStyle envStyle = new EnvStyle(GParameters.getStyle());
        env.setElement(envStyle.getEnvElement());
        env.setVerb(envStyle.getEnvVerb());
        String hmin = envStyle.getEnvHmin();
        String hmax = envStyle.getEnvHmax();
        if(hmin!=null){
            env.setHmin(Double.valueOf(hmin));
        }
        if(hmax!=null){
            env.setHmax(Double.valueOf(hmax));
        }
        return env;
    }

}

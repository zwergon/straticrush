package stratifx.application.bl2d;

import fr.ifpen.kine.BL2D.Env;

public class EnvMapper {

    public EnvMapper(){}

    public Env defaultEnv(){
        Env env = new Env();
        env.setElement("q1.1");
        env.setHmax(200.0);
        return env;
    }
}

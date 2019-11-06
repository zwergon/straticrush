package stratifx.model.loader;

import fr.ifp.kronosflow.model.style.Style;
import fr.ifp.kronosflow.utils.LOGGER;
import fr.ifpen.kine.crypto.AsymmetricCryptography;
import fr.ifpen.kine.crypto.PublicBytes;
import org.json.simple.JSONObject;
import stratifx.application.webkine.WebServiceStyle;
import stratifx.model.persistable.IPersisted;
import stratifx.model.persistable.PersistedParameters;

import java.security.PublicKey;

public class ParametersLoader  extends AbstractLoader{
    @Override
    public IPersisted create() {
        return new PersistedParameters();
    }

    @Override
    public IPersisted load(JSONObject object) {
        PersistedParameters persistedParameters = (PersistedParameters)data;

        Style style = new Style();
        WebServiceStyle serviceStyle = new WebServiceStyle(style);

        serviceStyle.setHost((String)object.get(serviceStyle.HOST));
        serviceStyle.setPort( ((Long)object.get(serviceStyle.PORT)).intValue());
        serviceStyle.setLogin((String)object.get(serviceStyle.LOGIN));

        try {
            AsymmetricCryptography ac = new AsymmetricCryptography();
            PublicKey publicKey = ac.getPublic(PublicBytes.get());
            serviceStyle.setPassWord(ac.decryptText((String)object.get(serviceStyle.PASSWD), publicKey));
        }
        catch(Exception ex){
            LOGGER.error("unable to decode passwd", getClass());
        }

        persistedParameters.setStyle(style);

        return persistedParameters;
    }
}

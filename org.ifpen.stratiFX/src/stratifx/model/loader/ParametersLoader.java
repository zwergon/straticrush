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

        Object retrieved = object.get(serviceStyle.HOST);
        if ( retrieved != null ) {
            serviceStyle.setHost((String) retrieved);
        }

        retrieved = object.get(serviceStyle.PORT);
        if ( retrieved != null ) {
            serviceStyle.setPort( ((Long)retrieved).intValue());
        }

        retrieved = object.get(serviceStyle.LOGIN);
        if ( retrieved != null ) {
            serviceStyle.setLogin((String) retrieved);
        }


        retrieved = object.get(serviceStyle.WITHPROXY);
        if ( retrieved != null ) {
            serviceStyle.setWithProxy((Boolean) retrieved);
        }

        retrieved = object.get(serviceStyle.PROXY);
        Object retrieved2 = object.get(serviceStyle.PROXYPORT);

        if (( retrieved != null ) && ( retrieved2 != null )) {
            serviceStyle.setProxy((String) retrieved, ((Long) retrieved2).intValue());
        }
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

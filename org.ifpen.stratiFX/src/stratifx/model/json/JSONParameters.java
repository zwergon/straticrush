package stratifx.model.json;


import fr.ifp.kronosflow.model.style.Style;
import fr.ifp.kronosflow.utils.LOGGER;
import fr.ifpen.kine.crypto.AsymmetricCryptography;
import fr.ifpen.kine.crypto.PrivateBytes;
import stratifx.application.webkine.WebServiceStyle;
import stratifx.model.persistable.PersistedParameters;

import java.security.PrivateKey;


public class JSONParameters extends JSONPersisted {

    public JSONParameters(PersistedParameters persisted) {
        super(persisted);

        try {
            AsymmetricCryptography ac = new AsymmetricCryptography();
            Style style = persisted.getStyle();

            WebServiceStyle webStyle = new WebServiceStyle(style);
            put(webStyle.HOST, webStyle.getHost());
            put(webStyle.PORT, webStyle.getPort());
            put(webStyle.LOGIN, webStyle.getLogin());
            put(webStyle.WITHPROXY, webStyle.hasProxy());
            put(webStyle.PROXY, webStyle.getProxy());
            put(webStyle.PROXYPORT, webStyle.getProxyPort());
            PrivateKey privateKey = ac.getPrivate(PrivateBytes.get());
            put(webStyle.PASSWD, ac.encryptText(webStyle.getPassWord(), privateKey));
        }
        catch(Exception ex ){
            LOGGER.error( "unable to serialize PersistedParameters", getClass());
        }

        }
}

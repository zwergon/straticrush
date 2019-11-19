package stratifx.application.webkine;

import fr.ifp.kronosflow.model.style.IStyleProvider;
import fr.ifp.kronosflow.model.style.Style;

public class WebServiceStyle implements IStyleProvider {

    public final String PORT = "*webservice*port";
    public final String HOST = "*webservice*host";
    public final String LOGIN = "*webservice*login";
    public final String PASSWD = "*webservice*password";
    public final String WITHPROXY = "*webservice*with_proxy";
    public final String PROXY = "*webservice*proxy";
    public final String PROXYPORT = "*webservice*proxy_port";

    private Style style;

    public WebServiceStyle(Style style) {
        this.style = style;
    }

    @Override
    public Style getStyle() {
        return style;
    }

    public void setPort( int port ){
        style.setAttributeI(PORT, port);
    }

    public int getPort(){
        return style.findAttributeI(PORT, 8090);
    }

    public void setHost( String host ){
        style.setAttribute(HOST, host);
    }

    public String getHost(){
        return style.findAttributeS(HOST, "localhost");
    }

    public void setLogin(String login){
        style.setAttribute(LOGIN, login);
    }

    public String getLogin(){
        return style.findAttributeS(LOGIN, "stratifx");
    }

    public void setPassWord(String pw){
        style.setAttribute(PASSWD, pw);
    }

    public String getPassWord(){
        return style.findAttributeS(PASSWD, "6Y7RzeMs3");
    }

    public String getBaseUrl() {
        return String.format("http://%s:%d", getHost(), getPort());
    }

    public boolean hasProxy() { return style.findAttributeB(WITHPROXY, false); }

    public void setWithProxy( boolean with_proxy ) {
        style.setAttributeB(WITHPROXY, with_proxy);
    }

    public int getProxyPort() { return style.findAttributeI(PROXYPORT, 8082); }

    public String getProxy() { return style.findAttributeS(PROXY, "irproxyweb1"); }

    public void setProxy( String proxy, int proxy_port ){
        if (!proxy.isEmpty()){
            style.setAttributeB(WITHPROXY, true);
            style.setAttribute(PROXY, proxy);
            style.setAttributeI(PROXYPORT, proxy_port);
        }
        else {
            style.setAttributeB(WITHPROXY, false);
        }
    }


}

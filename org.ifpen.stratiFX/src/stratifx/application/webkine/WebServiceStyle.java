package stratifx.application.webkine;

import fr.ifp.kronosflow.model.style.IStyleProvider;
import fr.ifp.kronosflow.model.style.Style;

public class WebServiceStyle implements IStyleProvider {

    private final String PORT = "*webservice*port";
    private final String HOST = "*webservice*host";
    private final String LOGIN = "*webservice*login";
    private final String PASSWD = "*webservice*password";

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
}

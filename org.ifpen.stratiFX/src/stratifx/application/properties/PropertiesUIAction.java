package stratifx.application.properties;

import stratifx.application.UIAction;

public class PropertiesUIAction extends UIAction {
	
	String propertyName;
	
	public PropertiesUIAction( String property ) {
		super(Properties);
		this.propertyName = property;
	}

	public String getProperty() {
		return propertyName;
	}

}

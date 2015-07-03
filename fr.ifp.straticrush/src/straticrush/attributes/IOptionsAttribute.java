package straticrush.attributes;

import org.eclipse.swt.widgets.Composite;

public interface IOptionsAttribute {
	
	String getLabel();
	
	Composite createUI(Composite parent, int style);

	void apply();

}

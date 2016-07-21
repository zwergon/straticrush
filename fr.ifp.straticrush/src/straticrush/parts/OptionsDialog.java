package straticrush.parts;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import straticrush.attributes.IOptionsAttribute;

public class OptionsDialog extends Dialog {
	
	List<IOptionsAttribute> attributes = new ArrayList<IOptionsAttribute>();
	
	
	@Inject
	public OptionsDialog(@Named(IServiceConstants.ACTIVE_SHELL) Shell parentShell) {
		super(parentShell);
		setShellStyle( SWT.RESIZE | SWT.CLOSE | SWT.MODELESS | SWT.BORDER | SWT.TITLE);
		setBlockOnOpen(false);
	}
	

	@Override
	protected boolean isResizable() {
		return true;
	}
	

	public Control createDialogArea(Composite parent) {
		
		
		Composite area = (Composite) super.createDialogArea(parent);
		area.setLayout(new FillLayout());
			
		TabFolder tabFolder = new TabFolder(area, SWT.BORDER);
		

		ViewAttribute view = new ViewAttribute();
		TabItem item = new TabItem (tabFolder, SWT.NONE);
		item.setText ( view.getLabel() );
		item.setControl ( view.createUI(tabFolder, SWT.NONE) );
		
		attributes.add( view );
		
		
		DynamicAttribute dynamic = new DynamicAttribute();

		item = new TabItem (tabFolder, SWT.NONE);
		item.setText ( dynamic.getLabel() );
		item.setControl ( dynamic.createUI(tabFolder, SWT.NONE) );
		
		attributes.add( dynamic );
		
		tabFolder.pack ();
		
		
		return area;
	}
	
	@Override
	protected void okPressed() {
		super.okPressed();
		for( IOptionsAttribute attribute : attributes ){
			attribute.apply();
		}
	}
	
	
}

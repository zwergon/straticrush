package straticrush.geoscheduler.tree;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class TreeDialog extends Dialog {
	
	@Inject
	public TreeDialog(@Named(IServiceConstants.ACTIVE_SHELL) Shell parentShell) {
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
			
				
		return area;
	}
	

	

}

package straticrush.parts;



import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import straticrush.attributes.IOptionsAttribute;

public class DynamicAttribute implements IOptionsAttribute {
	

	@Override
	public String getLabel() {
		return "Deformation";
	}

	
	@Override
	public Composite createUI(Composite parent, int style) {
		
		Composite composite = new Composite(parent, style);
		
		FillLayout fillLayout = new FillLayout();
		fillLayout.type = SWT.HORIZONTAL;
		
		
		composite.setLayout(new GridLayout(1, false));

		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
	
		// Creating the Screen
		Section section = toolkit.createSection(composite, Section.DESCRIPTION
				| Section.TITLE_BAR);
		section.setText("Integrator Settings"); //$NON-NLS-1$
		section.setDescription("Select how to integrate Newton law");
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		gridData.horizontalSpan = 1;
		gridData.grabExcessHorizontalSpace = true;
		section.setLayoutData(gridData);
		
		// Composite for storing the data
		Composite client = toolkit.createComposite(section, SWT.WRAP);
		
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		gridData.horizontalSpan = 1;
		gridData.grabExcessHorizontalSpace = true;
		client.setLayoutData(gridData);
		
		RowLayout rowLayout = new RowLayout();
		rowLayout.wrap = false;
		rowLayout.pack = false;
		rowLayout.justify = false;
		rowLayout.type = SWT.VERTICAL;
		rowLayout.spacing = 0;
		client.setLayout(rowLayout);
		
		Button button = new Button(client, SWT.RADIO);
		button.setText("Forward Euler");
		button = new Button(client, SWT.RADIO);
		button.setText("Backward Euler");
		button = new Button(client, SWT.RADIO);
		button.setText("Verlet");
		button.setSelection(true);
		
		section.setClient( client );
		  
		
		section = toolkit.createSection(composite, Section.DESCRIPTION
				| Section.TITLE_BAR);
		section.setText("Stiffness Settings"); //$NON-NLS-1$
		section.setDescription("Select which kind of Stiffness");
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		gridData.horizontalSpan = 1;
		gridData.grabExcessHorizontalSpace = true;
		section.setLayoutData(gridData);
		
		client = toolkit.createComposite(section, SWT.WRAP);
		rowLayout = new RowLayout();
		rowLayout.wrap = false;
		rowLayout.pack = false;
		rowLayout.justify = false;
		rowLayout.type = SWT.VERTICAL;
		rowLayout.spacing = 0;
		client.setLayout(rowLayout);
		
		button = new Button(client, SWT.RADIO);
		button.setText("Springs");
		button = new Button(client, SWT.RADIO);
		button.setText("Triangles");
		button.setSelection(true);
		
		section.setClient( client );


		return composite;
	}


	@Override
	public void apply() {
		// TODO Auto-generated method stub
		
	}

	
}

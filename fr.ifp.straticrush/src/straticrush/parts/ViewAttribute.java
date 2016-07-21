package straticrush.parts;

import java.util.Locale;

import no.geosoft.cc.graphics.GColorMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import straticrush.attributes.IOptionsAttribute;
import straticrush.interaction.StratiCrushServices;
import straticrush.view.Plot;
import straticrush.view.StratiWindow;

public class ViewAttribute implements IOptionsAttribute{
	
	ColorsLabel colorLabel;
	Text cmaxLabel ;
	Text cminLabel;
	
	

	@Override
	public String getLabel() {
		return "View";
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
		section.setText("ColorMap"); //$NON-NLS-1$
		section.setDescription("Modify colors applied to Section");
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
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		client.setLayout(gridLayout);
		
		StratiWindow window =  StratiCrushServices.getInstance().getWindow();
		Plot plot = window.getPlot();
		GColorMap colormap = plot.getColorMap();
		
		colorLabel = new ColorsLabel(client, colormap );
		gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.verticalSpan = 2;
		colorLabel.setLayoutData(gridData);
		
		cmaxLabel = new Text( client, SWT.SINGLE );
		cmaxLabel.setEditable(false);
		cmaxLabel.setText( String.format(Locale.US, "%8.3f", colormap.getCurrentMax() ) );
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = SWT.BEGINNING;
		cmaxLabel.setLayoutData(gridData);
		cmaxLabel.addMouseWheelListener(  new MaxMouseListener(colormap) );

		cminLabel = new Text( client, SWT.SINGLE );
		cminLabel.setEditable(false);
		cminLabel.setText( String.format(Locale.US, "%8.3f", colormap.getCurrentMin() ) );
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = SWT.END;
		cminLabel.setLayoutData(gridData);
		cminLabel.addMouseWheelListener( new MinMouseListener(colormap) );
		
		
		section.setClient( client );
		  
		return composite;
	}

	@Override
	public void apply() {
		// TODO Auto-generated method stub
		
	}
	
	private class MaxMouseListener implements MouseWheelListener {
		
		GColorMap colormap;
		
		public MaxMouseListener(  GColorMap cmap ) {
			colormap = cmap;
		}
		
		@Override
		public void mouseScrolled(MouseEvent e) {	
			
			double step = (colormap.getMax()-colormap.getMin())/(double)colormap.getNColors();
			double value = Double.valueOf(cmaxLabel.getText()) + Math.signum(e.count)*step;
			
			
			if ( value >=  colormap.getMax() ){
				value = colormap.getMax();
			}
			if ( value <= colormap.getCurrentMin() ){
				value = colormap.getCurrentMin();
			}
			
			
			colormap.setCurrentMax( value  );
			cmaxLabel.setText( String.format(Locale.US, "%8.3f", value) );
			colorLabel.redraw();
			
			StratiCrushServices.getInstance().getWindow().refresh();
		}
		
	}
	
	private class MinMouseListener implements MouseWheelListener {
		
		GColorMap colormap;
		
		public MinMouseListener(  GColorMap cmap ) {
			colormap = cmap;
		}
		
		@Override
		public void mouseScrolled(MouseEvent e) {		
			
			double step = (colormap.getMax()-colormap.getMin())/(double)colormap.getNColors();
			double value = Double.valueOf(cminLabel.getText()) + Math.signum(e.count)*step;
			
			
			if ( value <=  colormap.getMin() ){
				value = colormap.getMin();
			}
			if ( value >= colormap.getCurrentMax() ){
				value = colormap.getCurrentMax();
			}
			
			
			colormap.setCurrentMin( value  );
			cminLabel.setText( String.format(Locale.US, "%8.3f", value) );
			colorLabel.redraw();
			
			StratiCrushServices.getInstance().getWindow().refresh();
			
		}
		
	}

}

package straticrush.view;

import java.awt.Insets;

import straticrush.interaction.StratiCrushServices;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.property.IPropertyValue;
import fr.ifp.kronosflow.property.Property;
import fr.ifp.kronosflow.property.PropertyStatistic;
import fr.ifp.kronosflow.property.PropertyStyle;
import fr.ifp.kronosflow.utils.UID;
import no.geosoft.cc.graphics.GColorMap;
import no.geosoft.cc.graphics.GScene;
import no.geosoft.cc.graphics.GWindow;

public class Plot extends GScene {

	private Insets   insets_;
	
	GColorMap colormap;
	
	Property currentProp;


	public Plot (GWindow window, Insets insets)
	{
		super (window);
		insets_ = insets;
		
		colormap = new GColorMap();
		colormap.createDefaultColormap();
	}


	protected void resize (double dx, double dy)
	{
		super.resize (dx, dy);
		setViewport (insets_.left, insets_.top,
				getWindow().getWidth() - insets_.left - insets_.right,
				getWindow().getHeight() - insets_.top - insets_.bottom);
	}
	
	public Property getCurrentProp() {
		
		Section section = StratiCrushServices.getInstance().getSection();

		Property property = null;
		PropertyStyle propStyle = new PropertyStyle(section.getStyle());
		UID currentPropUID = propStyle.getCurrent();
		if ( currentPropUID != null ){
			property = section.getPropertyDB().findByUID(currentPropUID);

			if ( null != property ){
				updateColormap(property);
			}
		}

		return property;
	}

	private void updateColormap(Property property ) {

		PropertyStatistic stat = PropertyStatistic.create(property);

		if ( stat == null ){
			return;
		}

		stat.compute();

		IPropertyValue min = stat.min();
		IPropertyValue max = stat.max();

		colormap.setMinMax( min.real(), max.real() );
	}


	public GColorMap getColorMap() {
		return colormap;
	}
}



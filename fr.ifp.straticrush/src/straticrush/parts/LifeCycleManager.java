package straticrush.parts;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;

import straticrush.filters.ImportSVGFile;
import straticrush.properties.PoissonComputer;
import straticrush.properties.PorosityComputer;
import straticrush.properties.SolidSurfaceComputer;
import straticrush.properties.SurfacePropertyComputer;
import straticrush.properties.XYPropertyComputer;
import fr.ifp.jdeform.deformations.StrateOrientationComputer;
import fr.ifp.kronosflow.controllers.property.PropertyController;
import fr.ifp.kronosflow.extensions.IExtension;
import fr.ifp.kronosflow.extensions.ray.RayExtension;
import fr.ifp.kronosflow.geoscheduler.GeoschedulerSection;
import fr.ifp.kronosflow.geoscheduler.ISectionState;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.model.explicit.ExplicitPolyLine;
import fr.ifp.kronosflow.model.filters.SectionFactory;
import fr.ifp.kronosflow.polyline.PolyLine;
import fr.ifp.kronosflow.property.IPropertyAccessor;
import fr.ifp.kronosflow.property.IPropertyInterpolatorBuilder;
import fr.ifp.kronosflow.property.map.MapPropertyAccessor;
import fr.ifp.kronosflow.property.map.MapPropertyInterpolatorBuilder;
import fr.ifp.kronosflow.utils.KronosContext;

public class LifeCycleManager {
	
	public static class DummySectionState implements ISectionState {
		
		
		@Override
		public void setAge(double age) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public double getAge() {
			// TODO Auto-generated method stub
			return 0;
		}
		@Override
		public void setComment(String comment) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public String getComment() {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public void setSection(Section section) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public Section getSection() {
			// TODO Auto-generated method stub
			return null;
		}
		
	};
	
	@PostContextCreate
	public void startup(IEclipseContext context) {
		
		PropertyController.registerBuilder("Porosity", new PorosityComputer.Builder());
		PropertyController.registerBuilder("Poisson", new PoissonComputer.Builder());
		PropertyController.registerBuilder("Surface", new SurfacePropertyComputer.Builder() );
		PropertyController.registerBuilder("XY", new XYPropertyComputer.Builder() );
		PropertyController.registerBuilder("Strate Orientation", new StrateOrientationComputer.Builder() );
		PropertyController.registerBuilder("SolidSurface", new SolidSurfaceComputer.Builder() );
		
		SectionFactory.registerImporter( "svg", new ImportSVGFile.Builder() );
		
		
		KronosContext.registerClass( Section.class,  GeoschedulerSection.class );
		KronosContext.registerClass( ISectionState.class, DummySectionState.class );
		KronosContext.registerClass( PolyLine.class, ExplicitPolyLine.class );
		KronosContext.registerClass( IExtension.class, RayExtension.class );
		KronosContext.registerClass( IPropertyAccessor.class, MapPropertyAccessor.class );
		KronosContext.registerClass( IPropertyInterpolatorBuilder.class, MapPropertyInterpolatorBuilder.class );
	}

}

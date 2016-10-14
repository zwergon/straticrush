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
import fr.ifp.kronosflow.geoscheduler.GeoschedulerSection;
import fr.ifp.kronosflow.geoscheduler.ISectionState;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.model.explicit.ExplicitPolyLine;
import fr.ifp.kronosflow.model.filters.SectionFactory;
import fr.ifp.kronosflow.polyline.PolyLine;
import fr.ifp.kronosflow.utils.KronosContext;

public class LifeCycleManager {
	
	public static class DummySectionState implements ISectionState {
		
		public Section getSection() {
			return section;
		}
		public void setSection(Section section) {
			this.section = section;
		}
		public int getAge() {
			return age;
		}
		public void setAge(int age) {
			this.age = age;
		}
		Section section;
		int age;
		
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
	}

}

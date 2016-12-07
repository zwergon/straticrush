/*******************************************************************************
 * Copyright (c) 2010 - 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Lars Vogel <lars.Vogel@gmail.com> - Bug 419770
 *******************************************************************************/
package straticrush.parts;

import java.io.File;
import java.util.Map;

import javax.annotation.PostConstruct;

import no.geosoft.cc.graphics.GInteraction;
import no.geosoft.cc.interfaces.ICanvas;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.swt.widgets.Composite;

import straticrush.interaction.DilatationInteraction;
import straticrush.interaction.FlattenInteraction;
import straticrush.interaction.NodeMoveInteraction;
import straticrush.interaction.RemoveUnitInteraction;
import straticrush.interaction.ResetGeometryInteraction;
import straticrush.interaction.StratiCrushServices;
import straticrush.interaction.TopBorderInteraction;
import straticrush.interaction.TriangulateInteraction;
import straticrush.interaction.ZoomInteraction;
import straticrush.menu.Menu;
import straticrush.menu.MenuInteraction;
import straticrush.view.Plot;
import straticrush.view.StratiWindow;
import fr.ifp.kronosflow.geometry.RectD;
import fr.ifp.kronosflow.geoscheduler.GeoschedulerSection;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchLibrary;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.model.factory.ModelFactory.ComplexityType;
import fr.ifp.kronosflow.model.factory.ModelFactory.GridType;
import fr.ifp.kronosflow.model.factory.ModelFactory.NatureType;
import fr.ifp.kronosflow.model.factory.SceneStyle;
import fr.ifp.kronosflow.model.filters.SectionFactory;
import fr.ifp.kronosflow.utils.KronosContext;
import fr.ifp.kronosflow.utils.LOGGER;


public class SectionPart  {

	private StratiWindow   window_;
	Section section;

	private Menu      menu;

	@PostConstruct
	public void createComposite(Composite parent) {
		window_ = new StratiWindow( parent );  
	}

	@Focus
	public void setFocus() {
		ICanvas canvas = window_.getCanvas();
		canvas.setFocus();
	}

	@Persist
	public void save() {
	}


	public Section getSection(){
		return section;
	}
	
	public StratiWindow getWindow(){
		return window_;
	}


	public void openMenu(boolean checked) {

		if ( checked ){
			// Create a value specific "plot" scene
			menu = new Menu (window_);


			menu.populate(section);
			window_.startInteraction( new MenuInteraction( menu ) );

		}
		else {
			window_.stopInteraction();
			menu.removeAll();
			window_.removeScene(menu);
		}

		window_.update();

	}


	public void loadSection( String filename ){
		
		String basename = filename.substring(0, filename.lastIndexOf('.'));

		LOGGER.debug("load " + basename , this.getClass() );

		section = KronosContext.make(Section.class);
		section.setName(basename);
		
		SceneStyle sceneStyle = new SceneStyle(section.getStyle());
		sceneStyle.setNatureType(NatureType.EXPLICIT);
		sceneStyle.setGridType(GridType.TRGL);
		sceneStyle.setComplexityType(ComplexityType.SINGLE);

		StratiCrushServices.getInstance().setSection(section);

		PatchLibrary patchLib = section.getPatchLibrary();

		Map<String,String> unitMap = SectionFactory.createBorders( filename, section );
		
		File f = new File(basename + ".xml");
		if(f.exists() && !f.isDirectory()) { 
			SectionFactory.createDummyUnit( basename + ".xml", section, unitMap);
		}
		else {
			f = new File(basename + ".unit");
			if(f.exists() && !f.isDirectory()) { 
				SectionFactory.createDummyUnit( basename + ".unit", section, unitMap);
			}
		}


		Plot plot = window_.getPlot();
		plot.destroyAllViews();

		// Create a graphic object
		for( Patch patch : patchLib.getPatches() ){
			plot.createView( patch );   
		}

		plot.createView( patchLib.getPaleobathymetry() );

		RectD bbox = patchLib.getBoundingBox();
		plot.setWorldExtent( bbox.left, bbox.bottom, bbox.width(), -bbox.height());
		
		
		
		bbox.inset( -bbox.width()/10., -bbox.height()/10. );
		section.getPropertyDB().setDomain(bbox);


		window_.update();
	}

	public void startInteraction( String interactionType, String deformationType ){


		Plot plot = window_.getPlot();
		
		GInteraction interaction = null;
		if ( interactionType.equals("Zoom") ){
			interaction = new ZoomInteraction(plot);
		}
		else if ( interactionType.equals("Reset") ){
			interaction = new ResetGeometryInteraction(plot);
		}
		else if ( interactionType.equals("NodeMoveInteraction") ) {
			interaction = new NodeMoveInteraction(plot, deformationType) ;
		}
		else if ( interactionType.equals("FlattenInteraction") ) {
			interaction = new FlattenInteraction(plot, deformationType ) ;
		}
		else if ( interactionType.equals("DilatationInteraction") ) {
			interaction = new DilatationInteraction(plot, deformationType ) ;
		}
		else if ( interactionType.equals("TopBorderInteraction") ) {
			interaction = new TopBorderInteraction(plot, deformationType );
		}
		else if ( interactionType.equals("Triangulate") ) {
			interaction = new TriangulateInteraction(plot, interactionType);
		}
		else if ( interactionType.equals("RemoveUnit") ) {
			interaction = new RemoveUnitInteraction(plot, interactionType);
		}

		if ( interaction == null ){
			return;
		}

		window_.startInteraction( interaction );
	}

	public void loadMesh(String basename) {
		LOGGER.debug("load " + basename , this.getClass() );

		section = KronosContext.make(Section.class);
		section.setName(basename);

		StratiCrushServices.getInstance().setSection(section);

		PatchLibrary patchLib = section.getPatchLibrary();

		SectionFactory.createDummyMesh( basename + ".msh", section );

		
		Plot plot = window_.getPlot();
		plot.destroyAllViews();

		// Create a graphic object
		for( Patch patch : patchLib.getPatches() ){
			plot.createView( patch );   
		}

		plot.createView( patchLib.getPaleobathymetry() );

		RectD bbox = patchLib.getBoundingBox();
		plot.setWorldExtent( bbox.left, bbox.bottom, bbox.width(), -bbox.height());
		
		section.getPropertyDB().setDomain(bbox);

		window_.update();

	}

}
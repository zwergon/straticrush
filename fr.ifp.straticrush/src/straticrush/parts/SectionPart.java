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

import java.awt.Insets;
import java.util.Map;

import javax.annotation.PostConstruct;

import no.geosoft.cc.graphics.GColor;
import no.geosoft.cc.graphics.GInteraction;
import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GScene;
import no.geosoft.cc.graphics.GWindow;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.swt.widgets.Composite;

import straticrush.interaction.FlattenInteraction;
import straticrush.interaction.NodeMoveInteraction;
import straticrush.interaction.ResetGeometryInteraction;
import straticrush.interaction.StratiCrushServices;
import straticrush.interaction.TopBorderInteraction;
import straticrush.interaction.TriangulateInteraction;
import straticrush.interaction.ZoomInteraction;
import straticrush.menu.Menu;
import straticrush.menu.MenuInteraction;
import straticrush.view.Annotation;
import straticrush.view.Plot;
import straticrush.view.ViewFactory;
import fr.ifp.jdeform.dummy.MeshObjectFactory;
import fr.ifp.kronosflow.geometry.RectD;
import fr.ifp.kronosflow.geoscheduler.GeoschedulerSection;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchLibrary;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.utils.LOGGER;


public class SectionPart  {

	private GWindow   window_;
	Section section;

	private Menu      menu;

	@PostConstruct
	public void createComposite(Composite parent) {
		
		
		window_ = new GWindow( parent, new GColor(0.8f, 0.8f, 0.8f) );
		StratiCrushServices.getInstance().setWindow(window_);
	
		// Create the graphic canvas

		// Definition of exact chart location inside window
		Insets insets = new Insets(80, 60, 20, 20);

		// Create a "background" device oriented annotation scene
		GScene annotationScene = new GScene (window_);
		GObject annotation = new Annotation (insets);
		annotationScene.add (annotation);


		// Create a value specific "plot" scene
		GScene plot = new Plot (window_, insets);
		annotationScene.setUserData (plot);
		plot.shouldWorldExtentFitViewport (false);
		plot.shouldZoomOnResize (false);   
		
	
		window_.startInteraction (new ZoomInteraction(plot));
		
	}

	@Focus
	public void setFocus() {
		
	}

	@Persist
	public void save() {
	}
	
	
	public Section getSection(){
		return section;
	}
	
	public Plot getPlot() {
		Plot scene = null;

		for( GScene sc : window_.getScenes() ){
			if ( sc instanceof Plot ) {
				scene = (Plot)sc;
				break;
			}
		}

		return scene;
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
	  
	  
	  public void loadSection( String basename ){
	      
	      LOGGER.debug("load " + basename , this.getClass() );
	      
	      section = new GeoschedulerSection(basename);
	      
	      StratiCrushServices.getInstance().setSection(section);
	      
	      PatchLibrary patchLib = section.getPatchLibrary();

	      Map<String,String> unitMap = MeshObjectFactory.createDummyGeo( basename + ".geo", section);
	      MeshObjectFactory.createDummyUnit( basename + ".unit", section, unitMap);


	      Plot plot = getPlot();
	      plot.removeAll();

	      // Create a graphic object
	      for( Patch patch : patchLib.getPatches() ){
	          ViewFactory.getInstance().createView( plot, patch );   
	      }

	      ViewFactory.getInstance().createView( plot, patchLib.getPaleobathymetry() );

	      RectD bbox = patchLib.getBoundingBox();
	      plot.setWorldExtent( bbox.left, bbox.bottom, bbox.width(), -bbox.height());


	      window_.update();
	  }
	  
	  public void startInteraction( String interactionType, String deformationType ){
	      
		  
		  GInteraction interaction = null;
	      if ( interactionType.equals("Zoom") ){
	         interaction = new ZoomInteraction(getPlot());
	      }
	      else if ( interactionType.equals("Reset") ){
	          interaction = new ResetGeometryInteraction(getPlot());
	      }
	      else if ( interactionType.equals("NodeMoveInteraction") ) {
              interaction = new NodeMoveInteraction(getPlot(), deformationType) ;
          }
	      else if ( interactionType.equals("FlattenInteraction") ) {
              interaction = new FlattenInteraction(getPlot(), deformationType ) ;
	      }
	      else if ( interactionType.equals("TopBorderInteraction") ) {
             interaction = new TopBorderInteraction(getPlot(), deformationType );
	      }
	      else if ( interactionType.equals("Triangulate") ) {
               interaction = new TriangulateInteraction(getPlot(), interactionType);
          }
	      
	      if ( interaction == null ){
	    	  return;
	      }
	      
	      window_.startInteraction( interaction );
	  }

	public void loadMesh(String basename) {
		 LOGGER.debug("load " + basename , this.getClass() );
	      
	      section = new GeoschedulerSection(basename);
	      
	      StratiCrushServices.getInstance().setSection(section);
	      
	      PatchLibrary patchLib = section.getPatchLibrary();
	      
	      MeshObjectFactory.createDummyMesh( basename + ".msh", section);
	      
	      Plot plot = getPlot();
	      plot.removeAll();

	      // Create a graphic object
	      for( Patch patch : patchLib.getPatches() ){
	          ViewFactory.getInstance().createView( plot, patch );   
	      }

	      ViewFactory.getInstance().createView( plot, patchLib.getPaleobathymetry() );

	      RectD bbox = patchLib.getBoundingBox();
	      plot.setWorldExtent( bbox.left, bbox.bottom, bbox.width(), -bbox.height());
	      
	      window_.update();
		
	}
	  
}
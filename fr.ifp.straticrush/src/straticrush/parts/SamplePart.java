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
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import no.geosoft.cc.graphics.GColor;
import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GScene;
import no.geosoft.cc.graphics.GWindow;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import fr.ifp.kronosflow.geometry.RectD;
import fr.ifp.jdeform.dummy.MeshObjectFactory;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchLibrary;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.topology.Contact;
import straticrush.interaction.ZoomInteraction;
import straticrush.menu.Menu;
import straticrush.menu.MenuInteraction;
import straticrush.view.Annotation;
import straticrush.view.ContactView;
import straticrush.view.Plot;
import straticrush.view.View;
import straticrush.view.ViewFactory;


public class SamplePart {

	
	private GWindow   window_;
	Section section;

	private Menu      menu;

	@PostConstruct
	public void createComposite(Composite parent) {
		
		
		window_ = new GWindow( parent, new GColor(0.8f, 0.8f, 0.8f) );
	
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
		
		section = new Section();
		PatchLibrary patchLib = section.getPatchLibrary();


		MeshObjectFactory.createDummyGeo( "/home/irsrvhome1/R11/lecomtje/workspace/Data/Geo_Gmsh/nigeriamodel.geo", section);
		MeshObjectFactory.createDummyUnit("/home/irsrvhome1/R11/lecomtje/workspace/Data/Geo_Gmsh/nigeriamodel.unit", section);


		//MeshObjectFactory.createDummyGeo( "/home/irsrvhome1/R11/lecomtje/workspace/Data/Geo_Gmsh/4blocs.geo", section);
		//MeshObjectFactory.createDummyUnit("/home/irsrvhome1/R11/lecomtje/workspace/Data/Geo_Gmsh/4blocs.unit", section);

		//MeshObjectFactory.createDummyGeo( "/home/irsrvhome1/R11/lecomtje/workspace/Data/Geo_Gmsh/model5bloc.geo", section);
		//MeshObjectFactory.createDummyUnit("/home/irsrvhome1/R11/lecomtje/workspace/Data/Geo_Gmsh/model5bloc.unit", section);


		//MeshObjectFactory.createDummyGeo( file.getAbsolutePath(), section );
		plot.removeAll();

		// Create a graphic object
		for( Patch patch : patchLib.getPatches() ){
			ViewFactory.getInstance().createView( plot, patch );   
		}

		ViewFactory.getInstance().createView( plot, patchLib.getPaleobathymetry() );

		RectD bbox = patchLib.getBoundingBox();
		plot.setWorldExtent( bbox.left, bbox.bottom, bbox.width(), -bbox.height());


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
}
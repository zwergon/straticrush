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
package straticrush.handlers;

import java.util.ArrayList;
import java.util.List;

import no.geosoft.cc.graphics.GObject;

import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledItem;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import fr.ifp.kronosflow.model.PatchLibrary;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.topology.Contact;
import straticrush.parts.SectionPart;
import straticrush.view.PartitionLineView;
import straticrush.view.Plot;
import straticrush.view.View;
import straticrush.view.ViewFactory;

public class ContactsHandler {

	@CanExecute
	public boolean canExecute(EPartService partService) {
		return true;
	}

	@Execute
	public void execute( @Active MPart part, MHandledItem handledItem ) {
		displayContacts( (SectionPart)part.getObject(), handledItem.isSelected()  );
	}
	
	private void displayContacts( SectionPart sectionPart, boolean checked) {

		Section section = sectionPart.getSection();
		if ( sectionPart.getSection() == null ){
			return;
		}

		Plot plot = sectionPart.getPlot();
		if ( plot == null ) return;


		List<GObject> copy = new ArrayList<GObject>( plot.getChildren() );
		for( GObject object : copy ){
			if ( object instanceof PartitionLineView ){
				ViewFactory.getInstance().destroyView(plot, (View)object);
			}
		}

		if ( checked ){
			PatchLibrary patchLib = section.getPatchLibrary();
			// Create a graphic object
			for( Contact contact : patchLib.getContacts() ){
				ViewFactory.getInstance().createView(plot, contact);
			}
		}

		plot.refresh();
	}
}
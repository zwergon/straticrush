/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
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

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import straticrush.parts.SamplePart;
public class OpenHandler {

	@Execute
	public void execute(Shell shell, MPart part ){
	    
		FileDialog dialog = new FileDialog(shell);
		String file = dialog.open();
		
		//no file selected or error, return.
		if ( file == null ){
		    return;
		}
		
		String basename = file.substring(0, file.lastIndexOf('.'));
		
		SamplePart sectionPart = (SamplePart)part.getObject();
		sectionPart.loadSection(basename);
	
	}
}

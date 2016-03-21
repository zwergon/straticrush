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

import static java.lang.System.currentTimeMillis;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import straticrush.archive.DBArchiver;
import straticrush.archive.DBStub;
import straticrush.interaction.StratiCrushServices;

public class SaveHandler {

	private static void withinTimer(String name, Runnable runnable) {
        final long start = currentTimeMillis();
        runnable.run();
        System.out.printf("%20s: %d ms\n", name, currentTimeMillis() - start);
    }


	@Execute
	public void execute(EPartService partService) {
		withinTimer( "save Section", new Runnable( ){
			@Override
			public void run() {
				DBArchiver archiver = new DBArchiver();
				archiver.open();
				archiver.write( new DBStub(StratiCrushServices.getInstance().getSection()) );
				archiver.close();
				
			}
		});
		
		
	}
}
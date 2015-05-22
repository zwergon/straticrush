package straticrush.interaction;


import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;


/**
 * 
 *  Swing/SWT integration has important threading implications. 
 *  Each UI toolkit has its own event queue, and each event queue is processed by a separate thread. 
 *  Most SWT APIs must be called from the SWT event thread. 
 *  Swing has similar restrictions though they are not as strictly enforced. 
 *  This split is the major drawback of mixing the toolkits, and it adds some complexity to the code.

 * Applications must be aware of the current thread, and, where necessary, 
 * schedule tasks to run on the appropriate UI toolkit thread. 
 *
 * To schedule work on the AWT event thread, use:
 * javax.swing.SwingUtilities.invokeLater()
 * javax.swing.SwingUtilities.invokeAndWait() 
 *
 * To schedule work on the SWT event thread, use:
 * org.eclipse.swt.widgets.Display.asyncExec()
 * org.eclipse.swt.widgets.Display.syncExec() 
 *
 */
public class PopupMenuRunnable implements Runnable {
	
	Display display;
	
	int xPos, yPos;
	
	
	
	public PopupMenuRunnable( int x, int y ) {
		xPos = x;
		yPos = y;
	}

	@Override
	public void run() {
		Menu menu = new Menu( Display.getDefault().getActiveShell(), SWT.POP_UP);
		
		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText("Reset");
		item.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
			}
		});
		
		/*
		new MenuItem(menu, SWT.SEPARATOR);
		MenuItem toolItem = new MenuItem(menu, SWT.CASCADE);
		toolItem.setText("Tools");
		Menu toolMenu = new Menu(toolItem);
		toolItem.setMenu( toolMenu );
		for( String toolName : AbstractToolFactory.getRegisteredTools() ){
			AbstractToolInfo toolInfo = AbstractToolFactory.getDescription(toolName);
			item = new MenuItem(toolMenu, SWT.CHECK );
			if ( canvas.isCurrentTool(toolInfo) ){
				item.setSelection(true);
			}
			item.setText( toolInfo.getDescription() );
			item.addListener(SWT.Selection, new ToolListener(toolInfo) );
		}
		*/
		
		new MenuItem(menu, SWT.SEPARATOR);
		item = new MenuItem(menu, SWT.PUSH);
		item.setText("Remove all views");
		item.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
			}
		});
		
		
		
		
        menu.setLocation(Display.getDefault().getCursorLocation() );
        menu.setVisible(true);
		
	}
}
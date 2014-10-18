package straticrush.view;

import java.awt.Insets;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollBar;

import no.geosoft.cc.graphics.GInteraction;
import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GScene;
import no.geosoft.cc.graphics.GWindow;
import no.geosoft.cc.graphics.GL.GGLSwtCanvas;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.PathData;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.PendingUpdateAdapter;

import straticrush.interaction.ComputeContactInteraction;
import straticrush.interaction.NodeMoveInteraction;
import straticrush.interaction.ResetGeometryInteraction;
import straticrush.interaction.TranslateInteraction;
import straticrush.interaction.ZoomInteraction;
import straticrush.interaction.NodeMoveInteraction.NodeMoveType;
import fr.ifp.kronosflow.geometry.RectD;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchLibrary;
import fr.ifp.kronosflow.model.Section;
import fr.ifp.kronosflow.topology.Contact;
import fr.ifp.jdeform.dummy.MeshObjectFactory;


/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class SectionView extends ViewPart implements ISelectionListener {

    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "StratiCrush.SectionView";

    Composite section_composite_;

    private Action loadAction;
    private Action translateAction;
    private Action zoomAction;
    private Action oneOneAction;
    private Action resetAction;
    private Action chainMailAction;
    private Action massSpringAction;
    private Action displaySymbolsAction;
    private Action displayContactsAction;
    
    private Action openWindowAction;

   
    private GWindow   window_;
    private JScrollBar hScrollBar;
    private JScrollBar vScrollBar;


    private Section section = null;

    static int cur_x;
    
    static void loadPath(Region region, float[] points, byte[] types) {
        int start = 0, end = 0;
        for (int i = 0; i < types.length; i++) {
            switch (types[i]) {
                case SWT.PATH_MOVE_TO: {
                    if (start != end) {
                        int n = 0;
                        int[] temp = new int[end - start];
                        for (int k = start; k < end; k++) {
                            temp[n++] = Math.round(points[k]);
                        }
                        region.add(temp);
                    }
                    start = end;
                    end += 2;
                    break;
                }
                case SWT.PATH_LINE_TO: {
                    end += 2;
                    break;
                }
                case SWT.PATH_CLOSE: {
                    if (start != end) {
                        int n = 0;
                        int[] temp = new int[end - start];
                        for (int k = start; k < end; k++) {
                            temp[n++] = Math.round(points[k]);
                        }
                        region.add(temp);
                    }
                    start = end;
                    break;
                }
            }
        }
    }

    /**
     * The constructor.
     */
    public SectionView() {	
    }




    /**
     * This is a callback that will allow us
     * to create the viewer and initialize it.
     */
    public void createPartControl(Composite parent) {


        /*AWT
        Composite treeComp = new Composite(parent,  SWT.EMBEDDED); 

		FillLayout layout = new FillLayout(SWT.VERTICAL);
		treeComp.setLayout(layout);


	    java.awt.Frame frame = SWT_AWT.new_Frame(treeComp);
	    frame.setBackground(Color.white);
         */

        window_ = new GWindow( parent );

        /*AWT
		frame.add ((GAwtCanvas)window_.getCanvas(), BorderLayout.CENTER);

	    // Create the GUI
	    hScrollBar = new JScrollBar (JScrollBar.HORIZONTAL);
	    frame.add (hScrollBar, BorderLayout.SOUTH);

	    vScrollBar = new JScrollBar (JScrollBar.VERTICAL);
	    frame.add (vScrollBar, BorderLayout.EAST);
         */

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


        //scene.installScrollHandler (hScrollBar, vScrollBar);

        makeActions();

        contributeToActionBars();

        getSite().getPage().addSelectionListener(this);  

    }





    private void fillContextMenu(IMenuManager manager) {
        manager.add(translateAction);
        manager.add(resetAction);
        // Other plug-ins can contribute there actions here
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    private void contributeToActionBars() {
        IActionBars bars = getViewSite().getActionBars();
        fillLocalPullDown(bars.getMenuManager());
        fillLocalToolBar(bars.getToolBarManager());
    }

    private void fillLocalPullDown(IMenuManager manager) {
	
    	manager.add(resetAction);
        manager.add(translateAction);
        //manager.add(translate_action_);
        manager.add(chainMailAction);
        manager.add(massSpringAction);

    }



    private void fillLocalToolBar(IToolBarManager manager) {
        manager.add(loadAction);
        manager.add(new Separator());
        manager.add(zoomAction);
        manager.add(oneOneAction);
        manager.add(openWindowAction);
        manager.add(new Separator());
        manager.add(displaySymbolsAction);
        manager.add(displayContactsAction);
    }

    private class WindowAction extends Action {

        private GWindow window_;
        private GInteraction interaction_;

        WindowAction( GWindow window, GInteraction interaction ){
            window_ = window;
            interaction_ = interaction;

            if ( interaction instanceof ISelectionProvider ){
                getSite().setSelectionProvider( (ISelectionProvider) interaction );
            }
        }

        public void run() {
            window_.startInteraction (interaction_);
        }
    }

    private void makeActions() {
        loadAction = new Action() {
            public void run() {
                load();
            }
        };
        loadAction.setText("Load");
        loadAction.setToolTipText("Load");
        loadAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
                getImageDescriptor(ISharedImages.IMG_OBJ_ADD));


        translateAction =  new WindowAction( window_, new TranslateInteraction(getPlot()) );
        translateAction.setText("Translate");
        translateAction.setToolTipText("eforme one patch by translation");
        translateAction.setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin("StratiCrush", "icons/translate.gif" ) );


        chainMailAction =  new WindowAction( window_, new NodeMoveInteraction(getPlot(), NodeMoveType.CHAINMAIL ) );
        chainMailAction.setText("Chainmail");
        chainMailAction.setToolTipText("Deforme one patch by chainmail");
        chainMailAction.setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin("StratiCrush", "icons/translate.gif" ) );


        massSpringAction =  new WindowAction( window_, new NodeMoveInteraction(getPlot(), NodeMoveType.SPRINGMASS ) );
        massSpringAction.setText("SpringMass");
        massSpringAction.setToolTipText("Deforme one patch by spring/mass system");
        massSpringAction.setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin("StratiCrush", "icons/translate.gif" ) );



        zoomAction =  new WindowAction( window_, new ZoomInteraction(getPlot()) );
        zoomAction.setText("Zoom");
        zoomAction.setToolTipText("Zoom scene");
        zoomAction.setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin("StratiCrush", "icons/zoomArea.gif") );		


        oneOneAction =  new Action() {
            public void run() {
                getPlot().unzoom();
            }
        };
        oneOneAction.setText("Unzoom");
        oneOneAction.setToolTipText("Zoom reset");
        oneOneAction.setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin("StratiCrush", "icons/oneOne.gif") );		

        
        openWindowAction =  new Action() {
            
            
            public void run() {
                openWindow();
            }

            private void openWindow() {
                
                Composite pageComposite = (GGLSwtCanvas)window_.getCanvas();
              
                final Shell dialog = new Shell (pageComposite.getShell(), SWT.NO_TRIM | SWT.ON_TOP );
                final Display display = pageComposite.getDisplay();
                
                
                final Rectangle rect = pageComposite.getClientArea();
               
                final Point location = pageComposite.toDisplay(rect.x+ rect.width/2, rect.y);
                
                dialog.setBackground(pageComposite.getDisplay().getSystemColor(SWT.COLOR_BLACK));
                dialog.setAlpha(128);
                
                dialog.addListener (SWT.Paint, new Listener () {
                    @Override
                    public void handleEvent (Event e) {
                        GC gc = e.gc;
                        Rectangle rect = dialog.getClientArea ();
                        Color color1 = new Color (display, 234, 246, 253);
                        Color color2 = new Color (display, 217, 240, 252);
                        Color color3 = new Color (display, 190, 230, 253);
                        Color color4 = new Color (display, 167, 217, 245);
                        Pattern p1 = new Pattern (display, 0, 0, 0, rect.height / 2, color1, color2);
                        gc.setBackgroundPattern (p1);
                        gc.fillRectangle (rect.x, rect.y, rect.width, rect.height / 2);
                        Pattern p2 = new Pattern (display, 0, rect.height / 2, 0, rect.height, color3, color4);
                        gc.setBackgroundPattern (p2);
                        gc.fillRectangle (rect.x, rect.y + (rect.height / 2), rect.width, rect.height / 2 + 1);
                        p1.dispose ();
                        p2.dispose ();
                        color1.dispose ();
                        color2.dispose ();
                        color3.dispose ();
                        color4.dispose ();
                    }
                });


                dialog.addListener(SWT.KeyDown, new Listener() {
                    @Override
                    public void handleEvent(Event e)  {
                        if (e.character == SWT.ESC) {
                            dialog.dispose();
                        }
                    }
                });
                
                final int start_x = location.x + rect.width/2;
                final int end_x = location.x;
                cur_x = 0;
                
                
                dialog.setSize( 0, rect.height);
                dialog.setLocation(cur_x  , location.y);
                dialog.open ();
                
                final int time = 1;
                Runnable timer = new Runnable () {
                    @Override
                    public void run () {
                        if (dialog.isDisposed()) return;
                        cur_x +=20;
                        dialog.setRedraw(false);
                        dialog.setSize( cur_x , rect.height);
                        dialog.setLocation(start_x - cur_x  , location.y);
                        dialog.setRedraw(true);
                        
                        if ( cur_x < rect.width/2 )
                            display.timerExec (time, this);
                    }
                };
                display.timerExec (time, timer);


                
                
                
                
/*
                Composite newWindow = new Composite(pageComposite, SWT.NO_REDRAW_RESIZE);
                newWindow.setLayout(new GridLayout());
                newWindow.setLayoutData(new GridData());
                if (pageNum++ % 2 == 0) {
                    Table table = new Table(newWindow, SWT.BORDER);
                    table.setLayoutData(new GridData());
                    for (int i = 0; i < 5; i++) {
                        new TableItem(table, SWT.NONE).setText("table item " + i);
                    }
                } else {
                    new Button(newWindow, SWT.RADIO).setText("radio");
                }*/
               

                
            }
        };
        openWindowAction.setText("Open Window");
        openWindowAction.setToolTipText("Open sliding window");
        openWindowAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
                getImageDescriptor(ISharedImages.IMG_DEF_VIEW) );       



        displaySymbolsAction =  new Action("Display Symbols", Action.AS_CHECK_BOX ) {
            private boolean checked = false;
            public void run() {
                checked = !checked;
                setChecked( checked );
                Plot scene = getPlot();
                for ( GObject gobject : scene.getChildren() ){
                    gobject.setVisibility( (checked)? GObject.SYMBOLS_VISIBLE : GObject.SYMBOLS_INVISIBLE );
                }
                window_.refresh();


            }
        };
        displaySymbolsAction.setToolTipText("Display symbols for patch nodes");
        displaySymbolsAction.setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin("StratiCrush", "icons/allNodes.png") );		


        resetAction = new WindowAction( window_, new ResetGeometryInteraction(getPlot()) );
        resetAction.setText("Reset");
        resetAction.setToolTipText("Reset geometry to initial");
        resetAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
                getImageDescriptor(ISharedImages.IMG_TOOL_BACK));
        
        
        displayContactsAction =  new Action("Display Contacts", Action.AS_CHECK_BOX ) {
        	private boolean checked = false;
        	public void run() {
        		checked = !checked;
        		setChecked( checked );
        		displayContacts( checked );
        	}
        };
        displayContactsAction.setText("Contacts");
        displayContactsAction.setToolTipText("Display Contacts");
        displayContactsAction.setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin("StratiCrush", "icons/contacts.png") );		

    }


    private File chooseVolatileFile() {
        FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
        dialog.setFilterExtensions(new String[] { "*.geo" });
        dialog.setFilterNames(new String[] { "geofile (*.geo)" });

        String filename = dialog.open();
        if ( filename == null || filename.isEmpty() ){
            return null;
        }
        return new File(filename);
    }


    private void load(){
        Plot plot = getPlot();
        if ( plot == null ) return;

        File file = chooseVolatileFile();
        if ( file == null || !file.exists() ){
            return;
        }

        section = new Section();
        PatchLibrary patchLib = section.findOrCreatePatchLibrary();
      

        MeshObjectFactory.createDummyGeo( file.getAbsolutePath(), patchLib );
        plot.removeAll();

        // Create a graphic object
        for( Patch patch : patchLib.getPatches() ){
        	ViewFactory.getInstance().createView( plot, patch );
            
        }

        RectD bbox = patchLib.getBoundingBox();
        plot.setWorldExtent( bbox.left, bbox.top, bbox.width(), bbox.height());

        window_.update();

    }
    
    private void displayContacts(boolean checked) {
    	
    	if ( section == null ){
    		return;
    	}
    	
    	Plot plot = getPlot();
        if ( plot == null ) return;
		
        
        List<GObject> copy = new ArrayList<GObject>( plot.getChildren() );
        for( GObject object : copy ){
        	if ( object instanceof ContactView ){
        		ViewFactory.getInstance().destroyView(plot, object);
        	}
        }
        
        if ( checked ){
        	PatchLibrary patchLib = section.findOrCreatePatchLibrary();
        	// Create a graphic object
        	for( Contact contact : patchLib.getContacts() ){
        		ViewFactory.getInstance().createView(plot, contact);
        	}
        }
         
        plot.refresh();
	}




    private Plot getPlot() {
        Plot scene = null;

        for( GScene sc : window_.getScenes() ){
            if ( sc instanceof Plot ) {
                scene = (Plot)sc;
                break;
            }
        }

        return scene;
    }

    @Override
    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        if (part instanceof SectionView) {  
            Patch patch = (Patch)((StructuredSelection) selection).getFirstElement();
            if ( null != patch ){
                System.out.println(" patch " + patch.toString() );
            }
        }  
    }




    @Override
    public void setFocus() {	
    }







}

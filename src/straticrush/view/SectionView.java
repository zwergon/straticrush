package straticrush.view;

import java.awt.Insets;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollBar;

import no.geosoft.cc.graphics.GColor;
import no.geosoft.cc.graphics.GInteraction;
import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GScene;
import no.geosoft.cc.graphics.GWindow;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import straticrush.interaction.ComputeContactInteraction;
import straticrush.interaction.NodeMoveInteraction;
import straticrush.interaction.ResetGeometryInteraction;
import straticrush.interaction.TranslateInteraction;
import straticrush.interaction.ZoomInteraction;
import straticrush.interaction.NodeMoveInteraction.NodeMoveType;
import straticrush.menu.Menu;
import straticrush.menu.MenuInteraction;
import straticrush.menu.MenuItem;
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
    private Action openMenuAction;

   
    private GWindow   window_;
    private Menu      menu;
    private JScrollBar hScrollBar;
    private JScrollBar vScrollBar;


    private Section section = null;


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

        window_ = new GWindow( parent, GColor.BLACK );

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
        manager.add(openMenuAction);
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
        
        openMenuAction =  new Action( "Display Symbols", Action.AS_CHECK_BOX ) {
        	private boolean checked = false;
        	public void run() {
        		checked = !checked;
        		setChecked( checked );
        		openMenu(checked);
        	}
        };
        openMenuAction.setText("Menu");
        openMenuAction.setToolTipText("Menu");
        openMenuAction.setImageDescriptor( PlatformUI.getWorkbench().getSharedImages().
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


    protected void openMenu(boolean checked) {
	
    	if ( checked ){
    		// Create a value specific "plot" scene
            menu = new Menu (window_);
            
            PatchLibrary patchLib = section.findOrCreatePatchLibrary();
            menu.populate(patchLib);
            window_.startInteraction( new MenuInteraction( menu ) );
    		
    	}
    	else {
    		window_.stopInteraction();
    		menu.removeAll();
    		window_.removeScene(menu);
    	}
    	
    	window_.update();
    		
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

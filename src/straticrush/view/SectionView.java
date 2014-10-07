package straticrush.view;

import java.awt.Insets;
import java.io.File;

import javax.swing.JScrollBar;

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
import fr.ifp.kronosflow.geometry.RectD;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.kronosflow.model.PatchLibrary;
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

    private Action load_action;
    private Action translate_action_;
    private Action zoom_action_;
    private Action oneone_action_;
    private Action reset_action;
    private Action chainmail_action_;
    private Action masspring_action_;
    private Action display_symbols_;
    private Action computeContactAction;

    //private World world_ = null;
    private PatchView object_view_; 

    private GWindow   window_;
    private JScrollBar hScrollBar;
    private JScrollBar vScrollBar;


    private PatchLibrary patches_;


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
        manager.add(translate_action_);
        manager.add(reset_action);
        // Other plug-ins can contribute there actions here
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    private void contributeToActionBars() {
        IActionBars bars = getViewSite().getActionBars();
        fillLocalPullDown(bars.getMenuManager());
        fillLocalToolBar(bars.getToolBarManager());
    }

    private void fillLocalPullDown(IMenuManager manager) {



        manager.add(translate_action_);
        //manager.add(translate_action_);
        manager.add(chainmail_action_);
        manager.add(masspring_action_);

    }



    private void fillLocalToolBar(IToolBarManager manager) {
        manager.add(load_action);
        manager.add(reset_action);
        manager.add(new Separator());
        manager.add(zoom_action_);
        manager.add(oneone_action_);
        manager.add(display_symbols_);
        manager.add(new Separator());
        manager.add(computeContactAction);



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
        load_action = new Action() {
            public void run() {
                load();
            }
        };
        load_action.setText("Load");
        load_action.setToolTipText("Load");
        load_action.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
                getImageDescriptor(ISharedImages.IMG_OBJ_ADD));


        translate_action_ =  new WindowAction( window_, new TranslateInteraction(get_plot()) );
        translate_action_.setText("Translate");
        translate_action_.setToolTipText("eforme one patch by translation");
        translate_action_.setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin("StratiCrush", "icons/translate.gif" ) );


        chainmail_action_ =  new WindowAction( window_, new NodeMoveInteraction(get_plot(), NodeMoveType.CHAINMAIL ) );
        chainmail_action_.setText("Chainmail");
        chainmail_action_.setToolTipText("Deforme one patch by chainmail");
        chainmail_action_.setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin("StratiCrush", "icons/translate.gif" ) );


        masspring_action_ =  new WindowAction( window_, new NodeMoveInteraction(get_plot(), NodeMoveType.SPRINGMASS ) );
        masspring_action_.setText("SpringMass");
        masspring_action_.setToolTipText("Deforme one patch by spring/mass system");
        masspring_action_.setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin("StratiCrush", "icons/translate.gif" ) );



        zoom_action_ =  new WindowAction( window_, new ZoomInteraction(get_plot()) );
        zoom_action_.setText("Zoom");
        zoom_action_.setToolTipText("Zoom scene");
        zoom_action_.setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin("StratiCrush", "icons/zoomArea.gif") );		


        oneone_action_ =  new Action() {
            public void run() {
                get_plot().unzoom();
            }
        };
        oneone_action_.setText("Unzoom");
        oneone_action_.setToolTipText("Zoom reset");
        oneone_action_.setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin("StratiCrush", "icons/oneOne.gif") );		


        display_symbols_ =  new Action("Display Symbols", Action.AS_CHECK_BOX ) {
            private boolean checked_ = false;
            public void run() {
                checked_ = !checked_;
                setChecked( checked_ );
                Plot scene = get_plot();
                for ( GObject gobject : scene.getChildren() ){
                    gobject.setVisibility( (checked_)? GObject.SYMBOLS_VISIBLE : GObject.SYMBOLS_INVISIBLE );
                }
                window_.refresh();


            }
        };
        display_symbols_.setToolTipText("Display symbols for patch nodes");
        display_symbols_.setImageDescriptor( AbstractUIPlugin.imageDescriptorFromPlugin("StratiCrush", "icons/allNodes.png") );		



        reset_action = new WindowAction( window_, new ResetGeometryInteraction(get_plot()) );
        reset_action.setText("Reset");
        reset_action.setToolTipText("Reset geometry to initial");
        reset_action.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
                getImageDescriptor(ISharedImages.IMG_TOOL_BACK));
        
        
        computeContactAction = new WindowAction( window_, new ComputeContactInteraction(get_plot()) );
        computeContactAction.setText("Compute Contact");
        computeContactAction.setToolTipText("Compute Contact");
        computeContactAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
                getImageDescriptor(ISharedImages.IMG_TOOL_BACK));


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
        Plot scene = get_plot();
        if ( scene == null ) return;

        File file = chooseVolatileFile();
        if ( file == null || !file.exists() ){
            return;
        }

        if ( null != patches_ && !patches_.getPatches().isEmpty() ){
            patches_.getPatches().clear();
        }



        patches_ = MeshObjectFactory.createDummyGeo( file.getAbsolutePath() );
        scene.removeAll();

        // Create a graphic object
        for( Patch patch : patches_.getPatches() ){
            object_view_ = (PatchView)ViewFactory.createView( patch );
            if ( null != object_view_ ){
                scene.add (object_view_);
            }
        }

        RectD bbox = patches_.boundingBox();
        scene.setWorldExtent( bbox.left, bbox.top, bbox.width(), bbox.height());

        window_.update();

    }




    private Plot get_plot() {
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
package straticrush.interaction;


import java.util.ArrayList;
import java.util.List;










import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;

import straticrush.view.PatchView;
import fr.ifp.kronosflow.model.Patch;
import fr.ifp.jdeform.deformation.TranslateNodeMove;
import no.geosoft.cc.graphics.GColor;
import no.geosoft.cc.graphics.GEvent;
import no.geosoft.cc.graphics.GInteraction;
import no.geosoft.cc.graphics.GObject;
import no.geosoft.cc.graphics.GScene;
import no.geosoft.cc.graphics.GSegment;
import no.geosoft.cc.graphics.GStyle;
import no.geosoft.cc.graphics.GTransformer;


public class TranslateInteraction implements GInteraction, ISelectionProvider {
	
	private GScene    scene_;
	private List<GSegment>  interactionSegment_;
	private int       x0_, y0_;
	

	ListenerList listeners = new ListenerList();  
	
	
	public TranslateInteraction( GScene scene ){
		scene_ = scene;
		interactionSegment_ = new ArrayList<GSegment>();	
	}
	
	// Move interaction
	public void event (GScene scene, GEvent event )
	{
		
		if ( scene != scene_ ){
			return;
		}
			
		GObject interaction = scene.find ("interaction");
		if (interaction == null) {
			interaction = new GObject("interaction");
			scene.add (interaction);
		}
		
		interaction.removeSegments();

		switch (event.type) {
		
		case GEvent.MOTION:
			GSegment selected = scene.findSegment (event.x, event.y);
			if ( selected !=  null ){
				GObject gobject = selected.getOwner();
				if ( gobject instanceof PatchView ){
					GSegment highlight = new GSegment();
					GStyle highlightStyle  = new GStyle();
					highlightStyle.setBackgroundColor (new GColor(1.0f, 1.0f, 1.0f, 0.7f));
					highlight.setStyle (highlightStyle);
					interaction.addSegment (highlight);
					highlight.setGeometry ( ((PatchView) gobject).getBorder().getGeometry() );
				}
				
			}
			break;
			
		case GEvent.BUTTON1_DOWN :
			selected = scene.findSegment (event.x, event.y);
			if ( selected !=  null && !interactionSegment_.contains(selected) ){
				GObject gobject = selected.getOwner();
				if ( gobject instanceof PatchView ){
					interactionSegment_.add( selected );
					setSelection( getSelection() );
					
				}
			}
			
			x0_ = event.x;
			y0_ = event.y;
			
			break;

		case GEvent.BUTTON1_DRAG :
			int dx = event.x - x0_;
			int dy = event.y - y0_;
			if (!interactionSegment_.isEmpty()) {
				for ( GSegment segment : interactionSegment_ ){
					GObject gobject = segment.getOwner();
					if ( gobject instanceof PatchView ){
						PatchView view = (PatchView)gobject;
						Patch object = view.getObject();

						TranslateNodeMove node_move = new TranslateNodeMove(object);

						GTransformer transformer = view.getTransformer();
						double[] dw0 = transformer.deviceToWorld (0,  0);
						double[] dw1 = transformer.deviceToWorld (dx, dy); 		
						double[] t = new double[2];
						t[0] = dw1[0] - dw0[0];
						t[1] = dw1[1] - dw0[1];		
						node_move.setTranslation(t);
						node_move.move();
					}
				}
				
			}
			x0_ = event.x;
			y0_ = event.y;
			break;

		case GEvent.BUTTON1_UP :
			if ( !event.isCtrlDown() ){
				interactionSegment_.clear();
			}
			break;
			
		case GEvent.CONTEXT_MENU:
			break;

		}

		scene_.refresh();
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		 //listeners.add(listener);  
	}

	@Override
	public ISelection getSelection() {
		
		List<Patch> selected = new ArrayList<Patch>();
		if (!interactionSegment_.isEmpty()) {
			for ( GSegment segment : interactionSegment_ ){
				GObject gobject = segment.getOwner();
				if ( gobject instanceof PatchView ){
					PatchView view = (PatchView)gobject;
					selected.add( view.getObject() );
				}
			}
		}
		return new StructuredSelection( selected );
	}

	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		 listeners.remove( listener );
		
	}

	@Override
	public void setSelection(ISelection selection) {
		Object[] list = listeners.getListeners();  
		  for (int i = 0; i < list.length; i++) {  
		   ((ISelectionChangedListener) list[i])  
		     .selectionChanged(new SelectionChangedEvent(this, selection));  
		  }  
	}
	
}

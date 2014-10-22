package straticrush.interaction;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import fr.ifp.kronosflow.controller.Event;

	public class ViewNotifier {
		
		/**
		 * Notify all listeners about change in this Shape.
		 * 
		 * @param Event  Describe the change in the Shape.
		 */
		public void notifyViews( Object object, Event event )
		{
			Iterator<WeakListener> i = listeners.iterator();
			while( i.hasNext() ) {
				IViewListener listener = i.next().get();

				// If the listener has been GC'd, remove it from the list
				if (listener == null)
					i.remove();
				else
					listener.objectChanged(object, event);
			}
		}



		/**
		 * Add a listener to this style. When the style is changed, a
		 * styleChanged() signal is sent to the listener.
		 * 
		 * @param listener  Style listener to add.
		 */
		public void addListener (IViewListener listener)
		{
			// Check if the listener is there already
			Iterator< WeakListener > i = listeners.iterator();
			while(i.hasNext()) {
				if (i.next().get() == listener)
					return;
			}

			// Add the listener
			listeners.add (new WeakListener(listener) );
		}



		/**
		 * Remove specified listener from this style.
		 * 
		 * @param listener  Style listener to remove.
		 */
		public void removeListener (IViewListener listener)
		{
			Iterator< WeakListener > i = listeners.iterator();
			while(i.hasNext()) {
				if (i.next().get() == listener){
					i.remove();
					break;
				}
			}
		}


		private class WeakListener extends WeakReference<IViewListener> {
			public WeakListener(IViewListener referent) {
				super(referent);
			}
		};

		private final Collection<WeakListener> listeners = new ArrayList<WeakListener>();

}

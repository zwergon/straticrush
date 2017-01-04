package stratifx.application.interaction;

import stratifx.canvas.graphics.GObject;
import stratifx.canvas.graphics.GSegment;

public class GTranslator {
	
	private GObject object;
	
	public GTranslator( GObject object ){
		this.object = object;
	}
	
	
	public void execute( int[] translation ){
		recurseExecute(object, translation);
	}
	
	private void recurseExecute( GObject object, int[] translation ){
		translateSegments( object, translation );
		for( GObject child : object.getChildren() ){
			translateSegments( child, translation );
		}
	}

	
	private void translateSegments( GObject object, int[] translation ){
		System.out.println("translate");
		for( GSegment segment : object.getSegments() ){
			int[] xy = segment.getGeometry();
			for( int i=0; i<xy.length/2; i++){
				xy[2*i] += translation[0];
				xy[2*i+1] += translation[1];
			}
			
			segment.setGeometry(xy);
		}
	}
}

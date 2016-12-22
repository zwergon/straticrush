package stratifx.application.plot;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point3D;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.NumberAxis;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import stratifx.application.IUIController;
import stratifx.application.UIAction;
import stratifx.canvas.graphics.GColor;
import stratifx.canvas.graphics.GSegment;
import stratifx.canvas.graphics.GStyle;
import stratifx.canvas.graphics.GWorldExtent;
import stratifx.canvas.interaction.DummyInteration;
import stratifx.canvas.interaction.GInteraction;
import stratifx.canvas.interaction.GMouseEvent;

public class PlotController 
	implements 
		Initializable,
		IUIController
{

    @FXML private Canvas canvasId ;
    
    @FXML private Group plotGroupId;
    
    @FXML private AnchorPane paneId;
    
    NumberAxis axisY;
    
    NumberAxis axisX;

    GFXScene gfxScene;
    
    GInteraction interaction_;
    
    double[] xy = new double[2];
    
    public void drawCanvas() {
    	
    	startInteraction( new DummyInteration() );

    	double[][] we = new double[][]{
    		{ -2, -2 },
    		{  2, -2 },
    		{ -2,  2 }
    	};

    	setWorldExtent( we[0], we[1], we[2] );

    	GSegment segment = new GSegment();

    	GStyle style = new GStyle();
    	style.setForegroundColor ( GColor.BLACK );
    	style.setBackgroundColor ( GColor.ORANGE );

    	style.setLineWidth (1);
    	segment.setStyle (style);

    	gfxScene.addSegment(segment);

    	double[] x = new double[]{
    			0, 1, 1.5, 1.6, 1.7, 1.8, 1	
    	};

    	double[] y = new double[]{
    			0, 1, .5, .4, .3, 0, -1.5	
    	};

    	segment.setWorldGeometry(x, y);

    	gfxScene.refresh();

    }

    @FXML
    private void onPlotScrolled( ScrollEvent event ) {
    	
    	double sx = 0;
		if ( event.getDeltaY()< 0 ){
			sx = plotGroupId.getScaleX()*0.95;
		}
		else {
			sx = plotGroupId.getScaleX()*1.05;	
		}
		
		plotGroupId.setScaleX(sx);
		plotGroupId.setScaleY(sx);
		
	}
    
    
    public Canvas getCanvas(){
    	return canvasId;
    }
   
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    	
    	gfxScene = new GFXScene(canvasId);
    	
    	GWorldExtent extent = gfxScene.getWorldExtent();
    	    	
    	double left = extent.left();
    	double right = extent.right();
    	double width = extent.getWidth();
        axisX = new NumberAxis( left, right , width/10.);
        axisX.setMouseTransparent(true);
        axisX.setSide( Side.BOTTOM );
        axisX.setPrefWidth( canvasId.getWidth() );
        axisX.setLayoutX( canvasId.getLayoutX() );
        axisX.setLayoutY( canvasId.getLayoutY() + canvasId.getHeight()/2. );
        plotGroupId.getChildren().add( axisX );
        
        
        double bottom = extent.bottom();
        double top = extent.top();
        double height = extent.getHeight();
        axisY = new NumberAxis(bottom, top, height/10.);
        axisY.setMouseTransparent(true);
        axisY.setSide( Side.LEFT );
        axisY.setPrefHeight( canvasId.getHeight() );
        axisY.setLayoutX( canvasId.getLayoutX()  );
        axisY.setLayoutY( canvasId.getLayoutY() );
        plotGroupId.getChildren().add( axisY );
        
        Rectangle clipRectangle = new Rectangle( 
        		canvasId.getLayoutX(), 
        		canvasId.getLayoutY(), 
        		canvasId.getWidth(), 
        		canvasId.getHeight() );
        paneId.setClip( clipRectangle );
          
        
    }
    
    
    public void setWorldExtent( double[] w0, double[] w1, double[] w2 ){
    	
    	gfxScene.setWorldExtent(w0, w1, w2);
    	
    	axisX.setLowerBound(w0[0]);
    	axisX.setUpperBound(w1[0]);
    	axisX.setTickUnit( Math.abs(w1[0]-w0[0])/10.);
    	
    	axisY.setLowerBound(w0[1]);
    	axisY.setUpperBound(w2[1]);
    	axisY.setTickUnit( Math.abs(w2[1]-w0[1])/10.);
    }
    
    

    /**
     * Install the specified interaction on this window. As a window
     * can administrate only one interaction at the time, the current
     * interaction (if any) is first stopped.
     * 
     * @param interaction  Interaction to install and start.
     */
    public void startInteraction (GInteraction interaction)
    {
      if (interaction_ != null)
        stopInteraction();

      interaction_      = interaction;
      if ( null != interaction ){
    	  interaction_.start(gfxScene);
      }
    }


    
    /**
     * Stop the current interaction. The current interaction will get
     * an ABORT event so it has the possibility to do cleanup. If no
     * interaction is installed, this method has no effect. 
     */
    public void stopInteraction()
    {
      // Nothing to do if no current interaction
      if (interaction_ == null) return;
      
      interaction_.stop( gfxScene );
      interaction_      = null;    
    }
    
    private int getGFXModifier( MouseEvent mouseEvent ){
    	return 0;
    }
    
    
    private boolean gfxHandleMouse( int type, MouseEvent mouseEvent ){
    	if ( null != interaction_ ){
    		PickResult result = mouseEvent.getPickResult();
    		if ( result.getIntersectedNode().equals( canvasId ) ){
    			Point3D pt = result.getIntersectedPoint();
    			
    			GMouseEvent gevent = new GMouseEvent( 
						type,
						getGFXButton(mouseEvent),
						(int)pt.getX(), 
						(int)pt.getY(),
						getGFXModifier(mouseEvent)
						);
    			return interaction_.mouseEvent( gfxScene , gevent);
    		}
    	}
    	return false;
    }
    
    private int getGFXButton( MouseEvent mouseEvent ){
    	return GMouseEvent.BUTTON_1;
    }
  
    @FXML 
    private void onMouseClicked( MouseEvent mouseEvent ){
    }
    
    @FXML
    private void onMouseMoved( MouseEvent mouseEvent ){
    }
    
    @FXML
    private void onMousePressed( MouseEvent mouseEvent ){
    	
    	xy[0] = mouseEvent.getX();
    	xy[1] = mouseEvent.getY();
    	

    	if ( gfxHandleMouse(GMouseEvent.BUTTON_DOWN, mouseEvent) ){
			mouseEvent.consume();
		}	
    }
    
    @FXML
    private void onMouseReleased( MouseEvent mouseEvent ){	
    	if ( gfxHandleMouse(GMouseEvent.BUTTON_UP, mouseEvent) ){
			mouseEvent.consume();
		}	
    }

    @FXML
    private void onMouseDragged( MouseEvent mouseEvent ){
    	
    	if ( mouseEvent.isMiddleButtonDown() ){
    		
    		double[] dxy = new double[]{
    				plotGroupId.getTranslateX() + mouseEvent.getX() - xy[0],
    				plotGroupId.getTranslateY() + mouseEvent.getY() - xy[1]
    		};
    		
    		plotGroupId.setTranslateX(dxy[0]);
    		plotGroupId.setTranslateY(dxy[1]);
    		
    		xy[0] = mouseEvent.getX();
        	xy[1] = mouseEvent.getY();
    		return;
    	}
    	if ( gfxHandleMouse(GMouseEvent.BUTTON_DRAG, mouseEvent) ){
			mouseEvent.consume();
		}
    }
    
    @FXML
    private void onMouseEntered( MouseEvent mouseEvent ){	
    }
    
    @FXML
    private void onMouseExited( MouseEvent mouseEvent ){	
    }

	@Override
	public boolean handleAction(UIAction action) {
		
		switch( action.getType() ){
		case UIAction.DummyDraw:
			drawCanvas();
			break;
		case UIAction.ZoomOneOne:
			restoreZoom();
			break;
		}
		return false;
	}

	private void restoreZoom() {
		plotGroupId.setScaleX(1);
		plotGroupId.setScaleY(1);
		plotGroupId.setTranslateX(0);
		plotGroupId.setTranslateY(0);
		
	}


    
   
    
    
  

}

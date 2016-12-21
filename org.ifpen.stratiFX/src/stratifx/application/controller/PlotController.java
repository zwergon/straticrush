package stratifx.application.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.NumberAxis;
import javafx.scene.input.ScrollEvent;
import stratifx.canvas.graphics.GColor;
import stratifx.canvas.graphics.GSegment;
import stratifx.canvas.graphics.GStyle;
import stratifx.canvas.graphics.GWorldExtent;

public class PlotController implements Initializable {

    @FXML private Canvas canvasId ;
    
    @FXML private Group plotGroupId;
    
    NumberAxis axisY;
    
    NumberAxis axisX;

    GFXCanvas gCanvas;
    
    public void drawCanvas() {

    	double[][] we = new double[][]{
    		{-2, -2 },
    		{ 2,  -2 },
    		{ -2,   2 }
    	};

    	setWorldExtent( we[0], we[1], we[2] );

    	GSegment segment = new GSegment();

    	GStyle style = new GStyle();
    	style.setForegroundColor ( GColor.BLACK );
    	style.setBackgroundColor ( GColor.ORANGE );

    	style.setLineWidth (1);
    	segment.setStyle (style);

    	gCanvas.addSegment(segment);

    	double[] x = new double[]{
    			0, 1, 1.5, 1.6, 1.7, 1.8, 1	
    	};

    	double[] y = new double[]{
    			0, 1, .5, .4, .3, 0, -1.5	
    	};

    	segment.setWorldGeometry(x, y);

    	gCanvas.refresh();

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
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    	
    	gCanvas = new GFXCanvas(canvasId);
    	
    	GWorldExtent extent = gCanvas.getWorldExtent();
    	    	
    	double left = extent.left();
    	double right = extent.right();
    	double width = extent.getWidth();
        axisX = new NumberAxis( left, right , width/10.);
        axisX.setSide( Side.BOTTOM );
        axisX.setPrefWidth( canvasId.getWidth() );
        axisX.setLayoutX( canvasId.getLayoutX() );
        axisX.setLayoutY( canvasId.getLayoutY() + canvasId.getHeight()/2. );
        plotGroupId.getChildren().add( axisX );
        
        
        double bottom = extent.bottom();
        double top = extent.top();
        double height = extent.getHeight();
        axisY = new NumberAxis(bottom, top, height/10.);
        axisY.setSide( Side.LEFT );
        axisY.setPrefHeight( canvasId.getHeight() );
        axisY.setLayoutX( canvasId.getLayoutX()  );
        axisY.setLayoutY( canvasId.getLayoutY() );
        plotGroupId.getChildren().add( axisY );
          
        
    }
    
    
    public void setWorldExtent( double[] w0, double[] w1, double[] w2 ){
    	gCanvas.setWorldExtent(w0, w1, w2);
    	
    	axisX.setLowerBound(w0[0]);
    	axisX.setUpperBound(w1[0]);
    	axisX.setTickUnit( Math.abs(w1[0]-w0[0])/10.);
    	
    	axisY.setLowerBound(w0[1]);
    	axisY.setUpperBound(w2[1]);
    	axisY.setTickUnit( Math.abs(w2[1]-w0[1])/10.);
    }
    
    
  

}

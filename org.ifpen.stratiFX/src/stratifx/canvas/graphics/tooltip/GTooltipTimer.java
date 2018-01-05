/*
* Copyright (C) 2014-2018 by IFPEN
* All rights reserved.
*
* IFPEN Headquarters:
* 1 & 4, avenue de Bois-Preau
* 92852 Rueil-Malmaison Cedex - France
*/
package stratifx.canvas.graphics.tooltip;

import javafx.application.Platform;


/**
 * Thread that triggers show (when timeElapsed > timeToShow) or hide of a Tooltip
 * by calling {@link ITooltipAction}
 * @author lecomtje
 *
 */
public class GTooltipTimer extends Thread {
    
    long delay = 20;
    
    long timeElapsed;
    
    long timeToShow = 1000;
    
    ITooltipAction toolipAction;
    
    boolean visible = false;
    
    boolean goOn = true;
    
    int x, y;
    
    /**
     * As soon mouse is used or key is pressed, must reset timeElapsed.
     */
    public synchronized void reset(){
        timeElapsed = 0;
    }
    
    /**
     * where is the mouse now ?
     */
    public synchronized void setPos( int x, int y ){
        this.x = x;
        this.y = y;
    }
    
    public void setAction( ITooltipAction action ){
        toolipAction = action;
    }
    
    @Override
    public void run() {
        
        reset();
        
        try {
            while( goOn ){
                
                if ( ( timeElapsed == 0 ) && (visible) ){
                    if ( null != toolipAction ){
                        Platform.runLater(new Runnable() {
                            @Override public void run() {
                                toolipAction.hide();
                            }
                        });
                    }
                    visible = false;
                }
                
                if ( timeElapsed > timeToShow )  {
                    if ( !visible ){
                        if ( null != toolipAction ){
                            Platform.runLater(new Runnable() {
                                @Override public void run() {
                                    toolipAction.show(x, y);
                                }
                            });
                        }
                        visible = true;
                    }
                }
                else {
                    timeElapsed += delay;
                }
                  
                sleep(delay);
            }
            
            
            
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
    }
    
    public synchronized void canStop() {
        goOn = false;
    }
    
}

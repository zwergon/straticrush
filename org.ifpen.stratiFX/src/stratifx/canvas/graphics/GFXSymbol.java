/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stratifx.canvas.graphics;

import java.nio.IntBuffer;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

/**
 *
 * @author Jef
 */
public class GFXSymbol extends GImage {
    
    
    
     // Some predefined images suitable for use as vertex images
    public static final int SYMBOL_NONE    = -1;
    public static final int SYMBOL_SQUARE1 = 1;
    public static final int SYMBOL_SQUARE2 = 2;
    public static final int SYMBOL_SQUARE3 = 3;
    public static final int SYMBOL_SQUARE4 = 4;
    public static final int SYMBOL_CIRCLE1 = 5;  // TODO
    public static final int SYMBOL_CIRCLE2 = 6;  // TODO
    public static final int SYMBOL_CIRCLE3 = 7;  // TODO
    public static final int SYMBOL_CIRCLE4 = 8;  // TODO
    
     /**
     * Create image of a predefined type.
     *
     * @see GImage#setPositionHint(int)
     *
     * @param symbolType Symbol to create.
     * @param positionHint Position hint.
     */
    public GFXSymbol(int symbolType, int positionHint) {
        super(positionHint);

        int width;
        int height;

        switch (symbolType) {
            case SYMBOL_SQUARE1:
            case SYMBOL_CIRCLE1:
                width = 5;
                height = 5;
                break;
            case SYMBOL_SQUARE2:
            case SYMBOL_CIRCLE2:
                width = 7;
                height = 7;
                break;
            case SYMBOL_SQUARE3:
            case SYMBOL_CIRCLE3:
                width = 9;
                height = 9;
                break;
            case SYMBOL_SQUARE4:
            case SYMBOL_CIRCLE4:
                width = 11;
                height = 11;
                break;
            // TODO: Define circles.
            default:
                return; // Unknown symbol type
        }

        int data[] = new int[width * height];

        // Set bits specified
        switch (symbolType) {
            case SYMBOL_SQUARE1:
            case SYMBOL_SQUARE2:
            case SYMBOL_SQUARE3:
            case SYMBOL_SQUARE4:
                for (int i = 0; i < data.length; i++) {
                    data[i] = 1;
                }
                break;
            case SYMBOL_CIRCLE1:
            case SYMBOL_CIRCLE2:
            case SYMBOL_CIRCLE3:
            case SYMBOL_CIRCLE4:
                double r = width / 2.;
                for (int j = 0; j < height; j++) {
                    double y2 = (j - r) * (j - r);
                    for (int i = 0; i < width; i++) {
                        double x2 = (i - r) * (i - r);
                        if (x2 + y2 < r * r) {
                            data[j + i * width] = 1;
                        } else {
                            data[j + i * width] = 0;
                        }
                    }
                }
                break;
                
        }

        initImage(width, height, data);
    }

    /**
     * Create an image of predefined type and with default position hints.
     *
     * @param symbolType Predefined symbol type.
     */
    public GFXSymbol(int symbolType) {
        this(symbolType, DEFAULT_POSITION_HINT);
    }
    
   
    @Override
    protected final void setImage(int width, int height, int[] data) {
        initImage(width, height, data);
    }

    
}

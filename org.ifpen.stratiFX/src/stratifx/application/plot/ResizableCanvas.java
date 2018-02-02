package stratifx.application.plot;


import javafx.scene.canvas.Canvas;

public class ResizableCanvas extends Canvas {

    @Override
    public boolean isResizable() {
       return true;
    }
}

package stratifx.canvas.graphics;



import java.nio.IntBuffer;

import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import stratifx.canvas.graphics.GImage;

public class GFXTexture extends GImage {

    WritableImage fxImg;

    public GFXTexture() {
        super();
    }

    public Image getImageFX() {
        return fxImg;
    }

    @Override
    public void setImage(int width, int height, int data[]) {
        initImage(width, height, data);

        fxImg = new WritableImage(width, height);
        PixelWriter pw = fxImg.getPixelWriter();

        PixelFormat<IntBuffer> pixelFormat = PixelFormat.getIntArgbInstance();
        pw.setPixels(0, 0, width, height, pixelFormat, data, 0, width);

    }

}

/* 
 * Copyright 2017 lecomtje.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

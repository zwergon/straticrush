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

public abstract class GImage extends GPositional {

    protected static final int DEFAULT_POSITION_HINT = GPosition.CENTER
            | GPosition.STATIC;

    protected int[] imageData_;

    public GImage() {
        this(DEFAULT_POSITION_HINT);
    }

    public GImage(int positionHint) {
        super(positionHint, true);
        imageData_ = null;
    }

    /**
     * Create a image based on specified color section.
     *
     * @see GAwtImage#setPositionHint(int)
     *
     * @param width Width of image.
     * @param height Height of image.
     * @param data Color values for image.
     * @param positionHint Position hint.
     */
    public GImage(int width, int height, int data[], int positionHint) {
        super(positionHint, true);
        initImage(width, height, data);
    }

    /**
     * Create a image based on specified color section. Use defult position hints.
     *
     * @param width Width of image.
     * @param height Height of image.
     * @param data Color values for image.
     */
    public GImage(int width, int height, int data[]) {
        this(width, height, data, DEFAULT_POSITION_HINT);
    }

    public int[] getImageData() {
        return imageData_;
    }

    /**
     * Set image section.
     *
     * @param width Width of image.
     * @param height Height of image.
     * @param data Color values of image.
     */
    protected final void initImage(int width, int height, int data[]) {
        rectangle_.width = width;
        rectangle_.height = height;

        // Copy the image section locally
        int size = width * height;
        imageData_ = new int[size];
        for (int i = 0; i < size; i++) {
            imageData_[i] = data != null && data.length > i ? data[i] : 0;
        }

        imageData_ = data;
    }

    @Override
    void computeSize() {
        //nothing to do! size is defined by the image itself.
    }

    protected abstract void setImage(int width, int height, int data[]);

}

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
     * Create a image based on specified color data.
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
     * Create a image based on specified color data. Use defult position hints.
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
     * Set image data.
     *
     * @param width Width of image.
     * @param height Height of image.
     * @param data Color values of image.
     */
    protected final void initImage(int width, int height, int data[]) {
        rectangle_.width = width;
        rectangle_.height = height;

        // Copy the image data locally
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

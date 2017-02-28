package stratifx.canvas.graphics;

public class GImage extends GPositional {

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

    private static final int DEFAULT_POSITION_HINT = GPosition.CENTER
            | GPosition.STATIC;

    protected int[] imageData_;

    /**
     * Create image of a predefined type.
     *
     * @see GImage#setPositionHint(int)
     *
     * @param symbolType Symbol to create.
     * @param positionHint Position hint.
     */
    public GImage(int symbolType, int positionHint) {
        super(positionHint, true);

        int width;
        int height;

        switch (symbolType) {
            case SYMBOL_SQUARE1:
                width = 5;
                height = 5;
                break;
            case SYMBOL_SQUARE2:
                width = 7;
                height = 7;
                break;
            case SYMBOL_SQUARE3:
                width = 9;
                height = 9;
                break;
            case SYMBOL_SQUARE4:
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
        }

        initImage(width, height, data);
    }

    /**
     * Create an image of predefined type and with default position hints.
     *
     * @param symbolType Predefined symbol type.
     */
    public GImage(int symbolType) {
        this(symbolType, DEFAULT_POSITION_HINT);
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
    private void initImage(int width, int height, int data[]) {
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
    
    
    protected void setImage( int width, int height, int data[] ){
        initImage(width, height, data);
    }

    @Override
    void computeSize() {
        //nothing to do! size is defined by the image itself.
    }

}

package straticrush.view;

import java.awt.Insets;

import no.geosoft.cc.graphics.GScene;
import no.geosoft.cc.graphics.GWindow;

public class Plot extends GScene {

    private Insets insets_;

    public Plot(GWindow window, Insets insets) {
        super(window);
        insets_ = insets;
    }

    protected void resize(double dx, double dy) {
        super.resize(dx, dy);
        setViewport(insets_.left, insets_.top,
                getWindow().getWidth() - insets_.left - insets_.right,
                getWindow().getHeight() - insets_.top - insets_.bottom);
    }
}


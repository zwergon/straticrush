package stratifx.application.views;

import java.util.Iterator;
import java.util.List;

import fr.ifp.kronosflow.polyline.ICurviPoint;
import fr.ifp.kronosflow.polyline.IPolyline;
import fr.ifp.kronosflow.warp.IWarp;
import stratifx.canvas.graphics.GColor;
import stratifx.canvas.graphics.GFXSymbol;
import stratifx.canvas.graphics.GImage;
import stratifx.canvas.graphics.GObject;
import stratifx.canvas.graphics.GPosition;
import stratifx.canvas.graphics.GRect;
import stratifx.canvas.graphics.GSegment;
import stratifx.canvas.graphics.GStyle;
import stratifx.canvas.graphics.GText;
import stratifx.canvas.graphics.GTransformer;

public class GPolyline extends GDeformableObject {

    GProperty gProperty;

    GSegment border;
    
    GSegment boundingBox;
    
    boolean withBox=false;
    
    public GPolyline(IPolyline iPolyline) {
        super(iPolyline);

        setVisibility(GObject.DATA_VISIBLE | GObject.ANNOTATION_INVISIBLE | GObject.SYMBOLS_INVISIBLE);

        border = new GSegment();
        addSegment(border);
        
        if (withBox) {
            boundingBox = new GSegment();
            GStyle bboxStyle = new GStyle();
            bboxStyle.setForegroundColor(new GColor(100, 100, 150));
            bboxStyle.setBackgroundColor(null);
            addSegment(boundingBox);
            boundingBox.setStyle(bboxStyle);
        }

        GStyle textStyle = new GStyle();
        textStyle.setForegroundColor(new GColor(100, 100, 150));
        textStyle.setBackgroundColor(null);
        int offset = 0;

        Iterator<ICurviPoint> itr = iPolyline.iterator();
        while (itr.hasNext()) {
            itr.next();
            GText text = new GText(String.valueOf(offset), GPosition.FIRST | GPosition.STATIC);
            text.setPositionOffset(offset++);

            text.setStyle(textStyle);
            border.addText(text);
        }

        GStyle symbolStyle = new GStyle();
        symbolStyle.setForegroundColor(new GColor(100, 100, 150));
        GImage square = new GFXSymbol(GFXSymbol.SYMBOL_CIRCLE2);
        square.setStyle(symbolStyle);

        border.setVertexImage(square);
        
       

    }

    public IPolyline getLine() {
        return (IPolyline) getUserData();
    }

    @Override
    protected void directDraw() {

        IPolyline line = getLine();

        List<ICurviPoint> pts = line.getPoints();

        int npts = (line.isClosed()) ? pts.size() + 1 : pts.size();
        double[] xpts = new double[npts];
        double[] ypts = new double[npts];

        double[] w_pt = new double[2];

        int i = 0;
        for (ICurviPoint tp : pts) {
            line.getPosition(tp, w_pt);
            xpts[i] = w_pt[0];
            ypts[i] = w_pt[1];
            i++;
        }
        if (line.isClosed()) {
            ICurviPoint tp = pts.get(0);
            line.getPosition(tp, w_pt);
            xpts[npts - 1] = w_pt[0];
            ypts[npts - 1] = w_pt[1];
        }

        border.setWorldGeometry(xpts, ypts);
        
        if (boundingBox != null) {
            GRect bbox = border.getRectangle();
            
            int[] xx = new int[5];
            int[] yy = new int[5];
            
            xx[0] = bbox.x;
            yy[0] = bbox.y;
            xx[1] = bbox.x+bbox.width;
            yy[1] = bbox.y;
            xx[2] = bbox.x+bbox.width;
            yy[2] = bbox.y+bbox.height;
            xx[3] = bbox.x;
            yy[3] = bbox.y+bbox.height;
            xx[4] = bbox.x;
            yy[4] = bbox.y;
            
            boundingBox.setGeometry( xx, yy );
        }

    }

    @Override
    protected void warpedDraw(IWarp warp) {

        IPolyline line = getLine();

        List<ICurviPoint> pts = line.getPoints();

        int npts = (line.isClosed()) ? pts.size() + 1 : pts.size();
        double[] xpts = new double[npts];
        double[] ypts = new double[npts];

        double[] w_pt = new double[2];
        double[] w_dst = new double[2];
        int i = 0;
        Iterator<ICurviPoint> itr = line.iterator();
        while (itr.hasNext()) {
            ICurviPoint tp = itr.next();
            line.getPosition(tp, w_pt);
            warp.getDeformed(w_pt, w_dst);
            xpts[i] = w_dst[0];
            ypts[i] = w_dst[1];
            i++;
        }
        if (line.isClosed()) {
            ICurviPoint tp = pts.get(0);
            line.getPosition(tp, w_pt);
            warp.getDeformed(w_pt, w_dst);
            xpts[npts - 1] = w_dst[0];
            ypts[npts - 1] = w_dst[1];
        }

        border.setWorldGeometry(xpts, ypts);
    }

    public void setProperty(GProperty gProperty) {
        this.gProperty = gProperty;
        border.createTexture();
    }

    @Override
    public GColor getColor(int x, int y) {

        if (hasProperty()) {
            GTransformer transformer = getScene().getTransformer();
            double[] wCoord = transformer.deviceToWorld(x, y);
            return gProperty.getColor(wCoord);
        }

        return GColor.black;
    }

    public boolean hasProperty() {
        return (gProperty != null);
    }

}

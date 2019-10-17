package stratifx.application.interaction.tools;

import fr.ifp.kronosflow.deform.scene.FaultMS;
import fr.ifp.kronosflow.deform.scene.TriShearFault;
import fr.ifp.kronosflow.kernel.geometry.Matrix2d;
import fr.ifp.kronosflow.kernel.geometry.Point2D;
import fr.ifp.kronosflow.kernel.geometry.Vector2D;
import fr.ifp.kronosflow.kernel.polyline.PolyLine;
import fr.ifp.kronosflow.kernel.polyline.explicit.ExplicitPolyLine;
import fr.ifp.kronosflow.model.Patch;
import stratifx.application.interaction.MasterSlaveInteraction;
import stratifx.application.views.GPolyline;
import stratifx.application.views.GVectorField;
import stratifx.canvas.graphics.GColor;
import stratifx.canvas.graphics.GObject;
import stratifx.canvas.graphics.GScene;
import stratifx.canvas.graphics.GStyle;
import stratifx.canvas.interaction.GMouseEvent;

import java.util.ArrayList;
import java.util.List;

public class TriShearInteraction  extends MasterSlaveInteraction {

    class Vector {
        double[] ori;
        double[] dir;
    }

    private class GFault extends GObject {

        PolyLine line1;

        PolyLine line2;

        public GFault(TriShearFault tsf){
            setUserData(tsf);
            GPolyline gFault = new GPolyline(tsf.getFault());

            GStyle style = new GStyle();
            style.setForegroundColor(GColor.red);
            style.setLineWidth(2);
            gFault.setStyle(style);

            add(gFault);

            double length = tsf.getFaultLength();
            double time = length / tsf.getV0();

            List<Point2D> pts = new ArrayList<>();
            double[] start = tsf.get_tip_position(time);
            pts.add(new Point2D(start));
            Point2D end1 = new Point2D(
                    start[0] + length*Math.cos(tsf.getFaultAngle() + tsf.getPhi1()),
                    start[1] + length*Math.sin(tsf.getFaultAngle() + tsf.getPhi1())
                    );
            pts.add(end1);
            line1 = new ExplicitPolyLine();
            line1.initialize(pts);

            GPolyline gLine1 = new GPolyline(line1);

            style = new GStyle();
            style.setForegroundColor(GColor.blue);
            style.setLineWidth(2);
            gLine1.setStyle(style);
            add(gLine1);


            pts = new ArrayList<>();
            pts.add(new Point2D(start));
            Point2D end2 = new Point2D(
                    start[0] + length*Math.cos(tsf.getFaultAngle() - tsf.getPhi2()),
                    start[1] + length*Math.sin(tsf.getFaultAngle() - tsf.getPhi2())
            );
            pts.add(end2);
            line2 = new ExplicitPolyLine();
            line2.initialize(pts);

            GPolyline gLine2 = new GPolyline(line2);

            style = new GStyle();
            style.setForegroundColor(GColor.blue);
            style.setLineWidth(2);
            gLine2.setStyle(style);
            add(gLine2);


            int n_vectors = 10;
            GVectorField gVectorField = new GVectorField();
            gVectorField.setRatio(.6);
            double dx = length / (double)(n_vectors-1);
            double[] xy = new double[2];
            for( int i =0; i<n_vectors; i++ ){
                xy[0] = i*dx ;

                for( int j=0; j<2*n_vectors; j++ ) {
                    xy[1] = j * dx *.5 - length/2.;

                    double[] ori = tsf.get_world_coordinate(time, xy);
                    double[] dir;
                    double m = Math.tan(tsf.getPhi1());
                    if ( xy[1] >= xy[0]*m){
                        dir = new double[]{ tsf.getV0(), 0 };
                    }
                    else if ( xy[1] <= -xy[0]*m ){
                        dir = new double[]{ 0, 0 };
                    }
                    else {
                        double sign = (xy[1] < 0) ? -1 : 1;
                        dir = new double[]{
                                tsf.getV0() * .5 * (sign * Math.abs(xy[1]) / xy[0] / m + 1),
                                tsf.getV0() * .25 * m * (Math.pow(Math.abs(xy[1]) / xy[0] / m, 2) - 1)
                        };
                    }

                    double mc = Math.cos(tsf.getFaultAngle());
                    double ms = Math.sin(tsf.getFaultAngle());
                    Matrix2d rotation = new Matrix2d(mc, -ms, ms, mc);
                    rotation.multiply(dir, dir);

                    gVectorField.addVector(ori, dir);
                }
            }
            add(gVectorField);

        }

        public TriShearFault getTriShearFault(){
            return (TriShearFault)getUserData();
        }
    }

    public TriShearInteraction(GScene scene) {
        super(scene);
    }

    protected void handleMousePress(GMouseEvent event) {

        if (event.button != GMouseEvent.BUTTON_1) {
            return;
        }

        Patch patch = getSelectedPatch(event.x, event.y);

        if ( null == patch ){
            return;
        }

        scene = createPatchScene(patch);

        FaultMS faultMS = (FaultMS)selectFault(event.x, event.y);



        if ( null != faultMS ){

            TriShearFault tsf = new TriShearFault(faultMS.getSupport());

            GFault gFault = new GFault(tsf);
            gObjects.put(G_FAULT, gFault);
            gscene.add(gFault);

            gFault.redraw();

        }


    }


}

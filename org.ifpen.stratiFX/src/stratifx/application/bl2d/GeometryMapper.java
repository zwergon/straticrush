package stratifx.application.bl2d;

import fr.ifp.kronosflow.kernel.geometry.Point2D;
import fr.ifp.kronosflow.kernel.polyline.Node;
import fr.ifp.kronosflow.mesh.Mesh2D;
import fr.ifp.kronosflow.uids.IHandle;
import fr.ifpen.kine.BL2D.geometry.*;

import java.util.ArrayList;
import java.util.List;

public class GeometryMapper {

    public GeometryMapper(){ }

    public List<Point> createDummyPoints(List<Point2D> ps){
        List<Point> pts = new ArrayList<Point>();

        int id = 1;

        for (Point2D p : ps){
            pts.add(new Point(Integer.toString(id),Double.toString(p.x()),Double.toString(p.y())));
        }

        return pts;
    }

    public List<Curve> createDummyCurves(List<Point> pts){
        List<Curve> cs = new ArrayList<Curve>();

        int id = 500;
        Point bP = null;
        Point cP = null;
        Point fP = null;

        for (Point pt : pts){
            if(cP != null){
                bP = new Point(cP.getPointID(),cP.getPointX(),cP.getPointY());
            }
            else{
                fP = new Point(pt.getPointID(),pt.getPointX(),pt.getPointY());
            }

            cP = pt;

            if(bP != null && cP != null){
                cs.add(new Curve(Integer.toString(id),"NULL",bP.getPointID(),new ArrayList<String>(),cP.getPointID(),"NULL"));
            }
            bP = new Point(pt.getPointID(),pt.getPointX(),pt.getPointY());
        }

        cs.add(new Curve(Integer.toString(id),"NULL",bP.getPointID(),new ArrayList<String>(),fP.getPointID(),"NULL"));

        return cs;
    }

    public List<Domain> createDummyDomain(List<Curve> cs){
        List<Domain> ds = new ArrayList<Domain>();

        Curve c = cs.remove(0);
        ds.add(new Domain(c.getCurveID(),"-1",0));

        return ds;
    }

    public Geometry geomFromMesh2D(List<Point2D> ps){
        List<Point> pts = createDummyPoints(ps);
        Geometry geometry = new Geometry();
        geometry.setMaxNP(1000);
        geometry.setMaxNC(1000);
        geometry.setMaxND(1000);
        geometry.setPoints(pts);
        geometry.setImposedPoints(new ArrayList<String>());
        List<Curve> cs = createDummyCurves(pts);
        geometry.setCurves(cs);
        geometry.setRefOnPoints(new ArrayList<RefOnPoint>());
        geometry.setRefOnCurves(new ArrayList<RefOnCurve>());
        List<Domain> ds = createDummyDomain(cs);
        geometry.setDomains(ds);
        geometry.setMetric(new Metrics("isotrope", new ArrayList<Metric>()));

        return geometry;
    }
}

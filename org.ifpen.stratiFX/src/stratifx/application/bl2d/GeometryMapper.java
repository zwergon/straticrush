package stratifx.application.bl2d;

import fr.ifp.kronosflow.kernel.geometry.Point2D;
import fr.ifp.kronosflow.kernel.polyline.Node;
import fr.ifp.kronosflow.kernel.polyline.PolyLine;
import fr.ifp.kronosflow.kernel.polyline.SortedList;
import fr.ifp.kronosflow.mesh.Mesh2D;
import fr.ifp.kronosflow.uids.IHandle;
import fr.ifpen.kine.BL2D.geometry.*;
import stratifx.application.main.GParameters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeometryMapper {

    private int maxPoints = 0;
    private int maxCurves = 0;
    private int maxDomains = 0;
    private List<Point> pointList = new ArrayList<Point>();
    private List<String> imposedPoints = new ArrayList<String>();
    private List<Curve> curveList = new ArrayList<Curve>();
    private List<RefOnPoint> refOnPointList = new ArrayList<RefOnPoint>();
    private List<RefOnCurve> refOnCurveList = new ArrayList<RefOnCurve>();
    private List<Domain> domainList = new ArrayList<Domain>();

    public GeometryMapper(){ }

    public void createGeometry(PolyLine line){
        List<Point2D> borderPoints = line.getPoints2D();
        int size = borderPoints.size();
        this.maxPoints = size*10;
        this.maxCurves = size*10;
        this.maxDomains = size*10;
        List<String> pointIDs = new ArrayList<String>();
        for(int i = 0; i < size; i++){
            this.pointList.add(new Point("P"+Integer.toString(i),Double.toString(borderPoints.get(i).x()),Double.toString(borderPoints.get(i).y())));
            pointIDs.add("P"+Integer.toString(i));

        }

        for(int i = 1; i < size; i++){
            this.curveList.add(new Curve("L"+Integer.toString(i),"NULL",pointIDs.get(i-1),new ArrayList<String>(),pointIDs.get(i),"NULL"));
        }

        this.curveList.add(new Curve("L"+Integer.toString(size),"NULL",pointIDs.get(size-1),new ArrayList<String>(),pointIDs.get(0),"NULL"));

        this.domainList.add(new Domain(this.curveList.get(0).getCurveID(),"+1",0));
    }

    public Geometry geomFromMesh2D(PolyLine line){
        createGeometry(line);
        Geometry geometry = new Geometry();
        geometry.setMaxNP(this.maxPoints);
        geometry.setMaxNC(this.maxCurves);
        geometry.setMaxND(this.maxDomains);
        geometry.setPoints(pointList);
        geometry.setImposedPoints(this.imposedPoints);
        geometry.setCurves(curveList);
        geometry.setRefOnPoints(this.refOnPointList);
        geometry.setRefOnCurves(this.refOnCurveList);
        geometry.setDomains(domainList);
        geometry.setMetric(new Metrics("isotrope", new ArrayList<Metric>()));

        return geometry;
    }
}

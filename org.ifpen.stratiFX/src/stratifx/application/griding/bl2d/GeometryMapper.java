package stratifx.application.griding.bl2d;

import fr.ifp.kronosflow.incubator.extractor.CompositeLine;
import fr.ifp.kronosflow.incubator.extractor.LineExtractor;
import fr.ifp.kronosflow.kernel.geometry.Point2D;
import fr.ifp.kronosflow.kernel.polyline.*;
import fr.ifp.kronosflow.model.*;
import fr.ifp.kronosflow.uids.UID;
import fr.ifp.kronosflow.utils.LOGGER;
import fr.ifpen.kine.BL2D.geometry.*;
import fr.ifpen.kine.gmsh.geom.Line;
import org.json.simple.JSONObject;
import stratifx.application.main.GParameters;
import stratifx.application.patches.PatchStyle;
import stratifx.model.loader.AbstractLoader;
import stratifx.model.persistable.IPersisted;

import java.util.*;

public class GeometryMapper {

    private int maxPoints = 100;
    private int maxCurves = 100;
    private int maxDomains = 100;
    private List<Point> pointList = new ArrayList<Point>();
    private List<String> imposedPoints = new ArrayList<String>();
    private List<Curve> curveList = new ArrayList<Curve>();
    private List<RefOnPoint> refOnPointList = new ArrayList<RefOnPoint>();
    private List<RefOnCurve> refOnCurveList = new ArrayList<RefOnCurve>();
    private List<Domain> domainList = new ArrayList<Domain>();
    private List<String> curvesDomains = new ArrayList<String>();

    private int pID = 0;
    private int cID = 0;
    private Map<UID,String> idsMap = new HashMap<UID,String>();
    private Map<String,Point2D> point2DMap = new HashMap<String, Point2D>();

    public GeometryMapper(){ }

    private List<String> createPoints(CompositeLine line){
        List<String> list = new ArrayList<String>();
        for (ICurviPoint p : line.getPoints()){
            LinePoint lp = (LinePoint)p;
            PatchPoint pp = (PatchPoint)lp;
            if (this.idsMap.get(pp.getCurviPoint().getUID())==null){
                this.idsMap.put(pp.getCurviPoint().getUID(),Integer.toString(this.pID));
                this.point2DMap.put(Integer.toString(this.pID),pp.getPosition());

                this.pointList.add(
                        new Point(
                                Integer.toString(this.pID),
                                Double.toString(pp.getPosition().x()),
                                Double.toString(pp.getPosition().y())
                        )
                );
                this.pID++;
                this.maxPoints++;

            }
            list.add(this.idsMap.get(pp.getCurviPoint().getUID()));
        }
        return list;
    }

    private void createLineImposedPoints(CompositeLine line){
        List<ICurviPoint> pts = line.getPoints();
        for (ICurviPoint p : pts){
            LinePoint lp = (LinePoint)p;
            PatchPoint pp = (PatchPoint)lp;
            if (this.idsMap.get(pp.getCurviPoint().getUID())==null){
                this.idsMap.put(pp.getCurviPoint().getUID(),Integer.toString(this.pID));
                this.point2DMap.put(Integer.toString(this.pID),pp.getPosition());

                this.pointList.add(
                        new Point(
                                Integer.toString(this.pID),
                                Double.toString(pp.getPosition().x()),
                                Double.toString(pp.getPosition().y())
                        )
                );
                this.pID++;
                this.maxPoints++;
                this.imposedPoints.add(this.idsMap.get(pp.getCurviPoint().getUID()));
            }
        }
    }

    private void createImposedPoints(List<CompositeLine> lines){
        for (CompositeLine line : lines){
            createLineImposedPoints(line);
        }
    }

    private void createBaseCurve(String deb, List<String> interns, String end){
        this.curveList.add(
                new Curve(
                        "C"+Integer.toString(this.cID),
                        "NULL",
                        deb,
                        interns,
                        end,
                        "NULL"
                )
        );
        this.domainList.add(
                new Domain(
                        "C"+Integer.toString(this.cID),
                        "+1",
                        0
                )
        );
        this.cID++;
        this.maxCurves++;
        this.maxDomains++;
    }
    private void createCurve(CompositeLine line){
        List<String> list = createPoints(line);
        int size = list.size();
        String end = list.remove(size-1);
        String deb = list.remove(0);
        if (size == 2){
            createBaseCurve(deb,new ArrayList<String>(),end);
        }
        else {
            createBaseCurve(deb,list,end);
        }
    }

    private void createSegments(CompositeLine line){
        List<String> list = createPoints(line);
        int size = list.size();
        for (int i = 0; i < size-1; i++){
            createBaseCurve(list.get(i),new ArrayList<String>(),list.get(i+1));
        }
    }

    private void createLineSegments(List<CompositeLine> lines){
        for (CompositeLine line : lines){
            createSegments(line);
        }
    }

    private void createCurves(List<CompositeLine> lines){
        for (CompositeLine line : lines){
            createCurve(line);
        }
    }

    public Geometry geomFromMesh2D(Patch selected){
        BL2DStyle style = new BL2DStyle(GParameters.getStyle());
        LineExtractor lineExtractor = new LineExtractor();
        lineExtractor.extractLines(selected);

        PatchStyle patchStyle = new PatchStyle(GParameters.getStyle());
        List<String> names = lineExtractor.getpNames();
        for (String name : names){
            if (!patchStyle.getPatch(name)){
                lineExtractor.removePatch(name);
            }
        }

        List<CompositeLine> borderLines = lineExtractor.getBorders();

        List<CompositeLine> interns = lineExtractor.getInternals();

        if (style.getBORDERPOINTS().equals("Yes")){
            createLineSegments(borderLines);
        }
        else {
            createCurves(borderLines);
        }
        if (style.getINNERCONTACTS().equals("Inner Curves")){
            createCurves(interns);
        }
        else if (style.getINNERCONTACTS().equals("Inner Segments")){
            createLineSegments(interns);
        }
        else if (style.getINNERCONTACTS().equals("Inner Points")){
            createImposedPoints(interns);
        }


        return createGeometry();
    }

    private List<String> create2Points(List<Point2D> point2DS){
        List<String> ids = new ArrayList<String>();
        for (Point2D point2D : point2DS) {
            this.pointList.add(new Point(Integer.toString(this.pID), Double.toString(point2D.x()), Double.toString(point2D.y())));
            ids.add(Integer.toString(this.pID));
            this.pID++;
            this.maxPoints++;
        }
        return ids;
    }

    private void create2Curve(List<Point2D> point2DS){
        List<String> listIDS = create2Points(point2DS);
        String deb = listIDS.remove(0);
        this.curveList.add(new Curve("C"+Integer.toString(this.cID),"NULL",deb,listIDS,deb,"NULL"));
        this.domainList.add(new Domain("C"+Integer.toString(this.cID),"+1",0));
        this.cID++;
        this.maxCurves++;
        this.maxDomains++;
    }

    private void create2Segments(List<Point2D> point2DS){
        List<String> listIDS = create2Points(point2DS);
        int sz = listIDS.size();
        int deb = this.cID;
        for (int i = 0; i < sz-1; i++){
            this.curveList.add(new Curve("C"+Integer.toString(this.cID),"NULL",listIDS.get(i),new ArrayList<String>(),listIDS.get(i+1),"NULL"));
            this.cID++;
            this.maxCurves++;
        }
        this.curveList.add(new Curve("C"+Integer.toString(this.cID),"NULL",listIDS.get(sz-1),new ArrayList<String>(),listIDS.get(0),"NULL"));
        this.cID++;
        this.maxCurves++;
        this.domainList.add(new Domain("C"+Integer.toString(deb),"+1",0));
        this.maxDomains++;
    }

    public Geometry geom2FromMesh2D(List<Point2D> point2DS){
        BL2DStyle style = new BL2DStyle(GParameters.getStyle());
        if (style.getBORDERPOINTS().equals("Yes")){
            create2Segments(point2DS);
        }
        else{
            create2Curve(point2DS);
        }
        return createGeometry();
    }

    private Geometry createGeometry(){

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

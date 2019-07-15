package stratifx.application.griding.bl2d;

import fr.ifp.kronosflow.incubator.extractor.CompositeLine;
import fr.ifp.kronosflow.incubator.extractor.LineExtractor;
import fr.ifp.kronosflow.kernel.geometry.Point2D;
import fr.ifp.kronosflow.kernel.polyline.*;
import fr.ifp.kronosflow.model.*;
import fr.ifp.kronosflow.uids.UID;
import fr.ifpen.kine.BL2D.geometry.*;
import stratifx.application.main.GParameters;

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
        pts.remove(0);
        pts.remove(pts.size()-1);
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
            }
            this.imposedPoints.add(this.idsMap.get(pp.getCurviPoint().getUID()));
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
        EnvStyle style = new EnvStyle(GParameters.getStyle());
        LineExtractor lineExtractor = new LineExtractor();
        lineExtractor.extractLines(selected);
        if (style.getBORDERPOINTS().equals("Yes")){
            createLineSegments(lineExtractor.getBorderLines());
        }
        else {
            createCurves(lineExtractor.getBorderLines());
        }
        if (style.getINNERCONTACTS().equals("Inner Curves")){
            createCurves(lineExtractor.getInternLines());
        }
        else if (style.getINNERCONTACTS().equals("Inner Segments")){
            createLineSegments(lineExtractor.getInternLines());
        }
        else if (style.getINNERCONTACTS().equals("Inner Points")){
            createImposedPoints(lineExtractor.getInternLines());
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

package stratifx.application.filters;

import fr.ifp.kronosflow.kernel.geometry.Point2D;
import fr.ifp.kronosflow.kernel.polyline.explicit.ExplicitPolyLine;
import fr.ifp.kronosflow.model.style.Style;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import fr.ifp.kronosflow.model.LineSet;
import fr.ifp.kronosflow.model.StyledLine;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LineSetImportJSON {

    String filename;

    LineSet lineSet;

    public LineSetImportJSON(String filename){
        this.filename = filename;
    }

    public LineSet getLineSet(){
        return lineSet;
    }

    public boolean execute() {

        JSONParser parser = new JSONParser();

        try {
            JSONObject obj = (JSONObject) parser.parse(new FileReader(filename));

            lineSet = new LineSet();

            JSONArray linesArray = (JSONArray)obj.get("lines");
            for(int i =0; i<linesArray.size(); i++ ){
                JSONObject line = (JSONObject)linesArray.get(i);
                JSONArray points = (JSONArray)line.get("points");

                List<Point2D> pts = new ArrayList<>();

                for( int j =0; j<points.size(); j++ ){
                    JSONArray xyArray = (JSONArray)(points.get(j));
                    pts.add( new Point2D( (double)xyArray.get(0), (double)xyArray.get(1)) );
                }

                ExplicitPolyLine polyline = new ExplicitPolyLine();
                polyline.initialize(pts);

                StyledLine styledLine = new StyledLine(polyline);
                Style style = styledLine.getStyle();

                style.setAttribute("featureName", (String)line.get("featureName"));
                style.setAttribute("featureType", (String)line.get("featureType"));


                lineSet.add(styledLine);


            }

            return true;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }



}

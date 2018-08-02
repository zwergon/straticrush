package stratifx.application.views;

import fr.ifp.kronosflow.controllers.events.IControllerEvent;
import fr.ifp.kronosflow.model.KinObject;
import fr.ifp.kronosflow.model.LineSet;
import fr.ifp.kronosflow.model.StyledLine;
import stratifx.canvas.graphics.GColor;
import stratifx.canvas.graphics.GStyle;

import java.awt.*;

public class GLineSetView extends GView {
    @Override
    public void setModel(Object object) {
        setUserData(object);

        LineSet lineSet = (LineSet) object;
        setName(lineSet.getName());

        createLines();

    }

    void createLines(){
        LineSet lineSet = getLineSet();

        for(KinObject child : lineSet.getChildren() ){
            StyledLine line = (StyledLine)child;
            GPolyline gLine = new GPolyline(line.getPolyline());

            Color aColor = line.getColor();

            GStyle style = new GStyle();
            style.setForegroundColor(new GColor(aColor.getRed(), aColor.getGreen(), aColor.getBlue(), aColor.getAlpha()));
            style.setLineWidth(2);
            gLine.setStyle(style);

            add(gLine);
        }
    }

    @Override
    public void modelChanged(IControllerEvent<?> event) {

    }

    public LineSet getLineSet(){
        return (LineSet)getUserData();
    }
}

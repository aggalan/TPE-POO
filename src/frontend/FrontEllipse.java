package frontend;

import backend.model.Ellipse;
import backend.model.Figure;
import backend.model.Point;
import javafx.scene.canvas.GraphicsContext;

import java.awt.*;

public class FrontEllipse<T extends Ellipse> extends FrontFigure<T>{

    public FrontEllipse(Point centerPoint, double sMayorAxis, double sMinorAxis, GraphicsContext gc) {
        super(gc);

    }

//    @Override
//    public void create(Figure figure) {
//        Ellipse ellipse = (Ellipse) figure;
//        gc.strokeOval(ellipse.getCenterPointX() - (ellipse.getsMayorAxis() / 2), ellipse.getCenterPointY() - (ellipse.getsMinorAxis() / 2), ellipse.getsMayorAxis(), ellipse.getsMinorAxis());
//        gc.fillOval(ellipse.getCenterPointX() - (ellipse.getsMayorAxis() / 2), ellipse.getCenterPointY() - (ellipse.getsMinorAxis() / 2), ellipse.getsMayorAxis(), ellipse.getsMinorAxis());
//    }


    @Override
    public void create(FrontFigure<Figure> frontFigure) {

    }
}

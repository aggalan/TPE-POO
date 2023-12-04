package frontend;

import backend.model.Ellipse;
import backend.model.Figure;
import backend.model.Point;
import javafx.scene.canvas.GraphicsContext;

import java.awt.*;

public class FrontEllipse extends Ellipse implements FrontGc{
    private GraphicsContext gc;
    public FrontEllipse(Point centerPoint, double sMayorAxis, double sMinorAxis, GraphicsContext gc) {
        super(centerPoint, sMayorAxis, sMinorAxis);
        this.gc = gc;
    }

    @Override
    public void create(Figure figure) {
        Ellipse ellipse = (Ellipse) figure;
        gc.strokeOval(ellipse.getCenterPointX() - (ellipse.getsMayorAxis() / 2), ellipse.getCenterPointY() - (ellipse.getsMinorAxis() / 2), ellipse.getsMayorAxis(), ellipse.getsMinorAxis());
        gc.fillOval(ellipse.getCenterPointX() - (ellipse.getsMayorAxis() / 2), ellipse.getCenterPointY() - (ellipse.getsMinorAxis() / 2), ellipse.getsMayorAxis(), ellipse.getsMinorAxis());
    }
}

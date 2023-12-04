package frontend;

import backend.model.Ellipse;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class FrontEllipse<T extends Ellipse> extends FrontFigure<T>{

    public FrontEllipse(T ellipse, GraphicsContext gc, Color color) {
        super(gc, ellipse, color);
    }

    @Override
    public void create() {
        Ellipse ellipse = super.getFigure();
        super.getGc().strokeOval(ellipse.getCenterPointX() - (ellipse.getsMayorAxis() / 2), ellipse.getCenterPointY() - (ellipse.getsMinorAxis() / 2), ellipse.getsMayorAxis(), ellipse.getsMinorAxis());
        super.getGc().fillOval(ellipse.getCenterPointX() - (ellipse.getsMayorAxis() / 2), ellipse.getCenterPointY() - (ellipse.getsMinorAxis() / 2), ellipse.getsMayorAxis(), ellipse.getsMinorAxis());
    }



}


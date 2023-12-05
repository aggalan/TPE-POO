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
        super.getGc().strokeOval(ellipse.getCenterPoint().getX() - (ellipse.getWidth() / 2), ellipse.getCenterPoint().getY() - (ellipse.getHeight() / 2), ellipse.getWidth(), ellipse.getHeight());
        super.getGc().fillOval(ellipse.getCenterPoint().getX() - (ellipse.getWidth() / 2), ellipse.getCenterPoint().getY() - (ellipse.getHeight() / 2), ellipse.getWidth(), ellipse.getHeight());
    }



}


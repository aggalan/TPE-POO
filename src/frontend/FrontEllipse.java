package frontend;

import backend.model.Ellipse;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.*;

public class FrontEllipse<T extends Ellipse> extends FrontFigure<T>{

    public FrontEllipse(T ellipse, GraphicsContext gc, Color color) {
        super(gc, ellipse, color);
    }

    @Override
    public void create(double offset) {
        Ellipse ellipse = super.getFigure();
         if(gradientStatus && offset == 0){
            RadialGradient radialGradient = new RadialGradient(0, 0, 0.5, 0.5, 0.5, true,
                    CycleMethod.NO_CYCLE,
                    new Stop(0, getColor()),
                    new Stop(1, getColor().invert()));
            getGc().setFill(radialGradient);
        }
        super.getGc().strokeOval(ellipse.getCenterPoint().getX() - (ellipse.getWidth() / 2), ellipse.getCenterPoint().getY() - (ellipse.getHeight() / 2), ellipse.getWidth(), ellipse.getHeight());
        super.getGc().fillOval(ellipse.getCenterPoint().getX() - (ellipse.getWidth() / 2) + offset, ellipse.getCenterPoint().getY() - (ellipse.getHeight() / 2) + offset, ellipse.getWidth(), ellipse.getHeight());
    }

}


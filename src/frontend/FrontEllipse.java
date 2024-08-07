package frontend;

import backend.Ellipse;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.*;
import javafx.scene.shape.ArcType;

public class FrontEllipse<T extends Ellipse> extends FrontFigure<T> {

    public FrontEllipse(T ellipse, GraphicsContext gc, Color color) {
        super(gc, ellipse, color);
    }

    @Override
    public void create(double offset) {
        Ellipse ellipse = super.getFigure();
        if(gradientStatus && offset == 0){
            createGradient();
        }
        getGc().setLineWidth(3);
        super.getGc().strokeOval(ellipse.getCenterPoint().getX() - (ellipse.getWidth() / 2), ellipse.getCenterPoint().getY() - (ellipse.getHeight() / 2), ellipse.getWidth(), ellipse.getHeight());
        super.getGc().fillOval(ellipse.getCenterPoint().getX() - (ellipse.getWidth() / 2) + offset, ellipse.getCenterPoint().getY() - (ellipse.getHeight() / 2) + offset, ellipse.getWidth(), ellipse.getHeight());
        if (beveledStatus && offset == 0) {
            createBeveled();
        }
        getGc().setLineWidth(1);
    }


    private void createBeveled(){
        Ellipse ellipse = getFigure();
        double arcX = ellipse.getCenterPoint().getX() - ellipse.getWidth()/2;
        double arcY = ellipse.getCenterPoint().getY() - ellipse.getHeight()/2;
        double bevelOffset = 4 ;
        double beveledWidth = ellipse.getWidth() + 2 * bevelOffset;
        double beveledHeight = ellipse.getHeight() + 2 * bevelOffset;
        getGc().setLineWidth(5);
        getGc().setStroke(Color.LIGHTGRAY);
        getGc().strokeArc(arcX - bevelOffset, arcY - bevelOffset, beveledWidth, beveledHeight, 45, 180, ArcType.OPEN);
        getGc().setStroke(Color.BLACK);
        getGc().strokeArc(arcX - bevelOffset, arcY - bevelOffset, beveledWidth, beveledHeight, 225, 180, ArcType.OPEN);

    }

    private void createGradient(){
        RadialGradient radialGradient = new RadialGradient(0, 0, 0.5, 0.5, 0.5, true,
                CycleMethod.NO_CYCLE,
                new Stop(0, getColor()),
                new Stop(1, getColor().invert()));
        getGc().setFill(radialGradient);
    }
}
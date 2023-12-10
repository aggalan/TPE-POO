package frontend;

import backend.Rectangle;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.*;

public class FrontRectangle<T extends Rectangle>  extends FrontFigure<T> {

    public FrontRectangle(T rectangle, GraphicsContext gc, Color color) {
        super(gc, rectangle, color);
    }

    public void create(double offset){
        Rectangle rectangle = super.getFigure();
        if (gradientStatus && offset == 0){
            addGradient();
        }
        if (isInvisibleRectangle) {
            getGc().setLineDashes(10);
        }
        getGc().fillRect(rectangle.getTopLeftX() + offset, rectangle.getTopLeftY() + offset, rectangle.getWidth(), rectangle.getHeight());
        getGc().strokeRect(rectangle.getTopLeftX(), rectangle.getTopLeftY(), rectangle.getWidth(), rectangle.getHeight());
        if (beveledStatus && offset == 0){
            addBevel();
        }
        getGc().setLineWidth(1);
        getGc().setLineDashes(0);
    }

    private void addBevel(){
        Rectangle rectangle  = getFigure();
        double x = rectangle.getTopLeft().getX();
        double y = rectangle.getTopLeft().getY();
        getGc().setLineWidth(5);
        double bevelOffset = 3;
        getGc().setStroke(Color.LIGHTGRAY);
        getGc().strokeLine(x - bevelOffset, y - bevelOffset, x + rectangle.getWidth() + bevelOffset, y - bevelOffset);
        getGc().strokeLine(x - bevelOffset, y - bevelOffset, x - bevelOffset, y + rectangle.getHeight() + bevelOffset);
        getGc().setStroke(Color.BLACK);
        getGc().strokeLine(x - bevelOffset, y + rectangle.getHeight() + bevelOffset, x + rectangle.getWidth() + bevelOffset, y + rectangle.getHeight() + bevelOffset);
        getGc().strokeLine(x + rectangle.getWidth() + bevelOffset, y - bevelOffset, x + rectangle.getWidth() + bevelOffset, y + rectangle.getHeight() + bevelOffset);
    }
    private void addGradient(){
        LinearGradient linearGradient = new LinearGradient(0, 0, 1, 0, true,
                CycleMethod.NO_CYCLE,
                new Stop(0, getColor()),
                new Stop(1, getColor().invert()));
        getGc().setFill(linearGradient);
    }

}

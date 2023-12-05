package frontend;

import backend.model.Rectangle;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.*;
import javafx.scene.transform.Rotate;

public class FrontRectangle<T extends Rectangle>  extends FrontFigure<T> {
    private T rectangle;

    public FrontRectangle(T rectangle, GraphicsContext gc, Color color) {
        super(gc, rectangle, color);
    }

    public void create(double offset) {
        Rectangle rectangle = super.getFigure();
         if(gradientStatus && offset == 0){
            LinearGradient linearGradient = new LinearGradient(0, 0, 1, 0, true,
                    CycleMethod.NO_CYCLE,
                    new Stop(0, getColor()),
                    new Stop(1, getColor().invert()));
            getGc().setFill(linearGradient);
        }
        getGc().fillRect(rectangle.getTopLeftX() + offset, rectangle.getTopLeftY() + offset,
                rectangle.getWidth(), rectangle.getHeight());
        getGc().strokeRect(rectangle.getTopLeftX(), rectangle.getTopLeftY(),
                rectangle.getWidth(), rectangle.getHeight());
    }

}

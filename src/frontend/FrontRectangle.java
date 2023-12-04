package frontend;

import backend.model.Rectangle;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class FrontRectangle<T extends Rectangle>  extends FrontFigure<T> {
    private T rectangle;
    private T rectangle;


    public FrontRectangle(T rectangle, GraphicsContext gc, Color color) {
        super(gc, rectangle, color);
    }

    public void create() {
        Rectangle rectangle = super.getFigure();
        getGc().fillRect(rectangle.getTopLeftX(), rectangle.getTopLeftY(),
                Math.abs(rectangle.getTopLeftX() - rectangle.getBottomRightX()), Math.abs(rectangle.getTopLeftY() - rectangle.getBottomRightY()));
        getGc().strokeRect(rectangle.getTopLeftX(), rectangle.getTopLeftY(),
                Math.abs(rectangle.getTopLeftX() - rectangle.getBottomRightX()), Math.abs(rectangle.getTopLeftY() - rectangle.getBottomRightY()));
    }


}

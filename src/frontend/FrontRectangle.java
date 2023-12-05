package frontend;

import backend.model.Rectangle;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

public class FrontRectangle<T extends Rectangle>  extends FrontFigure<T> {
    private T rectangle;

    public FrontRectangle(T rectangle, GraphicsContext gc, Color color) {
        super(gc, rectangle, color);
    }

    public void create() {
        Rectangle rectangle = super.getFigure();
        getGc().fillRect(rectangle.getTopLeftX(), rectangle.getTopLeftY(),
                rectangle.getWidth(), rectangle.getHeight());
        getGc().strokeRect(rectangle.getTopLeftX(), rectangle.getTopLeftY(),
                rectangle.getWidth(), rectangle.getHeight());
    }


}

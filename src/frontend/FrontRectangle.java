package frontend;

import backend.model.Figure;
import backend.model.Point;
import backend.model.Rectangle;
import javafx.scene.canvas.GraphicsContext;

public class FrontRectangle extends Rectangle implements FrontGc {

    private GraphicsContext gc;
    public FrontRectangle(Point topLeft, Point bottomRight, GraphicsContext gc) {
        super(topLeft, bottomRight);
        this.gc = gc;
    }

    public void create(Figure figure) {
        Rectangle rectangle = (Rectangle) figure;
        gc.fillRect(rectangle.getTopLeftX(), rectangle.getTopLeftY(),
                Math.abs(rectangle.getTopLeftX() - rectangle.getBottomRightX()), Math.abs(rectangle.getTopLeftY() - rectangle.getBottomRightY()));
        gc.strokeRect(rectangle.getTopLeftX(), rectangle.getTopLeftY(),
                Math.abs(rectangle.getTopLeftX() - rectangle.getBottomRightX()), Math.abs(rectangle.getTopLeftY() - rectangle.getBottomRightY()));
    }
}

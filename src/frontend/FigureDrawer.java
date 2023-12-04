package frontend;

import backend.model.Ellipse;
import backend.model.Rectangle;
import javafx.scene.canvas.GraphicsContext;

public class FigureDrawer{

    private final GraphicsContext gc;

    public FigureDrawer(GraphicsContext gc) {
        this.gc = gc;
    }

    public void createRectangle(Rectangle rectangle) {
        gc.fillRect(rectangle.getTopLeftX(), rectangle.getTopLeftY(),
                Math.abs(rectangle.getTopLeftX() - rectangle.getBottomRightX()), Math.abs(rectangle.getTopLeftY() - rectangle.getBottomRightY()));
        gc.strokeRect(rectangle.getTopLeftX(), rectangle.getTopLeftY(),
                Math.abs(rectangle.getTopLeftX() - rectangle.getBottomRightX()), Math.abs(rectangle.getTopLeftY() - rectangle.getBottomRightY()));
    }

    public void createElipse(Ellipse ellipse) {
        gc.strokeOval(ellipse.getCenterPointX() - (ellipse.getsMayorAxis() / 2), ellipse.getCenterPointY() - (ellipse.getsMinorAxis() / 2), ellipse.getsMayorAxis(), ellipse.getsMinorAxis());
        gc.fillOval(ellipse.getCenterPointX() - (ellipse.getsMayorAxis() / 2), ellipse.getCenterPointY() - (ellipse.getsMinorAxis() / 2), ellipse.getsMayorAxis(), ellipse.getsMinorAxis());
    }
}

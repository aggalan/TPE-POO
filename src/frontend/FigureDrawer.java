package frontend;

import backend.model.*;
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

    public void createSquare(Square square) {
        createRectangle(new Rectangle(square.getTopLeft(), square.getBottomRight()));
    }

    public void createCircle(Circle circle) {
        createElipse(new Ellipse(circle.getCenterPoint(), circle.getsMayorAxis(), circle.getsMinorAxis()));
    }
}

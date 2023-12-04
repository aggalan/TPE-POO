package frontend;

import backend.model.Point;
import javafx.scene.canvas.GraphicsContext;

public class FrontSquare extends FrontRectangle implements FrontGc{
    public FrontSquare(Point topLeft, Point bottomRight, GraphicsContext gc) {
        super(topLeft, bottomRight, gc);
    }
}

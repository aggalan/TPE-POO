package frontend;

import backend.model.Rectangle;
import javafx.scene.canvas.GraphicsContext;

public class FrontRectangle<T extends Rectangle>  extends FrontFigure<T> {
    private T rectangle;
    public FrontRectangle(T rectangle, GraphicsContext gc) {
        super(gc);
        this.rectangle = rectangle;
    }

    @Override
    public T getFigure() {
        return getRectangle();
    }

    public T getRectangle() {
        return rectangle;
    }

    //    public void create(FrontFigure<T> figure) {
//        Rectangle rectangle = (Rectangle) figure;
//        getGc().fillRect(rectangle.getTopLeftX(), rectangle.getTopLeftY(),
//                Math.abs(rectangle.getTopLeftX() - rectangle.getBottomRightX()), Math.abs(rectangle.getTopLeftY() - rectangle.getBottomRightY()));
//        getGc().strokeRect(rectangle.getTopLeftX(), rectangle.getTopLeftY(),
//                Math.abs(rectangle.getTopLeftX() - rectangle.getBottomRightX()), Math.abs(rectangle.getTopLeftY() - rectangle.getBottomRightY()));
//    }

    @Override
    public void create() {
        getGc().fillRect(rectangle.getTopLeftX(), rectangle.getTopLeftY(),
                Math.abs(rectangle.getTopLeftX() - rectangle.getBottomRightX()), Math.abs(rectangle.getTopLeftY() - rectangle.getBottomRightY()));
        getGc().strokeRect(rectangle.getTopLeftX(), rectangle.getTopLeftY(),
                Math.abs(rectangle.getTopLeftX() - rectangle.getBottomRightX()), Math.abs(rectangle.getTopLeftY() - rectangle.getBottomRightY()));
    }


}

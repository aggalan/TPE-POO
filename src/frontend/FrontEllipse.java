package frontend;

import backend.model.Ellipse;
import javafx.scene.canvas.GraphicsContext;

public class FrontEllipse<T extends Ellipse> extends FrontFigure<T>{

    private T ellipse;

    public FrontEllipse(T ellipse, GraphicsContext gc) {
        super(gc);
        this.ellipse = ellipse;
    }

    @Override
    public T getFigure() {
        return getEllipse();
    }

    public T getEllipse() {
        return ellipse;
    }

    //    @Override
//    public void create(Figure figure) {
//        Ellipse ellipse = (Ellipse) figure;
//        gc.strokeOval(ellipse.getCenterPointX() - (ellipse.getsMayorAxis() / 2), ellipse.getCenterPointY() - (ellipse.getsMinorAxis() / 2), ellipse.getsMayorAxis(), ellipse.getsMinorAxis());
//        gc.fillOval(ellipse.getCenterPointX() - (ellipse.getsMayorAxis() / 2), ellipse.getCenterPointY() - (ellipse.getsMinorAxis() / 2), ellipse.getsMayorAxis(), ellipse.getsMinorAxis());
//    }


    @Override
    public void create() {
        getGc().strokeOval(ellipse.getCenterPointX() - (ellipse.getsMayorAxis() / 2), ellipse.getCenterPointY() - (ellipse.getsMinorAxis() / 2), ellipse.getsMayorAxis(), ellipse.getsMinorAxis());
        getGc().fillOval(ellipse.getCenterPointX() - (ellipse.getsMayorAxis() / 2), ellipse.getCenterPointY() - (ellipse.getsMinorAxis() / 2), ellipse.getsMayorAxis(), ellipse.getsMinorAxis());
    }
}

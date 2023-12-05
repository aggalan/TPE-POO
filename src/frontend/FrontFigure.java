package frontend;

import backend.model.Figure;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public abstract class FrontFigure<T extends Figure> {

    private GraphicsContext gc;
    private T figure;
    private final Color color;
    protected boolean shadowStatus = false;
    private boolean gradientStatus = false;
    private boolean beveldStatus = false;

    protected boolean isShadow = false;

    public FrontFigure(GraphicsContext gc, T figure, Color color) {
        this.gc = gc;
        this.figure = figure;
        this.color = color;
    }

    public void applyShadow(){
        shadowStatus = true;
    }

    public void removeShadow(){
        shadowStatus = false;
    }

    @Override
    public String toString() {
        return figure.toString();
    }

    public T getFigure(){
        return figure;
    }

    public Color getColor() {
        return color;
    }

    public GraphicsContext getGc() {
        return gc;
    }
    public abstract void create(double offset);

    public void createShadow() {
        Paint aux = getGc().getFill();
        getGc().setLineWidth(0);
        getGc().setFill(Color.GRAY);
        create(10);
        getGc().setLineWidth(1);
        getGc().setFill(aux);
    }
}

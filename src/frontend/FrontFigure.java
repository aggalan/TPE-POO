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
    protected boolean gradientStatus = false;
    protected boolean beveledStatus = false;
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

    public void applyGradient(){
        gradientStatus = true;
    }

    public void removeGradient(){
        gradientStatus = false;
    }

    public void applyBeveled(){
        beveledStatus = true;
    }
    public void removeBeveled(){
        beveledStatus = false;
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
    public abstract void drawBorder();

    public void createShadow() {
        Paint aux = getGc().getFill();
        getGc().setLineWidth(0);
        getGc().setFill(Color.GRAY);
        create(10);
        getGc().setLineWidth(1);
        getGc().setFill(aux);
    }
}

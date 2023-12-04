package frontend;

import backend.model.Figure;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class FrontFigure<T extends Figure> {

    private GraphicsContext gc;
    private T figure;
    private final javafx.scene.paint.Color color;

    public FrontFigure(GraphicsContext gc, T figure, Color color) {
        this.gc = gc;
        this.figure = figure;
        this.color = color;
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
    public abstract void create();
}

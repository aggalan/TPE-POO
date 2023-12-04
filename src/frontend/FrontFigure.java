package frontend;

import backend.model.Figure;
import javafx.scene.canvas.GraphicsContext;

public abstract class FrontFigure<T extends Figure>  {
    public abstract void create();
    private GraphicsContext gc;

    public abstract T getFigure();

    public FrontFigure(GraphicsContext gc) {
        this.gc = gc;
    }

    public GraphicsContext getGc() {
        return gc;
    }
}

package frontend;

import backend.model.Figure;
import javafx.scene.canvas.GraphicsContext;

public abstract class FrontFigure<T extends Figure> {
    public abstract void create(FrontFigure<Figure> frontFigure);
    private GraphicsContext gc;

    public FrontFigure(GraphicsContext gc) {
        this.gc = gc;
    }

    public GraphicsContext getGc() {
        return gc;
    }
}

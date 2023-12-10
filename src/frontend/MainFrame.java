package frontend;

import backend.model.Figure;
import javafx.scene.layout.VBox;

import java.util.List;

public class MainFrame extends VBox {

    public MainFrame(List<FrontFigure<? extends Figure>> canvasState) {
        getChildren().add(new AppMenuBar());
        StatusPane statusPane = new StatusPane();
        getChildren().add(new PaintPane(canvasState, statusPane));
        getChildren().add(statusPane);
    }

}

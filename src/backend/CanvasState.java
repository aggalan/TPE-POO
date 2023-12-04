package backend;

import backend.model.Figure;
import frontend.FrontFigure;

import java.util.ArrayList;

public class CanvasState extends ArrayList<FrontFigure<? extends Figure>>{

    public static final Color LINE_COLOR = Color.BLACK;
    public static final Color DEFAULT_FILL_COLOR = Color.YELLOW;

    public boolean isSelected(FrontFigure<? extends Figure> figure){
        return false;
    }



}
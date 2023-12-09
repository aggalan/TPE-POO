package frontend;

import backend.model.Figure;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class FrontFigure<T extends Figure> {

    private GraphicsContext gc;
    private T figure;
    private final Color color;
    protected boolean shadowStatus = false;
    protected boolean gradientStatus = false;
    protected boolean beveledStatus = false;

    protected boolean isInvisibleRectangle = false;

    public boolean getShadowStatus() {
        return shadowStatus;
    }

    public boolean getGradientStatus() {
        return gradientStatus;
    }

    public boolean getBeveledStatus() {
        return beveledStatus;
    }



    public void setCheckBoxStatus(CheckBox checkBox){

    }

    public FrontFigure(GraphicsContext gc, T figure, Color color) {
        this.gc = gc;
        this.figure = figure;
        this.color = color;
    }

    public void shadowStatus(boolean status){
        shadowStatus = status;
    }

    public void gradientStatus(boolean status){
        gradientStatus = status;
    }


    public void beveledStatus(boolean status){
        beveledStatus = status;
    }

    public void setInvisibleRectangle(boolean status) {
        isInvisibleRectangle = status;
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

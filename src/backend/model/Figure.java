package backend.model;

public abstract class Figure {

    public abstract boolean belongs(Point eventPoint);
    public abstract boolean belongsInRectangle(Rectangle imaginaryRectangle);
    public abstract void move(double diffX, double diffY);
    public abstract void rotate();
    public abstract double getHeight();
    public abstract double getWidth();
    public void flipVertically(){
        move(0, getHeight());
    }
    public void flipHorizontally(){
        move(getWidth(), 0);
    }






}


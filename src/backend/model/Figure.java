package backend.model;

public abstract class Figure {

    public abstract boolean belongs(Point eventPoint);
    public abstract boolean belongsInRectangle(Rectangle imaginaryRectangle);
    public abstract void move(double diffX, double diffY);
    public abstract void rotate();
    public abstract double getHeight();
    public abstract double getWidth();
    protected abstract void setWidth(double newWidth);
    protected abstract void setHeight(double newHeight);
    public void flipVertically(){
        move(0, getHeight());
    }
    public void flipHorizontally(){
        move(getWidth(), 0);
    }

    public void scale(){
        setWidth(getWidth() * 1.25);
        setHeight(getHeight() * 1.25);
    }
    public void deScale(){
        setWidth(getWidth() * 0.75);
        setHeight(getHeight() * 0.75);
    }






}


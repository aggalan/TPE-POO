package backend;

public abstract class Figure {
    private static final double RATIO = 0.25;

    public abstract boolean belongs(Point eventPoint);
    public abstract boolean belongsInRectangle(Rectangle imaginaryRectangle);
    public abstract void move(double diffX, double diffY);
    public abstract void rotate();
    public abstract double getHeight();
    public abstract double getWidth();
    protected abstract void setWidth(double newWidth);
    protected abstract void setHeight(double newHeight);
    public abstract void changeSize(double ratio);
    public void scale(){
        changeSize(1+RATIO);
    }
    public void descale(){
        changeSize(1-RATIO);
    }
    public void flipVertically(){
        move(0, getHeight());
    }
    public void flipHorizontally(){
        move(getWidth(), 0);
    }







}


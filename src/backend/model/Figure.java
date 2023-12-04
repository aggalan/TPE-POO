package backend.model;

public abstract class Figure {
    
    public abstract boolean belongs(Point eventPoint);
    
    public abstract void move(double moveX, double moveY);
    
}


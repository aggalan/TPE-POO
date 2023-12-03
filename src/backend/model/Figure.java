package backend.model;

public abstract class Figure {
    public abstract boolean belongs(Point point);
    public abstract void move(double moveX, double moveY);
}


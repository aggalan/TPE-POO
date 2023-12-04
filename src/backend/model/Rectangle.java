package backend.model;

public class Rectangle extends Figure {

    private final Point topLeft, bottomRight;

    public Rectangle(Point topLeft, Point bottomRight) {
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
    }

    public Point getTopLeft() {
        return topLeft;
    }

    public double getTopLeftX() {
        return getTopLeft().getX();
    }

    public double getTopLeftY() {
        return getTopLeft().getY();
    }

    public double getBottomRightX() {
        return getBottomRight().getX();
    }

    public double getBottomRightY() {
        return getBottomRight().getY();
    }

    public Point getBottomRight() {
        return bottomRight;
    }

    @Override
    public String toString() {
        return String.format("Rectángulo [ %s , %s ]", topLeft, bottomRight);
    }

    @Override
    public void move(double diffX, double diffY){
        getTopLeft().x += diffX;
		getBottomRight().x += diffX;
		getTopLeft().y += diffY;
		getBottomRight().y += diffY;
    }

    @Override
    public boolean belongs(Point eventPoint){
        return eventPoint.getX() > getTopLeft().getX() && eventPoint.getX() < getBottomRight().getX() && eventPoint.getY() > getTopLeft().getY() && eventPoint.getY() < getBottomRight().getY();
    }
// ver si se puede modularizar


    @Override
    public boolean belongsInRectangle(Rectangle imaginaryRectangle) {
        return imaginaryRectangle.getTopLeft().getX() <= getTopLeft().getX() && imaginaryRectangle.getBottomRight().getX() >= getBottomRight().getX()
                && imaginaryRectangle.getTopLeft().getY() <= getTopLeft().getY() && imaginaryRectangle.getBottomRight().getY() >= getBottomRight().getY();
    }


}

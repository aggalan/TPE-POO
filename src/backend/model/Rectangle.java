package backend.model;

public class Rectangle extends Figure {

    private Point  topLeft;
    private Point bottomRight;
    private double height, width;



    public Rectangle(Point topLeft, Point bottomRight) {

        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
        this.width = Math.abs(topLeft.getX() - bottomRight.getX());
        this.height = Math.abs(topLeft.getY() - bottomRight.getY());

    }

    @Override
    protected void setHeight(double height) {
        this.height = height;
    }

    @Override
    protected void setWidth(double width) {
        this.width = width;
    }

    private Point getCenterPoint(){
        return new Point((bottomRight.getX() + topLeft.getX())/2, (topLeft.getY() + bottomRight.getY())/2);
    }

    public void rotate() {
        Point centerPoint = getCenterPoint();
        topLeft = new Point(centerPoint.getX() - height/2, centerPoint.getY() - width/2 );
        bottomRight = new Point(centerPoint.getX() + height/2, centerPoint.getY() + width/2);
        double temp = width;
        width = getHeight();
        height = temp;
    }

    public void changeSize(double ratio){
        Point centerPoint = getCenterPoint();
        setWidth(width * ratio);
        setHeight(height * ratio);
        topLeft = new Point(centerPoint.getX() - width/2, centerPoint.getY() - height/2);
        bottomRight = new Point(centerPoint.getX() + width/2, centerPoint.getY() + height/2);
    }



    public double getHeight() {
        return height;
    }

    public double getWidth() {
        return width;
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
    public void move(double diffX, double diffY) {
        topLeft.movePoint(diffX, diffY);
        bottomRight.movePoint(diffX, diffY);


    }

    @Override
    public boolean belongs(Point eventPoint){
        return eventPoint.getX() > getTopLeft().getX() && eventPoint.getX() < getBottomRight().getX() && eventPoint.getY() > getTopLeft().getY() && eventPoint.getY() < getBottomRight().getY();
    }


    @Override
    public boolean belongsInRectangle(Rectangle imaginaryRectangle) {
        return imaginaryRectangle.getTopLeft().getX() <= getTopLeft().getX() && imaginaryRectangle.getBottomRight().getX() >= getBottomRight().getX()
                && imaginaryRectangle.getTopLeft().getY() <= getTopLeft().getY() && imaginaryRectangle.getBottomRight().getY() >= getBottomRight().getY();
    }


}

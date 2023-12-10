package backend;

public class Ellipse extends Figure {

    protected Point centerPoint;
    protected  double sMayorAxis, sMinorAxis;

    public Ellipse(Point centerPoint, double sMayorAxis, double sMinorAxis) {
        this.centerPoint = centerPoint;
        this.sMayorAxis = sMayorAxis;
        this.sMinorAxis = sMinorAxis;
    }

    protected void setWidth(double width) {
        this.sMayorAxis = width;
    }

    protected void setHeight(double height) {
        this.sMinorAxis = height;
    }

    public Point getCenterPoint() {
        return centerPoint;
    }

    @Override
    public void rotate() {
        double tempAxis = sMayorAxis;
        sMayorAxis = sMinorAxis;
        sMinorAxis = tempAxis;
    }

    @Override
    public void changeSize(double ratio) {
        setWidth(sMayorAxis * ratio);
        setHeight(sMinorAxis * ratio);
    }


    @Override
    public String toString() {
        return String.format("Elipse [Centro: %s, DMayor: %.2f, DMenor: %.2f]", centerPoint, sMayorAxis, sMinorAxis);
    }

    @Override
    public double getWidth() {
        return sMayorAxis;
    }
    @Override
    public double getHeight() {
        return sMinorAxis;
    }



    @Override
    public void move(double diffX, double diffY){
        centerPoint.movePoint(diffX, diffY);
    }


    @Override
    public boolean belongsInRectangle(Rectangle imaginaryRectangle) {
        return centerPoint.getX() + (getWidth()/2) <= imaginaryRectangle.getBottomRightX() && centerPoint.getX() - (getWidth()/2) >= imaginaryRectangle.getTopLeftX() && centerPoint.getY() + (getHeight()/2) <= imaginaryRectangle.getBottomRightY() && centerPoint.getY() - (getHeight()/2) >= imaginaryRectangle.getTopLeftY();
    }

    @Override
    public boolean belongs(Point eventPoint){
        return ((Math.pow(eventPoint.getX() - getCenterPoint().getX(), 2) / Math.pow(getWidth(), 2)) + (Math.pow(eventPoint.getY() - getCenterPoint().getY(), 2) / Math.pow(getHeight(), 2))) <= 0.30;
    }



}


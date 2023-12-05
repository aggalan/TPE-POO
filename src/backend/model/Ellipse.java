package backend.model;

public class Ellipse extends Figure {

    protected final Point centerPoint;
    protected final double sMayorAxis, sMinorAxis;

    public Ellipse(Point centerPoint, double sMayorAxis, double sMinorAxis) {
        this.centerPoint = centerPoint;
        this.sMayorAxis = sMayorAxis;
        this.sMinorAxis = sMinorAxis;
    }

    @Override
    public String toString() {
        return String.format("Elipse [Centro: %s, DMayor: %.2f, DMenor: %.2f]", centerPoint, sMayorAxis, sMinorAxis);
    }

    public Point getCenterPoint() {
        return centerPoint;
    }

    public double getsMayorAxis() {
        return sMayorAxis;
    }

    public double getsMinorAxis() {
        return sMinorAxis;
    }

    public double getCenterPointX() {
        return getCenterPoint().getX();
    }

    public double getCenterPointY() {
        return getCenterPoint().getY();
    }

    @Override
    public void move(double diffX, double diffY){
        getCenterPoint().x += diffX;
        getCenterPoint().y += diffY;
    }


    @Override
    public boolean belongsInRectangle(Rectangle imaginaryRectangle) {
        return getCenterPointX() + (getsMayorAxis()/2) <= imaginaryRectangle.getBottomRightX() && getCenterPointX() - (getsMayorAxis()/2) >= imaginaryRectangle.getTopLeftX() && getCenterPointY() + (getsMinorAxis()/2) <= imaginaryRectangle.getBottomRightY() && getCenterPointY() - (getsMinorAxis()/2) >= imaginaryRectangle.getTopLeftY();
    }

    @Override
    public boolean belongs(Point eventPoint){
        return ((Math.pow(eventPoint.getX() - getCenterPoint().getX(), 2) / Math.pow(getsMayorAxis(), 2)) + (Math.pow(eventPoint.getY() - getCenterPoint().getY(), 2) / Math.pow(getsMinorAxis(), 2))) <= 0.30;
    }



}


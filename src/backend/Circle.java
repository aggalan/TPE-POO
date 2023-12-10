package backend;

public class Circle extends Ellipse {
    public Circle(Point centerPoint, double radius) {
        super(centerPoint, 2*radius, 2*radius);
    }
    @Override
    public String toString() {
        return String.format("Círculo [Centro: %s, Radio: %.2f]", centerPoint, getRadius());
    }


    public double getRadius() {
        return getWidth()/2;
    }

    public boolean belongs(Point eventPoint){
        return Math.sqrt(Math.pow(getCenterPoint().getX() - eventPoint.getX(), 2) + Math.pow(getCenterPoint().getY() - eventPoint.getY(), 2)) < getRadius();
    }

}

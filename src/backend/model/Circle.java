package backend.model;

public class Circle extends Ellipse {
    public Circle(Point centerPoint, double radius) {
        super(centerPoint, radius, radius);
    }
    @Override
    public String toString() {
        return String.format("Círculo [Centro: %s, Radio: %.2f]", centerPoint, super.getMayorAxis());
    }

    public Point getCenterPoint() {
        return super.getCenterPoint();
    }

    public double getRadius() {
        return super.getMayorAxis();
    }

}

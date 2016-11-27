package org.graviton.maths;

/**
 * Created by Botan on 19/11/2016 : 11:03
 */
public class Vector {
    public static final Vector NIL = new Vector(0, 0);
    private int x, y;

    private Vector(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Vector fromPoints(Point a, Point b) {
        return create(b.getAbscissa() - a.getAbscissa(), b.getOrdinate() - a.getOrdinate());
    }

    public static Vector create(int x, int y) {
        if (x == 0 && y == 0) return NIL;
        return new Vector(x, y);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Point apply(Point point) {
        return new Point(
                point.getAbscissa() + x,
                point.getOrdinate() + y
        );
    }

    public boolean isCollinearWith(Vector that) {
        if (that == null) throw new NullPointerException("that");
        return this == NIL || that == NIL || (this.x * that.y) == (this.y * that.x);
    }

    public Double getCoefficient(Vector that) {
        if (this == NIL || that == NIL) return 0.0;
        if (!isCollinearWith(that)) return null;

        if (this.x != 0 && that.x != 0) {
            return (double) that.x / (double) this.x;
        }
        if (this.y != 0 && that.y != 0) {
            return (double) that.y / (double) this.y;
        }

        // (0,1) et (0,-1)

        throw new RuntimeException(); // must not happen
    }

    public boolean hasSameDirectionOf(Vector that) {
        Double coef = this.getCoefficient(that);
        return coef != null && coef > 0.0;
    }

    @Override
    public boolean equals(Object that) {
        return this.getClass() == that.getClass() && equals((Vector) that);

    }

    public boolean equals(Vector that) {
        return that != null && (that == this || this.x == that.x && this.y == that.y);
    }

    @Override
    public int hashCode() {
        return x + y;
    }

    @Override
    public String toString() {
        return String.format("(%d;%d)", x, y);
    }
}

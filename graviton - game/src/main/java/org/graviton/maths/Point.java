package org.graviton.maths;

import lombok.Data;

/**
 * Created by Botan on 19/11/2016 : 11:02
 */
@Data
public class Point {
    private int abscissa;
    private int ordinate;

    public Point(int abscissa, int ordinate) {
        this.abscissa = abscissa;
        this.ordinate = ordinate;
    }

    @Override
    public int hashCode() {
        return abscissa + ordinate;
    }

    @Override
    public boolean equals(Object that) {
        return this == that ||
                that != null &&
                        this.getClass() == that.getClass() &&
                        this.equals((Point) that);
    }

    public boolean equals(Point that) {
        return that == this ||
                that != null &&
                        this.abscissa == that.abscissa && this.ordinate == that.ordinate;
    }

    @Override
    public String toString() {
        return String.format("(%d;%d)", abscissa, ordinate);
    }
}

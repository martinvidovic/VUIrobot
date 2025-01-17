package cz.mendelu.vui2.agents;

import java.util.Objects;

public class Coor {
    public final int x, y;
    public Coor(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coor coor = (Coor) o;
        return x == coor.x &&
                y == coor.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + "] ";
    }
}

package sample;

import java.util.Objects;

public class Vector2d {
    public final int x;
    public final int y;

    public Vector2d(int x, int y) {
        this.x = x;
        this.y = y;
    }


    public String toString() {
        return "(" + this.x + "," + this.y + ")";
    }


    public boolean follows(Vector2d other) {
        if (other.x <= this.x && other.y <= this.y) return true;
        return false;
    }


    public Vector2d add(Vector2d other) {
        return new Vector2d(other.x + this.x, other.y + this.y);
    }



    public boolean equals(Object other){
        if (this == other)
            return true;
        if (!(other instanceof Vector2d))
            return false;
        Vector2d that = (Vector2d) other;

        if(that.x != x) return false;

        if(that.y != y) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.y);
    }


}

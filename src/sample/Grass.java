package sample;

public class Grass implements IMapElement {
    private final Vector2d grassPosition;

    public Grass(Vector2d grassPosition)
    {
        this.grassPosition = grassPosition;
    }

    public Vector2d getPosition()
    {
        return grassPosition;
    }

}
package interfaces;

import classes.Animal;
import classes.Vector2d;

public interface IPositionChangeObserver {
    void positionChanged(Vector2d oldPosition, Vector2d newPosition, Animal animal);    // czy nowa pozycja jest potrzebna, skoro mamy całe zwierzę?


}

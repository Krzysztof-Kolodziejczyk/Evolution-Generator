package classes;


import enumClasses.AnimalDirection;
import interfaces.IMapElement;
import interfaces.IPositionChangeObserver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Animal implements IMapElement {
    private AnimalDirection orientation = AnimalDirection.NORTH;
    private final WorldMap worldMap;
    private Vector2d position;
    public int[] animalGene;
    public int energy;
    public int startEnergy;
    public int survivedDaysNumber;
    public int childNumber;
    private final ArrayList<IPositionChangeObserver> positionChangeObservers;


    // constructor to create firsts animals
    public Animal(WorldMap worldMap, Vector2d position, int energy) {
        this.worldMap = worldMap;
        this.position = position;
        animalGene = new int[32];
        this.energy = energy;
        this.startEnergy = energy;
        this.survivedDaysNumber = 1;
        this.childNumber = 0;
        positionChangeObservers = new ArrayList<>();
        Random rand = new Random();
        for(int i=0; i<32; i++)
        {
            animalGene[i] = rand.nextInt(32) % 8;
        }
        Arrays.sort(animalGene);
    }


    // constructor to create animal`s child
    public Animal(Vector2d position, int energy, int[] animalGene, WorldMap worldMap, int startEnergy)
    {
        this.position = position;
        this.energy = energy;
        this.animalGene = animalGene;
        this.worldMap = worldMap;
        this.startEnergy = startEnergy;
        this.positionChangeObservers = new ArrayList<>();

        Random rand = new Random();
        int direction = rand.nextInt(8);
        switch (direction) {
            case 1 -> orientation = AnimalDirection.NORTH_EAST;
            case 2 -> orientation = AnimalDirection.EAST;
            case 3 -> orientation = AnimalDirection.SOUTH_EAST;
            case 4 -> orientation = AnimalDirection.SOUTH;
            case 5 -> orientation = AnimalDirection.SOUTH_WEST;
            case 6 -> orientation = AnimalDirection.WEST;
            case 7 -> orientation = AnimalDirection.NORTH_WEST;
        }
    }


    public void move() {
        Random rand = new Random();
        int bound = animalGene[rand.nextInt(8)];
        for (int i = 0; i < bound; i++) {
            orientation = orientation.next(orientation);
        }


        Vector2d oldPosition;
        switch (orientation) {
            case NORTH -> {
                oldPosition = position;
                if (position.y - 1 < 0) {
                    position = new Vector2d(position.x, worldMap.height - 1);
                } else {
                    position = new Vector2d(position.x, (position.y - 1) % worldMap.height);
                }
                notifyObservers(oldPosition, position);
            }
            case NORTH_EAST -> {
                oldPosition = position;
                if (position.y - 1 < 0) {
                    position = new Vector2d((position.x + 1) % worldMap.width, worldMap.height - 1);
                } else {
                    position = new Vector2d((position.x + 1) % worldMap.width, (position.y - 1) % worldMap.height);
                }
                notifyObservers(oldPosition, position);
            }
            case EAST -> {
                oldPosition = position;
                position = new Vector2d((position.x + 1) % worldMap.width, position.y);
                notifyObservers(oldPosition, position);
            }
            case SOUTH_EAST -> {
                oldPosition = position;
                position = new Vector2d((position.x + 1) % worldMap.width, (position.y + 1) % worldMap.height);
                notifyObservers(oldPosition, position);
            }
            case SOUTH -> {
                oldPosition = position;
                position = new Vector2d(position.x, (position.y + 1) % worldMap.height);
                notifyObservers(oldPosition, position);
            }
            case SOUTH_WEST -> {
                oldPosition = position;
                if (position.x - 1 < 0) {
                    position = new Vector2d(worldMap.width - 1, (position.y + 1) % worldMap.height);
                } else {
                    position = new Vector2d((position.x - 1) % worldMap.width, (position.y + 1) % worldMap.height);
                }
                notifyObservers(oldPosition, position);
            }
            case WEST -> {
                oldPosition = position;
                if (position.x - 1 < 0) {
                    position = new Vector2d(worldMap.width - 1, position.y);
                } else {
                    position = new Vector2d((position.x - 1) % worldMap.width, position.y);
                }
                notifyObservers(oldPosition, position);
            }
            case NORTH_WEST -> {
                oldPosition = position;
                if (position.x - 1 < 0 && position.y - 1 >= 0) {
                    position = new Vector2d(worldMap.width - 1, (position.y - 1) % worldMap.height);
                } else if (position.x - 1 >= 0 && (position.y - 1) < 0) {
                    position = new Vector2d((position.x - 1) % worldMap.width, worldMap.height - 1);
                } else if (position.x - 1 < 0) {
                    position = new Vector2d(worldMap.width - 1, worldMap.height - 1);
                } else {
                    position = new Vector2d((position.x - 1) % worldMap.width, (position.y - 1) % worldMap.height);
                }
                notifyObservers(oldPosition, position);
            }
        }
    }

    public void addObserver(IPositionChangeObserver observer)
    {
        positionChangeObservers.add(observer);
    }

    private void notifyObservers(Vector2d oldPosition, Vector2d newPosition)
    {
        for(IPositionChangeObserver positionChangeObserver: positionChangeObservers)
        {
            positionChangeObserver.positionChanged(oldPosition, newPosition, this);
        }
    }

    public Vector2d getPosition() {
        return position;
    }

    public int animalColor()
    {
        int color = 10*energy/startEnergy;
        return Math.min(color, 9);
    }

}
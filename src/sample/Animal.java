package sample;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Animal implements IMapElement {
    private AnimalDirection orientation = AnimalDirection.NORTH;
    private final WorldMap worldMap;
    private Vector2d position;
    private int[] animalGene;
    public int energy;

    public Animal(WorldMap worldMap, Vector2d position, int energy) {
        this.worldMap = worldMap;
        this.position = position;
        animalGene = new int[32];
        this.energy = energy;
        Random rand = new Random();
        for(int i=0; i<32; i++)
        {
            animalGene[i] = rand.nextInt(32);
        }
        Arrays.sort(animalGene);
    }


    public void move()
    {
        Random rand = new Random();
        int bound = animalGene[rand.nextInt(8)];
        for(int i=0; i<bound; i++)
        {
            orientation = orientation.next(orientation);
        }


        switch (orientation)
        {
            case NORTH:
                if(position.y -1 < 0)
                {
                    position = new Vector2d(position.x, worldMap.height - 1);
                }
                else
                {
                    position = new Vector2d(position.x, (position.y-1)% worldMap.height);
                }
                break;
            case NORTH_EAST:
                if(position.y - 1 < 0)
                {
                    position = new Vector2d((position.x+1)% worldMap.width, worldMap.height - 1);
                }
                else {
                    position = new Vector2d((position.x+1)% worldMap.width, (position.y-1)% worldMap.height);
                }
                break;
            case EAST:
                position = new Vector2d((position.x+1)% worldMap.width, position.y);
                break;
            case SOUTH_EAST:
                position = new Vector2d((position.x+1)% worldMap.width, (position.y+1)% worldMap.height);
                break;
            case SOUTH:
                position = new Vector2d(position.x, (position.y+1)% worldMap.height);
                break;
            case SOUTH_WEST:
                if(position.x -1 < 0)
                {
                    position = new Vector2d(worldMap.width-1, (position.y+1)% worldMap.height);
                }
                else
                {
                    position = new Vector2d((position.x-1)% worldMap.width, (position.y+1)% worldMap.height);
                }
                break;
            case WEST:
                if(position.x -1 < 0)
                {
                    position = new Vector2d(worldMap.width-1, position.y);
                }
                else
                {
                    position = new Vector2d((position.x-1)% worldMap.width, position.y);
                }
                break;
            case NORTH_WEST:
                if(position.x - 1 < 0 && position.y -1 >= 0)
                {
                    position = new Vector2d(worldMap.width - 1, (position.y-1)% worldMap.height);
                }
                else if(position.x - 1 >= 0 && (position.y -1) < 0)
                {
                    position = new Vector2d((position.x-1)% worldMap.width, worldMap.height - 1);
                }
                else if(position.x - 1 < 0)
                {
                    position = new Vector2d(worldMap.width-1, worldMap.height-1);
                }
                else
                {
                    position = new Vector2d((position.x-1)% worldMap.width, (position.y-1)% worldMap.height);
                }
                break;
        }
    }

    public Vector2d getPosition() {
        return position;
    }



}
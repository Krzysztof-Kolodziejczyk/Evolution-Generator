package sample;

import java.util.Random;

public class SimulationEngine {
    public final WorldMap worldMap;

    public SimulationEngine(int width, int height, int startEnergy, int plantEnergy, double jungleRatio, int startAnimalNumber, int dayEnergyCost) {

        worldMap = new WorldMap(width, height, dayEnergyCost,plantEnergy, jungleRatio);
        placeAnimals(startAnimalNumber,width,height,startEnergy);
    }

    private void placeAnimals(int startAnimalNumber, int width, int height, int startEnergy) {
        Random rand = new Random();
        for (int i = 0; i < startAnimalNumber; i++) {
            Animal animal = new Animal(worldMap, new Vector2d(rand.nextInt(width), rand.nextInt(height)), startEnergy);
            worldMap.place(animal);

        }
    }

    public void day()
    {
        worldMap.dayCycle();
    }

}

package sample;

import java.util.*;

public class WorldMap {

    public final int height, width;
    public HashMap<Vector2d, ArrayList<Animal>> animalsOnMap;
    public HashMap<Vector2d, Grass> grassesOnMap;
    public ArrayList<Vector2d> eatenGrasses;
    public ArrayList<Vector2d> addedGrasses;
    private int dayEnergyCost;
    private int plantEnergy;
    private Vector2d jungleLowerLeft;
    private int jungleHeight, jungleWidth;
    public int animalsNumber;

    public WorldMap(int width, int height, int dayEnergyCost, int plantEnergy, double jungleRatio) {
        this.height = height;
        this.width = width;
        this.dayEnergyCost = dayEnergyCost;
        this.plantEnergy = plantEnergy;
        setJunglePosition(jungleRatio);

        animalsOnMap = new HashMap<>();
        grassesOnMap = new HashMap<>();
        eatenGrasses = new ArrayList<>();
        addedGrasses = new ArrayList<>();
        animalsNumber = 0;

    }

    protected void addElementToAnimalMap(Vector2d position, Animal mapElement) {
        if (animalsOnMap.get(position) == null) {
            animalsOnMap.put(position, new ArrayList<>());
            animalsOnMap.get(position).add(mapElement);
        } else {
            animalsOnMap.get(position).add(mapElement);
        }
    }


    public void place(Animal animal) {
        addElementToAnimalMap(animal.getPosition(), animal);
        animalsNumber += 1;
    }


    public void positionChanged(Vector2d oldPosition, Vector2d newPosition, Animal animal) {
        ArrayList<Animal> objects = animalsOnMap.get(oldPosition);
        if (objects.size() == 1) {
            addElementToAnimalMap(newPosition, animal);
            animalsOnMap.remove(oldPosition);
        } else if (objects.size() > 1) {
            for (Animal element : objects) {
                if (element.getClass() == Animal.class && element.equals(animal)) {
                    objects.remove(element);
                    addElementToAnimalMap(newPosition, element);
                    return;
                }
            }
        }
    }

    public void dayCycle() {
        addedGrasses = new ArrayList<>();
        placeGrasses();
        ArrayList<Vector2d> objectsPositions = new ArrayList<>();

        Iterator iterator = animalsOnMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry mapElement = (Map.Entry) iterator.next();
            objectsPositions.add((Vector2d) mapElement.getKey());
        }

        //przejście po wszystkich zwierzętach i weryfikacja zdarzeń (jedzenie, rozmnażanie, śmierć)
        eatenGrasses = new ArrayList<>();
        for (Vector2d position : objectsPositions) {
            eat(position);
            death(position);
            multiplication(position);
        }

        // ruch zwierząt
        iterator = animalsOnMap.entrySet().iterator();
        objectsPositions = new ArrayList<>();
        while (iterator.hasNext()) {
            Map.Entry mapElement = (Map.Entry) iterator.next();
            objectsPositions.add((Vector2d) mapElement.getKey());
        }


        for (Vector2d position : objectsPositions)
        {
            ArrayList<Animal> copiedAnimalList = new ArrayList<>(animalsOnMap.get(position));
            for(Animal animal: copiedAnimalList)
            {
                Vector2d oldPosition = animal.getPosition();
                animal.move();
                Vector2d newPosition = animal.getPosition();
                positionChanged(oldPosition,newPosition,animal);
                animal.energy -= dayEnergyCost;
            }

        }

    }

    private Animal getMaximumEnergyAnimal(Vector2d position) {
        ArrayList<Animal> animals = animalsOnMap.get(position);
        int max = -1;
        Animal resultAnimal = null;
        if (animals.size() == 0) return null;
        for (Animal animal : animals) {
            if (animal.energy > max) {
                resultAnimal = animal;
                max = resultAnimal.energy;
            }
        }
        return resultAnimal;
    }

    private Animal getSecondMaxEnergyAnimal(Vector2d position, Animal maxEnergyAnimal)
    {
        ArrayList<Animal> animals = animalsOnMap.get(position);
        int max = -1;
        Animal resultAnimal = null;
        if (animals.size() == 0) return null;
        for (Animal animal : animals) {
            if (animal.energy > max && !animal.equals(maxEnergyAnimal)) {
                resultAnimal = animal;
                max = resultAnimal.energy;
            }
        }
        return resultAnimal;
    }

    // na jedym polu może być maksymalnie jedna trawa i int zwierząt
    private void eat(Vector2d position) {
        Animal strongestAnimal = getMaximumEnergyAnimal(position);
        Grass grass = grassesOnMap.get(position);

        if(strongestAnimal != null && grass != null)
        {
            strongestAnimal.energy += plantEnergy;
            grassesOnMap.remove(position);
            eatenGrasses.add(position);
        }
    }

    private void death(Vector2d position) {
        ArrayList<Animal> animals = animalsOnMap.get(position);
        ArrayList<Animal> deadAnimals = new ArrayList<>();

        for(Animal animal: animals)
        {
            if(animal.energy < 0)
            {
                deadAnimals.add(animal);
                animalsNumber -= 1;
            }
        }

        for(Animal deadAnimal: deadAnimals)
        {
            animals.remove(deadAnimal);
        }
        if(animals.size() == 0)
        {
            animalsOnMap.remove(position);
        }
    }

    private void multiplication(Vector2d position)
    {
        if(animalsOnMap.get(position) != null && animalsOnMap.get(position).size() > 1)
        {
            Animal maxEnergyAnimal = getMaximumEnergyAnimal(position);
            Animal secondMaxEnergyAnimal = getSecondMaxEnergyAnimal(position,maxEnergyAnimal);

            // utrata energi
            int lostEnergy1 = (int) (0.25 * maxEnergyAnimal.energy);
            int lostEnergy2 = (int)( 0.25 * secondMaxEnergyAnimal.energy);
            maxEnergyAnimal.energy -= lostEnergy1;
            secondMaxEnergyAnimal.energy -= lostEnergy2;

            // znalezienie miejsca dla potomka
            Vector2d parentPosition = maxEnergyAnimal.getPosition();
            Vector2d childPosition = findPlaceForChild(parentPosition);

            if(childPosition != null)
            {
                Animal animal = new Animal(childPosition, lostEnergy1 + lostEnergy2, resultGenoTypeMaker(maxEnergyAnimal, secondMaxEnergyAnimal), this);
                place(animal);
            }
        }

    }

    private void setJunglePosition(double jungleRatio) {

        jungleHeight = (int) (jungleRatio * height);
        jungleWidth = (int) (jungleRatio * width);

        jungleLowerLeft = new Vector2d(width / 2 - jungleWidth / 2, height / 2 - jungleHeight / 2);
    }

    private void placeGrasses() {
        Random rand = new Random();
        Vector2d grassPosition;

        // placing grass in jungle
        for (int i = 0; i < 5; i++) {
            grassPosition = new Vector2d(rand.nextInt(jungleWidth) + jungleLowerLeft.x, rand.nextInt(jungleHeight) + jungleLowerLeft.y);

            if(grassesOnMap.get(grassPosition) == null)
            {
                grassesOnMap.put(grassPosition, new Grass(grassPosition));
                addedGrasses.add(grassPosition);
                break;
            }
        }

        // placing grass on savanna
        for (int i = 0; i < 10; i++) {
            grassPosition = new Vector2d(rand.nextInt(width), rand.nextInt(height));
            if (grassPosition.x >= jungleLowerLeft.x && grassPosition.x < jungleLowerLeft.x + jungleWidth && grassPosition.y >= jungleLowerLeft.y && grassPosition.y < jungleLowerLeft.y + jungleHeight) {
                continue;
            }
            if (grassesOnMap.get(grassPosition) == null) {
                grassesOnMap.put(grassPosition, new Grass(grassPosition));
                addedGrasses.add(grassPosition);
                break;
            }
        }

    }

    private Vector2d findPlaceForChild(Vector2d parentPosition)
    {
        Animal animal;
        for(int i=0; i<8; i++)
        {
            animal = new Animal(parentPosition,i,this);
            animal.move();
            if(grassesOnMap.get(animal.getPosition()) == null && animalsOnMap.get(animal.getPosition()) == null)
            {
                return animal.getPosition();
            }
        }
        // brak miejsca
        return null;
    }

    private int[] genotypeSeparator(int [] genes)
    {
        Random rand = new Random();
        int position;
        int currentGroup = 0;
        int resultArray[] = new int[32];
        for(int i=0; i<32; i++)
        {
            resultArray[i] = -1;
        }

        for(int i=0; i<32; i++)
        {
            position = rand.nextInt(32);
            while(resultArray[position] != -1)
            {
                position += 1;
                position %= 32;
            }
            resultArray[position] = currentGroup;
            currentGroup += 1;
            currentGroup %= 3;
        }
        return resultArray;
    }

    private int[] genotypeMaker(int[] genesPosA, int[] genesPosB, int[] genesA, int[] genesB)
    {
        Random rand = new Random();
        int [] newGenotype = new int[32];
        int currentPosition = 0;

        for(int i=0; i<32; i++)
        {
            if(genesPosB[i] != 2)
            {
                newGenotype[currentPosition] = genesA[i];
                currentPosition += 1;
            }
        }

        for(int i=0; i<32; i++)
        {
            if(genesPosB[i] == 2)
            {
                newGenotype[currentPosition] = genesB[i];
                currentPosition += 1;
            }
        }

        Arrays.sort(newGenotype);
        ArrayList<Integer> missingGenes;
        do {
            boolean [] genesInArray = new boolean[8];
            for(int i=0; i<32; i++)
            {
                genesInArray[newGenotype[i]] = true;
            }
            missingGenes = new ArrayList<>();
            for(int i=0; i<7; i++)
            {
                if(!genesInArray[i])
                {
                    missingGenes.add(i);
                }
            }
            for(Integer missingGene: missingGenes)
            {
                newGenotype[rand.nextInt(32)] = missingGene;
            }
        }while (missingGenes.size() > 0);
        Arrays.sort(newGenotype);
        return newGenotype;
    }

    private int[] resultGenoTypeMaker(Animal animal1, Animal animal2)
    {
        return genotypeMaker(genotypeSeparator(animal1.animalGene), genotypeSeparator(animal2.animalGene), animal1.animalGene, animal2.animalGene);
    }
}

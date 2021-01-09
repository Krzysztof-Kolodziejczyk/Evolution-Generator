package classes;

import interfaces.IPositionChangeObserver;

import java.util.*;

public class WorldMap implements IPositionChangeObserver {

    public final int height, width;
    public HashMap<Vector2d, ArrayList<Animal>> animalsOnMap;   // public?
    public ArrayList<Vector2d> animalsPositions;
    public HashMap<Vector2d, Grass> grassesOnMap;
    public ArrayList<Vector2d> eatenGrasses;
    public ArrayList<Vector2d> addedGrasses;
    private final int dayEnergyCost;
    private final int plantEnergy;
    private Vector2d jungleLowerLeft;
    private int jungleHeight, jungleWidth;
    public int animalsNumber;
    public int averageSurvivedDaysNumber;
    private int deadAnimalsNumber;
    private int survivedDaysByDeadAnimals;
    private final int startEnergy;

    public WorldMap(int width, int height, int dayEnergyCost, int plantEnergy, int jungleRatio, int startEnergy) {
        this.height = height;
        this.width = width;
        this.dayEnergyCost = dayEnergyCost;
        this.plantEnergy = plantEnergy;
        this.averageSurvivedDaysNumber = 0;
        this.deadAnimalsNumber = 0;
        survivedDaysByDeadAnimals = 0;
        this.animalsPositions = new ArrayList<>();
        this.startEnergy = startEnergy;
        setJunglePosition(jungleRatio);

        animalsOnMap = new HashMap<>();
        grassesOnMap = new HashMap<>();
        eatenGrasses = new ArrayList<>();
        addedGrasses = new ArrayList<>();
        animalsNumber = 0;

    }

    private void addElementToAnimalMap(Vector2d position, Animal animal) {
        animalsOnMap.computeIfAbsent(position, k -> new ArrayList<>());
        animalsOnMap.get(position).add(animal);
    }


    public void place(Animal animal) {
        if (animalsOnMap.get(animal.getPosition()) == null) {
            animalsPositions.add(animal.getPosition());
        }
        addElementToAnimalMap(animal.getPosition(), animal);
        animalsNumber += 1;
        animal.addObserver(this);
    }


    public void positionChanged(Vector2d oldPosition, Vector2d newPosition, Animal animal) {
        ArrayList<Animal> objects = animalsOnMap.get(oldPosition);
        if (objects.size() == 1) {
            if (animalsOnMap.get(newPosition) == null || animalsOnMap.get(newPosition).size() == 0) {
                animalsPositions.add(newPosition);
            }
            addElementToAnimalMap(newPosition, animal);
            animalsOnMap.remove(oldPosition);
            if (animalsOnMap.get(oldPosition) == null) {
                animalsPositions.remove(oldPosition);
            }

        } else if (objects.size() > 1) {
            for (Animal element : objects) {
                if (element.equals(animal)) {
                    objects.remove(element);
                    if (animalsOnMap.get(newPosition) == null) {
                        animalsPositions.add(newPosition);
                    }
                    addElementToAnimalMap(newPosition, element);
                    return;
                }
            }
        }
    }

    public void dayCycle() {    // czy to jest odpowiedzialność mapy?
        addedGrasses = new ArrayList<>();
        placeGrasses();

        //przejście po wszystkich zwierzętach i weryfikacja zdarzeń (jedzenie, rozmnażanie, śmierć)
        eatenGrasses = new ArrayList<>();
        ArrayList<Vector2d> newAnimalsPositions = new ArrayList<>(animalsPositions);    // czemu new?

        for (Vector2d position : newAnimalsPositions) {
            eat(position);
            death(position);
            copulation(position);
        }

        // ruch zwierząt
        newAnimalsPositions = new ArrayList<>(animalsPositions);
        for (Vector2d position : newAnimalsPositions) {
            ArrayList<Animal> animalListInHashMap = new ArrayList<>(animalsOnMap.get(position));
            for (Animal animal : animalListInHashMap) {
                animal.move();
                animal.energy -= dayEnergyCost;
                animal.survivedDaysNumber += 1;
            }
        }

    }

    public Animal getMaximumEnergyAnimal(Vector2d position) {
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

    private Animal getSecondMaxEnergyAnimal(Vector2d position, Animal maxEnergyAnimal) {
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
        Animal strongestAnimal = getMaximumEnergyAnimal(position);  // a jeśli kilka zwierząt ma równą energię?
        Grass grass = grassesOnMap.get(position);

        if (strongestAnimal != null && grass != null) {
            strongestAnimal.energy += plantEnergy;
            grassesOnMap.remove(position);
            eatenGrasses.add(position);
        }
    }

    private void death(Vector2d position) {
        ArrayList<Animal> animals = animalsOnMap.get(position);
        ArrayList<Animal> deadAnimals = new ArrayList<>();

        for (Animal animal : animals) {
            if (animal.energy < 0) {
                deadAnimals.add(animal);
                animalsNumber -= 1;
                survivedDaysByDeadAnimals += animal.survivedDaysNumber;
                deadAnimalsNumber += 1;
            }
        }

        for (Animal deadAnimal : deadAnimals) {
            animals.remove(deadAnimal);
        }
        if (animals.size() == 0) {
            animalsPositions.remove(position);
            animalsOnMap.remove(position);
        }
        if (deadAnimalsNumber > 0) {
            averageSurvivedDaysNumber = survivedDaysByDeadAnimals / deadAnimalsNumber;
        }
    }

    private void copulation(Vector2d position) {
        if (animalsOnMap.get(position) != null && animalsOnMap.get(position).size() > 1) {
            Animal maxEnergyAnimal = getMaximumEnergyAnimal(position);
            Animal secondMaxEnergyAnimal = getSecondMaxEnergyAnimal(position, maxEnergyAnimal);
            assert secondMaxEnergyAnimal != null;
            if (secondMaxEnergyAnimal.energy >= secondMaxEnergyAnimal.startEnergy / 2) {

                // znalezienie miejsca dla potomka
                Vector2d parentPosition = maxEnergyAnimal.getPosition();
                Vector2d childPosition = findPlaceForChild(parentPosition);

                if (childPosition != null) {
                    // utrata energi
                    int lostEnergy1 = (int) (0.25 * maxEnergyAnimal.energy);
                    int lostEnergy2 = (int) (0.25 * secondMaxEnergyAnimal.energy);
                    maxEnergyAnimal.energy -= lostEnergy1;
                    secondMaxEnergyAnimal.energy -= lostEnergy2;
                    Animal animal = new Animal(childPosition, lostEnergy1 + lostEnergy2, resultGenoTypeMaker(maxEnergyAnimal, secondMaxEnergyAnimal), this, startEnergy);
                    place(animal);
                    maxEnergyAnimal.childNumber += 1;
                    secondMaxEnergyAnimal.childNumber += 1;
                }
            }

        }

    }

    private void setJunglePosition(int jungleRatio) {

        jungleHeight = (jungleRatio * height / 100);
        jungleWidth = (jungleRatio * width / 100);

        jungleLowerLeft = new Vector2d(width / 2 - jungleWidth / 2, height / 2 - jungleHeight / 2);
    }

    private void placeGrasses() {
        Random rand = new Random();
        Vector2d grassPosition;

        // placing grass in jungle
        for (int i = 0; i < 5; i++) {
            grassPosition = new Vector2d(rand.nextInt(jungleWidth) + jungleLowerLeft.x, rand.nextInt(jungleHeight) + jungleLowerLeft.y);

            if (grassesOnMap.get(grassPosition) == null) {
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

    private Vector2d findPlaceForChild(Vector2d parentPosition) {   // długa ta metoda
        // 1) x, y+1
        Vector2d newPosition;
        if (animalsOnMap.get(new Vector2d(parentPosition.x, (parentPosition.y + 1) % height)) == null) {
            return new Vector2d(parentPosition.x, (parentPosition.y + 1) % height);
        }
        // 2) x+1, y+1
        if (animalsOnMap.get(new Vector2d((parentPosition.x + 1) % width, (parentPosition.y + 1) % height)) == null) {
            return new Vector2d((parentPosition.x + 1) % width, (parentPosition.y + 1) % height);
        }
        // 3) x+1 , y
        if (animalsOnMap.get(new Vector2d((parentPosition.x + 1) % width, parentPosition.y)) == null) {
            return new Vector2d((parentPosition.x + 1) % width, parentPosition.y);
        }
        // 4) x+1, y-1
        if (parentPosition.y - 1 < 0) {
            newPosition = new Vector2d((parentPosition.x + 1) % width, height - 1);
        } else {
            newPosition = new Vector2d((parentPosition.x + 1) % width, (parentPosition.y - 1) % height);
        }
        if (animalsOnMap.get(newPosition) == null) {
            return newPosition;
        }
        // 5) x, y-1
        if (parentPosition.y - 1 < 0) {
            newPosition = new Vector2d(parentPosition.x, height - 1);
        } else {
            newPosition = new Vector2d(parentPosition.x, (parentPosition.y - 1) % height);
        }
        if (animalsOnMap.get(newPosition) == null) {
            return newPosition;
        }
        // 6) x-1, y-1
        if (parentPosition.y - 1 < 0 && parentPosition.x - 1 < 0) {
            newPosition = new Vector2d(width - 1, height - 1);
        } else if (parentPosition.y - 1 < 0) {
            newPosition = new Vector2d((parentPosition.x - 1) % width, height - 1);
        } else if (parentPosition.x - 1 < 0) {
            newPosition = new Vector2d(width - 1, (parentPosition.y - 1) % height);
        } else {
            newPosition = new Vector2d((parentPosition.x - 1) % width, (parentPosition.y - 1) % height);
        }
        if (animalsOnMap.get(newPosition) == null) {
            return newPosition;
        }
        // 7) x-1,y
        if (parentPosition.x - 1 < 0) {
            newPosition = new Vector2d(width - 1, parentPosition.y);
        } else {
            newPosition = new Vector2d((parentPosition.x - 1) % width, parentPosition.y);
        }
        if (animalsOnMap.get(newPosition) == null) {
            return newPosition;
        }
        // 8) x-1, y+1
        if (parentPosition.x - 1 < 0) {
            newPosition = new Vector2d(width - 1, (parentPosition.y) % height);
        } else {
            newPosition = new Vector2d((parentPosition.x - 1) % width, (parentPosition.y) % height);
        }
        if (animalsOnMap.get(newPosition) == null) {
            return newPosition;
        }
        return null;
    }

    private int[] genotypeSeparator() {
        Random rand = new Random();
        int position;
        int currentGroup = 0;
        int[] resultArray = new int[32];
        for (int i = 0; i < 32; i++) {
            resultArray[i] = -1;
        }

        for (int i = 0; i < 32; i++) {
            position = rand.nextInt(32);
            while (resultArray[position] != -1) {
                position += 1;
                position %= 32;
            }
            resultArray[position] = currentGroup;
            currentGroup += 1;
            currentGroup %= 3;
        }
        return resultArray;
    }

    private int[] genotypeMaker(int[] genesPosA, int[] genesPosB, int[] genesA, int[] genesB) {
        Random rand = new Random();
        int[] newGenotype = new int[32];
        int currentPosition = 0;

        for (int i = 0; i < 32; i++) {
            if (genesPosA[i] != 2) {
                newGenotype[currentPosition] = genesA[i];
                currentPosition += 1;
            }
        }

        for (int i = 0; i < 32; i++) {
            if (genesPosB[i] == 2) {
                newGenotype[currentPosition] = genesB[i];
                currentPosition += 1;
            }
        }

        Arrays.sort(newGenotype);
        ArrayList<Integer> missingGenes;
        do {
            boolean[] genesInArray = new boolean[8];
            for (int i = 0; i < 32; i++) {
                genesInArray[newGenotype[i]] = true;
            }
            missingGenes = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                if (!genesInArray[i]) {
                    missingGenes.add(i);
                }
            }
            for (Integer missingGene : missingGenes) {
                newGenotype[rand.nextInt(32)] = missingGene;
            }
        } while (missingGenes.size() > 0);
        Arrays.sort(newGenotype);
        return newGenotype;
    }

    private int[] resultGenoTypeMaker(Animal animal1, Animal animal2) {
        return genotypeMaker(genotypeSeparator(), genotypeSeparator(), animal1.animalGene, animal2.animalGene);
    }

    public int getAverageEnergyLevel() {

        int avergeEnergyLevel = 0;
        for (Vector2d position : animalsPositions) {
            for (Animal animal : animalsOnMap.get(position)) {
                avergeEnergyLevel += animal.energy;
            }
        }
        if (animalsNumber > 0) {
            return avergeEnergyLevel / animalsNumber;
        }
        return 0;
    }

    public int getAverageAnimalChildNumber() {

        int averageChildNumber = 0;

        for (Vector2d position : animalsPositions) {
            for (Animal animal : animalsOnMap.get(position)) {
                averageChildNumber += animal.childNumber;
            }
        }

        if (animalsNumber > 0) {
            return averageChildNumber / animalsNumber;
        }
        return 0;
    }

}

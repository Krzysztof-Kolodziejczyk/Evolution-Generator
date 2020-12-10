package sample;

import java.awt.*;
import java.util.*;

public class WorldMap {

    public final int height, width;
    private final Vector2d lowerLeft;
    private final Vector2d upperRight;
    public HashMap<Vector2d, ArrayList<IMapElement>> objectsOnMap;
    private int dayEnergyCost;
    private int plantEnergy;
    private Vector2d jungleLowerLeft, jungleLowerRight, jungleUpperRight, jungleUpperLeft;
    private int jungleHeight, jungleWidth;

    public WorldMap(int width, int height, int dayEnergyCost, int plantEnergy, double jungleRatio) {
        this.height = height;
        this.width = width;
        this.dayEnergyCost = dayEnergyCost;
        this.plantEnergy = plantEnergy;
        setJunglePosition(jungleRatio);

        objectsOnMap = new HashMap<>();

        this.lowerLeft = new Vector2d(0, 0);
        this.upperRight = new Vector2d(width - 1, height - 1);

    }

    protected void addElementHashMap(Vector2d position, IMapElement mapElement) {
        if (objectsOnMap.get(position) == null) {
            objectsOnMap.put(position, new ArrayList<>());
            objectsOnMap.get(position).add(mapElement);
        } else {
            objectsOnMap.get(position).add(mapElement);
        }
    }

    // If on position is grass and Animal, objectAt returns Animal
    public Object objectAt(Vector2d position) {
        if (objectsOnMap.get(position) == null) return null;
        IMapElement result = null;
        for (IMapElement mapElement : objectsOnMap.get(position)) {
            result = mapElement;
            if (mapElement.getClass() == Animal.class) {
                return mapElement;
            }
        }
        return result;
    }


    public boolean canMoveTo(Vector2d position) {
        if (objectAt(position) == null) return true;
        return !(objectAt(position).getClass() == Animal.class);
    }


    public boolean place(Animal animal) {
        if (!this.canMoveTo(animal.getPosition())) {
            return false;
        } else {
            addElementHashMap(animal.getPosition(), animal);
            return true;
        }
    }


    public void positionChanged(Vector2d oldPosition, Vector2d newPosition, Animal animal) {
        ArrayList<IMapElement> objects = objectsOnMap.get(oldPosition);
        if (objects.size() == 1) {
            addElementHashMap(newPosition, animal);
            objectsOnMap.remove(oldPosition);
        } else if (objects.size() > 1) {
            for (IMapElement element : objects) {
                if (element.getClass() == Animal.class && element.equals(animal)) {
                    objects.remove(element);
                    addElementHashMap(newPosition, element);
                    return;
                }
            }
        }
    }

    public void dayCycle() {
        placeGrasses();
        ArrayList<Vector2d> objectsPositions = new ArrayList<>();

        Iterator iterator = objectsOnMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry mapElement = (Map.Entry) iterator.next();
            objectsPositions.add((Vector2d) mapElement.getKey());
        }

        //przejście po wszystkich zwierzętach i weryfikacja zdarzeń (jedzenie, rozmnażanie, śmierć)
        for (Vector2d position : objectsPositions) {
            ArrayList<IMapElement> iMapElements = objectsOnMap.get(position);
            eat(iMapElements);
            death(iMapElements);
        }


        for (Vector2d position : objectsPositions) {
            ArrayList<IMapElement> iMapElements = objectsOnMap.get(position);
            if(iMapElements != null)
            {
                ArrayList<Animal> animalsToMove = new ArrayList<>();
                for (IMapElement iMapElement : iMapElements)
                {
                    if (iMapElement.getClass() == Animal.class) {
                        animalsToMove.add((Animal) iMapElement);
                    }
                }
                for(Animal animal: animalsToMove)
                {
                    // animal move
                    Vector2d oldPosition = animal.getPosition();
                    animal.move();
                    Vector2d newPosition = animal.getPosition();
                    positionChanged(oldPosition, newPosition, animal);
                    animal.energy -= dayEnergyCost;
                }
            }
        }

    }

    private Animal getMaximumEnergyAnimal(ArrayList<IMapElement> iMapElements) {
        int max = -1;
        Animal resultAnimal = null;
        if (iMapElements.size() == 0) return null;
        for (IMapElement iMapElement : iMapElements) {
            if (iMapElement.getClass() == Animal.class && ((Animal) iMapElement).energy > max) {
                resultAnimal = (Animal) iMapElement;
                max = resultAnimal.energy;
            }
        }
        return resultAnimal;
    }

    // na jedym polu może być maksymalnie jedna trawa i int zwierząt
    private void eat(ArrayList<IMapElement> iMapElements) {
        if (iMapElements != null) {
            if (iMapElements.size() > 1) {
                Grass grass;
                boolean grassWasInList = false;
                for (IMapElement iMapElement : iMapElements) {
                    if (iMapElement.getClass() == Grass.class) {
                        grass = (Grass) iMapElement;
                        iMapElements.remove(grass);
                        grassWasInList = true;
                        break;
                    }
                }
                if (grassWasInList) {
                    Animal maxEnergyAniaml = getMaximumEnergyAnimal(iMapElements);
                    if (maxEnergyAniaml != null) {
                        maxEnergyAniaml.energy += plantEnergy;
                    }
                }
            }
        }
    }

    private void death(ArrayList<IMapElement> iMapElements) {
        ArrayList<IMapElement> deathAnimals = new ArrayList<>();
        if (iMapElements != null) {
            for (IMapElement iMapElement : iMapElements) {
                if (iMapElement.getClass() == Animal.class && ((Animal) iMapElement).energy < 0) {
                    deathAnimals.add(iMapElement);
                }
            }
            for(IMapElement deathAnimal: deathAnimals)
            {
                iMapElements.remove(deathAnimal);
            }
            if(iMapElements.size() == 0)
            {
                objectsOnMap.remove(deathAnimals.get(0).getPosition());
            }
        }
    }

    private void multiplication() {

    }

    private void setJunglePosition(double jungleRatio) {

        jungleHeight = (int) (jungleRatio * height);
        jungleWidth = (int) (jungleRatio * width);

        jungleLowerLeft = new Vector2d(width / 2 - jungleWidth / 2, height / 2 - jungleHeight / 2);
        jungleLowerRight = new Vector2d(jungleLowerLeft.x + jungleWidth, jungleLowerLeft.y);
        jungleUpperLeft = new Vector2d(jungleLowerLeft.x, jungleLowerLeft.y + jungleHeight);
        jungleUpperRight = new Vector2d(jungleLowerRight.x, jungleUpperLeft.y);
    }

    private void placeGrasses() {
        Random rand = new Random();
        ArrayList<IMapElement> objectsOnPosiiton;
        Vector2d grassPosition;

        // placing grass in jungle
        for (int i = 0; i < 20; i++) {
            grassPosition = new Vector2d(rand.nextInt(jungleWidth) + jungleLowerLeft.x, rand.nextInt(jungleHeight) + jungleLowerLeft.y);

            boolean isGrassOnPosition = false;
            objectsOnPosiiton = objectsOnMap.get(grassPosition);
            if (objectsOnMap.get(grassPosition) == null) {
                objectsOnMap.put(grassPosition, new ArrayList<>());
                objectsOnMap.get(grassPosition).add(new Grass(grassPosition));
                break;
            }
            for (IMapElement iMapElement : objectsOnPosiiton) {
                if (iMapElement.getClass() == Grass.class) {
                    isGrassOnPosition = true;
                    break;
                }
            }
            if (!isGrassOnPosition) {
                objectsOnPosiiton.add(new Grass(grassPosition));
                break;
            }
        }

        // placing grass on savanna
        for (int i = 0; i < 20; i++) {
            grassPosition = new Vector2d(rand.nextInt(width), rand.nextInt(height));
            if (grassPosition.x >= jungleLowerLeft.x && grassPosition.x < jungleLowerLeft.x + jungleWidth && grassPosition.y >= jungleLowerLeft.y && grassPosition.y < jungleLowerLeft.y + jungleHeight) {
                continue;
            }

            boolean isGrassOnPosition = false;
            objectsOnPosiiton = objectsOnMap.get(grassPosition);
            if (objectsOnPosiiton == null) {
                objectsOnMap.put(grassPosition, new ArrayList<>());
                objectsOnMap.get(grassPosition).add(new Grass(grassPosition));
                break;
            }

            for (IMapElement iMapElement : objectsOnPosiiton) {
                if (iMapElement.getClass() == Grass.class) {
                    isGrassOnPosition = true;
                    break;
                }
            }
            if (!isGrassOnPosition) {
                objectsOnPosiiton.add(new Grass(grassPosition));
                break;
            }
        }

    }
}

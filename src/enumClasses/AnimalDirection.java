package enumClasses;    // to czy coś jest enumem to nie jest dobre kryterium podziału na pakiety

public enum AnimalDirection {   // czemu Animal?
    NORTH,
    NORTH_EAST,
    EAST,
    SOUTH_EAST,
    SOUTH,
    SOUTH_WEST,
    WEST,
    NORTH_WEST;


    public AnimalDirection next(AnimalDirection animalDirection)    // co daje NORTH.next(SOUTH)?
    {
        return switch (animalDirection) {
            case NORTH -> AnimalDirection.NORTH_EAST;
            case NORTH_EAST -> AnimalDirection.EAST;
            case EAST -> AnimalDirection.SOUTH_EAST;
            case SOUTH_EAST -> AnimalDirection.SOUTH;
            case SOUTH -> AnimalDirection.SOUTH_WEST;
            case SOUTH_WEST -> AnimalDirection.WEST;
            case WEST -> AnimalDirection.NORTH_WEST;
            case NORTH_WEST -> AnimalDirection.NORTH;
        };
    }

}

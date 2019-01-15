public enum Direction {
    UP(0, -1),
    RIGHT(1, 0),
    DOWN(0, 1),
    LEFT(-1, 0);

    int x, y;

    // constructor
    Direction(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // get position of the enum through ordinal() method
    // and 
    public Direction previous() {
        int nextIndex = ordinal() - 1;
        System.out.println("previous enum: "+ordinal());

        if (nextIndex == -1) {
            nextIndex = Direction.values().length - 1;
        }

        return Direction.values()[nextIndex];
    }

    // 
    public Direction next() {
        int nextIndex = ordinal() + 1;

        if (nextIndex == Direction.values().length) {
            nextIndex = 0;
        }

        return Direction.values()[nextIndex];
    }
}

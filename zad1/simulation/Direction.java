package zad1.simulation;

import java.util.Random;

public enum Direction {
    // Wartości x rosną w prawo, a wartości y - w dół.
    UP(0, -1),
    DOWN(0, 1),
    LEFT(-1, 0),
    RIGHT(1, 0);

    private final int coordinateX;
    private final int coordinateY;

    Direction(int x, int y) {
        this.coordinateX = x;
        this.coordinateY = y;
    }

    public int getX() {
        return coordinateX;
    }

    public int getY() {
        return coordinateY;
    }

    @Override
    public String toString() {
        switch (this) {
            case UP -> {
                return "góra";
            }
            case DOWN -> {
                return "dół";
            }
            case LEFT -> {
                return "lewo";
            }
            case RIGHT -> {
                return "prawo";
            }
        }
        return "";
    }

    public static Direction getByCoords(int x, int y) {
        for (Direction dir : Direction.values())
            if (dir.getX() == x & dir.getY() == y)
                return dir;

        return null;
    }

    public static Direction getRandom() {
        Random r = new Random();
        return Direction.values()[r.nextInt(4)];
    }

    public static Direction getOpposite(Direction dir) {
        return Direction.getByCoords(-dir.getX(), -dir.getY());
    }

    public static Direction turnLeft(Direction dir) {
        return Direction.getByCoords(dir.getY(), -dir.getX());
    }

    public static Direction turnRight(Direction dir) {
        return Direction.getByCoords(-dir.getY(), dir.getX());
    }
}

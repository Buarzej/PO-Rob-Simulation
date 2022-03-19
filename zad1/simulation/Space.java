package zad1.simulation;

public class Space {
    private final int x;
    private final int y;

    public Space(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean hasFood() {
        return false;
    }
}

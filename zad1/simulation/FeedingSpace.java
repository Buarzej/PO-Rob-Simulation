package zad1.simulation;

import zad1.exceptions.UnknownParameterException;
import zad1.input.Parameters;

public class FeedingSpace extends Space {
    private int foodGrowTimer;  // Za ile rund urośnie jedzenie (0 = jedzenie dostępne).

    public FeedingSpace(int x, int y) {
        super(x, y);
        this.foodGrowTimer = 0;
    }

    public boolean hasFood() {
        return foodGrowTimer == 0;
    }

    public void eatFood() throws UnknownParameterException {
        foodGrowTimer = Parameters.getInstance().getInt("ile_rośnie_jedzenie");
    }

    public void growFood() {
        if (foodGrowTimer > 0)
            foodGrowTimer--;
    }
}

package zad1.simulation;

import zad1.exceptions.UnknownParameterException;
import zad1.input.Parameters;

import java.util.ArrayList;
import java.util.Random;

public class Rob {
    private int posX;
    private int posY;
    private Direction direction;

    private final ArrayList<Character> program;
    private int energy;
    private int age;

    public Rob(int posX, int posY, Direction direction, ArrayList<Character> program, int energy) {
        this.posX = posX;
        this.posY = posY;
        this.direction = direction;
        this.program = program;
        this.energy = energy;
    }

    public boolean isAlive() {
        return energy > 0;
    }

    public ArrayList<Character> getProgram() {
        return program;
    }

    public int getEnergy() {
        return energy;
    }

    public int getAge() {
        return age;
    }

    private boolean tryToEat(Simulation simulation, int x, int y) throws UnknownParameterException {
        Space currSpace = simulation.getSpace(x, y);
        if (currSpace.hasFood()) {
            ((FeedingSpace) currSpace).eatFood();
            energy += Parameters.getInstance().getInt("ile_daje_jedzenie");
            return true;
        }

        return false;
    }

    private void turnLeft() {
        direction = Direction.turnLeft(direction);
    }

    private void turnRight() {
        direction = Direction.turnRight(direction);
    }

    private void goForward(Simulation simulation) throws UnknownParameterException {
        posX = Math.floorMod(posX + direction.getX(), simulation.getBoardSizeX());
        posY = Math.floorMod(posY + direction.getY(), simulation.getBoardSizeY());
        tryToEat(simulation, posX, posY);
    }

    private void smell(Simulation simulation) {
        for (int i = 0; i < 4; i++) {
            Direction tempDir = Direction.turnLeft(direction);
            int checkX = Math.floorMod(posX + tempDir.getX(), simulation.getBoardSizeX());
            int checkY = Math.floorMod(posY + tempDir.getY(), simulation.getBoardSizeY());
            if (simulation.getSpace(checkX, checkY).hasFood())
                direction = tempDir;
        }
    }

    private void eat(Simulation simulation) throws UnknownParameterException {
        for (Space space : simulation.getEightAdjacent(posX, posY)) {
            if (tryToEat(simulation, space.getX(), space.getY())) {
                posX = space.getX();
                posY = space.getY();
                return;
            }
        }
    }

    private ArrayList<Character> mutateProgram() throws UnknownParameterException {
        ArrayList<Character> newProgram = new ArrayList<>(program);
        ArrayList<Character> instructionsList = Parameters.getInstance().getArrayList("spis_instr");
        Random r = new Random();

        // Usunięcie ostatniej instrukcji.
        if (Math.random() < Parameters.getInstance().getDouble("pr_usunięcia_instr") && newProgram.size() > 0) {
            newProgram.remove(newProgram.size() - 1);
        }

        // Dodanie nowej instrukcji na końcu.
        if (Math.random() < Parameters.getInstance().getDouble("pr_dodania_instr")) {
            Character randomInstruction = instructionsList.get(r.nextInt(instructionsList.size()));
            newProgram.add(randomInstruction);
        }

        // Zamiana losowej instrukcji na inną.
        if (Math.random() < Parameters.getInstance().getDouble("pr_zmiany_instr") && newProgram.size() > 0) {
            Character randomInstruction = instructionsList.get(r.nextInt(instructionsList.size()));
            newProgram.set(r.nextInt(newProgram.size()), randomInstruction);
        }

        return newProgram;
    }

    public void playRound(Simulation simulation) throws UnknownParameterException {
        for (Character instruction : program) {
            if (energy <= 0)
                return;  // Rob umiera, nie może wykonać ruchu.

            energy--;
            switch (instruction) {
                case 'l' -> turnLeft();
                case 'p' -> turnRight();
                case 'i' -> goForward(simulation);
                case 'w' -> smell(simulation);
                case 'j' -> eat(simulation);
            }
        }

        // Powielenie.
        if (Math.random() < Parameters.getInstance().getDouble("pr_powielenia") &&
                energy >= Parameters.getInstance().getInt("limit_powielania")) {
            int newRobEnergy = (int) (Parameters.getInstance().getDouble("ułamek_energii_rodzica") * energy);
            energy -= newRobEnergy;

            Rob newRob = new Rob(posX, posY, Direction.getOpposite(direction), mutateProgram(), newRobEnergy);
            simulation.addNewRob(newRob);
        }

        energy -= Parameters.getInstance().getInt("koszt_tury");
        age++;
    }

    @Override
    public String toString() {
        return "pozycja (" + posX + ", " + posY + "), kierunek " + direction.toString() + ", dł. prog. " + program.size() +
                ", energia " + energy + ", wiek " + age;
    }
}

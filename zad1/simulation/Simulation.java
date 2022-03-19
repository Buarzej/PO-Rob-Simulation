package zad1.simulation;

import zad1.exceptions.UnknownParameterException;
import zad1.input.Parameters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.stream.Collectors;

public class Simulation {
    private final Space[][] board;
    private final int boardSizeX;
    private final int boardSizeY;
    private final int totalRounds;
    private final ArrayList<Rob> aliveRobs;
    private ArrayList<Rob> newRobs;  // Roby urodzone w danej turze.

    public Simulation(Space[][] board) throws UnknownParameterException {
        this.board = board;
        this.boardSizeX = Parameters.getInstance().getInt("rozmiar_planszy_x");
        this.boardSizeY = Parameters.getInstance().getInt("rozmiar_planszy_y");
        this.totalRounds = Parameters.getInstance().getInt("ile_tur");
        this.aliveRobs = new ArrayList<>();
    }

    public void addStartingRob(Rob rob) {
        aliveRobs.add(rob);
    }

    public void addNewRob(Rob rob) {
        newRobs.add(rob);
    }

    public int getBoardSizeX() {
        return boardSizeX;
    }

    public int getBoardSizeY() {
        return boardSizeY;
    }

    public Space getSpace(int x, int y) {
        return board[x][y];
    }

    public Space[] getEightAdjacent(int x, int y) {
        Space[] adjacent = new Space[8];
        int index = 0;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0)
                    continue;
                adjacent[index++] = board[Math.floorMod(x + i, boardSizeX)][Math.floorMod(y + j, boardSizeY)];
            }
        }

        return adjacent;
    }

    private void getRoundReport(int round) {
        int availableFood = 0;
        for (Space[] line : board)
            for (Space space : line)
                if (space.hasFood())
                    availableFood++;

        int minProgramLength = Integer.MAX_VALUE, maxProgramLength = -1, totalProgramLength = 0;
        int minEnergy = Integer.MAX_VALUE, maxEnergy = -1, totalEnergy = 0;
        int minAge = Integer.MAX_VALUE, maxAge = -1, totalAge = 0;
        for (Rob rob : aliveRobs) {
            minProgramLength = Math.min(minProgramLength, rob.getProgram().size());
            maxProgramLength = Math.max(maxProgramLength, rob.getProgram().size());
            totalProgramLength += rob.getProgram().size();
            minEnergy = Math.min(minEnergy, rob.getEnergy());
            maxEnergy = Math.max(maxEnergy, rob.getEnergy());
            totalEnergy += rob.getEnergy();
            minAge = Math.min(minAge, rob.getAge());
            maxAge = Math.max(maxAge, rob.getAge());
            totalAge += rob.getAge();
        }

        double avgProgramLength = (double) totalProgramLength / (double) aliveRobs.size();
        double avgEnergy = (double) totalEnergy / (double) aliveRobs.size();
        double avgAge = (double) totalAge / (double) aliveRobs.size();

        System.out.println("tura " + round + ": roby " + aliveRobs.size() + ", żywność " + availableFood + ", dł. prog. " + minProgramLength + "/"
                + String.format("%.2f", avgProgramLength) + "/" + maxProgramLength + ", energia " + minEnergy + "/"
                + String.format("%.2f", avgEnergy) + "/" + maxEnergy + ", wiek " + minAge + "/" + String.format("%.2f", avgAge) + "/" + maxAge);
    }

    public void simulate() throws UnknownParameterException {
        // Wygenerowanie początkowego stanu symulacji.
        int startingRobsNumber = Parameters.getInstance().getInt("pocz_ile_robów");
        ArrayList<Character> startingProgram = Parameters.getInstance().getArrayList("pocz_progr");
        int startingEnergy = Parameters.getInstance().getInt("pocz_energia");

        for (int i = 0; i < startingRobsNumber; i++) {
            Random r = new Random();
            Rob startingRob = new Rob(r.nextInt(boardSizeX), r.nextInt(boardSizeY), Direction.getRandom(), startingProgram, startingEnergy);
            this.addStartingRob(startingRob);
        }

        System.out.println("Początek symulacji");
        for (int i = 0; i < aliveRobs.size(); i++)
            System.out.println("rob " + (i + 1) + ": " + aliveRobs.get(i).toString());

        // Właściwe przeprowadzenie wszystkich tur.
        int simulationReportCounter = 0;
        for (int round = 1; round <= totalRounds; round++) {
            newRobs = new ArrayList<>();
            Collections.shuffle(aliveRobs);

            for (Rob rob : aliveRobs)
                rob.playRound(this);

            for (Space[] line : board)
                for (Space space : line)
                    if (space.getClass().getSimpleName().equals("FeedingSpace"))
                        ((FeedingSpace) space).growFood();

            aliveRobs.addAll(newRobs);

            // Usunięcie nieżywych robów.
            aliveRobs.removeAll(aliveRobs.stream().filter(rob -> !rob.isAlive()).collect(Collectors.toList()));

            // Opis stanu symulacji.
            if (aliveRobs.size() > 0) {
                getRoundReport(round);
            } else {
                System.out.println("tura " + round + ": brak żyjących robów, zakończenie symulacji");
                return;
            }

            if (++simulationReportCounter == Parameters.getInstance().getInt("co_ile_wypisz")) {
                simulationReportCounter = 0;
                for (int i = 0; i < aliveRobs.size(); i++)
                    System.out.println("rob " + (i + 1) + ": " + aliveRobs.get(i).toString());
            }
        }

        System.out.println("Zakończenie symulacji");
        for (int i = 0; i < aliveRobs.size(); i++)
            System.out.println("rob " + (i + 1) + ": " + aliveRobs.get(i).toString());
    }
}

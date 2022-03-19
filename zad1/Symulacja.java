package zad1;

import zad1.exceptions.*;
import zad1.input.Parser;
import zad1.simulation.Simulation;
import zad1.simulation.Space;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Symulacja {

    public static final ArrayList<Scanner> activeScanners = new ArrayList<>();

    public static void main(String[] args) {
        try {
            if (args.length < 2)
                throw new NoParameterFileException();

            Parser parser = new Parser();
            parser.parseParameters(args);
            Space[][] board = parser.parseBoard(args);

            Simulation simulation = new Simulation(board);
            simulation.simulate();
        } catch (NoParameterFileException e) {
            System.out.println("Brak plików z parametrami");
        } catch (FileNotFoundException e) {
            System.out.println("Niepoprawny plik z danymi: " + e.getMessage());
        } catch (InvalidFileContentsException e) {
            System.out.println("Niepoprawna treść pliku " + e.getMessage());
        } catch (InvalidParameterException | UnknownParameterException e) {
            System.out.println("Niepoprawny parametr " + e.getMessage());
        } catch (DuplicateParameterException e) {
            System.out.println("Zduplikowany parametr " + e.getMessage());
        } catch (InvalidBoardException e) {
            System.out.println("Niepoprawna plansza");
        } finally {
            for (Scanner scanner : activeScanners)
                scanner.close();
        }
    }
}

package zad1.input;

import zad1.Symulacja;
import zad1.exceptions.*;
import zad1.simulation.FeedingSpace;
import zad1.simulation.Space;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

public class Parser {

    private final Character[] validInstructions = {'l', 'p', 'i', 'w', 'j'};

    public Parser() {
    }

    private boolean checkInstructionValidity(Character instruction) {
        return Arrays.asList(validInstructions).contains(instruction);
    }

    private ArrayList<Character> convertToInstructions(String key, String value) throws InvalidParameterException {
        ArrayList<Character> instructions = new ArrayList<>();

        for (int i = 0; i < value.length(); i++) {
            if (checkInstructionValidity(value.charAt(i)))
                instructions.add(value.charAt(i));
            else
                throw new InvalidParameterException(key);
        }

        return instructions;
    }

    private boolean checkProgramValidity(ArrayList<Character> program) throws UnknownParameterException {
        ArrayList<Character> validInstructions = Parameters.getInstance().getArrayList("spis_instr");

        for (char instruction : program)
            if (!validInstructions.contains(instruction))
                return false;

        return true;
    }

    public void parseParameters(String[] args) throws FileNotFoundException, InvalidParameterException, DuplicateParameterException, InvalidFileContentsException, UnknownParameterException {
        File parametersFile = new File(args[1]);
        Scanner fileScanner = new Scanner(parametersFile);
        Symulacja.activeScanners.add(fileScanner);

        while (fileScanner.hasNextLine()) {
            Scanner lineScanner = new Scanner(fileScanner.nextLine()).useLocale(Locale.ENGLISH);
            Symulacja.activeScanners.add(lineScanner);

            if (!lineScanner.hasNext())
                throw new InvalidFileContentsException(parametersFile.getName());

            String key = lineScanner.next();
            switch (Parameters.getInstance().getParameterType(key)) {
                case "int" -> {
                    if (!lineScanner.hasNextInt())
                        throw new InvalidParameterException(key);
                    int value = lineScanner.nextInt();
                    if (value < 0)
                        throw new InvalidParameterException(key);
                    Parameters.getInstance().addParameter(key, value);
                }
                case "double" -> {
                    if (!lineScanner.hasNextDouble())
                        throw new InvalidParameterException(key);
                    double value = lineScanner.nextDouble();
                    if (value < 0 || value > 1)
                        throw new InvalidParameterException(key);
                    Parameters.getInstance().addParameter(key, value);
                }
                case "string" -> {
                    if (!lineScanner.hasNext())
                        throw new InvalidParameterException(key);
                    Parameters.getInstance().addParameter(key, convertToInstructions(key, lineScanner.next()));
                }
            }

            if (lineScanner.hasNext())
                throw new InvalidParameterException(key);
        }

        // Sprawdzenie, czy wszystkie parametry zostały podane.
        if (!Parameters.getInstance().checkParametersValidity())
            throw new InvalidFileContentsException(parametersFile.getName());

        // Sprawdzenie, czy początkowy program zawiera niedozwolone instrukcje.
        if (!checkProgramValidity(Parameters.getInstance().getArrayList("pocz_progr")))
            throw new InvalidParameterException("pocz_progr");
    }

    public Space[][] parseBoard(String[] args) throws FileNotFoundException, InvalidBoardException, DuplicateParameterException {
        File parametersFile = new File(args[0]);
        Scanner fileScanner = new Scanner(parametersFile);
        Symulacja.activeScanners.add(fileScanner);

        // Sprawdzenie rozmiaru planszy.
        int boardSizeX = 0;
        int boardSizeY = 0;

        while (fileScanner.hasNextLine()) {
            boardSizeY++;
            String line = fileScanner.nextLine();

            if (boardSizeX != 0 && boardSizeX != line.length())
                throw new InvalidBoardException();

            boardSizeX = line.length();
        }

        if (boardSizeY == 0)
            throw new InvalidBoardException();

        // Wczytanie planszy.
        Space[][] board = new Space[boardSizeX][boardSizeY];
        Parameters.getInstance().addParameter("rozmiar_planszy_x", boardSizeX);
        Parameters.getInstance().addParameter("rozmiar_planszy_y", boardSizeY);

        fileScanner.close();
        fileScanner = new Scanner(parametersFile);

        int y = 0;
        while (fileScanner.hasNextLine()) {
            String line = fileScanner.nextLine();
            for (int x = 0; x < line.length(); x++) {
                switch (line.charAt(x)) {
                    case ' ' -> board[x][y] = new Space(x, y);
                    case 'x' -> board[x][y] = new FeedingSpace(x, y);
                    default -> throw new InvalidBoardException();
                }
            }
            y++;
        }

        return board;
    }
}

package zad1.input;

import zad1.exceptions.DuplicateParameterException;
import zad1.exceptions.InvalidParameterException;
import zad1.exceptions.UnknownParameterException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Parameters {
    private final Map<String, Object> parameters = new HashMap<>();

    private final String[] intParameters = {"ile_tur", "pocz_ile_robów", "pocz_energia", "ile_daje_jedzenie",
            "ile_rośnie_jedzenie", "koszt_tury", "limit_powielania", "co_ile_wypisz"};
    private final String[] doubleParameters = {"pr_powielenia", "ułamek_energii_rodzica", "pr_usunięcia_instr",
            "pr_dodania_instr", "pr_zmiany_instr"};
    private final String[] stringParameters = {"pocz_progr", "spis_instr"};

    private static final Parameters INSTANCE = new Parameters();

    private Parameters() {
    }

    public static Parameters getInstance() {
        return INSTANCE;
    }

    public String getParameterType(String parameter) throws InvalidParameterException {
        if (Arrays.asList(intParameters).contains(parameter))
            return "int";
        else if (Arrays.asList(doubleParameters).contains(parameter))
            return "double";
        else if (Arrays.asList(stringParameters).contains(parameter))
            return "string";
        else
            throw new InvalidParameterException(parameter);
    }

    public boolean checkParametersValidity() {
        ArrayList<String> validParameters = new ArrayList<>();
        validParameters.addAll(Arrays.asList(intParameters));
        validParameters.addAll(Arrays.asList(doubleParameters));
        validParameters.addAll(Arrays.asList(stringParameters));

        for (String key : parameters.keySet())
            if (!validParameters.contains(key))
                return false;

        return parameters.size() == validParameters.size();
    }

    public void addParameter(String key, Object value) throws DuplicateParameterException {
        if (!parameters.containsKey(key))
            parameters.put(key, value);
        else
            throw new DuplicateParameterException(key);
    }

    public int getInt(String key) throws UnknownParameterException {
        if (parameters.containsKey(key))
            return (int) parameters.get(key);
        else
            throw new UnknownParameterException(key);
    }

    public double getDouble(String key) throws UnknownParameterException {
        if (parameters.containsKey(key))
            return (double) parameters.get(key);
        else
            throw new UnknownParameterException(key);
    }

    public ArrayList<Character> getArrayList(String key) throws UnknownParameterException {
        if (parameters.containsKey(key))
            return (ArrayList<Character>) parameters.get(key);
        else
            throw new UnknownParameterException(key);
    }
}

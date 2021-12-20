package Connection_AND_ConsoleHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleHelper {
    private static final BufferedReader reader =
            new BufferedReader(new InputStreamReader(System.in));

    /**
     * Вывод в консоль строки из аргумента.
     *
     * @param str строка, будет выведена в консоль
     */
    public static void printInConsole(String str) {
        System.out.println(str);
    }

    /**
     * Считывание данных с консоли, возвращает эти данные в формате String.
     *
     * @return String
     */
    public static String getStringFromConsole() {
        String tmp_Str = null;
        try {
            tmp_Str = reader.readLine();
        } catch (IOException e) {
            System.out.println("E--->ConsoleHelper/getFromConsole(): ");
            e.printStackTrace();
        }
        return tmp_Str;
    }

    /**
     * Считывание данных с консоли, возвращает эти данные в формате Integer.
     *
     * @return Integer
     */
    public static Integer getIntFromConsole() {
        Integer int_tmp = null;
        int count = 0;

        while (count < 2) { //даём 2 попытки
            try {
                int_tmp = Integer.parseInt(getStringFromConsole());
                break;
            } catch (NumberFormatException e) {
                if (count == 0) {
                    System.out.println("You entered a wrong number, please try again: ");
                    count++;
                } else {
                    System.out.println("E--->ConsoleHelper/getIntFromConsole():");
                    break;
                }
            }
        }
        return int_tmp;
    }
}

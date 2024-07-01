import myregex.Matcher;
import myregex.Pair;
import myregex.Pattern;
import myregex.Regex;

import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print("Please enter regex: ");
        Regex regex = new Regex(in.nextLine());
        Pattern pattern = regex.compile();

        System.out.println(pattern);
        System.out.println(pattern.convertToRegex());

        System.out.print("Please enter string for matching: ");
        String input = in.nextLine();
        while (!input.equals("exit")) {
            Matcher matcher = pattern.matcher(input);

            if (matcher.match()) {
                System.out.println("Is correct string.");
            } else {
                System.out.println("Is incorrect string.");
            }

            Matcher.Iterator iterator = matcher.iterator();
            while (iterator.hasNext()) {
                Pair<Integer, String> next = iterator.next();
                System.out.print("GroupNumber: ");
                System.out.print(next.first());
                System.out.print(" Group: ");
                System.out.println(next.second());
            }

            System.out.print("Please enter string for matching: ");
            input = in.nextLine();
        }

    }
}

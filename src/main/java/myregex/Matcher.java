package myregex;

import java.util.*;

public class Matcher {

    private final DFM dfm;
    private final String input;
    private final LinkedHashMap<Integer, Pair<Pair<Integer, Integer>, Boolean>> data = new LinkedHashMap<>();

    Matcher(String input, DFM dfm) {
        this.input = input;
        this.dfm = dfm;
    }

    public boolean match() {
        if (dfm.enterStartState()) {
            for (int position = 0; position < input.length(); position++) {
                boolean isErrorState = !dfm
                        .nextStage(input.charAt(position),
                                new Pair<>(position, data));
                if (isErrorState) {
                    data.clear();
                    return false;
                }
            }
        }

        if (dfm.isEnd()) {
            return true;
        }

        data.clear();
        return false;
    }

    public String group(int groupNumber) {

        boolean isGroupNumber = (data.containsKey(groupNumber) &&
                data.get(groupNumber).second());
        if (isGroupNumber) {
            Pair<Integer, Integer> positions = data.get(groupNumber).first();
            return input.substring(positions.first(), positions.second());
        }

        return null;
    }

    public Iterator iterator() {
        return new Iterator();
    }

    public class Iterator {
        private final java.util.Iterator<Integer> iterator = data.keySet().stream().sorted().iterator();

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public Pair<Integer, String> next() {
            if (!hasNext()) {
                return null;
            }
            int groupNumber = iterator.next();
            return new Pair<>(groupNumber, group(groupNumber));
        }

    }

}

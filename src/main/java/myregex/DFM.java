package myregex;

import java.util.*;

class DFM {

    private State start;
    private final HashSet<State> ends;

    private State current = null;

    public static DFM getAllStrings() {
        HashSet<State> ends = new HashSet<>();
        ends.add(null);
        return new DFM(null, ends);
    }

    public static DFM getNoneStrings() {
        return new DFM(null, new HashSet<>());
    }

    private DFM(State start, HashSet<State> ends) {
        this.start = start;
        this.ends = ends;
    }

    public DFM(NFM nfm) {
        ends = new HashSet<>();
        generateFromNFM(nfm);
    }

    private void generateFromNFM(NFM nfm) {
        NFM.StateSet stateSet = nfm.getEpsilonClosureForStart();
        start = new State(nfm.getActions(stateSet));
        HashMap<State, NFM.StateSet> states = new HashMap<>();
        states.put(start, stateSet);
        LinkedList<State> queue = new LinkedList<>();
        queue.addLast(start);

        State current;
        while (!queue.isEmpty()) {
            current = queue.getFirst();
            queue.removeFirst();

            stateSet = states.get(current);

            HashMap<Character, NFM.StateSet> transaction = nfm.getSateSetsBySymbols(stateSet);

            for (Map.Entry<Character, NFM.StateSet> entry: transaction.entrySet()) {
                stateSet = nfm.getEpsilonClosure(entry.getValue());

                State next = null;
                for (Map.Entry<State, NFM.StateSet> iterator: states.entrySet()) {
                    if (iterator.getValue().equals(stateSet)) {
                        next = iterator.getKey();
                        break;
                    }
                }

                if (next == null) {
                    next = new State(nfm.getActions(stateSet));
                    queue.addLast(next);
                    states.put(next, stateSet);
                    if (nfm.isEndState(stateSet)) {
                        ends.add(next);
                    }
                }

                current.addTransaction(entry.getKey(), next);

            }

        }

    }

    public boolean enterStartState(Pair<Integer, HashMap<Integer, Pair<Integer, Integer>>> data) {
        current = start;
        if (current != null) {
            current.getActions().forEach(action -> action.run(data));
            return true;
        }
        return false;
    }

    public boolean nextStage(char symbol, Pair<Integer, HashMap<Integer, Pair<Integer, Integer>>> data) {
        current = current.getNextState(symbol);
        if (current != null) {
            current.getActions().forEach(action -> action.run(data));
            return true;
        }
        return false;
    }

    public boolean isEnd() {
        return ends.contains(current);
    }

    public DFM multiplication(DFM other, boolean firstIsEnd, boolean secondIsEnd) {
        HashSet<State> ends = new HashSet<>();
        HashMap<Pair<State, State>, State> states = new HashMap<>();
        LinkedHashSet<State> thisStates = getStates();
        LinkedHashSet<State> otherStates = other.getStates();
        for (State thisState: thisStates) {
            for (State otherState: otherStates) {
                State newState = new State();

                boolean thisIsEnd = (this.ends.contains(thisState));
                boolean otherIsEnd = (other.ends.contains(otherState));
                boolean isEnd = ((thisIsEnd == firstIsEnd) &&
                        (otherIsEnd == secondIsEnd));
                if (isEnd) {
                    ends.add(newState);
                }

                states.put(new Pair<>(thisState, otherState), newState);
            }
        }

        for (State thisState: thisStates) {
            State newState = new State();

            boolean thisIsEnd = (this.ends.contains(thisState));
            boolean isEnd = (thisIsEnd == firstIsEnd);
            if (isEnd) {
                ends.add(newState);
            }

            states.put(new Pair<>(thisState, null), newState);
        }

        for (State otherState: otherStates) {
            State newState = new State();

            boolean otherIsEnd = (other.ends.contains(otherState));
            boolean isEnd = (otherIsEnd == secondIsEnd);
            if (isEnd) {
                ends.add(newState);
            }

            states.put(new Pair<>(null, otherState), newState);
        }

        for (Map.Entry<Pair<State, State>, State> entry: states.entrySet()) {
            State state1 = entry.getKey().first();
            State state2 = entry.getKey().second();

            if (state1 == null) {
                Set<Map.Entry<Character, State>> states2 = state2.getTransactions().entrySet();
                for(Map.Entry<Character, State> otherEntry: states2) {
                    char otherSymbol = otherEntry.getKey();
                    State otherState = otherEntry.getValue();
                    State next = states.get(new Pair<State, State>(null, otherState));
                    entry.getValue().addTransaction(otherSymbol, next);

                }
                continue;
            }

            Set<Map.Entry<Character, State>> states1 = state1.getTransactions().entrySet();
            if (state2 == null) {
                for(Map.Entry<Character, State> thisEntry: states1) {
                    char thisSymbol = thisEntry.getKey();
                    State thisState = thisEntry.getValue();
                    State next = states.get(new Pair<State, State>(thisState, null));
                    entry.getValue().addTransaction(thisSymbol, next);

                }
                continue;
            }

            for (Map.Entry<Character, State> thisEntry: states1) {
                char thisSymbol = thisEntry.getKey();
                State thisState = thisEntry.getValue();

                State otherState = state2.getNextState(thisSymbol);

                State next = states.get(new Pair<>(thisState, otherState));
                entry.getValue().addTransaction(thisSymbol, next);

            }

        }

        State start = states.get(new Pair<>(this.start, other.start));
        return new DFM(start, ends);
    }

    public void makeErrorIsEnd() {
        ends.add(null);
    }

    private LinkedHashSet<State> getStates() {
        LinkedHashSet<State> states = new LinkedHashSet<>();

        if (start == null) {
            return states;
        }

        LinkedList<State> queue = new LinkedList<>();
        queue.add(start);

        State current;
        while (!queue.isEmpty()) {
            current = queue.getFirst();
            queue.removeFirst();

            states.addLast(current);

            for (State next: current.getTransactions().values()) {
                boolean isNewState = !states.contains(next)
                        && !queue.contains(next);
                if (isNewState) {
                    queue.addLast(next);
                }
            }

        }

        return states;
    }

    public Pair<RegexType, String> convertToRegex() {

        if (start == null) {
            if (ends.contains(null)) {
                return new Pair<>(RegexType.ALL_STRINGS, null);
            }
            return new Pair<>(RegexType.NONE_STRINGS, null);
        }

        LinkedHashSet<State> states = getStates();
        LinkedList<State> queue = new LinkedList<>();
        for (State current: states){
            boolean isNotStartAndNotEnd = ((start != current) &&
                    (!ends.contains(current)));
            if (isNotStartAndNotEnd) {
                queue.addLast(current);
            }
        }

        HashMap<State, LinkedHashMap<State, StringBuilder>> outgoingTransactions = new LinkedHashMap<>();
        HashMap<State, LinkedHashMap<State, StringBuilder>> incomingTransactions = new LinkedHashMap<>();
        HashMap<State, StringBuilder> stateTransaction = new HashMap<>();
        for (State current: states) {
            LinkedHashMap<Character, State> transactions = current.getTransactions();

            outgoingTransactions.put(current, new LinkedHashMap<>());

            for (Map.Entry<Character, State> transaction: transactions.entrySet()) {
                StringBuilder regex = new StringBuilder();
                regex.append(transaction.getKey());

                if (transaction.getValue() == current) {
                    if (!stateTransaction.containsKey(current)) {
                        stateTransaction.put(current, regex);
                    } else {
                        stateTransaction.get(current).append("|").append(regex);
                    }
                    continue;
                }

                outgoingTransactions.get(current).put(transaction.getValue(), regex);

                if (!incomingTransactions.containsKey(transaction.getValue())) {
                    incomingTransactions.put(transaction.getValue(), new LinkedHashMap<>());
                }
                incomingTransactions.get(transaction.getValue()).put(current, regex);

            }

        }

        for (State state: states) {
            if (!incomingTransactions.containsKey(state)) {
                incomingTransactions.put(state, new LinkedHashMap<>());
            }
        }

        while (!queue.isEmpty()){
            State state = queue.getFirst();

            StringBuilder stateRegex = new StringBuilder();
            if (stateTransaction.containsKey(state)) {
                stateRegex.append("(").append(stateTransaction.get(state)).append("){0,}");
            }

            HashMap<State, StringBuilder> outgoing = outgoingTransactions.get(state);
            HashMap<State, StringBuilder> incoming = incomingTransactions.get(state);

            for (Map.Entry<State, StringBuilder> incomingTransaction: incoming.entrySet()) {
                for (Map.Entry<State, StringBuilder> outgoingTransaction: outgoing.entrySet()) {
                    State state1 = incomingTransaction.getKey();
                    State state2 = outgoingTransaction.getKey();
                    if (state1 == state2) {
                        StringBuilder regex = new StringBuilder();
                        regex.append("(").append(incomingTransaction.getValue()).append(")")
                                .append("(").append(outgoingTransaction.getValue()).append(")");
                        if (stateTransaction.containsKey(state1)) {
                            stateTransaction.get(state1).append("|").append(regex);
                        } else {
                            stateTransaction.put(state1, regex);
                        }
                    }

                    StringBuilder regex = new StringBuilder();
                    regex.append("(").append(incomingTransaction.getValue()).append(")")
                            .append(stateRegex)
                            .append("(").append(outgoingTransaction.getValue()).append(")");

                    if (outgoingTransactions.get(state1).containsKey(state2)) {
                        outgoingTransactions.get(state1).get(state2).append("|").append(regex);
                    } else {
                        outgoingTransactions.get(state1).put(state2, regex);
                    }
                    if (incomingTransactions.get(state2).containsKey(state1)) {
                        incomingTransactions.get(state2).get(state1).append("|").append(regex);
                    } else {
                        incomingTransactions.get(state2).put(state1, regex);
                    }
                }
            }

            queue.removeFirst();
        }

        HashSet<State> saved = new HashSet<>();
        for (State state: ends) {
            if(!outgoingTransactions.get(state).isEmpty()) {
                saved.add(state);
                queue.addLast(state);

                LinkedHashMap<State, StringBuilder> newRegexes = new LinkedHashMap<>();
                for (Map.Entry<State, StringBuilder> current: outgoingTransactions.get(state).entrySet()) {
                    StringBuilder regex = new StringBuilder();
                    regex.append("(").append(current.getValue()).append("){0,1}");
                    newRegexes.put(current.getKey(), regex);

                    incomingTransactions.get(current.getKey()).remove(state);
                    incomingTransactions.get(current.getKey()).put(state, regex);

                }
                outgoingTransactions.remove(state);
                outgoingTransactions.put(state, newRegexes);

                if (stateTransaction.containsKey(state)) {
                    StringBuilder regex = new StringBuilder();
                    regex.append("(").append(stateTransaction.get(state)).append("){0,1}");
                    stateTransaction.remove(state);
                    stateTransaction.put(state, regex);
                }


            }
        }
        ends.removeAll(saved);

        while (!queue.isEmpty()){
            State state = queue.getFirst();

            StringBuilder stateRegex = new StringBuilder();
            if (stateTransaction.containsKey(state)) {
                stateRegex.append("(").append(stateTransaction.get(state)).append("){0,}");
            }

            HashMap<State, StringBuilder> outgoing = outgoingTransactions.get(state);
            HashMap<State, StringBuilder> incoming = incomingTransactions.get(state);

            for (Map.Entry<State, StringBuilder> incomingTransaction: incoming.entrySet()) {
                for (Map.Entry<State, StringBuilder> outgoingTransaction: outgoing.entrySet()) {
                    StringBuilder regex = new StringBuilder();
                    regex.append("(").append(incomingTransaction.getValue()).append(")")
                            .append(stateRegex)
                            .append("(").append(outgoingTransaction.getValue()).append(")");

                    State state1 = incomingTransaction.getKey();
                    State state2 = outgoingTransaction.getKey();
                    if (outgoingTransactions.get(state1).containsKey(state2)) {
                        outgoingTransactions.get(state1).get(state2).append("|").append(regex);
                    } else {
                        outgoingTransactions.get(state1).put(state2, regex);
                    }
                    if (incomingTransactions.get(state2).containsKey(state1)) {
                        incomingTransactions.get(state2).get(state1).append("|").append(regex);
                    } else {
                        incomingTransactions.get(state2).put(state1, regex);
                    }
                }
            }

            queue.removeFirst();
        }

        StringJoiner regex = new StringJoiner("|");

        StringBuilder startRegex = new StringBuilder();
        if (stateTransaction.containsKey(start)) {
            startRegex.append("(").append(stateTransaction.get(start)).append(")|");
        }

        for (State state: ends) {
            if (!outgoingTransactions.get(start).containsKey(state)) {
                continue;
            }

            StringBuilder endRegex = new StringBuilder();
            if (stateTransaction.containsKey(state)) {
                endRegex.append("(").append(stateTransaction.get(state)).append("){0,}");
            }

            StringBuilder outRegex = outgoingTransactions.get(start).get(state);
            StringBuilder innerRegex = new StringBuilder();
            if (incomingTransactions.get(start).containsKey(state)) {
                StringBuilder inRegex = incomingTransactions.get(start).get(state);
                innerRegex.append("(")
                        .append(startRegex)
                        .append("(").append(outRegex).append(")")
                        .append(endRegex)
                        .append("(").append(inRegex).append(")")
                        .append(")").append("{0,}");
            }

            StringBuilder finalRegex = new StringBuilder();
            finalRegex.append(innerRegex)
                    .append("(").append(outRegex).append(")")
                    .append(endRegex);

            regex.add(finalRegex);
        }

        if (ends.contains(start)) {
            ends.remove(start);

            startRegex = new StringBuilder();
            if (stateTransaction.containsKey(start)) {
                startRegex.append("(").append(stateTransaction.get(start)).append(")|");
            }

            for (State state: ends) {
                if (!outgoingTransactions.get(start).containsKey(state)) {
                    continue;
                }

                if (!incomingTransactions.get(start).containsKey(state)) {
                    continue;
                }

                StringBuilder endRegex = new StringBuilder();
                if (stateTransaction.containsKey(state)) {
                    endRegex.append("(").append(stateTransaction.get(state)).append("){0,}");
                }

                StringBuilder outRegex = outgoingTransactions.get(start).get(state);
                StringBuilder inRegex = incomingTransactions.get(start).get(state);
                StringBuilder finalRegex = new StringBuilder();

                finalRegex.append("(")
                        .append(startRegex)
                        .append("(").append(outRegex).append(")")
                        .append(endRegex)
                        .append("(").append(inRegex).append(")")
                        .append("){0,}");

                startRegex = finalRegex;

            }

            regex.add("^");
            if (!startRegex.isEmpty()) {
                regex.add(startRegex);
            }

            ends.add(start);
        }

        ends.addAll(saved);

        return new Pair<>(RegexType.REGEX, regex.toString());
    }

    private Pair<Integer, String> stateToString(State state, int current, HashMap<State, Integer> states) {
        StringBuilder result = new StringBuilder();
        result.append(states.get(state))
                .append(":");

        for (Map.Entry<Character, State> entry: state.getTransactions().entrySet()) {
            if (states.containsKey(entry.getValue())) {
                result.append(" (")
                        .append(entry.getKey())
                        .append(", ")
                        .append(states.get(entry.getValue()).toString())
                        .append(")");
            } else {
                result.append(" (")
                        .append(entry.getKey())
                        .append(", ")
                        .append(current)
                        .append(")");
                states.put(entry.getValue(), current);
                current++;
            }
        }

        result.append(" Actions count: ").append(state.getActions().size());

        if (ends.contains(state)) {
            result.append(" Is end");
        }

        result.append("\n");

        return new Pair<>(current, result.toString());
    }

    @Override
    public String toString() {

        if (start == null) {
            if (ends.contains(null)) {
                return "0: Actions count: 0 Is end\n";
            }
            return "0: Actions count: 0\n";
        }

        HashMap<State, Integer> states = new HashMap<>();
        states.put(start, 0);
        int currentNumber = 1;
        StringBuilder result = new StringBuilder();
        HashSet<State> passedStates = new HashSet<>();
        LinkedList<State> queue = new LinkedList<>();
        queue.add(start);

        State current;
        while (!queue.isEmpty()) {
            current = queue.getFirst();
            queue.removeFirst();

            Pair<Integer, String> out = stateToString(current, currentNumber, states);
            currentNumber = out.first();
            result.append(out.second());

            passedStates.add(current);

            for (State next: current.getTransactions().values()) {
                boolean isNewState = !passedStates.contains(next)
                        && !queue.contains(next);
                if (isNewState) {
                    queue.addLast(next);
                }
            }

        }

        if (ends.contains(null)) {
            result.append("Error: Is end\n");
        }

        return result.toString();
    }

    private static class State {

        private final LinkedHashMap<Character, State> transactions = new LinkedHashMap<>();
        private final LinkedHashSet<Action> actions;

        private State() {
            this.actions = new LinkedHashSet<>();
        }
        State(LinkedHashSet<Action> actions) {
            this.actions = actions;
        }

        public LinkedHashSet<Action> getActions() {
            return actions;
        }

        public LinkedHashMap<Character, State> getTransactions() {
            return transactions;
        }

        public void addTransaction(Character character, State state) {
            transactions.put(character, state);
        }

        public State getNextState(Character character) {
            return transactions.get(character);
        }

    }

}

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
        start = new State();
        if (nfm.isEndState(stateSet)) {
            ends.add(start);
        }
        HashMap<State, NFM.StateSet> states = new HashMap<>();
        states.put(start, stateSet);
        LinkedList<State> queue = new LinkedList<>();
        queue.addLast(start);

        State current;
        while (!queue.isEmpty()) {
            current = queue.getFirst();
            queue.removeFirst();

            stateSet = states.get(current);

            HashMap<Character, Pair<NFM.StateSet, Set<Action>>> transactions = nfm.getSateSetsBySymbols(stateSet);

            for (Map.Entry<Character, Pair<NFM.StateSet, Set<Action>>> entry: transactions.entrySet()) {
                stateSet = nfm.getEpsilonClosure(entry.getValue().first());

                State next = null;
                for (Map.Entry<State, NFM.StateSet> iterator: states.entrySet()) {
                    if (iterator.getValue().equals(stateSet)) {
                        next = iterator.getKey();
                        break;
                    }
                }

                if (next == null) {
                    next = new State();
                    queue.addLast(next);
                    states.put(next, stateSet);
                    if (nfm.isEndState(stateSet)) {
                        ends.add(next);
                    }
                }

                current.addTransaction(entry.getKey(), next, entry.getValue().second());

            }

        }

    }

    public DFM minimization() {
        Set<Set<State>> stateSets = new HashSet<>();

        Set<State> ends = new HashSet<>(this.ends);
        Set<State> states = getStates();
        states.removeAll(ends);
        stateSets.add(states);
        stateSets.add(ends);

        boolean isNotEnd = true;
        while (isNotEnd) {
            Set<Set<State>> previous = new HashSet<>(stateSets);

            for (Set<State> stateSet: previous) {
                LinkedList<State> queue = new LinkedList<>(stateSet);
                while (!queue.isEmpty()) {
                    State state1 = queue.getFirst();
                    queue.removeFirst();

                    LinkedList<State> others = new LinkedList<>(stateSet);
                    others.remove(state1);

                    while (!others.isEmpty()) {
                        State state2 = others.getFirst();
                        others.remove(state2);

                        LinkedHashMap<Character, Pair<State, Set<Action>>> transactions1 =
                                state1.getTransactions();

                        LinkedHashMap<Character, Pair<State, Set<Action>>> transactions2 =
                                state2.getTransactions();

                        boolean isOneGroup = true;
                        for (Map.Entry<Character, Pair<State, Set<Action>>> entry: transactions1.entrySet()) {
                            if (!transactions2.containsKey(entry.getKey())) {
                                isOneGroup = false;
                                break;
                            }

                            State next1 = entry.getValue().first();
                            State next2 = transactions2.get(entry.getKey()).first();

                            if (getSetState(stateSets, next1) != getSetState(stateSets, next2)) {
                                isOneGroup = false;
                                break;
                            }

                        }

                        if (!isOneGroup) {
                            queue.remove(state2);
                            Set<State> newStateSet = new HashSet<>();
                            newStateSet.add(state2);
                            stateSets.add(newStateSet);
                            stateSet.remove(state2);
                        }

                    }

                }

            }

            if (previous.size() != stateSets.size()) {
                continue;
            }

            isNotEnd = false;
            for (Set<State> stateSet: stateSets) {
                if (!previous.contains(stateSet)) {
                    isNotEnd = true;
                    break;
                }
            }

        }

        HashMap<Set<State>, State> newStates = new HashMap<>();
        State newStart = new State();
        HashSet<State> newEnds = new HashSet<>();
        for (Set<State> stateSet: stateSets) {
            State state;
            if (isStart(stateSet)) {
                state = newStart;
            } else {
                state = new State();
            }

            if (isEnd(stateSet)) {
                newEnds.add(state);
            }

            newStates.put(stateSet, state);
        }

        for (Set<State> stateSet: stateSets) {
            State state = newStates.get(stateSet);
            getTransactions(stateSet, stateSets).forEach((character, setSetPair) ->
                    state.addTransaction(character,
                            newStates.get(setSetPair.first()),
                            setSetPair.second()));
        }

        return new DFM(newStart, newEnds);
    }

    private Set<State> getSetState(Set<Set<State>> stateSets, State state) {
        for (Set<State> stateSet: stateSets) {
            if (stateSet.contains(state)) {
                return stateSet;
            }
        }
        return null;
    }

    private boolean isStart(Set<State> stateSet) {
        return stateSet.contains(start);
    }

    private boolean isEnd(Set<State> stateSet) {
        for (State state: stateSet) {
            if (ends.contains(state)) {
                return true;
            }
        }
        return false;
    }

    private HashMap<Character, Pair<Set<State>, Set<Action>>> getTransactions(Set<State> states, Set<Set<State>> stateSets) {
        HashMap<Character, Pair<Set<State>, Set<Action>>> result = new HashMap<>();
        for (State state: states) {
            state.getTransactions().forEach((character, stateSetPair) -> {
                if (!result.containsKey(character)) {
                    result.put(character,
                            new Pair<>(getSetState(stateSets, stateSetPair.first()),
                                    stateSetPair.second()));
                } else {
                    result.get(character).second().addAll(stateSetPair.second());
                }
            });
        }
        return result;
    }

    public boolean enterStartState() {
        current = start;
        return (current != null);
    }

    public boolean nextStage(char symbol,
                             Pair<Integer, HashMap<Integer, Pair<Pair<Integer, Integer>, Boolean>>> data) {
        Pair<State, Set<Action>> pair = current.getNextState(symbol);
        if (pair == null) {
            return false;
        }
        current = pair.first();
        pair.second().forEach(action -> action.run(data));
        return true;
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
                Set<Map.Entry<Character, Pair<State, Set<Action>>>> states2 = state2.getTransactions().entrySet();
                for(Map.Entry<Character, Pair<State, Set<Action>>> otherEntry: states2) {
                    char otherSymbol = otherEntry.getKey();
                    State otherState = otherEntry.getValue().first();
                    Set<Action> actions = otherEntry.getValue().second();
                    State next = states.get(new Pair<State, State>(null, otherState));
                    entry.getValue().addTransaction(otherSymbol, next, actions);

                }
                continue;
            }

            Set<Map.Entry<Character, Pair<State, Set<Action>>>> states1 = state1.getTransactions().entrySet();
            if (state2 == null) {
                for(Map.Entry<Character, Pair<State, Set<Action>>> thisEntry: states1) {
                    char thisSymbol = thisEntry.getKey();
                    State thisState = thisEntry.getValue().first();
                    Set<Action> actions = thisEntry.getValue().second();
                    State next = states.get(new Pair<State, State>(thisState, null));
                    entry.getValue().addTransaction(thisSymbol, next, actions);

                }
                continue;
            }

            for (Map.Entry<Character, Pair<State, Set<Action>>> thisEntry: states1) {
                char thisSymbol = thisEntry.getKey();
                State thisState = thisEntry.getValue().first();

                Pair<State, Set<Action>> pair = state2.getNextState(thisSymbol);
                State otherState;
                Set<Action> actions;
                if (pair == null) {
                    otherState = null;
                    actions = new HashSet<>();
                } else {
                    otherState = state2.getNextState(thisSymbol).first();
                    actions = thisEntry.getValue().second();
                }

                State next = states.get(new Pair<>(thisState, otherState));
                entry.getValue().addTransaction(thisSymbol, next, actions);

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

            for (Pair<State, Set<Action>> pair: current.getTransactions().values()) {
                State next = pair.first();
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
            LinkedHashMap<Character, Pair<State, Set<Action>>> transactions = current.getTransactions();

            outgoingTransactions.put(current, new LinkedHashMap<>());

            for (Map.Entry<Character, Pair<State, Set<Action>>> transaction: transactions.entrySet()) {
                State next = transaction.getValue().first();
                StringBuilder regex = new StringBuilder();
                regex.append(transaction.getKey());

                if (next == current) {
                    if (!stateTransaction.containsKey(current)) {
                        stateTransaction.put(current, regex);
                    } else {
                        stateTransaction.get(current).append("|").append(regex);
                    }
                    continue;
                }

                outgoingTransactions.get(current).put(next, regex);

                if (!incomingTransactions.containsKey(next)) {
                    incomingTransactions.put(next, new LinkedHashMap<>());
                }
                incomingTransactions.get(next).put(current, regex);

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

            incomingTransactions.remove(state);
            outgoingTransactions.remove(state);
            for (Map.Entry<State, LinkedHashMap<State, StringBuilder>> entry:
                    incomingTransactions.entrySet()) {
                entry.getValue().remove(state);
            }
            for (Map.Entry<State, LinkedHashMap<State, StringBuilder>> entry:
                    outgoingTransactions.entrySet()) {
                entry.getValue().remove(state);
            }
            queue.removeFirst();
        }

        for (Map.Entry<State, StringBuilder> entry: outgoingTransactions.get(start).entrySet()) {
            if (!outgoingTransactions.get(entry.getKey()).isEmpty()) {
                queue.addLast(entry.getKey());
            }
        }

        HashSet<State> saved = new HashSet<>();
        while (!queue.isEmpty()){
            State state = queue.getFirst();
            queue.removeFirst();
            saved.add(state);
            ends.remove(state);

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


            incomingTransactions.remove(state);
            outgoingTransactions.remove(state);
            for (Map.Entry<State, LinkedHashMap<State, StringBuilder>> entry:
                    incomingTransactions.entrySet()) {
                entry.getValue().remove(state);
            }
            for (Map.Entry<State, LinkedHashMap<State, StringBuilder>> entry:
                    outgoingTransactions.entrySet()) {
                entry.getValue().remove(state);
            }

            for (Map.Entry<State, StringBuilder> entry: outgoingTransactions.get(start).entrySet()) {
                if (!outgoingTransactions.get(entry.getKey()).isEmpty()
                        && !queue.contains(entry.getKey())) {
                    queue.addLast(entry.getKey());
                }
            }
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
                startRegex.append("(").append(stateTransaction.get(start)).append(")");
            }

            if (!startRegex.isEmpty()) {
                regex.add(startRegex.toString().concat("{0,}"));
            }

            startRegex.append("|");

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

            ends.add(start);
        }

        ends.addAll(saved);

        return new Pair<>(RegexType.REGEX, regex.toString());
    }

    private Pair<Integer, String> stateToString(State state, int current, HashMap<State, Integer> states) {
        StringBuilder result = new StringBuilder();
        result.append(states.get(state))
                .append(":");

        for (Map.Entry<Character, Pair<State, Set<Action>>> entry: state.getTransactions().entrySet()) {
            result.append(" (");
            if (states.containsKey(entry.getValue().first())) {
                result.append("(")
                        .append(entry.getKey())
                        .append(", ")
                        .append(states.get(entry.getValue().first()).toString())
                        .append(")");
            } else {
                result.append("(")
                        .append(entry.getKey())
                        .append(", ")
                        .append(current)
                        .append(")");
                states.put(entry.getValue().first(), current);
                current++;
            }
            if (!entry.getValue().second().isEmpty()) {
                result.append(", Action: ").append(entry.getValue().second().size());
            }
            result.append(")");
        }

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
                return "0: Is end\n";
            }
            return "0:\n";
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

            for (Pair<State, Set<Action>> pair: current.getTransactions().values()) {
                State next = pair.first();
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

        private final LinkedHashMap<Character, Pair<State, Set<Action>>> transactions = new LinkedHashMap<>();

        public LinkedHashMap<Character, Pair<State, Set<Action>>> getTransactions() {
            return transactions;
        }

        public void addTransaction(Character character, State state, Set<Action> action) {
            transactions.put(character, new Pair<>(state, action));
        }

        public Pair<State, Set<Action>> getNextState(Character character) {
            return transactions.get(character);
        }

    }

}

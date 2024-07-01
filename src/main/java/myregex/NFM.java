package myregex;

import java.util.*;

record NFM(State start, State end) {

    public static NFM buildFromAst(AST ast) {
        return (NFM) ast.travers((operation, data, children) -> {
            LinkedList<NFM> machines = new LinkedList<>();
            for (Object child : children) {
                machines.add((NFM) child);
            }
            return combineNFA(operation, data, machines);
        });
    }

    @SuppressWarnings("unchecked")
    private static NFM combineNFA(Type operation, Object data, LinkedList<NFM> machines) {
        NFM result;
        try {
            switch (operation) {
                case EPSILON: {
                    result = combineEpsilonNFM();
                    break;
                }
                case SYMBOL: {
                    result = combineSymbolNFM((Character) data);
                    break;
                }
                case DOT: {
                    result = combineDotNFM(machines.getFirst(), machines.getLast());
                    break;
                }
                case PIPE: {
                    result = combinePipeNFM(machines.getFirst(), machines.getLast());
                    break;
                }
                case PLUS: {
                    result = combinePlusNFM(machines.getFirst());
                    break;
                }
                case REPEAT: {
                    result = combineRepeateNFM(machines.getFirst(), (Pair<Integer, Integer>) data);
                    break;
                }
                case GROUP: {
                    result = combineGroupNFM(machines.getFirst(), (int) data);
                    break;
                }
                default:
                    throw new IllegalArgumentException("!!!Undefine type!!!");
            }
        } catch (ClassCastException ex) {
            throw new IllegalArgumentException("!!!Incorrect data!!!");
        }
        return result;
    }

    private static NFM combineDotNFM(NFM machine1, NFM machine2) {
        machine1.end().addTransaction(null, machine2.start());
        return new NFM(machine1.start(), machine2.end());
    }

    private static NFM combineEpsilonNFM() {
        State start = new State();
        State end = new State();
        start.addTransaction(null, end);

        return new NFM(start, end);
    }

    private static NFM combineSymbolNFM(char symbol) {
        State start = new State();
        State end = new State();
        start.addTransaction(symbol, end);

        return new NFM(start, end);
    }

    private static NFM combinePipeNFM(NFM machine1, NFM machine2) {
        State start = new State();
        start.addTransaction(null, machine1.start());
        start.addTransaction(null, machine2.start());
        State end = new State();
        machine1.end().addTransaction(null, end);
        machine2.end().addTransaction(null, end);

        return new NFM(start, end);
    }

    private static NFM combinePlusNFM(NFM machine) {
        State start = new State();
        start.addTransaction(null, machine.start());
        State end = new State();
        machine.end().addTransaction(null, end);
        machine.end().addTransaction(null, machine.start());

        return new NFM(start, end);
    }

    private static NFM combineRepeateNFM(NFM machine, Pair<Integer, Integer> numbers) {
        NFM result = combineEpsilonNFM();
        for (int iterator = 0; iterator < numbers.first(); iterator++) {
            Pair<State, State> clone = clone(machine.start());
            result = combineDotNFM(result, new NFM(clone.first(), clone.second()));
        }

        if (numbers.second() != null) {

            if (numbers.second() < numbers.first()) {
                throw new IllegalArgumentException("!!!Error: second more then first!!!");
            }

            for (int iterator = numbers.first(); iterator < numbers.second(); iterator++) {
                Pair<State, State> clone = clone(machine.start());
                result = combineDotNFM(result,
                        combinePipeNFM(combineEpsilonNFM(),
                                new NFM(clone.first(), clone.second())));
            }

        } else {
            Pair<State, State> clone = clone(machine.start());
            result = combineDotNFM(result,
                    combineStarNFM(new NFM(clone.first(), clone.second())));
        }

        return result;
    }

    private static NFM combineStarNFM(NFM machine) {
        State start = new State();
        start.addTransaction(null, machine.start());
        State end = new State();
        start.addTransaction(null, end);
        machine.end().addTransaction(null, end);
        machine.end().addTransaction(null, machine.start());

        return new NFM(start, end);
    }

    private static NFM combineGroupNFM(NFM machine, int number) {
        State start = new State();
        State end = new State();
        start.addTransaction(null, machine.start());
        machine.end().addTransaction(null, end);
        start.setAction(data -> {
            int position = data.first();
            Pair<Integer, Integer> group = new Pair<>(position, null);

            data.second().remove(number);

            data.second().put(number, group);

        });
        end.setAction(data -> {
            int position = data.first();
            Pair<Integer, Integer> oldGroup = data.second().get(number);
            data.second().remove(number);

            Pair<Integer, Integer> group = new Pair<>(oldGroup.first(), position);
            data.second().put(number, group);

        });
        return new NFM(start, end);
    }

    private static Pair<State, State> clone(State state) {
        return clone(state, new HashMap<>());
    }
    private static Pair<State, State> clone(State state, HashMap<State, State> passedStates) {
        State newState = new State();
        newState.setAction(state.getAction());
        passedStates.put(state, newState);

        Transaction transaction = state.getTransaction();

        boolean isEnd = (transaction == null) &&
                state.getEpsilonTransactions().isEmpty();
        if (isEnd) {
            return new Pair<>(newState, newState);
        }

        State end = null;
        if (transaction != null) {
            State next;
            if (!passedStates.containsKey(transaction.state())) {
                Pair<State, State> result = clone(transaction.state(), passedStates);
                if (result.second() != null) {
                    end = result.second();
                }
                next = result.first();
            } else {
                next = passedStates.get(transaction.state());
            }
            newState.addTransaction(transaction.symbol(), next);
        }

        for (State current : state.getEpsilonTransactions()) {
            State next;
            if (!passedStates.containsKey(current)) {
                Pair<State, State> result = clone(current, passedStates);
                if (result.second() != null) {
                    end = result.second();
                }
                next = result.first();
            } else {
                next = passedStates.get(current);
            }
            newState.addTransaction(null, next);
        }

        return new Pair<>(newState, end);
    }

    private Pair<Integer, String> stateToString(State state, int current, HashMap<State, Integer> states) {
        StringBuilder result = new StringBuilder();
        result.append(states.get(state))
                .append(":");
        if (state.getTransaction() != null) {
            State next = state.getTransaction().state();
            if (states.containsKey(next)) {
                result.append(" (")
                        .append(state.getTransaction().symbol())
                        .append(", ")
                        .append(states.get(next).toString())
                        .append(")");
            } else {
                result.append(" (")
                        .append(state.getTransaction().symbol())
                        .append(", ")
                        .append(current)
                        .append(")");
                states.put(next, current);
                current++;
            }
        }

        for (State next : state.getEpsilonTransactions()) {
            if (states.containsKey(next)) {
                result.append(" (".concat(states.get(next).toString()))
                        .append(")");
            } else {
                result.append(" (").append(current).append(")");
                states.put(next, current);
                current++;
            }
        }

        if (state.getAction() != null) {
            result.append(" ").append("Action");
        }

        result.append("\n");

        return new Pair<>(current, result.toString());
    }

    @Override
    public String toString() {
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

            if (current.getTransaction() != null) {
                State next = current.getTransaction().state();
                boolean isNewState = !passedStates.contains(next)
                        && !queue.contains(next);
                if (isNewState) {
                    queue.addLast(next);
                }
            }

            for (State next : current.getEpsilonTransactions()) {
                boolean isNewState = !passedStates.contains(next)
                        && !queue.contains(next);
                if (isNewState) {
                    queue.addLast(next);
                }
            }
        }

        return result.toString();
    }

    private static class State {

        private Transaction transaction = null;
        private final Set<State> epsilonTransactions = new LinkedHashSet<>();

        public Action action = null;

        public void setAction(Action action) {
            this.action = action;
        }

        public Action getAction() {
            return action;
        }

        public void addTransaction(Character symbol, State state) {
            boolean isEpsilon = (symbol == null);
            if (isEpsilon) {
                epsilonTransactions.add(state);
            } else {
                transaction = new Transaction(symbol, state);
            }
        }

        public Set<State> getEpsilonTransactions() {
            return epsilonTransactions;
        }

        public Transaction getTransaction() {
            return transaction;
        }

    }

    private StateSet getEpsilonClosure(State state) {
        StateSet result = new StateSet();
        LinkedList<State> queue = new LinkedList<>();
        queue.addLast(state);

        while (!queue.isEmpty()) {
            State current = queue.getFirst();
            queue.removeFirst();
            result.states.add(current);

            for (State iterator: current.getEpsilonTransactions()) {
                if (!result.states.contains(iterator)) {
                    queue.addLast(iterator);
                }
            }

        }

        return result;
    }

    public StateSet getEpsilonClosureForStart() {
        return getEpsilonClosure(start);
    }

    public StateSet getEpsilonClosure(StateSet stateSet) {
        StateSet result = new StateSet();
        for (State state: stateSet.states) {
            result.states.addAll(getEpsilonClosure(state).states);
        }
        return result;
    }

    public HashMap<Character, StateSet> getSateSetsBySymbols(StateSet stateSet) {
        HashMap<Character, StateSet> result = new HashMap<>();

        for (State state: stateSet.states) {
            Transaction transaction = state.getTransaction();
            boolean transactionExist = (transaction != null);
            if (transactionExist) {
                char symbol = transaction.symbol();
                if (result.containsKey(symbol)) {
                    result.get(symbol).states.add(transaction.state());
                } else {
                    StateSet states = new StateSet();
                    states.states.add(transaction.state());
                    result.put(symbol, states);
                }
            }
        }

        return result;
    }

    public boolean isEndState(StateSet stateSet) {
        for (State state: stateSet.states) {
            if (state == end) {
                return true;
            }
        }
        return false;
    }

    public LinkedHashSet<Action> getActions(StateSet stateSet) {
        LinkedHashSet<Action> resul = new LinkedHashSet<>();
        for (State state: stateSet.states) {
            boolean hasAction = (state.getAction() != null);
            if (hasAction) {
                resul.addLast(state.getAction());
            }
        }
        return resul;
    }

    private record Transaction(char symbol, State state) {}

    public static class StateSet {
        Set<State> states = new HashSet<>();

        StateSet() {}

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof StateSet stateSet) {

                if (states.size() != stateSet.states.size()) {
                    return false;
                }

                for (State state : states) {
                    if (!stateSet.states.contains(state)) {
                        return false;
                    }
                }

                return true;
            }

            throw new IllegalArgumentException("!!!Error: argument is not StateSet!!!");
        }
    }

}

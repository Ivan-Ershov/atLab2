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

    private static Pair<State, State> clone(State state) {
        return clone(state, new HashMap<>());
    }
    private static Pair<State, State> clone(State state, HashMap<State, State> passedStates) {
        State newState = new State();
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
            if (!passedStates.containsKey(transaction.getState())) {
                Pair<State, State> result = clone(transaction.getState(), passedStates);
                if (result.second() != null) {
                    end = result.second();
                }
                next = result.first();
            } else {
                next = passedStates.get(transaction.getState());
            }
            newState.addTransaction(transaction.getSymbol(), next, transaction.getActions());
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
        Action start = data -> {
            int position = data.first();

            Pair<Pair<Integer, Integer>, Boolean> group;
            if (data.second().containsKey(number)) {
                Pair<Pair<Integer, Integer>, Boolean> oldGroup = data.second().get(number);
                group = new Pair<>(
                        new Pair<>(position, oldGroup.first().second()),
                        oldGroup.second());
            } else {
                group = new Pair<>(
                        new Pair<>(position, null),
                        false);
            }

            data.second().remove(number);
            data.second().put(number, group);

        };

        Set<Transaction> transactions = machine.getTransactions(
                machine.getEpsilonClosure(machine.start()));
        transactions.forEach(transaction -> transaction.addAction(start));

        Action end = data -> {
            int position = data.first() + 1;

            Pair<Pair<Integer, Integer>, Boolean> group;
            if (data.second().containsKey(number)) {
                Pair<Pair<Integer, Integer>, Boolean> oldGroup = data.second().get(number);
                group = new Pair<>(
                        new Pair<>(oldGroup.first().first(), position),
                        true);
            } else {
                group = new Pair<>(
                        new Pair<>(null, position),
                        true);
            }

            data.second().remove(number);
            data.second().put(number, group);

        };

        Set<State> states = machine.getAllStates();
        states.forEach(state -> {
            Transaction transaction = state.getTransaction();
            if (transaction != null) {
                StateSet stateSet = machine.getEpsilonClosure(transaction.getState());
                if (machine.isEndState(stateSet)) {
                    transaction.addAction(end);
                }
            }
        });

        return machine;
    }

    private Set<State> getAllStates() {
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

            boolean transactionExists = (current.getTransaction() != null);
            if (transactionExists) {
                State next = current.getTransaction().getState();
                boolean isNewState = ((!states.contains(next)) &&
                        (!queue.contains(next)));
                if (isNewState) {
                    queue.addLast(next);
                }
            }

            for (State next : current.getEpsilonTransactions()) {
                boolean isNewState = ((!states.contains(next)) &&
                        (!queue.contains(next)));
                if (isNewState) {
                    queue.addLast(next);
                }
            }

        }

        return states;
    }

    private Pair<Integer, String> stateToString(State state, int current, HashMap<State, Integer> states) {
        StringBuilder result = new StringBuilder();
        result.append(states.get(state))
                .append(":");

        if (state.getTransaction() != null) {

            result.append(" (")
                    .append("(")
                    .append(state.getTransaction().getSymbol())
                    .append(", ");

            State next = state.getTransaction().getState();
            if (states.containsKey(next)) {
                result.append(states.get(next).toString());
            } else {
                result.append(current);
                states.put(next, current);
                current++;
            }

            result.append(")");

            if (!state.getTransaction().getActions().isEmpty()) {
                result.append(", Actions: ")
                        .append(state.getTransaction().getActions().size());
            }

            result.append(")");

        }

        for (State next : state.getEpsilonTransactions()) {
            result.append(" (");

            if (states.containsKey(next)) {
                result.append(states.get(next).toString());
            } else {
                result.append(current);
                states.put(next, current);
                current++;
            }

            result.append(")");

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
                State next = current.getTransaction().getState();
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

    public HashMap<Character, Pair<StateSet, Set<Action>>> getSateSetsBySymbols(StateSet stateSet) {
        HashMap<Character, Pair<StateSet, Set<Action>>> result = new HashMap<>();

        for (State state: stateSet.states) {
            Transaction transaction = state.getTransaction();
            boolean transactionExists = (transaction != null);
            if (transactionExists) {
                char symbol = transaction.getSymbol();
                if (result.containsKey(symbol)) {
                    result.get(symbol).first().states.add(transaction.getState());
                    result.get(symbol).second().addAll(transaction.getActions());
                } else {
                    StateSet states = new StateSet();
                    Set<Action> actions = new HashSet<>(transaction.getActions());
                    states.states.add(transaction.getState());
                    result.put(symbol, new Pair<>(states, actions));
                }
            }
        }

        return result;
    }

    private Set<Transaction> getTransactions(StateSet stateSet) {
        Set<Transaction> transactions = new HashSet<>();

        for (State state: stateSet.states) {
            Transaction transaction = state.getTransaction();
            boolean transactionExists = (transaction != null);
            if (transactionExists) {
                transactions.add(transaction);
            }
        }

        return transactions;
    }

    public boolean isEndState(StateSet stateSet) {
        for (State state: stateSet.states) {
            if (state == end) {
                return true;
            }
        }
        return false;
    }

    private static class State {

        private Transaction transaction = null;
        private final Set<State> epsilonTransactions = new LinkedHashSet<>();

        public void addTransaction(Character symbol, State state, Set<Action> actions) {
            boolean isEpsilon = (symbol == null);
            if (isEpsilon) {
                epsilonTransactions.add(state);
            } else {
                transaction = new Transaction(symbol, state, actions);
            }
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

    private static class Transaction {
        private final char symbol;
        private final State state;

        private final Set<Action> actions;

        private Transaction(char symbol, State state) {
            this.symbol = symbol;
            this.state = state;
            actions = new LinkedHashSet<>();
        }

        private Transaction(char symbol, State state, Set<Action> actions) {
            this.symbol = symbol;
            this.state = state;
            this.actions = actions;
        }

        public Set<Action> getActions() {
            return actions;
        }

        public void addAction(Action action) {
            actions.add(action);
        }

        public State getState() {
            return state;
        }

        public char getSymbol() {
            return symbol;
        }

    }

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

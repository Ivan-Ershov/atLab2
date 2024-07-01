package myregex;

import java.util.HashMap;

@FunctionalInterface
interface Action {
    void run(Pair<Integer, HashMap<Integer, Pair<Pair<Integer, Integer>, Boolean>>> data);
}

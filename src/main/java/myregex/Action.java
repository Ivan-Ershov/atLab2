package myregex;

import java.util.HashMap;

@FunctionalInterface
interface Action {
    void run(Pair<Integer, HashMap<Integer, Pair<Integer, Integer>>> data);
}

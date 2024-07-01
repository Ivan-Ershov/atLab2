package myregex;

import java.util.Objects;

public record Pair<T, V>(T first, V second) {
    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) object;
        return Objects.equals(first, pair.first) && Objects.equals(second, pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

}

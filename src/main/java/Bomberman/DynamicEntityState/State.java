package Bomberman.DynamicEntityState;

import java.util.HashMap;
import java.util.Map;

public enum State {
    UP(0), RIGHT(1), DOWN(2), LEFT(3), STOP(4), DIE(5);
    private final int value;
    private static final Map map = new HashMap<>();

    State(int value) {
        this.value = value;
    }

    static {
        for (State pageType : State.values()) {
            map.put(pageType.value, pageType);
        }
    }

    public static State valueOf(int x) {
        return (State) map.get(x);
    }

    public int getValue() {
        return value;
    }
}

package cz.mendelu.vui2.agents.gui;

public enum StateOfCoor {
    WALL('w'),
    FREE('f'),
    DIRTY('d'),
    CLEAN('c'),
    HOME('h');
    public final char code;

    StateOfCoor(char code) {
        this.code = code;
    }
}
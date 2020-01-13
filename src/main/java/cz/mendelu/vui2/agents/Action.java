package cz.mendelu.vui2.agents;

public enum Action {

    FORWARD('f'),
    TURN_LEFT('l'),
    TURN_RIGHT('r'),
    CLEAN('c'),
    TURN_OFF('t');

    public final char code;

    Action(char code){
        this.code = code;
    }


}

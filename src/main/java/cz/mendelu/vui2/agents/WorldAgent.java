package cz.mendelu.vui2.agents;

import com.sun.org.apache.xpath.internal.operations.Bool;
import cz.mendelu.vui2.agents.greenfoot.AbstractAgent;
import cz.mendelu.vui2.agents.gui.StateOfCoor;

import java.util.*;

public class WorldAgent extends AbstractAgent {
    public interface Rule {
        Action match(boolean canMove, boolean dirty, boolean dock, Boolean finished, Boolean wasHere);
    }
    private String actions = "";
    private Integer orientation = 0; //0up, 2down, 3left, 1right
    private Integer globalX = 0;
    private Integer globalY = 0;
    private List<Rule> rules = new ArrayList<>();
    private StateOfCoor stateOfCoor;
    private List<Action> makro1 = new ArrayList<>();
    private Boolean turnAround = false;
    private Boolean timeToGoHome = false;
    private static class Coor {
        public final int x, y;
        public Coor(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Coor coor = (Coor) o;
            return x == coor.x &&
                    y == coor.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

    }


    private HashMap<Coor, StateOfCoor> mapOfWorld = new HashMap<>();

    public WorldAgent() {
        makro1.add(Action.TURN_LEFT);
        makro1.add(Action.TURN_RIGHT);

        rules.add((canMove, dirty, dock, timeToGoHome, wasHere) -> (dirty) ? Action.CLEAN : null);
        rules.add((canMove, dirty, dock, timeToGoHome, wasHere) -> (timeToGoHome) ? this.goHome() : null);

        rules.add((canMove, dirty, dock, timeToGoHome, wasHere) -> (!canMove && !wasHere) ? Action.FORWARD : null);
        rules.add((canMove, dirty, dock, timeToGoHome, wasHere) -> (wasHere) ? Action.TURN_LEFT : null);


        rules.add((canMove, dirty, dock, timeToGoHome, wasHere) -> (canMove) ? this.randomTurn() : null);
        rules.add((canMove, dirty, dock, timeToGoHome, wasHere) -> (dock) ? Action.TURN_OFF : null);

//        rules.add((canMove, dirty, dock, action, mapOfWorld) -> (mapOfWorld.get()) ? Action.TURN_OFF : null);
    }

    @Override
    public Action doAction(boolean canMove, boolean dirty, boolean dock) {
        for (Rule rule: rules) {

//            if (actions.length() > 499 && !turnAround) {
//                actions += 'l';
//                actions += 'l';
//                timeToGoHome = true;
//                turnAround = true;
//            }
            // 470 because of CLEANing... in map 20x2 is lot of trash
            // higher number wont work in this map
            if (actions.length() >= 470) {
                if (!timeToGoHome) {
                    actions += 'l';
                    actions += 'l';
                    System.out.println(actions.length() + " " + actions);
                }
                timeToGoHome = true;
                turnAround = true;
            }
            Action action = rule.match(canMove, dirty, dock, timeToGoHome, canContinue());

            if (action != null) {
                if (!timeToGoHome) {
                    this.updateCurrentPosition();
                    this.saveForwardPosition(canMove);
                    this.setAtrributes(action);

                    if (action != Action.CLEAN) {
                        actions += action.code;
                    }
                }
                return action;
            }
        }
        return Action.TURN_OFF;
    }

    // function for deciding if robot can continue forward
    public Boolean canContinue() {
        Coor coor = getForwardCoor();
        System.out.println("can continue? :" + mapOfWorld.containsKey(coor));
        return mapOfWorld.containsKey(coor) && mapOfWorld.get(coor) == StateOfCoor.CLEAN;
    }

    // deals with random choice of turn left or right
    Action randomTurn() {
        return makro1.get(new Random().nextInt(makro1.size()));
    }

    // this function should save current position
    public void updateCurrentPosition() {
        savePosition(globalX, globalY, StateOfCoor.CLEAN);
        System.out.println("Current position:");
        System.out.println("["+globalX+","+globalY+"] is " + mapOfWorld.get(new Coor(globalX, globalY)));
    }

    // this function should save position in front of the robot
    public void saveForwardPosition(Boolean canMove) {
       Coor coor = getForwardCoor();
        if (canMove) {
            stateOfCoor = StateOfCoor.WALL;
        } else {
            stateOfCoor = StateOfCoor.FREE;
        }
        savePosition(coor.x, coor.y, stateOfCoor);
        System.out.println("Forward position:");
        System.out.println("["+coor.x+","+coor.y+"] : " + mapOfWorld.get(coor));
        System.out.println("-------------------------------");
    }

    // get coordinates for forward position
    public Coor getForwardCoor() {
        Integer localX = globalX;
        Integer localY = globalY;
        if (orientation == 0) {
            localY++;
        } else if (orientation == 1) {
            localX++;
        } else if (orientation == 2) {
            localY--;
        } else {
            localX--;
        }
        return new Coor(localX, localY);
    }

    // save to hashmap
    public void savePosition(Integer x, Integer y, StateOfCoor state) {
        mapOfWorld.put(new Coor(x, y), state);
    }

    // set or update position/coor attributes for next action, if orientation changes it is ok
    // because forward position has to be saved
    public void setAtrributes(Action action) {
        switch (action) {
            case FORWARD:
                if (orientation == 0) {
                    this.globalY++;
                } else if (orientation == 1) {
                    this.globalX++;
                } else if (orientation == 2) {
                    this.globalY--;
                } else {
                    this.globalX--;
                }
                break;
            case TURN_LEFT:
                if (this.orientation == 0) {
                    this.orientation = 3;
                } else {
                    this.orientation--;
                }
                break;
            case TURN_RIGHT:
                if (this.orientation == 3) {
                    this.orientation = 0;
                } else {
                    this.orientation++;
                }
                break;
        }
    }

    // get Home with the same path
    public Action goHome() {
        if (this.actions.length() == 0) { return Action.TURN_OFF; }
        char lastLetter = this.actions.charAt(this.actions.length() - 1);
        this.actions = this.removeLastChar(actions);
        Action action = Action.TURN_OFF;
        switch (lastLetter) {
            case 'f': action = Action.FORWARD; break;
            case 'l': action = Action.TURN_RIGHT; break;
            case 'r': action = Action.TURN_LEFT; break;
            case 't': action = Action.TURN_OFF; break;
        }
        return action;
    }

    // some static func to delete last char
    private static String removeLastChar(String str) {
        return str.substring(0, str.length() - 1);
    }
}


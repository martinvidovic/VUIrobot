package cz.mendelu.vui2.agents;

import cz.mendelu.vui2.agents.greenfoot.AbstractAgent;
import cz.mendelu.vui2.agents.gui.StateOfCoor;

import javax.xml.stream.Location;
import java.util.*;

public class GoalAgent extends AbstractAgent {
    public interface Rule {
        Action match(boolean canMove, boolean dirty, boolean dock, boolean finished, boolean wasHere, boolean turningAround);
    }

    private String actions = "";
    private Integer orientation = 0; //0up, 2down, 3left, 1right
    private Integer globalX = 0;
    private Integer globalY = 0;
    private List<Rule> rules = new ArrayList<>();

    private List<Action> makro1 = new ArrayList<>();
    private Boolean timeToGoHome = false;

    private Integer rotatingCount = 0;
    private boolean rotatingStatus = false;
    private boolean rotatingStarted = false;

    private LocationData leftLocationData;
    private LocationData rightLocationData;
    private LocationData currentLocationData;
    private LocationData forwardLocationData;
    private Integer localOrientation;
    private Integer stillTurning = 0;

    private HashMap<Coor, LocationData> mapOfWorld = new HashMap<>();

    public GoalAgent(){
        makro1.add(Action.TURN_LEFT);
        makro1.add(Action.TURN_RIGHT);

        rules.add((canMove, dirty, dock, timeToGoHome, wasHere, turningAround) -> (dirty) ? Action.CLEAN : null);


        rules.add((canMove, dirty, dock, timeToGoHome, wasHere, turningAround) -> (stuckedRobot()) ? dontBeStucked(canMove) : null);
        rules.add((canMove, dirty, dock, timeToGoHome, wasHere, turningAround) -> (turningAround) ? this.rotatingTurn(canMove) : null);

        rules.add((canMove, dirty, dock, timeToGoHome, wasHere, turningAround) -> (!canMove) ? Action.FORWARD : null);
//        rules.add((canMove, dirty, dock, timeToGoHome, wasHere, turningAround) -> (!canMove && !wasHere) ? Action.FORWARD : null);



        rules.add((canMove, dirty, dock, timeToGoHome, wasHere, turningAround) -> (dock) ? Action.TURN_OFF : null);

    }

    @Override
    public Action doAction(boolean canMove, boolean dirty, boolean dock) {

        for (Rule rule: rules) {
            Action action = rule.match(canMove, dirty, dock, timeToGoHome, wasHere(), observingSurroundings(canMove, wasHere()));
            if (action != null) {
                if (!timeToGoHome) {
                    this.updateCurrentPosition(canMove, dock);
                    this.saveForwardPosition(canMove, dock);
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

    boolean observingSurroundings(boolean canMove, boolean wasHere) {
        if ((canMove || wasHere) && !this.rotatingStarted) {
            rotatingStatus = true;
            rotatingStarted = true;
        }
        if (rotatingStatus && rotatingCount == 0 && rotatingStarted) {
            rotatingCount += 1;
        }
        return rotatingStatus;
    }

    // deals with random choice of turn left or right
    Action randomTurn() {
        return makro1.get(new Random().nextInt(makro1.size()));
    }

    //rotate robot for getting info around it
    Action rotatingTurn(boolean canMove) {
        //rotate to left
        if (this.rotatingCount == 1) {
            localOrientation = this.orientation;
            this.rotatingCount += 1; //robot is turned left
            return Action.TURN_LEFT;
        }
        //rotate back to middle
        if (this.rotatingCount == 2) {
            this.rotatingCount += 1; //robot is turned back in middle
            return Action.TURN_RIGHT;
        }
        //rotate to right
        if (this.rotatingCount == 3) {
            this.rotatingCount = 0;
            this.rotatingStatus = false;
            this.rotatingStarted = false;
            return Action.TURN_RIGHT;
        }
        return decideTurning(canMove);
    }
    // after finished rotating, action must be choosed, where to go
    public Action decideTurning(boolean canMove) {
        //if stucked, go away
        if (stuckedRobot()) { return dontBeStucked(canMove); }
        // comparing LEFT postion with RIGHT position, if RIGHT position is better -> go forward because direction is correct
        // else turning begins...
        if (leftLocationData.getNumberOfVisits().equals(rightLocationData.getNumberOfVisits())) {
            return Action.FORWARD;
        } else if (leftLocationData.getNumberOfVisits() > rightLocationData.getNumberOfVisits()) {
            return Action.FORWARD;
        } else {
            //rotating back to the LEFT
            if (stillTurning != 2) {
                this.stillTurning += 1;
                return Action.TURN_LEFT;
            } else {
                // rotated so go FORWARD
                stillTurning = 0;
                this.rotatingStatus = false;
                return Action.FORWARD;
            }
        }
    }

    // this function should save current position
    public void updateCurrentPosition(boolean canMove, boolean dock) {
        LocationData locationData = mapOfWorld.get(new Coor(globalX,globalY));
        if (locationData != null) {
            locationData.increaseNumberOfVisits();
            locationData.setWall(canMove);
            locationData.setDock(dock);
            locationData.setDiscovered(true);
        } else {
            locationData = new LocationData(canMove, 1, dock);
        }
        currentLocationData = locationData;
        System.out.println("Current position:");
        savePosition(globalX, globalY, locationData);
    }

    // this function should save position in front of the robot
    public void saveForwardPosition(Boolean canMove, boolean dock) {
        Coor forwardCoor = getForwardCoor(globalX,globalY);
        LocationData locationData = mapOfWorld.get(forwardCoor);

        if (locationData != null) {
            if (canMove) {
                locationData.setWall(true);
            } else {
                locationData.setWall(false);
            }
            if (rotatingCount == 1) {
                leftLocationData = locationData;
            } else if (rotatingCount == 3) {
                rightLocationData = locationData;
            }
        } else {
            locationData = new LocationData();
        }
        currentLocationData = locationData;
        System.out.println("Forward position:");
        savePosition(forwardCoor.x, forwardCoor.y, locationData);
        System.out.println("-------------------------------");
        System.out.println("");
    }

    // save to hashmap
    public void savePosition(Integer x, Integer y, LocationData locationData) {
        System.out.println("["+x+","+y+"] : " + locationData.toString());
        mapOfWorld.put(new Coor(x, y), locationData);
    }
    //check if robot was already in forward position
    public Boolean wasHere() {
        Coor coor = getForwardCoor(globalX, globalY);
        LocationData forwardLocation = mapOfWorld.get(coor);
        if (forwardLocation != null) {
            return forwardLocation.getNumberOfVisits() > 0;
        }
        return false;
    }
    // robot is rotating more then usuall
    public boolean stuckedRobot() {
        LocationData locationData = mapOfWorld.get(new Coor(globalX, globalY));
        if (locationData != null) {
            if (locationData.getNumberOfVisits() > 4) {
                return true;
            }
        }
        return false;
    }
    // get robot out of too much rotating
    public Action dontBeStucked(boolean canMove) {
        if (canMove) {
            return randomTurn();
        }
        return Action.FORWARD;
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

    // get coordinates for forward position
    public Coor getForwardCoor(Integer currentX, Integer currentY) {
        Integer localX = currentX;
        Integer localY = currentY;
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
}

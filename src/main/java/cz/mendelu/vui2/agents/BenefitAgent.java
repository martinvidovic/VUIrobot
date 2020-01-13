package cz.mendelu.vui2.agents;

import cz.mendelu.vui2.agents.greenfoot.AbstractAgent;

import java.util.*;

public class BenefitAgent extends AbstractAgent {
    public interface Rule {
        Action match(boolean canMove, boolean dirty, boolean dock, boolean finished, boolean wasHere, boolean turningAround);
    }

    private Integer orientation = 0; //0up, 2down, 3left, 1right
    private Integer globalX = 0;
    private Integer globalY = 0;
    private List<Rule> rules = new ArrayList<>();

    private List<Action> makro1 = new ArrayList<>();
    private Boolean timeToGoHome = false;

    private Integer rotatingCount = 0;
    private boolean rotatingStarted = false;

    private LocationData leftLocationData;
    private LocationData rightLocationData;
    private LocationData currentLocationData;
    private String actions = "";
    private LinkedList<Coor> aStarPath = new LinkedList<Coor>() {
        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean contains(Object o) {
            return false;
        }

        @Override
        public Iterator<Coor> iterator() {
            return null;
        }

        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @Override
        public <T> T[] toArray(T[] ts) {
            return null;
        }

        @Override
        public boolean add(Coor coor) {
            return false;
        }

        @Override
        public boolean remove(Object o) {
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            return false;
        }

        @Override
        public boolean addAll(Collection<? extends Coor> collection) {
            return false;
        }

        @Override
        public boolean addAll(int i, Collection<? extends Coor> collection) {
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> collection) {
            return false;
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            return false;
        }

        @Override
        public void clear() {

        }

        @Override
        public Coor get(int i) {
            return null;
        }

        @Override
        public Coor set(int i, Coor coor) {
            return null;
        }

        @Override
        public void add(int i, Coor coor) {

        }

        @Override
        public Coor remove(int i) {
            return null;
        }

        @Override
        public int indexOf(Object o) {
            return 0;
        }

        @Override
        public int lastIndexOf(Object o) {
            return 0;
        }

        @Override
        public ListIterator<Coor> listIterator() {
            return null;
        }

        @Override
        public ListIterator<Coor> listIterator(int i) {
            return null;
        }

        @Override
        public List<Coor> subList(int i, int i1) {
            return null;
        }
    };

    private HashMap<Coor, LocationData> mapOfWorld = new HashMap<>();

    public BenefitAgent() {
        makro1.add(Action.TURN_LEFT);
        makro1.add(Action.TURN_RIGHT);
//        rules.add((canMove, dirty, dock) -> (shouldReturnToDock()) ? actionWrapper(returnToDock(dock), canMove, dirty) : null);

        rules.add((canMove, dirty, dock, timeToGoHome, wasInFront, turningAround) -> (dirty) ? Action.CLEAN : null);
        rules.add((canMove, dirty, dock, timeToGoHome, wasInFront, turningAround) -> (timeToGoHome) ? goHome(dock) : null);
        rules.add((canMove, dirty, dock, timeToGoHome, wasInFront, turningAround) -> (stuckedRobot()) ? dontBeStucked(canMove) : null);
        rules.add((canMove, dirty, dock, timeToGoHome, wasInFront, turningAround) -> (canMove || wasInFront || turningAround) ? rotateRobot() : null);
        rules.add((canMove, dirty, dock, timeToGoHome, wasInFront, turningAround) -> (!canMove && !wasInFront) ? Action.FORWARD : null);
        rules.add((canMove, dirty, dock, timeToGoHome, wasInFront, turningAround) -> (dock) ? Action.TURN_OFF : null);
    }

    @Override
    public Action doAction(boolean canMove, boolean dirty, boolean dock) {
        for (Rule rule: rules) {
            Action action = rule.match(canMove, dirty, dock, shouldReturnToDock(), wasForward(), isRotating());
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

    public void getAStarPathfinding(){
            AStar astartClass = new AStar(new Coor(globalX, globalY), new Coor(0,0), this.mapOfWorld);
            LinkedList<Coor> optimalPathHome = astartClass.findPath();
            System.out.println("----------------------------------------------------------------A-star started----------------------------------------------------------------");
            this.aStarPath = optimalPathHome;
    }

    public boolean shouldReturnToDock() {
        return this.actions.length() > 850;
    }

    public Action goHome(boolean dock){
        if (dock) {
            return Action.TURN_OFF;
        }
        if (this.aStarPath.size() == 0) {
            getAStarPathfinding();
            this.aStarPath.pop();
        }
        Coor nextCoor = this.aStarPath.pop();
        if (getForwardCoor(globalX, globalY).equals(nextCoor)) {
            return Action.FORWARD;
        } else {
            this.aStarPath.push(nextCoor);
            return rotateToPath(nextCoor);
        }
    }

    Action rotateToPath(Coor nextCoor) {
        if (getLeftCoor(globalX,globalY).equals(nextCoor)) {
            // if on-left
            return Action.TURN_LEFT;
        } else if (getRightCoor(globalX, globalY).equals(nextCoor)) {
            // if on-right
            return Action.TURN_RIGHT;
        } else {
            // if robot has to rotate twice, just turn left and then condition if on-left will be executed next time
            return Action.TURN_LEFT;
        }
    }

    private boolean isRotating() {
        return this.rotatingStarted;
    }
    private void startRotating() {
        this.rotatingStarted = true;
        rotatingCount = 1;
    }
    private void stopRotating() {
        this.rotatingStarted = false;

    }

    //  function called when wall is front of robot, or robot already visited this place
    private Action rotateRobot() {
//        if (!rotatingStarted && rotatingCount == 0) { startRotating(); }
        if (!isRotating() && rotatingCount == 0) {
            startRotating();
        }
        // 0 when is not rotating and 1 for turning left
        if (this.rotatingCount == 1 && rotatingStarted) {
            rotatingCount += 1;
            return Action.TURN_LEFT;
        }
        // 2 when robot is rotated to left and needs to be rotated to right twice
        if (rotatingCount == 2 && rotatingStarted) {
            rotatingCount += 1;
            return Action.TURN_RIGHT;
        }
        // 3 because robot needs to be rotated second time
        if (rotatingCount == 3 && rotatingStarted) {
            rotatingCount += 1;
            return Action.TURN_RIGHT;
        }
        if (rotatingCount == 4 && rotatingStarted) {
            rotatingCount += 1;
            return Action.TURN_LEFT;
        }

        return calculatePath();
    }

    Action randomTurn() {
        return makro1.get(new Random().nextInt(makro1.size()));
    }

    // after rotating, decision has to be made where to go
    private Action calculatePath() {
        //we need to get to this function through rotating, so we turn of it here
        stopRotating();
        //when number of visits are same turn left
        //depending on number of visits -> turn other side
        boolean noWall = !leftLocationData.isWall() && !rightLocationData.isWall();
        if (leftLocationData.getNumberOfVisits() > rightLocationData.getNumberOfVisits() && noWall){
            return Action.TURN_RIGHT;
        } else if (leftLocationData.getNumberOfVisits() <= rightLocationData.getNumberOfVisits() && noWall){
            return Action.TURN_LEFT;
        }

        //if one side is wall, turn another one
        if (rightLocationData.isWall()) {
            return Action.TURN_LEFT;
        }
        if (leftLocationData.isWall()) {
            return Action.TURN_RIGHT;
        }
        return Action.TURN_OFF;
    }

    // robot is rotating more then usuall
    public boolean stuckedRobot() {
        LocationData locationData = getLocationData(new Coor(globalX, globalY));
        if (locationData.getNumberOfVisits() > 4 && !rotatingStarted) {
            return true;
        }
        return false;
    }
    // get robot out of too much rotating
    public Action dontBeStucked(boolean canMove) {
        if (canMove) {
            return Action.TURN_LEFT;
        }
        return Action.FORWARD;
    }

    // getter based on X,Y for LocationData
    private LocationData getLocationData(Coor coor) {
        LocationData locationData = mapOfWorld.get(coor);
        if (locationData != null) {
            return locationData;
        }
        return new LocationData();
    }

    // function for checking if robot was already in next position in front of him
    boolean wasForward() {
        LocationData forwardLocation = getLocationData(getForwardCoor(globalX, globalY));
        return forwardLocation.getNumberOfVisits() > 0;
    }

    // this function should save current position
    public void updateCurrentPosition(boolean canMove, boolean dock) {
        LocationData locationData = mapOfWorld.get(new Coor(globalX,globalY));
        if (locationData != null) {
            locationData.increaseNumberOfVisits();
            locationData.setDock(dock);
            locationData.setDiscovered(true);
        } else {
            locationData = new LocationData(1, dock);
        }
        System.out.println("Current position:");
        savePosition(globalX, globalY, locationData);
    }

    // this function should save position in front of the robot
    public void saveForwardPosition(Boolean canMove, boolean dock) {
        Coor forwardCoor = getForwardCoor(globalX,globalY);
        LocationData forwardLocationData = mapOfWorld.get(forwardCoor);

        if (forwardLocationData != null) {
            forwardLocationData.setWall(canMove);
        } else {
            forwardLocationData = new LocationData();
            forwardLocationData.setWall(canMove);
        }

        if (rotatingCount == 3  && isRotating()) {
            leftLocationData = forwardLocationData;
        } else if (rotatingCount == 5 && isRotating()) {
            rotatingCount = 0;
            rightLocationData = forwardLocationData;
        }

        System.out.println("Forward position:");
        savePosition(forwardCoor.x, forwardCoor.y, forwardLocationData);
        System.out.println("-------------------------------");
        System.out.println("");
    }

    // save to hashmap
    public void savePosition(Integer x, Integer y, LocationData locationData) {
        System.out.println("["+x+","+y+"] : " + locationData.toString());
        mapOfWorld.put(new Coor(x, y), locationData);
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
    //0up, 2down, 3left, 1right
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
    // get coordinates for left position
    //0up, 2down, 3left, 1right
    public Coor getLeftCoor(Integer currentX, Integer currentY) {
        Integer localX = currentX;
        Integer localY = currentY;
        if (orientation == 0) {
            localX--;
        } else if (orientation == 1) {
            localY++;
        } else if (orientation == 2) {
            localX++;
        } else {
            localY--;
        }
        return new Coor(localX, localY);
    }
    // get coordinates for right position
    //0up, 2down, 3left, 1right
    public Coor getRightCoor(Integer currentX, Integer currentY) {
        Integer localX = currentX;
        Integer localY = currentY;
        if (orientation == 0) {
            localX++;
        } else if (orientation == 1) {
            localY--;
        } else if (orientation == 2) {
            localX--;
        } else {
            localY++;
        }
        return new Coor(localX, localY);
    }
}

package cz.mendelu.vui2.agents;

import java.util.*;

import static java.lang.Math.abs;

/**
 * Source manual from: https://medium.com/@nicholas.w.swift/easy-a-star-pathfinding-7e6689c7f7b2
 *
 *  A* (star) Pathfinding
 * // Initialize both open and closed list                                      ----------------
 * let the openList equal empty list of nodes                                   --> constructor
 * let the closedList equal empty list of nodes                                 --> constructor   (set is better)
 * // Add the start node                                                        ----------------
 * put the startNode on the openList (leave it's f at zero)                     --> constructor
 * // Loop until you find the end                                               ---------------
 * while the openList is not empty                                                  --> findPath()
 *     // Get the current node                                                      ---------------
 *     let the currentNode equal the node with the least f value                    --> PART 1
 *     remove the currentNode from the openList                                     --> PART 2
 *     add the currentNode to the closedList                                        --> PART 3
 *     // Found the goal                                                            ---------------
 *     if currentNode is the goal                                                   --> PART 4
 *         Congratz! You've found the end! Backtrack to get path                    ---------------
 *     // Generate children                                                         --> PART 5
 *     let the children of the currentNode equal the adjacent nodes
 *
 *     for each child in the children                                               --> PART 6 - calculateDataForNeighbours()
 *         // Child is on the closedList                                                --> calculateCoor()
 *         if child is in the closedList                                                --> PART
 *             continue to beginning of for loop                                        ---------------
 *         // Create the f, g, and h values                                             --> PART 8
 *         child.g = currentNode.g + distance between child and current                 ---------------
 *         child.h = distance from child to end                                         ---------------
 *         child.f = child.g + child.h                                                  ---------------
 *         // Child is already in openList                                              --> PART 9
 *         if child.position is in the openList's nodes positions                       ---------------
 *             if the child.g is higher than the openList node's g                      ---------------
 *                 continue to beginning of for loop                                    ---------------
 *         // Add the child to the openList                                             --> PART 10
 *         add the child to the openList                                                ---------------
 */
public class AStar {

    private HashMap<Coor, AStarLocationData> aStarMapOfWorld = new HashMap<>();
    private LinkedList<Coor> openList = new LinkedList<>();
    private Set<Coor> closedSet = new HashSet<>();
    private Coor startLocation;
    private Coor endLocation;
    private LinkedList<Coor> optimalPath = new LinkedList<>();
    private Coor current;

    public AStar(Coor startLocation, Coor endLocation, HashMap<Coor, LocationData> mapOfWorld) {
        this.startLocation = startLocation;
        this.endLocation = endLocation;

        //firstly, mapOfWorld which contains LocationData, must be re-map to AStarLocationData which contains f,g,h
        setAStarLocationData(mapOfWorld);
        /** put the startNode on the openList */
        this.openList.push(this.aStarMapOfWorld.get(this.startLocation).getCoor());
    }

    public void setAStarLocationData(HashMap<Coor, LocationData> mapOfWorld) {
        for (Map.Entry<Coor, LocationData> locationDataEntry: mapOfWorld.entrySet()) {
            if (locationDataEntry.getValue().getNumberOfVisits() > 0 && !locationDataEntry.getValue().isWall()) {
                AStarLocationData aStarLocationData = new AStarLocationData(locationDataEntry.getKey());
                aStarLocationData.setWall(locationDataEntry.getValue().isWall());
                aStarLocationData.setDock(locationDataEntry.getValue().isDock());
                aStarLocationData.setNumberOfVisits(locationDataEntry.getValue().getNumberOfVisits());
                this.aStarMapOfWorld.put(locationDataEntry.getKey(), aStarLocationData);
            }
        }

        for (Map.Entry<Coor, AStarLocationData> aStarLocationDataEntry: aStarMapOfWorld.entrySet()) {
            Coor coor = aStarLocationDataEntry.getKey();
            ArrayList<Coor> neighboursCoor = new ArrayList<>();
            ArrayList<AStarLocationData> neighboursData = new ArrayList<>();

            neighboursCoor.add(getCoorDOWN(coor));
            neighboursCoor.add(getCoorUP(coor));
            neighboursCoor.add(getCoorLEFT(coor));
            neighboursCoor.add(getCoorRIGHT(coor));;

            for (Coor neighbourCoor: neighboursCoor) {
                AStarLocationData aStarLocationData = aStarMapOfWorld.get(neighbourCoor);
                if(aStarLocationData != null && !aStarLocationData.isWall()) {
                    neighboursData.add(aStarMapOfWorld.get(neighbourCoor));
                }
            }

            aStarMapOfWorld.get(coor).setNeighbours(neighboursData);
        }
    }

    public LinkedList<Coor> findPath() {
        if (this.current == null) {
            this.current = this.openList.get(0);
        }

        /** Loop until you find the end */
        while (!openList.isEmpty()) {
            /**
             * PART 1
             * let the currentNode equal the node with the least f value
             */
            int max = Integer.MAX_VALUE;
            for (int i = 0; i < this.openList.size(); i++) {
                if (this.aStarMapOfWorld.get(this.openList.get(i)).getfCost() < max) {
                    max = this.aStarMapOfWorld.get(this.openList.get(i)).getfCost();
                    this.current = this.openList.get(i);
                }
            }
            /**
             * PART 2
             * remove the currentNode from the openList
             */
            openList.remove(this.current);
            /**
             * PART 3
             * add the currentNode to the closedList
             */
            closedSet.add(current);
            Coor currentCoor = current;
            /**
             * PART 4
             * if currentNode is the goal
             */
            if (currentCoor.equals(endLocation)) {
                Coor localCoor = currentCoor;
                optimalPath.push(this.current);
                while (this.aStarMapOfWorld.get(localCoor).getParentCoor() != null){
                    optimalPath.push(this.aStarMapOfWorld.get(localCoor).getParentCoor());
                    localCoor = this.aStarMapOfWorld.get(localCoor).getParentCoor();
                }
                return getPath(current);
            }
            // remove from open and move to close and calculate all neighbours
            this.openList.remove(currentCoor);
            this.closedSet.add(currentCoor);
            calculateDataForNeighbours(currentCoor);
        }
        return getPath(endLocation);
    }

    /**
     * PART 6
     * for each child in the children
     */
    //function that calculate h,f,g for neighbours
    private void calculateDataForNeighbours(Coor currentCoor) {
        /**
         * PART 5
         * Generate children
         */
        ArrayList<AStarLocationData> neighbours = aStarMapOfWorld.get(currentCoor).getNeighbours();
        for (int i = 0; i < neighbours.size(); i++) {
            AStarLocationData aStarLocationData = neighbours.get(i);
            calculateCoor(aStarLocationData.getCoor());
        }
    }

    private void calculateCoor(Coor neighbourCoor) {
        /**
         * PART 7
         *  if child is in the closedList
         */
        if(!closedSet.contains(neighbourCoor)) {
            Integer gCost = aStarMapOfWorld.get(this.current).getgCost() + 1;

            /**
             * PART 8
             * Create the f, g, and h values
             */
            AStarLocationData aStarLocationData = aStarMapOfWorld.get(neighbourCoor);
            aStarLocationData.setgCost(gCost);
            aStarLocationData.sethCost(calculateHeuresticHCost(neighbourCoor));
            aStarLocationData.setfCost(aStarLocationData.getgCost() + aStarLocationData.gethCost());
            aStarLocationData.setParentCoor(this.current);
            /**
             * PART 9
             * Child is already in openList
             */
            if (openList.contains(neighbourCoor)) {
                if (gCost < aStarLocationData.getgCost()) {
                    aStarLocationData.setgCost(gCost);
                    aStarLocationData.setfCost(aStarLocationData.getgCost() + aStarLocationData.gethCost());
                    aStarMapOfWorld.put(neighbourCoor, aStarLocationData);
                }
            } else {
                openList.push(neighbourCoor);
            }
            /**
             * PART 10
             *  Add the child to the openList
             */
            aStarMapOfWorld.put(neighbourCoor, aStarLocationData);
        }
    }

    // function returns the whole path by getting parentCoor from the last location (currentCoor)
    public LinkedList<Coor> getPath(Coor currentCoor) {
        LinkedList<Coor> finalPath = new LinkedList<>();
        Coor localCoor = currentCoor;
        finalPath.push(localCoor);
        while (this.aStarMapOfWorld.get(localCoor).getParentCoor() != null) {
            finalPath.push(this.aStarMapOfWorld.get(localCoor).getParentCoor());
            localCoor = aStarMapOfWorld.get(localCoor).getParentCoor();
        }
        return finalPath;
    }

    private Coor getCoorUP(Coor coor) {
        return new Coor(coor.x, coor.y + 1);
    }
    private Coor getCoorDOWN(Coor coor) {
        return new Coor(coor.x, coor.y - 1);
    }
    private Coor getCoorLEFT(Coor coor) {
        return new Coor(coor.x - 1, coor.y);
    }
    private Coor getCoorRIGHT(Coor coor) {
        return new Coor(coor.x + 1, coor.y);
    }
    // Compute "h" (heuristic) length from between two coords.
    public int calculateHeuresticHCost(Coor currentLocation) {
        int x = currentLocation.x - this.endLocation.x;
        int y = currentLocation.y - this.endLocation.y;
        return (int) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    public HashMap<Coor, AStarLocationData> getaStarMapOfWorld() {
        return aStarMapOfWorld;
    }
}
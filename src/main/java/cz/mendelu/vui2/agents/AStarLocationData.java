package cz.mendelu.vui2.agents;

import java.util.ArrayList;

public class AStarLocationData {
    //distance from start
    private Integer gCost = 0;
    //distance from end
    private Integer hCost = 0;
    // gCost + fCost
    private Integer fCost = 0;
    private boolean wall;
    private Coor coor;
    private Coor parentCoor;
    private Integer numberOfVisits;
    private boolean dock;
    private boolean discovered;
    private ArrayList<AStarLocationData> neighbours = new ArrayList<>();

    public AStarLocationData(Coor coor) {
        this.coor = coor;
    }

    public Coor getCoor() {
        return coor;
    }

    public Coor getParentCoor() {
        return parentCoor;
    }

    public void setParentCoor(Coor parentCoor) {
        this.parentCoor = parentCoor;
    }

    public void setWall(boolean wall) {
        this.wall = wall;
    }

    public Integer getfCost() {
        return fCost;
    }
    public Integer getgCost() {
        return gCost;
    }
    public Integer gethCost() {
        return hCost;
    }
    public void setgCost(Integer value) {
        gCost = value;
    }
    public void sethCost(Integer value) {
        hCost = value;
    }

    public void setfCost(Integer value) {
        fCost = value;
    }

    public void setDock(boolean dock) {
        this.dock = dock;
    }

    public ArrayList<AStarLocationData> getNeighbours() {
        return neighbours;
    }

    public boolean isWall() {
        return wall;
    }

    public void setNeighbours(ArrayList<AStarLocationData> neighbours) {
        this.neighbours = neighbours;
    }

    public void setNumberOfVisits(Integer number) {
        this.numberOfVisits = number;
    }
}

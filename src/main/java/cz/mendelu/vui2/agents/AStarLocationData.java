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

    public void setCoor(Coor coor) {
        this.coor = coor;
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

    public boolean isDiscovered() {
        return discovered;
    }

    public void setDock(boolean dock) {
        this.dock = dock;
    }

    public Integer getNumberOfVisits() {
        return numberOfVisits;
    }

    public ArrayList<AStarLocationData> getNeighbours() {
        return neighbours;
    }

    public boolean isDock() {
        return dock;
    }

    public boolean isWall() {
        return wall;
    }

    public void setDiscovered(boolean discovered) {
        this.discovered = discovered;
    }

    public void setNeighbours(ArrayList<AStarLocationData> neighbours) {
        this.neighbours = neighbours;
    }

    public void increaseNumberOfVisits() {
        this.numberOfVisits += 1;
    }

    public void setNumberOfVisits(Integer number) {
        this.numberOfVisits = number;
    }
    public String getPrintableNeigbours() {
        String result = "";
        for (int i = 0; i < this.neighbours.size(); i++) {
            Coor coor = this.neighbours.get(i).coor;
            result += "["+ coor.x + "|" + coor.y + "] ";
        }
        return result;
    }
}

package cz.mendelu.vui2.agents;

import java.util.ArrayList;
import java.util.Objects;

public class LocationData {
    private boolean wall;
    private Integer numberOfVisits;
    private boolean dock;
    private boolean discovered;
    private ArrayList<Coor> neighborsData = new ArrayList<>();

    public LocationData(boolean wall, Integer numberOfVisits, boolean dock) {
        this.wall = wall;
        this.numberOfVisits = numberOfVisits;
        this.dock = dock;
        this.discovered = true;
    }
    public LocationData(Integer numberOfVisits, boolean dock) {
        this.numberOfVisits = numberOfVisits;
        this.dock = dock;
        this.discovered = true;
    }
    public LocationData() {
        this.numberOfVisits = 0;
        this.discovered = false;
    }

    @Override
    public String toString() {
        return "LocationData: " + "wall=" + wall + ", visited=" + numberOfVisits + ", dock=" + dock;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocationData locationData = (LocationData) o;
        return numberOfVisits == locationData.numberOfVisits &&
                Objects.equals(wall, locationData.wall);
    }

    public Integer getNumberOfVisits() {
        return numberOfVisits;
    }
    public void increaseNumberOfVisits() {
        this.numberOfVisits += 1;
    }

    public void setWall(boolean wall) {
        this.wall = wall;
    }

    public boolean isWall() {
        return this.wall;
    }

    public boolean isDock() {
        return this.dock;
    }

    public void setDock(boolean dock) {
        this.dock = dock;
    }

    public boolean isDiscovered() {
        return discovered;
    }
    public void setDiscovered(boolean discovered) { this.discovered = discovered; }
}

package com.example.hacknation_bydgo;

public class Point {
    private long id;
    private double lat;
    private double lon;
    private String name;
    private String description;
    private boolean visited;

    public Point(long id, double lat, double lon, String name, String description, boolean visited) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.name = name;
        this.description = description;
        this.visited = visited;
    }

    public Point(long id, double lat, double lon, String name, String description) {
        this(id, lat, lon, name, description, false);
    }

    public long getId() {
        return id;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }
}

package com.company.game.board;

import com.company.game.Settlement;
import com.company.game.types.LocationType;
import com.company.game.types.TerrainType;

import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;

public class Hex {
    private TerrainType terrainType;
    private Hex[] adjacentHexes;
    private Coordinate coordinate;
    private Settlement settlement;
    private Polygon polygon;

    private LocationTile locationTile;
    private boolean hasLocationTile;
    private int remainingLocationTileCount;

    public Hex(TerrainType terrainType, int x, int y) {
        this.terrainType = terrainType;
        this.adjacentHexes = new Hex[6];
        this.coordinate = new Coordinate(x, y);
        this.settlement = null;
        this.polygon = new Polygon();
        this.locationTile = null;
        this.hasLocationTile = false;
        this.remainingLocationTileCount = 0;
    }

    public TerrainType getTerrainType() {
        return terrainType;
    }

    public void setTerrainType(TerrainType terrainType) {
        this.terrainType = terrainType;

        // Get list of LocationType into string
        String locationTypes = Arrays.toString(LocationType.values());

        // If terrainType as a string is in the list of LocationType, then it has a location tile
        if (locationTypes.contains(terrainType.toString())) {
            this.hasLocationTile = true;
            this.remainingLocationTileCount = 2;

            // Get equivalent LocationType of terrainType
            LocationType locationType = LocationType.valueOf(terrainType.toString());
            locationTile = new LocationTile(locationType, coordinate, this);

            if (locationType != LocationType.CASTLE) {
                Board.locationTileCount.put(locationType, Board.locationTileCount.computeIfAbsent(locationType, k -> 0) + 2);
                Board.locationTileImages.put(locationType, locationTile.getImage());
            }

        }
    }

    public void grabLocationTile() {
        if (hasLocationTile) {
            remainingLocationTileCount--;
            Board.locationTileCount.put(locationTile.getType(), Board.locationTileCount.computeIfAbsent(locationTile.getType(), k -> 0) - 1);
            System.out.println(Board.locationTileCount);
        }
    }

    public boolean hasLocationTile() {
        return hasLocationTile;
    }

    public boolean hasRemainingLocationTile() {
        return remainingLocationTileCount > 0;
    }

    public boolean isHasLocationTile() {
        return hasLocationTile;
    }

    public void setHasLocationTile(boolean hasLocationTile) {
        this.hasLocationTile = hasLocationTile;
    }

    public Hex[] getAdjacentHexes() {
        return adjacentHexes;
    }

    public void setAdjacentHexes(Hex[] adjacentHexes) {
        this.adjacentHexes = adjacentHexes;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setSettlement(Settlement settlement) {
        this.settlement = settlement;
    }

    public Settlement getSettlement() {
        return settlement;
    }

    public LocationTile getLocationTile() {
        return locationTile;
    }

    public void setLocationTile(LocationTile locationTile) {
        this.locationTile = locationTile;
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public boolean isBuildable() {
        return terrainType == TerrainType.GRASS || terrainType == TerrainType.FLOWER_FIELD || terrainType == TerrainType.FOREST || terrainType == TerrainType.CANYON || terrainType == TerrainType.DESERT;
    }

    // Just print out the terrain type for now
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(" ").append(terrainType).append(" (").append(coordinate.getX()).append(",").append(coordinate.getY()).append(") ");
        return sb.toString();
    }
}
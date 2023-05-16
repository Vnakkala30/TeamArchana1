package com.company.game;

import com.company.game.board.Board;
import com.company.game.board.Coordinate;
import com.company.game.board.Hex;
import com.company.game.board.LocationTile;
import com.company.game.types.LocationType;
import com.company.game.types.TerrainType;
import com.company.game.util.ImageLoader;
import com.company.game.util.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Player {
    private Color color;
    private int playerNumber;
    private TerrainType terrainCard;
    private Settlement[] settlements;
    private ArrayList<LocationTile> locationTiles;
    private HashMap<Rectangle, LocationTile> locationTileButtonMap;

    private int settlementsBuilt;
    private int gold;

    private BufferedImage settlementImage;
    private BufferedImage playerSlot;
    private BufferedImage terrainCardImage;

    public Player(Color color, int playerNumber) {
        this.color = color;
        this.playerNumber = playerNumber;
        terrainCard = null;
        settlements = new Settlement[40];
        settlementsBuilt = 0;
        gold = 0;
        locationTiles = new ArrayList<>();
        locationTileButtonMap = new HashMap<>();

        try {
            if (color == Color.YELLOW) {
                settlementImage = ImageLoader.obj.loadImage("BeigeSettlement.png");
                playerSlot = ImageLoader.obj.loadImage("Player4Slot.png");
            } else if (color == Color.BLACK) {
                settlementImage = ImageLoader.obj.loadImage("BlackSettlement.png");
                playerSlot = ImageLoader.obj.loadImage("Player2Slot.png");
            } else if (color == Color.BLUE) {
                settlementImage = ImageLoader.obj.loadImage("BlueSettlement.png");
                playerSlot = ImageLoader.obj.loadImage("Player3Slot.png");
            } else if (color == Color.ORANGE) {
                settlementImage = ImageLoader.obj.loadImage("OrangeSettlement.png");
                playerSlot = ImageLoader.obj.loadImage("Player1Slot.png");
            }

            terrainCardImage = ImageLoader.obj.loadImage("GreenBackOfCard.png");
        } catch (Exception e) {
            System.out.println("Error loading settlement image");
        }
        
    }

    public void showAvailableSettlements(Board board, TerrainType terrainType) {
        HashSet<Coordinate> availableCoordinates = board.getAvailableSettlementPlacements(this, terrainType);
        // DisplayGraphics.showAvailableButtons(availableCoordinates);
    }

    // Shows available places to move a settlement to
    public void showAvailableMoveSettlements(Coordinate coordinate, Board board, TerrainType terrainType) {
        HashSet<Coordinate> availableCoordinates = board.getAvailableSettlementPlacements(this, terrainType, coordinate);
        availableCoordinates.remove(coordinate);

        // DisplayGraphics.showAvailableButtons(availableCoordinates);
    }

    public HashSet<Coordinate> buildSettlement(Coordinate coordinate, Board board, TerrainType terrainType, GameManager gm) {
        if (settlementsBuilt == settlements.length || (!gm.isCanUseExtraAction() && gm.getPlacedSettlements() == 3)) {
            Logger.log("No more settlements can be built");
            return null;
        }


        HashSet<Coordinate> availableCoordinates = board.getAvailableSettlementPlacements(this, terrainType);

        if (availableCoordinates.isEmpty()) {
            Logger.log("No available coordinates");
            return availableCoordinates;
        }

        // Check if settlement is available
        if (!availableCoordinates.contains(coordinate)) {
            Logger.log("Coordinate is not available");
            return null;
        }

        Settlement settlement = new Settlement(this);

        // Build settlement and check if the hex has a location tile
        LocationTile locationTile = board.addSettlement(settlement, coordinate);
        if (locationTile != null && !locationTiles.contains(locationTile) && !locationTile.getType().equals(LocationType.CASTLE)) {
            locationTile.getHex().grabLocationTile();
            Logger.log("Location tile found: " + locationTile);
            locationTiles.add(locationTile);
            Logger.log("Player has location tiles: " + locationTiles);
            // DisplayGraphics.updateLocationTileButtonGroup();
        }

        settlements[settlementsBuilt] = settlement;
        settlementsBuilt++;

        // DisplayGraphics.updateBoard();
        return availableCoordinates;
    }

    public HashSet<Coordinate> moveSettlement(Coordinate oldCoordinate, Coordinate newCoordinate, Board board, TerrainType terrainType) {
        Settlement settlement = new Settlement(this);
        HashSet<Coordinate> availableCoordinates = board.getAvailableSettlementPlacements(this, terrainType);

        // Remove the coordinate of the old settlement
        availableCoordinates.remove(oldCoordinate);

        if (availableCoordinates.isEmpty()) {
            Logger.log("No available coordinates");
            return availableCoordinates;
        }

        // Check if settlement is available
        if (!availableCoordinates.contains(newCoordinate)) {
            Logger.log("Coordinate is not available");
            return null;
        }

        // Build settlement and check if the hex has a location tile
        System.out.println("Old coordinate: " + oldCoordinate);
        LocationTile locationTile = board.moveSettlement(oldCoordinate, newCoordinate, settlement);

        // Replace old settlement with new settlement in the array
        for (int i = 0; i < settlements.length; i++) {
            if (settlements[i].getHex().getCoordinate().equals(oldCoordinate)) {
                settlements[i] = settlement;
                break;
            }
        }

        if (locationTile != null && !locationTiles.contains(locationTile) && !locationTile.getType().equals(LocationType.CASTLE)) {
            locationTile.getHex().grabLocationTile();
            Logger.log("Location tile found: " + locationTile);
            locationTiles.add(locationTile);
            Logger.log("Player has location tiles: " + locationTiles);
            // DisplayGraphics.updateLocationTileButtonGroup();
        }


        // DisplayGraphics.updateBoard();
        return availableCoordinates;
    }

    public TerrainType setTerrainCard(TerrainType terrainType) {
        terrainCard = terrainType;

        try {
            switch (terrainType) {
                case GRASS:
                    terrainCardImage = ImageLoader.obj.loadImage("PlainCard.png");
                    break;
                case CANYON:
                    terrainCardImage = ImageLoader.obj.loadImage("CanyonTerrain.jpg");
                    break;
                case DESERT:
                    terrainCardImage = ImageLoader.obj.loadImage("DesertTerrain.jpg");
                    break;
                case FOREST:
                    terrainCardImage = ImageLoader.obj.loadImage("ForestTerrain.jpg");
                    break;
                case FLOWER_FIELD:
                    terrainCardImage = ImageLoader.obj.loadImage("FlowerTerrain.jpg");
                    break;
            }
        } catch (Exception e) {
            System.out.println("Error loading terrain card image");
        }

        return terrainType;
    }

    public TerrainType getTerrainCard() {
        return terrainCard;
    }

    public void discardTerrainCard() {
        terrainCard = null;
    }

    public Settlement[] getSettlements() {
        return settlements;
    }

    public int getSettlementsBuilt() {
        return settlementsBuilt;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public Color getColor() {
        return color;
    }

    public ArrayList<LocationTile> getLocationTiles() {
        return locationTiles;
    }

    public void removeLocationTile(LocationTile locationTile) {
        locationTiles.remove(locationTile);
    }

    public BufferedImage getSettlementImage() {
        return settlementImage;
    }

    public BufferedImage getPlayerSlot() {
        return playerSlot;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public BufferedImage getTerrainCardImage() {
        return terrainCardImage;
    }

    public HashMap<Rectangle, LocationTile> getLocationTileButtonMap() {
        return locationTileButtonMap;
    }

    public void setLocationTileButtonMap(HashMap<Rectangle, LocationTile> locationTileButtonMap) {
        this.locationTileButtonMap = locationTileButtonMap;
    }

    public String toString() {
        return "Player " + (playerNumber+1);
    }
}
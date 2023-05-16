package com.company.game.board;

import com.company.game.GameManager;
import com.company.game.Player;
import com.company.game.Settlement;
import com.company.game.types.LocationType;
import com.company.game.util.ImageLoader;
import com.company.game.util.Logger;
import com.company.game.types.TerrainType;
import com.company.game.util.Randomizer;
import com.company.graphics.DisplayGraphics;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.*;

public class Board {
    public static Hex[][] hexes;
    public static HashMap<LocationType, Integer> locationTileCount = new HashMap<>();
    public static HashMap<LocationType, BufferedImage> locationTileImages = new HashMap<>();

    private int numRows;
    private int numCols;
    private BufferedImage boardImages[] = new BufferedImage[4];

    // Hashmap that has the terrain type as the key and the value an array of Coordinates of the hexes that have that terrain type
    private HashMap<TerrainType, HashSet<Coordinate>> terrainTypeToCoordinates;
    private GameManager gm;



    public Board(int numRows, int numCols, GameManager gameManager) {
        this.numRows = numRows;
        this.numCols = numCols;
        hexes = new Hex[numRows][numCols];
        this.terrainTypeToCoordinates = new HashMap<>();
        gm = gameManager;

        initializeBoard();
        setAdjacentTilesForAllTiles();
    }

    private void initializeBoard() {
        // Create all the hexes without their terrain types
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                hexes[row][col] = new Hex(null, row, col);
            }
        }

        // Initialize the hashmap
        for (TerrainType terrainType : TerrainType.values()) {
            terrainTypeToCoordinates.put(terrainType, new HashSet<>());
        }

        // Setting the terrain types for the hexes
        try {
            ArrayList<String> boardPieces = new ArrayList<>();
            ArrayList<String> selectedBoardPieces = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("board_pieces.txt")));
            HashMap<String, BufferedImage> boardPieceImages = new HashMap<>();

            int index = 1;
            String line;
            while ((line = reader.readLine()) != null) {
                boardPieces.add(line);
                boardPieceImages.put(line, ImageLoader.obj.loadImage("B" + index + ".png"));
                index++;
            }

            reader.close();

            // Select 4 random board pieces
            for (int i = 0; i < 4; i++) {
                int randomIndex = Randomizer.nextInt(boardPieces.size());
                selectedBoardPieces.add(boardPieces.get(randomIndex));
                boardImages[i] = boardPieceImages.get(boardPieces.get(randomIndex));
                boardPieces.remove(randomIndex);
            }

            // Set the terrain types for the hexes
            for (int selectedPiece = 0; selectedPiece < 4; selectedPiece++) {
                int strIndex = 0;
                for (int row = 0; row < 10; row++) {
                    for (int col = 0; col < 10; col++) {
                        String type = selectedBoardPieces.get(selectedPiece).substring(strIndex, strIndex + 1);

                        Hex selectedHex = null;
                        switch (selectedPiece) {
                            case 0:
                                selectedHex = hexes[row][col];
                                break;
                            case 1:
                                selectedHex = hexes[row][col + 10];
                                break;
                            case 2:
                                selectedHex = hexes[row + 10][col];
                                break;
                            case 3:
                                selectedHex = hexes[row + 10][col + 10];
                                break;
                        }

                        selectedHex.setTerrainType(getCorrespondingTerrainType(type));
                        terrainTypeToCoordinates.get(getCorrespondingTerrainType(type)).add(selectedHex.getCoordinate());

                        strIndex++;
                    }
                }
            }

            System.out.println(locationTileCount);

            // Print terrainTypeToCoordinates hashmap
            for (TerrainType terrainType : TerrainType.values()) {
                Logger.log(terrainType + ": " + terrainTypeToCoordinates.get(terrainType));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private TerrainType getCorrespondingTerrainType(String terrain) {
        switch (terrain) {
            case "G":
                return TerrainType.GRASS;
            case "Y":
                return TerrainType.CANYON;
            case "D":
                return TerrainType.DESERT;
            case "F":
                return TerrainType.FLOWER_FIELD;
            case "O":
                return TerrainType.FOREST;
            case "C":
                return TerrainType.CASTLE;
            case "W":
                return TerrainType.WATER;
            case "M":
                return TerrainType.MOUNTAIN;
            case "1":
                return TerrainType.BARN;
            case "2":
                return TerrainType.OASIS;
            case "3":
                return TerrainType.FARM;
            case "4":
                return TerrainType.ORACLE;
            case "5":
                return TerrainType.HARBOR;
            case "6":
                return TerrainType.TOWER;
            case "7":
                return TerrainType.TAVERN;
            default:
                return TerrainType.PADDOCK;
        }
    }

    public HashSet<Coordinate> getCoordinatesOfTerrainType(TerrainType terrainType) {
        return terrainTypeToCoordinates.get(terrainType);
    }

    public HashSet<Coordinate> getAvailableSettlementPlacements(Player player, TerrainType terrainType) {
        HashSet<Coordinate> availableSettlementPlacements = new HashSet<>();

        // Can only place settlements on hexes that are on the outer edge of the board and are not adjacent to any settlements
        if (terrainType == TerrainType.TOWER) {
            Logger.log("TOWER");
            // Get all the hexes that are on the outer edge of the board
            HashSet<Coordinate> outerEdgeHexes = new HashSet<>();
            for (int row = 0; row < numRows; row++) {
                // If the hex has a settlement on it, then do not add it to the outerEdgeHexes
                System.out.println(hexes[row][0] + " isBuildable: " + hexes[row][0].isBuildable());

                if (hexes[row][0].getSettlement() == null && hexes[row][0].isBuildable()) {
                    outerEdgeHexes.add(new Coordinate(row, 0));
                }

                if (hexes[row][numCols - 1].getSettlement() == null && hexes[row][numCols - 1].isBuildable()) {
                    outerEdgeHexes.add(new Coordinate(row, numCols - 1));
                }
            }

            for (int col = 0; col < numCols; col++) {
                // If the hex has a settlement on it, then do not add it to the outerEdgeHexes
                if (hexes[0][col].getSettlement() == null && hexes[0][col].isBuildable()) {
                    outerEdgeHexes.add(new Coordinate(0, col));
                }

                if (hexes[numRows - 1][col].getSettlement() == null && hexes[numRows - 1][col].isBuildable()) {
                    outerEdgeHexes.add(new Coordinate(numRows - 1, col));
                }
            }

            // Get all the outer edge hexes that are adjacent to settlements
            HashSet<Coordinate> hexesAdjacentToSettlements = new HashSet<>();

            for (Coordinate coordinate: outerEdgeHexes) {
                for (Hex hex: hexes[coordinate.getX()][coordinate.getY()].getAdjacentHexes()) {
                    if (hex == null) continue;
                    if (hex.getSettlement() != null && hex.getSettlement().getPlayer().equals(player)) {
                        hexesAdjacentToSettlements.add(coordinate);
                        break;
                    }
                }
            }

            // If there are no outer edge hexes that are adjacent to settlements, then all outer edge hexes are available. Else, only the hexes that are adjacent to settlements are available
            if (hexesAdjacentToSettlements.isEmpty()) {
                return outerEdgeHexes;
            } else {
                return hexesAdjacentToSettlements;
            }
        }

        if (terrainType == TerrainType.TAVERN) {
            return getTavernSettlementPlacements(player);
        } else if (terrainType == TerrainType.PADDOCK) {
            return getPaddockSettlementPlacements(gm.getOldCoordinate());
        }

        // if player's settlements array is empty, then all hexes of the terrain type are available except for hexes that contain a settlement
        if (player.getSettlements()[0] == null) {
            for (Coordinate coordinate: getCoordinatesOfTerrainType(terrainType)) {
                if (hexes[coordinate.getX()][coordinate.getY()].getSettlement() == null) {
                    availableSettlementPlacements.add(coordinate);
                }
            }

            return availableSettlementPlacements;
        }

        // if player's settlements array is not empty, then only the hexes that are adjacent to the player's settlements are available
        for (Settlement settlement: player.getSettlements()) {
            if (settlement == null) {
                break;
            }

            for (Hex hex: settlement.getAdjacentHexes()) {
                if (hex == null) {
                    continue;
                }

                if (hex.getTerrainType() == terrainType && hex.getSettlement() == null) {
                    availableSettlementPlacements.add(hex.getCoordinate());
                }
            }
        }

        // if no hexes are available, then all hexes of the terrain type are available except for hexes that contain a settlement
        if (availableSettlementPlacements.isEmpty()) {
            for (Coordinate coordinate: getCoordinatesOfTerrainType(terrainType)) {
                if (hexes[coordinate.getX()][coordinate.getY()].getSettlement() == null && hexes[coordinate.getX()][coordinate.getY()].isBuildable()) {
                    availableSettlementPlacements.add(coordinate);
                }
            }
        }

        return availableSettlementPlacements;
    }

    public HashSet<Coordinate> getAvailableSettlementPlacements(Player player, TerrainType terrainType, Coordinate coordinate) {
        if (terrainType == TerrainType.PADDOCK) {
            return getPaddockSettlementPlacements(coordinate);
        }

        return getAvailableSettlementPlacements(player, terrainType);
    }

    private HashSet<Coordinate> getTavernSettlementPlacements(Player player) {
        // Player can build one settlement at one end of a line of at least 3 of their settlements. Can either be horizontal or diagonal
        HashSet<Coordinate> availableSettlementPlacements = new HashSet<>();

        // Check for horizontal lines
        for (Settlement settlement: player.getSettlements()) {
            if (settlement == null) {
                break;
            }

            int numSettlementsInLine = 1;
            Coordinate coordinate = settlement.getHex().getCoordinate();

            Hex leftMostHex = settlement.getHex();
            Hex rightMostHex = settlement.getHex();

            // Check left
            for (int col = coordinate.getY() - 1; col >= 0; col--) {
                if (hexes[coordinate.getX()][col].getSettlement() != null && hexes[coordinate.getX()][col].getSettlement().getPlayer().equals(player)) {
                    leftMostHex = hexes[coordinate.getX()][col];
                    numSettlementsInLine++;
                } else {
                    break;
                }
            }

            // Check right
            for (int col = coordinate.getY() + 1; col < numCols; col++) {
                if (hexes[coordinate.getX()][col].getSettlement() != null && hexes[coordinate.getX()][col].getSettlement().getPlayer().equals(player)) {
                    rightMostHex = hexes[coordinate.getX()][col];
                    numSettlementsInLine++;
                } else {
                    break;
                }
            }

            if (numSettlementsInLine >= 3) {
                // Check left of leftMostHex
                if (leftMostHex.getCoordinate().getY() - 1 >= 0 && hexes[leftMostHex.getCoordinate().getX()][leftMostHex.getCoordinate().getY() - 1].getSettlement() == null) {
                    availableSettlementPlacements.add(new Coordinate(leftMostHex.getCoordinate().getX(), leftMostHex.getCoordinate().getY() - 1));
                }

                // Check right of rightMostHex
                if (rightMostHex.getCoordinate().getY() + 1 < numCols && hexes[rightMostHex.getCoordinate().getX()][rightMostHex.getCoordinate().getY() + 1].getSettlement() == null) {
                    availableSettlementPlacements.add(new Coordinate(rightMostHex.getCoordinate().getX(), rightMostHex.getCoordinate().getY() + 1));
                }
            }
        }

        // Check for bottom left to top right diagonal lines
        for (Settlement settlement: player.getSettlements()) {
            if (settlement == null) {
                break;
            }

            int numSettlementsInLine = 1;
            Hex hex = settlement.getHex();

            Hex bottomLeftMostHex = settlement.getHex();
            Hex topRightMostHex = settlement.getHex();

            // 4 represents the bottom left diagonal
            Hex temp = hex.getAdjacentHexes()[4];
            while (temp != null && temp.getSettlement() != null && temp.getSettlement().getPlayer().equals(player)) {
                bottomLeftMostHex = temp;
                numSettlementsInLine++;
                temp = temp.getAdjacentHexes()[4];
            }

            temp = hex.getAdjacentHexes()[1];

            while (temp != null && temp.getSettlement() != null && temp.getSettlement().getPlayer().equals(player)) {
                topRightMostHex = temp;
                numSettlementsInLine++;
                temp = temp.getAdjacentHexes()[1];
            }

            if (numSettlementsInLine >= 3) {
                Logger.log("Bottom left to top right diagonal line found: " + bottomLeftMostHex.getCoordinate() + " to " + topRightMostHex.getCoordinate());
                // Check bottom left of bottomLeftMostHex
                if (bottomLeftMostHex.getCoordinate().getX() + 1 < numRows && hexes[bottomLeftMostHex.getCoordinate().getX() + 1][bottomLeftMostHex.getCoordinate().getY()].getSettlement() == null) {
                    availableSettlementPlacements.add(new Coordinate(bottomLeftMostHex.getCoordinate().getX() + 1, bottomLeftMostHex.getCoordinate().getY()));
                }

                // Check top right of topRightMostHex
                if (topRightMostHex.getCoordinate().getX() - 1 >= 0 && hexes[topRightMostHex.getCoordinate().getX() - 1][topRightMostHex.getCoordinate().getY()].getSettlement() == null) {
                    availableSettlementPlacements.add(new Coordinate(topRightMostHex.getCoordinate().getX() - 1, topRightMostHex.getCoordinate().getY()));
                }
            }
        }

        // Check for top left to bottom right diagonal lines
        for (Settlement settlement: player.getSettlements()) {
            if (settlement == null) {
                break;
            }

            int numSettlementsInLine = 1;
            Hex hex = settlement.getHex();

            Hex topLeftMostHex = settlement.getHex();
            Hex bottomRightMostHex = settlement.getHex();

            // 0 represents the top left diagonal
            Hex temp = hex.getAdjacentHexes()[0];
            while (temp != null && temp.getSettlement() != null && temp.getSettlement().getPlayer().equals(player)) {
                topLeftMostHex = temp;
                numSettlementsInLine++;
                temp = temp.getAdjacentHexes()[0];
            }

            temp = hex.getAdjacentHexes()[3];

            while (temp != null && temp.getSettlement() != null && temp.getSettlement().getPlayer().equals(player)) {
                bottomRightMostHex = temp;
                numSettlementsInLine++;
                temp = temp.getAdjacentHexes()[3];
            }

            if (numSettlementsInLine >= 3) {
                Logger.log("Top left to bottom right diagonal line found: " + topLeftMostHex.getCoordinate() + " " + bottomRightMostHex.getCoordinate());
                // Check top left of topLeftMostHex
                if (topLeftMostHex.getCoordinate().getX() - 1 >= 0 && hexes[topLeftMostHex.getCoordinate().getX() - 1][topLeftMostHex.getCoordinate().getY()].getSettlement() == null) {
                    availableSettlementPlacements.add(new Coordinate(topLeftMostHex.getCoordinate().getX() - 1, topLeftMostHex.getCoordinate().getY()));
                }

                // Check bottom right of bottomRightMostHex
                if (bottomRightMostHex.getCoordinate().getX() + 1 < numRows && hexes[bottomRightMostHex.getCoordinate().getX() + 1][bottomRightMostHex.getCoordinate().getY()].getSettlement() == null) {
                    availableSettlementPlacements.add(new Coordinate(bottomRightMostHex.getCoordinate().getX() + 1, bottomRightMostHex.getCoordinate().getY()));
                }
            }
        }

        return availableSettlementPlacements;
    }

    private HashSet<Coordinate> getPaddockSettlementPlacements(Coordinate coordinate) {
        HashSet<Coordinate> availableSettlementPlacements = new HashSet<>();

        Hex hex = hexes[coordinate.getX()][coordinate.getY()];
        Hex[] adjacentHexes = hex.getAdjacentHexes();
        // Add hexes that are two hexes away from the selected hex to the available settlement placements

        // Horizontal line - left
        if (coordinate.getY() - 2 >= 0 && hexes[coordinate.getX()][coordinate.getY() - 2].getSettlement() == null && hexes[coordinate.getX()][coordinate.getY() - 2].isBuildable()) {
            availableSettlementPlacements.add(new Coordinate(coordinate.getX(), coordinate.getY() - 2));
        }

        // Horizontal line - right
        if (coordinate.getY() + 2 < numCols && hexes[coordinate.getX()][coordinate.getY() + 2].getSettlement() == null && hexes[coordinate.getX()][coordinate.getY() + 2].isBuildable()) {
            availableSettlementPlacements.add(new Coordinate(coordinate.getX(), coordinate.getY() + 2));
        }

        Hex temp = null;
        // Diagonal line - top left
        if (adjacentHexes[0] != null) {
            temp = adjacentHexes[0].getAdjacentHexes()[0];
            if (temp != null && temp.getSettlement() == null && temp.isBuildable()) {
                availableSettlementPlacements.add(temp.getCoordinate());
            }
        }

        // Diagonal line - top right
        if (adjacentHexes[1] != null) {
            temp = adjacentHexes[1].getAdjacentHexes()[1];
            if (temp != null && temp.getSettlement() == null && temp.isBuildable()) {
                availableSettlementPlacements.add(temp.getCoordinate());
            }
        }

        // Diagonal line - bottom right
        if (adjacentHexes[3] != null) {
            temp = adjacentHexes[3].getAdjacentHexes()[3];
            if (temp != null && temp.getSettlement() == null && temp.isBuildable()) {
                availableSettlementPlacements.add(temp.getCoordinate());
            }
        }

        // Diagonal line - bottom left
        if (adjacentHexes[4] != null) {
            temp = adjacentHexes[4].getAdjacentHexes()[4];
            if (temp != null && temp.getSettlement() == null && temp.isBuildable()) {
                availableSettlementPlacements.add(temp.getCoordinate());
            }
        }

        return availableSettlementPlacements;
    }

    public LocationTile addSettlement(Settlement settlement, Coordinate coordinate) {
        Hex selectedHex = hexes[coordinate.getX()][coordinate.getY()];
        selectedHex.setSettlement(settlement);
        settlement.setHex(selectedHex);

        // Check if settlement is adjacent to a LocationTile
        for (Hex hex : settlement.getAdjacentHexes()) {
            if (hex == null) {
                continue;
            }

            if (hex.hasRemainingLocationTile()) {
                return hex.getLocationTile();
            }
        }

        return null;
    }

    // Move settlement from one hex to another; returns the LocationTile that the settlement is now adjacent to
    public LocationTile moveSettlement(Coordinate oldCoordinate, Coordinate newCoordinate, Settlement settlement) {
        Hex oldHex = hexes[oldCoordinate.getX()][oldCoordinate.getY()];
        Hex newHex = hexes[newCoordinate.getX()][newCoordinate.getY()];

        System.out.println("Moving settlement from " + oldCoordinate + " to " + newCoordinate);
        System.out.println(oldHex + " " + newHex);

        oldHex.setSettlement(null);
        newHex.setSettlement(settlement);
        settlement.setHex(newHex);

        // Get the location tile that the settlement is adjacent to in the old hex
        LocationTile locationTile = null;
        System.out.println("REACHED HERE!!");
        for (Hex hex: oldHex.getAdjacentHexes()) {
            if (hex == null) {
                continue;
            }

            if (hex.hasLocationTile()) {
                System.out.println("Found location tile");
                locationTile = hex.getLocationTile();
                break;
            }
        }

        if (locationTile != null) {
            boolean isAdjacentToSettlement = false;

            for (Hex hex: locationTile.getHex().getAdjacentHexes()) {
                if (hex == null) {
                    continue;
                }

                if (hex.getSettlement() != null && hex.getSettlement().getPlayer().equals(settlement.getPlayer())) {
                    System.out.println("Settlement is adjacent to location tile");
                    isAdjacentToSettlement = true;
                    break;
                }
            }

            // None of the player's settlements are adjacent to the location tile
            if (!isAdjacentToSettlement) {
                Player player = settlement.getPlayer();
                player.removeLocationTile(locationTile);

                Logger.log("Player lost LocationTile: " + locationTile);
                Logger.log("Player: " + settlement.getPlayer().getLocationTiles());

                // DisplayGraphics.updateLocationTileButtonGroup();
            }
        }

        // Return the locationTile that the settlement is now adjacent to
        for (Hex hex : settlement.getAdjacentHexes()) {
            if (hex == null) {
                continue;
            }

            if (hex.hasRemainingLocationTile()) {
                return hex.getLocationTile();
            }
        }

        return null;
    }

    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < numRows && col >= 0 && col < numCols;
    }

    private void setAdjacentTilesForAllTiles() {
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                Hex hex = hexes[row][col];
                Hex[] adjacentHexes = new Hex[6];
                // Even row
                if (row % 2 == 0) {
                    if (isValidPosition(row - 1, col - 1)) {
                        adjacentHexes[0] = hexes[row - 1][col - 1]; // Top-left
                    }

                    if (isValidPosition(row - 1, col)) {
                        adjacentHexes[1] = hexes[row - 1][col]; // Top-right
                    }

                    if (isValidPosition(row, col + 1)) {
                        adjacentHexes[2] = hexes[row][col + 1]; // Right
                    }

                    if (isValidPosition(row + 1, col)) {
                        adjacentHexes[3] = hexes[row + 1][col]; // Bottom-right
                    }

                    if (isValidPosition(row + 1, col - 1)) {
                        adjacentHexes[4] = hexes[row + 1][col - 1]; // Bottom-left
                    }

                    if (isValidPosition(row, col - 1)) {
                        adjacentHexes[5] = hexes[row][col - 1]; // Left
                    }
                } else {
                    if (isValidPosition(row - 1, col)) {
                        adjacentHexes[0] = hexes[row - 1][col]; // Top-left
                    }

                    if (isValidPosition(row - 1, col + 1)) {
                        adjacentHexes[1] = hexes[row - 1][col + 1]; // Top-right
                    }

                    if (isValidPosition(row, col + 1)) {
                        adjacentHexes[2] = hexes[row][col + 1]; // Right
                    }

                    if (isValidPosition(row + 1, col + 1)) {
                        adjacentHexes[3] = hexes[row + 1][col + 1]; // Bottom-right
                    }

                    if (isValidPosition(row + 1, col)) {
                        adjacentHexes[4] = hexes[row + 1][col]; // Bottom-left
                    }

                    if (isValidPosition(row, col - 1)) {
                        adjacentHexes[5] = hexes[row][col - 1]; // Left
                    }
                }

                hex.setAdjacentHexes(adjacentHexes);
            }
        }
    }

    public static Hex getTile(Coordinate coordinate) {
        return hexes[coordinate.getX()][coordinate.getY()];
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
    }

    public BufferedImage[] getBoardImages() {
        return boardImages;
    }

    public static Hex[][] getHexes() {
        return hexes;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < hexes.length; row++) {
            for (int col = 0; col < hexes[row].length; col++) {
                Hex hex = hexes[row][col];
                sb.append(hex.toString());
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
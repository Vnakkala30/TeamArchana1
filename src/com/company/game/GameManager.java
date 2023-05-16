package com.company.game;

import com.company.game.board.Board;
import com.company.game.board.Coordinate;
import com.company.game.board.LocationTile;
import com.company.game.decks.KBDeck;
import com.company.game.decks.TerrainDeck;
import com.company.game.types.LocationType;
import com.company.game.types.TerrainType;
import com.company.game.util.Logger;
import com.company.graphics.DisplayGraphics;
import com.company.graphics.KBFrame;
import com.company.graphics.KBPanel;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class GameManager {
    private Board board;
    private TerrainDeck terrainDeck;
    private KBDeck kbDeck;
    private Player[] players;
    private Player currentPlayer;
    private int turn;
    private int placedSettlements;
    public TerrainType selectedTerrainType;

    private boolean isUsingExtraAction;
    private LocationType extraActionType;
    private boolean isMovingSettlement;
    private boolean canUseExtraAction;
    private int usedExtraActions;

    // For moving settlements
    private Coordinate oldCoordinate;
    private Coordinate newCoordinate;

    private ArrayList<LocationTile> unusedLocationTiles;
    private KBPanel kbPanel;

    public GameManager() {
        setup();
    }

    private void setup() {
        board = new Board(20, 20, this);
        terrainDeck = new TerrainDeck();
        kbDeck = new KBDeck();

        players = new Player[4];

        Color[] colors = {Color.ORANGE, Color.BLACK, Color.BLUE, Color.YELLOW};
        for (int i = 0; i < players.length; i++) {
            players[i] = new Player(colors[i], i);
        }

        turn = 0;
        placedSettlements = 0;
        currentPlayer = players[0];
        selectedTerrainType = currentPlayer.setTerrainCard(terrainDeck.draw());
        isUsingExtraAction = false;
        isMovingSettlement = false;
        canUseExtraAction = true;
        usedExtraActions = 0;

        oldCoordinate = null;
        newCoordinate = null;

        unusedLocationTiles = new ArrayList<>();

        EndGame.setBoard(board);
        EndGame.setGameManager(this);
        EndGame.setPlayerScores(players);

        KBFrame kbFrame = new KBFrame("Kingdom Builder", this);
        kbPanel = kbFrame.getKBPanel();
    }

    private void gameEnd() {
        HashSet<Player> winners = new HashSet<>();

        int maxGold = 0;

        for (Player player: players) {
            player.setGold(EndGame.getGold(player, kbDeck));

            if (player.getGold() > maxGold) {
                maxGold = player.getGold();
            }
        }

        for (Player player: players) {
            if (player.getGold() == maxGold) {
                System.out.println("Gold: " + player.getGold());
                winners.add(player);
            }
        }

        // DisplayGraphics.displayWinners(winners);
        kbPanel.displayWinners(winners);
    }

    public void useExtraAction(LocationType locationType, Coordinate coordinate) {
        isUsingExtraAction = true;
        extraActionType = locationType;
        Logger.log("Using extra action: " + locationType);

        switch (locationType) {
            case ORACLE:
                // Build settlement of same terrain type
                selectedTerrainType = currentPlayer.getTerrainCard();
                break;
            case FARM:
                // Build settlement on a grass hex
                selectedTerrainType = TerrainType.GRASS;
                break;
            case OASIS:
                // Build settlement on desert hex
                selectedTerrainType = TerrainType.DESERT;
                break;
            case TOWER:
                // Build settlement on edge of board
                selectedTerrainType = TerrainType.TOWER;
                break;
            case TAVERN:
                // Build settlement at one end of a line of at least 3 (horizontal or diagonal)
                selectedTerrainType = TerrainType.TAVERN;
                break;
            case BARN:
                // Move settlement to same terrain type
                selectedTerrainType = currentPlayer.getTerrainCard();
                isMovingSettlement = true;
                break;
            case HARBOR:
                // Move settlement to a water hex
                selectedTerrainType = TerrainType.WATER;
                isMovingSettlement = true;
                break;
            case PADDOCK:
                // Move settlement two hexes in a straight line (horizontal or diagonal)
                selectedTerrainType = TerrainType.PADDOCK;
                isMovingSettlement = true;
                break;
        }

        Logger.log("Selected terrain type: " + selectedTerrainType);
        if (isMovingSettlement) {
            currentPlayer.showAvailableMoveSettlements(coordinate, board, selectedTerrainType);
        } else {
            showSettlements();
        }

        for (LocationTile locationTile: unusedLocationTiles) {
            if (locationTile.getType() == locationType) {
                unusedLocationTiles.remove(locationTile);
                break;
            }
        }
    }

    public void nextTurn() {
        Logger.log("Next turn");
        turn++;
        placedSettlements = 0;
        isUsingExtraAction = false;
        isMovingSettlement = false;
        canUseExtraAction = true;
        oldCoordinate = null;
        newCoordinate = null;

        Logger.log("Selected KB: " + Arrays.toString(kbDeck.getSelectedKB()));
        Logger.log("Gold: " + EndGame.getGold(currentPlayer, kbDeck));

        if (currentPlayer.getSettlementsBuilt() == 40) {
            gameEnd();
            return;
        }

        if (turn == players.length) {
            turn = 0;
        }

        currentPlayer = players[turn];

        unusedLocationTiles.clear();
        unusedLocationTiles.addAll(currentPlayer.getLocationTiles());

        // If player has a terrain card, discard it and draw a new one
        if (currentPlayer.getTerrainCard() != null) {
            TerrainType temp = currentPlayer.getTerrainCard();
            terrainDeck.discard(temp);
        }

        selectedTerrainType = currentPlayer.setTerrainCard(terrainDeck.draw());
        showSettlements();
//        DisplayGraphics.updateBoard();
//        DisplayGraphics.updateLocationTileButtonGroup();
    }

    public void showSettlements() {
        currentPlayer.showAvailableSettlements(board, selectedTerrainType);
    }

    public void buildSettlement(Coordinate coordinate) {
        if (!isUsingExtraAction && placedSettlements == 3) return;

        HashSet<Coordinate> check = currentPlayer.buildSettlement(coordinate, board, selectedTerrainType, this);
        if (check == null) return;

        if (isUsingExtraAction) {
            isUsingExtraAction = false;
            usedExtraActions++;
            selectedTerrainType = currentPlayer.getTerrainCard();
            return;
        }

        if (!check.isEmpty()) {
            placedSettlements++;
            if (placedSettlements == 3 && usedExtraActions == 0) {
                canUseExtraAction = true;
            } else {
                canUseExtraAction = false;
            }
        } else {
            // There are no more available hexes to build on; draw a new terrain card
            TerrainType temp = terrainDeck.draw();

            // Removes useless terrain cards
            while (temp == currentPlayer.getTerrainCard()) {
                temp = terrainDeck.draw();
            }

            selectedTerrainType = currentPlayer.setTerrainCard(temp);
            Logger.log("New terrain card: " + currentPlayer.getTerrainCard());
        }

        Logger.log("Gold: " + EndGame.getGold(currentPlayer, kbDeck));


        if (currentPlayer.getSettlementsBuilt() == 40) {
            gameEnd();
        }
    }


    public boolean moveSettlement() {
        boolean flag = currentPlayer.moveSettlement(oldCoordinate, newCoordinate, board, selectedTerrainType) != null;

        isMovingSettlement = false;
        isUsingExtraAction = false;
        selectedTerrainType = currentPlayer.getTerrainCard();

        if (flag) {
            usedExtraActions++;
        }

        return flag;
    }

    public boolean isMovingSettlement() {
        return isMovingSettlement;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Coordinate getOldCoordinate() {
        return oldCoordinate;
    }

    public void setOldCoordinate(Coordinate oldCoordinate) {
        this.oldCoordinate = oldCoordinate;
    }

    public Coordinate getNewCoordinate() {
        return newCoordinate;
    }

    public void setNewCoordinate(Coordinate newCoordinate) {
        this.newCoordinate = newCoordinate;
    }

    public Player[] getPlayers() {
        return players;
    }

    public Board getBoard() {
        return board;
    }

    public int getPlacedSettlements() {
        return placedSettlements;
    }

    public int getTurn() {
        return turn;
    }

    public KBDeck getKbDeck() {
        return kbDeck;
    }

    public boolean isCanUseExtraAction() {
        return canUseExtraAction;
    }

    public boolean isUsingExtraAction() {
        return isUsingExtraAction;
    }

    public ArrayList<LocationTile> getUnusedLocationTiles() {
        return unusedLocationTiles;
    }
}

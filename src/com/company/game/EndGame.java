package com.company.game;

import com.company.game.board.Board;
import com.company.game.board.Hex;
import com.company.game.board.LocationTile;
import com.company.game.decks.KBDeck;
import com.company.game.types.KingdomBuilderType;
import com.company.game.types.TerrainType;
import com.company.game.util.Logger;
import com.company.graphics.KBScoring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class EndGame {

    private static Board board;
    private static GameManager gm;
    public static HashMap<Player, HashMap<KingdomBuilderType, Integer>> playerScores = new HashMap<>();
    public static KBScoring kbScoring;

    public static void setPlayerScores(Player[] players) {
        for (Player player: players) {
            playerScores.put(player, new HashMap<>());

            for (KingdomBuilderType kb: gm.getKbDeck().getSelectedKB()) {
                playerScores.get(player).put(kb, 0);
            }

            playerScores.get(player).put(KingdomBuilderType.CASTLE, 0);
        }
    }

    public static void setKbScoring(KBScoring scoring) {
        kbScoring = scoring;
    }

    public static void setBoard(Board b) {
        board = b;
    }

    public static void setGameManager(GameManager g) {
        gm = g;
    }


    public static HashMap<KingdomBuilderType, Integer> getString(Player player, KBDeck kbDeck) {
        KingdomBuilderType[] selectedKB = kbDeck.getSelectedKB();
        HashMap<KingdomBuilderType, Integer> kbMap = new HashMap<>();

        int gold = 0;
        for (KingdomBuilderType kb: selectedKB) {
            switch (kb) {
                case LORDS:
                    gold += getLordsGold(player);
                    break;
                case FISHERMEN:
                    gold += getFisherMenGold(player);
                    break;
                case MERCHANTS:
                    gold += getMerchantsGold(player);
                    break;
                case FARMERS:
                    gold += getFarmersGold(player);
                    break;
                case MINERS:
                    gold += getMinersGold(player);
                    break;
                case KNIGHTS:
                    gold += getKnightsGold(player);
                    break;
                case HERMITS:
                    gold += getHermitsGold(player);
                    break;
                case WORKERS:
                    gold += getWorkersGold(player);
                    break;
                case CITIZENS:
                    gold += getCitizensGold(player);
                    break;
                case DISCOVERERS:
                    gold += getDiscoverersGold(player);
                    break;
            }

            kbMap.put(kb, gold);
        }

        int castleGold = getCastleGold(player);
        kbMap.put(KingdomBuilderType.CASTLE, castleGold);
        return kbMap;
    }

    public static int getGold(Player player, KBDeck kbDeck) {
        KingdomBuilderType[] selectedKB = kbDeck.getSelectedKB();
        Player[] players = gm.getPlayers();
        for (Player p: players) {
            HashMap<KingdomBuilderType, Integer> map = playerScores.get(player);

            int gold = 0;
            for (KingdomBuilderType kb: selectedKB) {
                int goldBefore = gold;

                switch (kb) {
                    case LORDS:
                        gold += getLordsGold(player);
                        break;
                    case FISHERMEN:
                        gold += getFisherMenGold(player);
                        break;
                    case MERCHANTS:
                        gold += getMerchantsGold(player);
                        break;
                    case FARMERS:
                        gold += getFarmersGold(player);
                        break;
                    case MINERS:
                        gold += getMinersGold(player);
                        break;
                    case KNIGHTS:
                        gold += getKnightsGold(player);
                        break;
                    case HERMITS:
                        gold += getHermitsGold(player);
                        break;
                    case WORKERS:
                        gold += getWorkersGold(player);
                        break;
                    case CITIZENS:
                        gold += getCitizensGold(player);
                        break;
                    case DISCOVERERS:
                        gold += getDiscoverersGold(player);
                        break;
                }

                map.put(kb, gold - goldBefore);
                Logger.log("Player " + (player.getPlayerNumber()+1) + " got " + (gold - goldBefore) + " gold from " + kb);
            }

            map.put(KingdomBuilderType.CASTLE, getCastleGold(player));
            playerScores.put(player, map);
        }

        kbScoring.updateScores();
        return 12;
    }

    private static int getCastleGold(Player player) {
        int gold = 0;

        HashSet<Hex> castleHexes = new HashSet<>();

        for (Settlement settlement : player.getSettlements()) {
            // Player will earn 3 gold for each castle hex if they have built at least one of their own settlements next to it
            if (settlement == null) {
                continue;
            }

            // Get adjacent hexes
            Hex[] adjacentHexes = settlement.getHex().getAdjacentHexes();

            // Loop through adjacent hexes
            for (Hex adjacentHex : adjacentHexes) {
                if (adjacentHex == null) {
                    continue;
                }

                // If the adjacent hex is a castle hex, add 3 gold
                if (!castleHexes.contains(adjacentHex) && adjacentHex.getTerrainType().equals(TerrainType.CASTLE)) {
                    gold += 3;
                    castleHexes.add(adjacentHex);
                }
            }
        }

        return gold;
    }

    // 1 gold for each of the layer's settlements adjacent to one or more water hexes
    private static int getFisherMenGold(Player player) {
        int gold = 0;

        for (Settlement settlement : player.getSettlements()) {
            if (settlement == null) {
                continue;
            }

            for (Hex adjacentHex : settlement.getHex().getAdjacentHexes()) {
                if (adjacentHex == null) {
                    continue;
                }

                if (adjacentHex.getTerrainType().equals(TerrainType.WATER)) {
                    gold++;
                    break;
                }
            }
        }

        return gold;
    }

    // 4 gold for each location tile connected to another by a chain of settlements
    private static int getMerchantsGold(Player player) {
        int gold = 0;

        Settlement[] settlements = player.getSettlements();
        ArrayList<Settlement> startingPoints = new ArrayList<>();

        // Loop through all settlements and add settlements that are adjacent to a location tile to startingPoints
        for (Settlement settlement : settlements) {
            if (settlement == null) {
                continue;
            }
            for (Hex adjacentHex : settlement.getHex().getAdjacentHexes()) {
                if (adjacentHex == null) {
                    continue;
                }

                if (adjacentHex.hasLocationTile()) {
                    startingPoints.add(settlement);
                    break;
                }
            }
        }



        HashSet<LocationTile> locationTilesVisited = new HashSet<>();
        for (Settlement startingPoint: startingPoints) {
            HashSet<Settlement> settlementsVisited = new HashSet<>();

            settlementsVisited.add(startingPoint);
            // Get the adjacent location tile of the starting point and add it to locationTilesVisited
            for (Hex adjacentHex : startingPoint.getHex().getAdjacentHexes()) {
                if (adjacentHex == null) {
                    continue;
                }

                if (adjacentHex.hasLocationTile()) {
                    locationTilesVisited.add(adjacentHex.getLocationTile());
                    break;
                }
            }

            gold += getMerchantsGoldHelper(startingPoint, locationTilesVisited, settlementsVisited);
        }

        return gold;
    }

    private static int getMerchantsGoldHelper(Settlement settlement, HashSet<LocationTile> visitedLocationTiles, HashSet<Settlement> settlementsVisited) {
        int gold = 0;

        // If the settlement is adjacent to a location tile that has not been visited, add it to locationTile and add 4 gold
        for (Hex adjacentHex : settlement.getHex().getAdjacentHexes()) {
            if (adjacentHex == null) {
                continue;
            }

            if (adjacentHex.hasLocationTile() && !visitedLocationTiles.contains(adjacentHex.getLocationTile())) {
                visitedLocationTiles.add(adjacentHex.getLocationTile());
                gold += 4;
            }
        }

        // If the settlement is adjacent to a settlement that has not been visited, add it to settlementsVisited and call getMerchantsGoldHelper on it
        for (Hex adjacentHex : settlement.getHex().getAdjacentHexes()) {
            if (adjacentHex == null) {
                continue;
            }

            if (adjacentHex.getSettlement() == null) {
                continue;
            }

            if (!settlementsVisited.contains(adjacentHex.getSettlement())) {
                settlementsVisited.add(adjacentHex.getSettlement());
                gold += getMerchantsGoldHelper(adjacentHex.getSettlement(), visitedLocationTiles, settlementsVisited);
            }
        }

        return gold;
    }

    // 1 gold for each horizontal line of the board on which the player has at least one settlement
    private static int getDiscoverersGold(Player player) {
        HashSet<Integer> rows = new HashSet<>();

        for (Settlement settlement: player.getSettlements()) {
            if (settlement == null) {
                continue;
            }

            int row = settlement.getHex().getCoordinate().getX();
            rows.add(row);
        }

        return rows.size();
    }

    // 1 gold for each settlement adjacent to a mountain hex
    private static int getMinersGold(Player player) {
        int gold = 0;

        for (Settlement settlement : player.getSettlements()) {
            if (settlement == null) {
                continue;
            }

            for (Hex adjacentHex : settlement.getHex().getAdjacentHexes()) {
                if (adjacentHex == null) {
                    continue;
                }

                if (adjacentHex.getTerrainType().equals(TerrainType.MOUNTAIN)) {
                    gold++;
                    break;
                }
            }
        }

        return gold;
    }

    // 1 gold for each settlement adjacent to a location tile
    private static int getWorkersGold(Player player) {
        int gold = 0;

        for (Settlement settlement : player.getSettlements()) {
            if (settlement == null) {
                continue;
            }

            for (Hex adjacentHex : settlement.getHex().getAdjacentHexes()) {
                if (adjacentHex == null) {
                    continue;
                }

                if (adjacentHex.hasLocationTile()) {
                    gold++;
                    break;
                }
            }
        }

        return gold;
    }

    // 2 gold for each settlement along the horizontal line of the board on which the player has the most settlements
    private static int getKnightsGold(Player player) {
        // Key: row, Value: number of settlements on that row
        HashMap<Integer, Integer> rowCounts = new HashMap<>();

        for (Settlement settlement: player.getSettlements()) {
            if (settlement == null) {
                continue;
            }

            int row = settlement.getHex().getCoordinate().getX();
            rowCounts.put(row, rowCounts.getOrDefault(row, 0) + 1);
        }

        int max = 0;

        for (int row: rowCounts.keySet()) {
            if (rowCounts.get(row) > max) {
                max = rowCounts.get(row);
            }
        }

        return max * 2;
    }

    // Gain 3 gold per settlement in the quadrant with the fewest of the player's settlements. THe player must have built a settlement in each of the quadrants. Each quadrant is 10 rows by 10 columns.
    private static int getFarmersGold(Player player) {
        // Key: quadrant, Value: number of settlements in that quadrant
        HashMap<Integer, Integer> quadrantCounts = new HashMap<>();

        for (Settlement settlement: player.getSettlements()) {
            if (settlement == null) {
                continue;
            }

            int row = settlement.getHex().getCoordinate().getX();
            int column = settlement.getHex().getCoordinate().getY();

            int quadrant = getQuadrant(row, column);

            quadrantCounts.put(quadrant, quadrantCounts.getOrDefault(quadrant, 0) + 1);
        }

        // If the player has not built a settlement in each quadrant, return 0
        if (quadrantCounts.size() < 4) {
            return 0;
        }

        int min = Integer.MAX_VALUE;

        for (int quadrant: quadrantCounts.keySet()) {
            if (quadrantCounts.get(quadrant) < min) {
                min = quadrantCounts.get(quadrant);
            }
        }

        return min * 3;
    }

    // Per quadrant, gain 12 gold if you have the most settlements in that quadrant, 6 gold if tied for the most, and 0 gold otherwise. Each quadrant is 10 rows by 10 columns.
    private static int getLordsGold(Player p) {
        Player[] players = gm.getPlayers();

        // Key: quadrant, Value: number of settlements in that quadrant
        HashMap<Player, HashMap<Integer, Integer>> quadrantCounts = new HashMap<>();

        for (Player player : players) {
            quadrantCounts.put(player, new HashMap<>());

            for (Settlement settlement: player.getSettlements()) {
                if (settlement == null) {
                    continue;
                }

                int row = settlement.getHex().getCoordinate().getX();
                int column = settlement.getHex().getCoordinate().getY();

                int quadrant = getQuadrant(row, column);

                quadrantCounts.get(player).put(quadrant, quadrantCounts.get(player).getOrDefault(quadrant, 0) + 1);
            }
        }

        int[] maxQuadCounts = new int[4];

        HashMap<Integer, HashSet<Player>> quadrantWinners = new HashMap<>();

        // Find the max settlement count for each quadrant
        for (int quadrant = 0; quadrant < 4; quadrant++) {
            for (Player player : players) {
                int settlementCount = quadrantCounts.get(player).getOrDefault(quadrant, 0);
                if (settlementCount > maxQuadCounts[quadrant]) {
                    maxQuadCounts[quadrant] = quadrantCounts.get(player).get(quadrant);
                }
            }
        }

        // Find the players who have the max settlement count for each quadrant
        for (int quadrant = 0; quadrant < 4; quadrant++) {
            quadrantWinners.put(quadrant, new HashSet<>());

            for (Player player : players) {
                int settlementCount = quadrantCounts.get(player).getOrDefault(quadrant, 0);

                if (settlementCount == 0) {
                    continue;
                }

                if (settlementCount == maxQuadCounts[quadrant]) {
                    quadrantWinners.get(quadrant).add(player);
                }
            }
        }

        int gold = 0;

        for (int quadrant = 0; quadrant < 4; quadrant++) {
            HashSet<Player> quadWinners = quadrantWinners.get(quadrant);
            if (quadWinners.contains(p)) {
                if (quadWinners.size() == 1) {
                    gold += 12;
                } else {
                    gold += 6;
                }
            }
        }

        return gold;
    }

    private static int getQuadrant(int x, int y) {
        if (x < 10 && y < 10) {
            return 0;
        } else if (x < 10 && y >= 10) {
            return 1;
        } else if (x >= 10 && y < 10) {
            return 2;
        } else {
            return 3;
        }
    }

    // Gain 1 gold for each cluster of settlements the player has. A cluster is one or more adjacent settlements belonging to the player.
    private static int getHermitsGold(Player player) {
        int gold = 0;

        HashSet<Settlement> settlementsVisited = new HashSet<>();

        for (Settlement settlement: player.getSettlements()) {
            if (settlement == null) {
                continue;
            }

            if (settlementsVisited.contains(settlement)) {
                continue;
            }

            HashSet<Settlement> cluster = getCluster(settlement, settlementsVisited);
            settlementsVisited.addAll(cluster);

            if (cluster.size() > 1) {
                gold += 1;
            }
        }

        return gold;
    }

    private static HashSet<Settlement> getCluster(Settlement settlement, HashSet<Settlement> settlementsVisited) {
        HashSet<Settlement> cluster = new HashSet<>();

        if (settlementsVisited.contains(settlement)) {
            return cluster;
        }

        cluster.add(settlement);
        settlementsVisited.add(settlement);

        for (Hex adjacentHex: settlement.getHex().getAdjacentHexes()) {
            if (adjacentHex == null) {
                continue;
            }

            if (adjacentHex.getSettlement() == null) {
                continue;
            }

            if (adjacentHex.getSettlement().getPlayer() != settlement.getPlayer()) {
                continue;
            }

            if (settlementsVisited.contains(adjacentHex.getSettlement())) {
                continue;
            }

            cluster.addAll(getCluster(adjacentHex.getSettlement(), settlementsVisited));
        }

        return cluster;
    }

    // Gain 1 gold for every two settlements in the largest cluster of settlements.
    private static int getCitizensGold(Player player) {
        int max = 0;

        HashSet<Settlement> settlementsVisited = new HashSet<>();

        for (Settlement settlement: player.getSettlements()) {
            if (settlement == null) {
                continue;
            }

            if (settlementsVisited.contains(settlement)) {
                continue;
            }

            HashSet<Settlement> cluster = getCluster(settlement, settlementsVisited);
            settlementsVisited.addAll(cluster);

            if (cluster.size() > max) {
                max = cluster.size();
            }
        }

        return max / 2;
    }

}

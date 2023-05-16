package com.company.game.decks;

import com.company.game.types.TerrainType;
import com.company.game.util.Randomizer;

import java.util.Collections;
import java.util.Stack;

public class TerrainDeck {
    private Stack<TerrainType> terrainDeck;
    private Stack<TerrainType> discardPile;

    public TerrainDeck() {
        terrainDeck = new Stack<>();
        discardPile = new Stack<>();

        for (int i = 0; i < 5; i++) {
            terrainDeck.push(TerrainType.GRASS);
            terrainDeck.push(TerrainType.FLOWER_FIELD);
            terrainDeck.push(TerrainType.FOREST);
            terrainDeck.push(TerrainType.CANYON);
            terrainDeck.push(TerrainType.DESERT);
        }

        Collections.shuffle(terrainDeck, Randomizer.getRandom());
    }

    public TerrainType draw() {
        if (terrainDeck.isEmpty()) {
            terrainDeck.addAll(discardPile);
            discardPile.clear();
            Collections.shuffle(terrainDeck, Randomizer.getRandom());
        }

        return terrainDeck.pop();
    }

    public void shuffle() {
        Collections.shuffle(terrainDeck);
    }

    public int size() {
        return terrainDeck.size();
    }

    public boolean isEmpty() {
        return terrainDeck.isEmpty();
    }

    public void discard(TerrainType terrainType) {
        discardPile.push(terrainType);
    }

    public void add(TerrainType terrainType) {
        terrainDeck.push(terrainType);
    }
}

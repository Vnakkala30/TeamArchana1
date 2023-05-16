package com.company.game;

import com.company.game.board.Coordinate;
import com.company.game.board.Hex;

public class Settlement {
    private Player player;
    private Coordinate coordinate;
    private Hex hex;

    public Settlement(Player player) {
        this.player = player;
        this.coordinate = null;
    }

    public Hex[] getAdjacentHexes() {
        return hex.getAdjacentHexes();
    }

    public Player getPlayer() {
        return player;
    }

    public Hex setHex(Hex hex) {

        this.hex = hex;
        return hex;
    }

    public Hex getHex() {
        return hex;
    }

    @Override
    public String toString() {
        return "Settlement{" +
                "player=" + player +
                ", coordinate=" + coordinate +
                ", hex=" + hex +
                '}';
    }
}

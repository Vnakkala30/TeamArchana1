package com.company.game.board;

import com.company.game.types.LocationType;
import com.company.game.util.ImageLoader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class LocationTile {
    private LocationType type;
    private Coordinate coordinate;
    private Hex hex;
    private BufferedImage image;

    public LocationTile(LocationType type, Coordinate coordinate, Hex hex) {
        this.type = type;
        this.coordinate = coordinate;
        this.hex = hex;

        try {
            switch (type) {
                case FARM:
                    image = ImageLoader.obj.loadImage("KB-farm.png");
                    break;
                case HARBOR:
                    image = ImageLoader.obj.loadImage("KB-harbor.png");
                    break;
                case OASIS:
                    image = ImageLoader.obj.loadImage("KB-oasis.png");
                    break;
                case ORACLE:
                    image = ImageLoader.obj.loadImage("KB-oracle.png");
                    break;
                case PADDOCK:
                    image = ImageLoader.obj.loadImage("KB-paddock.png");
                    break;
                case TAVERN:
                    image = ImageLoader.obj.loadImage("KB-tavern.png");
                    break;
                case TOWER:
                    image = ImageLoader.obj.loadImage("KB-tower.png");
                    break;
                case BARN:
                    image = ImageLoader.obj.loadImage("KB-barn.png");
                    break;
                default:
                    image = null;
                    break;
            }
        } catch (Exception e) {
            System.out.println("Error loading location tile image");
        }
    }

    public Hex getHex() {
        return hex;
    }

    public LocationType getType() {
        return type;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(" ").append(type).append(" ");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LocationTile)) return false;
        LocationTile that = (LocationTile) o;
        return Objects.equals(coordinate, that.coordinate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coordinate);
    }

    public BufferedImage getImage() {
        return image;
    }
}

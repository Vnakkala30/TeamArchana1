package com.company.graphics;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Button {
    private BufferedImage enabledImage, disabledImage;
    private int x, y, width, height;
    private boolean enabled, hovering;

    public Button(int x, int y, int width, int height, BufferedImage enabledImage, BufferedImage disabledImage) {
        this.x = x; this.y = y;
        this.width = width; this.height = height;
        this.enabled = true; this.hovering = false;

        this.enabledImage = enabledImage;
        this.disabledImage = disabledImage;
    }

    public void draw(Graphics g) {
        if (!enabled) g.drawImage(disabledImage, x, y, width, height, null);
        else if (hovering) g.drawImage(enabledImage, x, y, width, height, null);
        else g.drawImage(enabledImage, x, y, width, height, null);
    }

    public BufferedImage getEnabledImage() {
        return enabledImage;
    }

    public BufferedImage getDisabledImage() {
        return disabledImage;
    }

    public void setCenterCoords(int x, int y) {
        int topY = y - height / 2;
        int leftX = x - width / 2;
        this.x = leftX; this.y = topY;
    }

    public boolean isClicked(int x1, int y1) {
        return enabled && inBounds(x1, y1);
    }

    public boolean inBounds(int x1, int y1) {
        return (x1 >= x && y1 >= y && x1 <= x + width && y1 <= y + height);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isHovering() {
        return hovering;
    }
}

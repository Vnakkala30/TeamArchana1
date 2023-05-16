package com.company.game.util;

import com.company.game.board.Board;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class ImageLoader {

    public static ImageLoader obj = new ImageLoader();

    public BufferedImage loadImage(String imageName) {
        // Images are located at package com.company.images
        try {
            return ImageIO.read(getClass().getClassLoader().getResource("com/company/images/" + imageName));
        } catch (Exception e) {
            System.out.println("Error loading " + imageName + " image");
            return null;
        }
    }
}

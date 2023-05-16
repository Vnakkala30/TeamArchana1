package com.company.game.decks;

import com.company.game.types.KingdomBuilderType;
import com.company.game.util.ImageLoader;
import com.company.game.util.Logger;
import com.company.game.util.Randomizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Collections;
import java.util.Stack;

public class KBDeck {
    private Stack<KingdomBuilderType> kbDeck;
    private KingdomBuilderType[] selectedKB;
    private BufferedImage[] selectedKBImages;

    public KBDeck() {
        kbDeck = new Stack<>();
        selectedKB = new KingdomBuilderType[3];
        selectedKBImages = new BufferedImage[3];

        // For each Kingdom Builder type, add a card to the deck
        for (KingdomBuilderType type : KingdomBuilderType.values()) {
            kbDeck.push(type);
        }

        kbDeck.remove(KingdomBuilderType.CASTLE);

        // Shuffle the deck
        Collections.shuffle(kbDeck, Randomizer.getRandom());

        // Select the first 3 cards
        for (int i = 0; i < 3; i++) {
            selectedKB[i] = kbDeck.pop();
        }

        setSelectedKBImages();
        Logger.log("Selected KB: "  + Arrays.toString(selectedKB));
    }

    private void setSelectedKBImages() {
        try {
            for (int i = 0; i < 3; i++)
            {
                System.out.println(selectedKB[i]);
                switch(selectedKB[i]) {
                    case DISCOVERERS:
                        selectedKBImages[i] = ImageLoader.obj.loadImage("DiscovererCard.png");
                        break;
                    case CITIZENS:
                        selectedKBImages[i] = ImageLoader.obj.loadImage("CitizenCard.png");
                        break;
                    case FARMERS:
                        selectedKBImages[i] = ImageLoader.obj.loadImage("FarmerCard.png");
                        break;
                    case MERCHANTS:
                        selectedKBImages[i] = ImageLoader.obj.loadImage("MerchantCard.png");
                        break;
                    case FISHERMEN:
                        selectedKBImages[i] = ImageLoader.obj.loadImage("FishermenCard.png");
                        break;
                    case HERMITS:
                        selectedKBImages[i] = ImageLoader.obj.loadImage("HermitCard.png");
                        break;
                    case MINERS:
                        selectedKBImages[i] = ImageLoader.obj.loadImage("MinerCard.png");
                        break;
                    case KNIGHTS:
                        selectedKBImages[i] = ImageLoader.obj.loadImage("KnightCard.png");
                        break;
                    case LORDS:
                        selectedKBImages[i] = ImageLoader.obj.loadImage("LordCard.png");
                        break;
                    case WORKERS:
                        selectedKBImages[i] = ImageLoader.obj.loadImage("WorkerCard.png");
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading KB card images");
        }
    }

    public KingdomBuilderType[] getSelectedKB() {
        return selectedKB;
    }

    public BufferedImage[] getSelectedKBImages() {
        return selectedKBImages;
    }
}

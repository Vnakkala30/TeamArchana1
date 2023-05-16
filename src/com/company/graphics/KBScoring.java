package com.company.graphics;

import com.company.game.EndGame;
import com.company.game.GameManager;
import com.company.game.Player;
import com.company.game.types.KingdomBuilderType;
import com.company.game.util.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class KBScoring extends JPanel implements MouseMotionListener, MouseListener {

    private BufferedImage background;
    private BufferedImage[] playerSlots;
    private ArrayList<HashMap<KingdomBuilderType, Integer>> playerScores;
    private GameManager gm;

    public KBScoring(GameManager gm) {
        addMouseListener(this);
        addMouseMotionListener(this);
        this.gm = gm;

        try {
            background = ImageLoader.obj.loadImage("Background.png");

            playerSlots = new BufferedImage[4];
            for (int i = 0; i < 4; i++) {
                playerSlots[i] = ImageLoader.obj.loadImage("Player" + (i + 1) + "Slot.png");
            }

            playerScores = new ArrayList<>();

            for (int i = 0; i < 4; i++) {
                playerScores.add(EndGame.getString(gm.getPlayers()[i], gm.getKbDeck()));
            }
        } catch (Exception e) {
            System.out.println("Error loading background image");
        }
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(background, 0, 0, 1000, 400, null);
        drawText(g);
    }

    public void drawText(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Sans Serif", Font.BOLD, 36));
        g.drawString("SCORING", 20, 50);

        g.setFont(new Font("Sans Serif", Font.BOLD, 24));
        g.setColor(Color.RED);
        g.drawString("Player 1", 30, 120);
        g.setColor(Color.BLACK);
        g.drawString("Player 2", 30, 180);
        g.setColor(Color.BLUE);
        g.drawString("Player 3", 30, 240);
        g.setColor(Color.PINK);
        g.drawString("Player 4", 30, 300);

        g.setFont(new Font("Sans Serif", Font.BOLD, 22));

        for (int i = 0; i < 4; i++) {
            playerScores.add(EndGame.getString(gm.getPlayers()[i], gm.getKbDeck()));
        }

        KingdomBuilderType[] types = playerScores.get(0).keySet().toArray(new KingdomBuilderType[0]);
        int x = 150;
        int y = 90;

        for (KingdomBuilderType kingdomBuilderType : types) {
            g.drawString(kingdomBuilderType.toString(), x, y);
            x += g.getFontMetrics().stringWidth(kingdomBuilderType.toString()) + 50;
        }

        g.drawString("TOTAL", x, y);


        Player[] players = gm.getPlayers();
        x = 150;
        y = 120;

        HashMap<Player, HashMap<KingdomBuilderType, Integer>> playerScores = EndGame.playerScores;
        int[] scores = new int[4];

        // System.out.println(playerScores);

        for (int i = 0; i < players.length; i++) {
            int count = 0;
            for (KingdomBuilderType type : types) {
                g.drawString(String.valueOf(playerScores.get(players[i]).get(type)), x, y);
                x += g.getFontMetrics().stringWidth(types[count].toString()) + 50;
                count++;

                scores[i] += playerScores.get(players[i]).get(type);
            }

            // Get total score
            g.drawString(String.valueOf(scores[i]), x, y);

            x = 150;
            y += 60;
        }

    }

    public void updateScores() {
        repaint();
    }


    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }


}

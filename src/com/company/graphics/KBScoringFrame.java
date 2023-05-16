package com.company.graphics;

import com.company.game.EndGame;
import com.company.game.GameManager;

import javax.swing.*;
import java.awt.*;

public class KBScoringFrame extends JFrame {

    private KBScoring kbPanel;

    public KBScoringFrame(GameManager gm) throws HeadlessException {
        setSize(1000, 400);
        setBackground(Color.white);
        setTitle("Kingdom Builder Scoring");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Resizable is false
        setResizable(false);
        KBScoring kbPanel = new KBScoring(gm);
        EndGame.setKbScoring(kbPanel);
        add(kbPanel);
        setVisible(true);
    }

    public void updateScores() {
        kbPanel.updateScores();
        repaint();
    }

    public static void main(String[] args) {
        new KBScoringFrame(new GameManager());
    }
}

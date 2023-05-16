package com.company.graphics;

import com.company.game.GameManager;
import com.company.game.Player;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;

public class KBFrame extends JFrame {
    public static int WIDTH = 1600, HEIGHT = 960;
    private KBPanel kbPanel;

    public KBFrame(String title, GameManager gm) {
        super(title);
        kbPanel = new KBPanel(gm, this);
        setSize(WIDTH, HEIGHT);
        setBackground(Color.white);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(kbPanel);
        setVisible(true);
    }

    public KBPanel getKBPanel() {
        return kbPanel;
    }

    public static void main(String[] args) {
        GameManager gm = new GameManager();
    }
}

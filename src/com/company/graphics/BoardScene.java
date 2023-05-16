package com.company.graphics;

import javax.swing.*;

public class BoardScene {
    private static JFrame frame;
    private static JPanel panel;

    // Set background image for the board
    public static void setBackground(String path) {
        panel = new JPanel();
        panel.setLayout(null);
        panel.setSize(800, 600);
        panel.setOpaque(false);
        panel.setVisible(true);

        JLabel background = new JLabel();
        background.setIcon(new ImageIcon(path));
        background.setBounds(0, 0, 800, 600);
        background.setVisible(true);

        panel.add(background);
    }
}

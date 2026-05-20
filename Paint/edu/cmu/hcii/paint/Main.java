package edu.cmu.hcii.paint;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        final int width = 800;
        final int height = 600;

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // ignore and continue with default L&F
            }

            new PaintWindow(width, height);
        });
    }
}

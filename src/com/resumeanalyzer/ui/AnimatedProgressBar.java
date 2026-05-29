package com.resumeanalyzer.ui;

import javax.swing.*;
import java.awt.*;
import javax.swing.Timer;

public class AnimatedProgressBar extends JPanel {
    private int targetValue = 0;
    private int currentValue = 0;
    private String label = "";
    private Color barColor;
    private Timer animTimer;

    public AnimatedProgressBar(String label, Color color) {
        this.label = label;
        this.barColor = color;
        setPreferredSize(new Dimension(300, 36));
        setOpaque(false);
    }

    public void setValue(int value) {
        this.targetValue = value;
        this.currentValue = 0;
        if (animTimer != null) animTimer.stop();
        animTimer = new Timer(12, e -> {
            if (currentValue < targetValue) {
                currentValue = Math.min(currentValue + 2, targetValue);
                repaint();
            } else {
                ((Timer)e.getSource()).stop();
            }
        });
        animTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        int barH = 14;
        int barY = h / 2 + 2;

        // Label
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2.setColor(new Color(50, 50, 70));
        g2.drawString(label, 0, barY - 4);

        // Score label (right)
        String scoreLabel = currentValue + "%";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(scoreLabel, w - fm.stringWidth(scoreLabel), barY - 4);

        // Background track
        g2.setColor(new Color(230, 230, 240));
        g2.fillRoundRect(0, barY, w, barH, barH, barH);

        // Progress fill
        int fillW = (int)((w) * (currentValue / 100.0));
        if (fillW > 0) {
            GradientPaint gp = new GradientPaint(0, 0, barColor.brighter(), fillW, 0, barColor.darker());
            g2.setPaint(gp);
            g2.fillRoundRect(0, barY, fillW, barH, barH, barH);
        }

        g2.dispose();
    }
}

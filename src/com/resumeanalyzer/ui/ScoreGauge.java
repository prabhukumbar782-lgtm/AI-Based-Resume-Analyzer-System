package com.resumeanalyzer.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class ScoreGauge extends JPanel {
    private int score = 0;
    private String label = "";
    private Color startColor = new Color(220, 53, 69);
    private Color endColor = new Color(40, 167, 69);

    public ScoreGauge(String label) {
        this.label = label;
        setPreferredSize(new Dimension(200, 200));
        setOpaque(false);
    }

    public void setScore(int score) {
        this.score = score;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        int cx = w / 2, cy = h / 2;
        int r = Math.min(w, h) / 2 - 20;

        // Background arc
        g2.setStroke(new BasicStroke(16, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(230, 230, 235));
        g2.drawArc(cx - r, cy - r, 2 * r, 2 * r, 220, -280);

        // Score arc
        float ratio = score / 100f;
        Color scoreColor = interpolateColor(startColor, endColor, ratio);
        g2.setColor(scoreColor);
        int arcAngle = (int)(-280 * ratio);
        if (arcAngle != 0) {
            g2.drawArc(cx - r, cy - r, 2 * r, 2 * r, 220, arcAngle);
        }

        // Center score text
        g2.setFont(new Font("SansSerif", Font.BOLD, 32));
        g2.setColor(new Color(30, 30, 40));
        String scoreStr = score + "";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(scoreStr, cx - fm.stringWidth(scoreStr) / 2, cy + fm.getAscent() / 2 - 8);

        // Label below score
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g2.setColor(new Color(100, 100, 120));
        String pct = "/ 100";
        FontMetrics fm2 = g2.getFontMetrics();
        g2.drawString(pct, cx - fm2.stringWidth(pct) / 2, cy + 14);

        // Bottom label
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2.setColor(new Color(50, 50, 70));
        FontMetrics fm3 = g2.getFontMetrics();
        g2.drawString(label, cx - fm3.stringWidth(label) / 2, h - 8);

        g2.dispose();
    }

    private Color interpolateColor(Color c1, Color c2, float ratio) {
        int r = (int)(c1.getRed() + (c2.getRed() - c1.getRed()) * ratio);
        int g = (int)(c1.getGreen() + (c2.getGreen() - c1.getGreen()) * ratio);
        int b = (int)(c1.getBlue() + (c2.getBlue() - c1.getBlue()) * ratio);
        return new Color(Math.max(0, Math.min(255, r)), Math.max(0, Math.min(255, g)), Math.max(0, Math.min(255, b)));
    }
}

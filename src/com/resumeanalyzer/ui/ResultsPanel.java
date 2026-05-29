package com.resumeanalyzer.ui;

import com.resumeanalyzer.model.AnalysisResult;
import com.resumeanalyzer.model.Resume;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.*;

public class ResultsPanel extends JPanel {

    private ScoreGauge overallGauge;
    private Map<String, AnimatedProgressBar> progressBars = new LinkedHashMap<>();
    private JTextArea summaryArea;
    private JPanel skillsMatchPanel;
    private JPanel suggestionsPanel;
    private JPanel strengthsPanel;
    private JLabel gradeLabel;
    private JLabel atsLabel;
    private JLabel levelLabel;
    private JLabel roleLabel;

    private static final Color[] BAR_COLORS = {
        new Color(99, 132, 255),
        new Color(255, 159, 64),
        new Color(75, 192, 192),
        new Color(255, 99, 132),
        new Color(153, 102, 255)
    };

    public ResultsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(248, 249, 252));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
    }

    private void initComponents() {
        // Header info bar
        JPanel headerBar = createHeaderBar();
        add(headerBar, BorderLayout.NORTH);

        // Center content (scrollable)
        JPanel centerContent = new JPanel(new GridBagLayout());
        centerContent.setBackground(new Color(248, 249, 252));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Left column: gauge + scores
        JPanel leftCol = new JPanel(new BorderLayout(8, 8));
        leftCol.setOpaque(false);
        overallGauge = new ScoreGauge("Overall Score");
        overallGauge.setPreferredSize(new Dimension(200, 200));
        leftCol.add(overallGauge, BorderLayout.NORTH);
        leftCol.add(createScoreBarsPanel(), BorderLayout.CENTER);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.38; gbc.weighty = 0.5;
        centerContent.add(leftCol, gbc);

        // Right column: summary + skills
        JPanel rightCol = new JPanel(new BorderLayout(8, 8));
        rightCol.setOpaque(false);
        rightCol.add(createSummaryPanel(), BorderLayout.NORTH);
        rightCol.add(createSkillsPanel(), BorderLayout.CENTER);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.62;
        centerContent.add(rightCol, gbc);

        // Bottom: suggestions + strengths/weaknesses
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; gbc.weightx = 1.0; gbc.weighty = 0.5;
        centerContent.add(createBottomPanel(), gbc);

        JScrollPane scroll = new JScrollPane(centerContent);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBackground(new Color(248, 249, 252));
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel createHeaderBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 8));
        bar.setBackground(new Color(30, 32, 48));
        bar.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

        gradeLabel = createInfoChip("Grade: --", new Color(255, 193, 7));
        atsLabel = createInfoChip("ATS: --", new Color(40, 167, 69));
        levelLabel = createInfoChip("Level: --", new Color(99, 132, 255));
        roleLabel = createInfoChip("Role: --", new Color(255, 99, 132));

        bar.add(roleLabel);
        bar.add(levelLabel);
        bar.add(gradeLabel);
        bar.add(atsLabel);
        return bar;
    }

    private JLabel createInfoChip(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(color.darker(), 1, true),
            BorderFactory.createEmptyBorder(3, 10, 3, 10)
        ));
        return label;
    }

    private JPanel createScoreBarsPanel() {
        JPanel panel = createCard("Category Scores");
        JPanel barsContainer = new JPanel();
        barsContainer.setLayout(new BoxLayout(barsContainer, BoxLayout.Y_AXIS));
        barsContainer.setOpaque(false);

        String[] categories = {"Skills Match", "Experience", "Education", "Formatting", "Keywords"};
        for (int i = 0; i < categories.length; i++) {
            AnimatedProgressBar bar = new AnimatedProgressBar(categories[i], BAR_COLORS[i]);
            bar.setPreferredSize(new Dimension(280, 40));
            bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            progressBars.put(categories[i], bar);
            barsContainer.add(bar);
            if (i < categories.length - 1) barsContainer.add(Box.createVerticalStrut(4));
        }
        panel.add(barsContainer, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSummaryPanel() {
        JPanel panel = createCard("AI Analysis Summary");
        summaryArea = new JTextArea(5, 30);
        summaryArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        summaryArea.setForeground(new Color(40, 40, 60));
        summaryArea.setBackground(new Color(248, 249, 252));
        summaryArea.setLineWrap(true);
        summaryArea.setWrapStyleWord(true);
        summaryArea.setEditable(false);
        summaryArea.setBorder(null);
        summaryArea.setText("Run analysis to see results here.");
        panel.add(new JScrollPane(summaryArea) {{ setBorder(null); setBackground(new Color(248,249,252)); }}, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSkillsPanel() {
        JPanel outer = new JPanel(new GridLayout(1, 2, 8, 0));
        outer.setOpaque(false);

        JPanel matchPanel = createCard("✅ Matched Skills");
        skillsMatchPanel = new JPanel();
        skillsMatchPanel.setLayout(new WrapLayout(FlowLayout.LEFT, 4, 4));
        skillsMatchPanel.setOpaque(false);
        JScrollPane sp1 = new JScrollPane(skillsMatchPanel); sp1.setBorder(null); sp1.setBackground(new Color(248,249,252));
        matchPanel.add(sp1, BorderLayout.CENTER);

        JPanel missingPanel = createCard("❌ Missing Skills");
        suggestionsPanel = new JPanel();
        suggestionsPanel.setLayout(new WrapLayout(FlowLayout.LEFT, 4, 4));
        suggestionsPanel.setOpaque(false);
        JScrollPane sp2 = new JScrollPane(suggestionsPanel); sp2.setBorder(null); sp2.setBackground(new Color(248,249,252));
        missingPanel.add(sp2, BorderLayout.CENTER);

        outer.add(matchPanel);
        outer.add(missingPanel);
        return outer;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 8, 0));
        panel.setOpaque(false);

        // Suggestions
        JPanel sugPanel = createCard("💡 Improvement Suggestions");
        strengthsPanel = new JPanel();
        strengthsPanel.setLayout(new BoxLayout(strengthsPanel, BoxLayout.Y_AXIS));
        strengthsPanel.setOpaque(false);
        JScrollPane sp = new JScrollPane(strengthsPanel); sp.setBorder(null); sp.setBackground(new Color(248,249,252));
        sugPanel.add(sp, BorderLayout.CENTER);

        // Strengths/weaknesses
        JPanel swPanel = createCard("📊 Strengths & Weaknesses");
        JPanel swContainer = new JPanel(new GridLayout(1, 2, 8, 0));
        swContainer.setOpaque(false);

        JPanel strPanel = new JPanel();
        strPanel.setLayout(new BoxLayout(strPanel, BoxLayout.Y_AXIS));
        strPanel.setOpaque(false);
        strPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(40,167,69), 1, true), "Strengths"));

        JPanel weakPanel = new JPanel();
        weakPanel.setLayout(new BoxLayout(weakPanel, BoxLayout.Y_AXIS));
        weakPanel.setOpaque(false);
        weakPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(220,53,69), 1, true), "Weaknesses"));

        swContainer.add(strPanel);
        swContainer.add(weakPanel);

        JScrollPane swScroll = new JScrollPane(swContainer); swScroll.setBorder(null);
        swPanel.add(swScroll, BorderLayout.CENTER);

        // Store references for update
        swPanel.putClientProperty("strPanel", strPanel);
        swPanel.putClientProperty("weakPanel", weakPanel);
        panel.putClientProperty("swPanel", swPanel);

        panel.add(sugPanel);
        panel.add(swPanel);

        panel.putClientProperty("sugPanel", sugPanel);
        this.putClientProperty("bottomPanel", panel);

        return panel;
    }

    private JPanel createCard(String title) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 225, 235), 1, true),
            BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        titleLabel.setForeground(new Color(30, 32, 48));
        titleLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 225, 235)));
        card.add(titleLabel, BorderLayout.NORTH);
        return card;
    }

    public void displayResults(AnalysisResult result, Resume resume) {
        // Header chips
        gradeLabel.setText("Grade: " + result.getScoreGrade());
        atsLabel.setText(result.getAtsCompatibility());
        levelLabel.setText("Level: " + result.getExperienceLevel());
        roleLabel.setText("Role: " + result.getJobRole());

        // Gauge
        overallGauge.setScore(result.getOverallScore());

        // Progress bars
        Map<String, Integer> scores = result.getCategoryScores();
        for (Map.Entry<String, AnimatedProgressBar> entry : progressBars.entrySet()) {
            Integer val = scores.get(entry.getKey());
            if (val != null) entry.getValue().setValue(val);
        }

        // Summary
        summaryArea.setText(result.getSummary());

        // Matched skills
        skillsMatchPanel.removeAll();
        for (String skill : result.getMatchedSkills()) {
            skillsMatchPanel.add(createChip(skill, new Color(208, 244, 222), new Color(21, 128, 61)));
        }

        // Missing skills
        suggestionsPanel.removeAll();
        for (String skill : result.getMissingSkills()) {
            suggestionsPanel.add(createChip(skill, new Color(254, 226, 226), new Color(185, 28, 28)));
        }

        // Suggestions & Strengths/Weaknesses
        JPanel bottomPanel = (JPanel) getClientProperty("bottomPanel");
        if (bottomPanel != null) {
            JPanel sugPanel = (JPanel) bottomPanel.getClientProperty("sugPanel");
            JPanel swPanel = (JPanel) bottomPanel.getClientProperty("swPanel");

            if (sugPanel != null) {
                Component[] comps = sugPanel.getComponents();
                for (Component c : comps) if (c instanceof JScrollPane) {
                    JScrollPane sp = (JScrollPane) c;
                    JPanel cont = (JPanel) sp.getViewport().getView();
                    cont.removeAll();
                    for (String sug : result.getSuggestions()) {
                        JLabel lbl = new JLabel("<html><body style='width:280px'>" + sug + "</body></html>");
                        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
                        lbl.setForeground(new Color(40, 60, 80));
                        lbl.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
                        cont.add(lbl);
                    }
                    cont.revalidate();
                    cont.repaint();
                }
            }

            if (swPanel != null) {
                JPanel strPanel = (JPanel) swPanel.getClientProperty("strPanel");
                JPanel weakPanel = (JPanel) swPanel.getClientProperty("weakPanel");
                if (strPanel != null) {
                    strPanel.removeAll();
                    for (String s : result.getStrengths()) {
                        strPanel.add(createBulletLabel("✓ " + s, new Color(21, 128, 61)));
                    }
                    strPanel.revalidate(); strPanel.repaint();
                }
                if (weakPanel != null) {
                    weakPanel.removeAll();
                    for (String w : result.getWeaknesses()) {
                        weakPanel.add(createBulletLabel("✗ " + w, new Color(185, 28, 28)));
                    }
                    weakPanel.revalidate(); weakPanel.repaint();
                }
            }
        }

        revalidate();
        repaint();
    }

    private JLabel createChip(String text, Color bg, Color fg) {
        JLabel chip = new JLabel(text);
        chip.setFont(new Font("SansSerif", Font.PLAIN, 11));
        chip.setForeground(fg);
        chip.setBackground(bg);
        chip.setOpaque(true);
        chip.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(fg.brighter(), 1, true),
            BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));
        return chip;
    }

    private JLabel createBulletLabel(String text, Color color) {
        JLabel label = new JLabel("<html><body style='width:200px'>" + text + "</body></html>");
        label.setFont(new Font("SansSerif", Font.PLAIN, 11));
        label.setForeground(color);
        label.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    // Simple wrap layout for chips
    static class WrapLayout extends FlowLayout {
        public WrapLayout(int align, int hgap, int vgap) { super(align, hgap, vgap); }
        @Override
        public Dimension preferredLayoutSize(Container target) {
            return layoutSize(target, true);
        }
        @Override
        public Dimension minimumLayoutSize(Container target) {
            return layoutSize(target, false);
        }
        private Dimension layoutSize(Container target, boolean preferred) {
            synchronized (target.getTreeLock()) {
                int targetWidth = target.getWidth();
                if (targetWidth == 0) targetWidth = Integer.MAX_VALUE;
                int hgap = getHgap(), vgap = getVgap();
                Insets insets = target.getInsets();
                int maxWidth = targetWidth - insets.left - insets.right;
                int rowWidth = 0, rowHeight = 0, totalHeight = insets.top + insets.bottom + vgap;
                for (int i = 0; i < target.getComponentCount(); i++) {
                    Component c = target.getComponent(i);
                    if (!c.isVisible()) continue;
                    Dimension d = preferred ? c.getPreferredSize() : c.getMinimumSize();
                    if (rowWidth + d.width + hgap > maxWidth) {
                        totalHeight += rowHeight + vgap;
                        rowWidth = d.width + hgap;
                        rowHeight = d.height;
                    } else {
                        rowWidth += d.width + hgap;
                        rowHeight = Math.max(rowHeight, d.height);
                    }
                }
                totalHeight += rowHeight;
                return new Dimension(maxWidth, totalHeight);
            }
        }
    }
}

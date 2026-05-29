package com.resumeanalyzer.ui;

import com.resumeanalyzer.analyzer.ResumeParser;
import com.resumeanalyzer.analyzer.ScoringEngine;
import com.resumeanalyzer.model.AnalysisResult;
import com.resumeanalyzer.model.Resume;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MainWindow extends JFrame {

    private JTextArea resumeInputArea;
    private JComboBox<String> roleComboBox;
    private ResultsPanel resultsPanel;
    private JButton analyzeBtn;
    private JButton clearBtn;
    private JButton loadBtn;
    private JLabel statusLabel;
    private JTabbedPane tabbedPane;

    private final ResumeParser parser = new ResumeParser();
    private final ScoringEngine scorer = new ScoringEngine();

    private static final String TESSERACT_PATH = "C:\\Program Files\\Tesseract-OCR\\tesseract.exe";
    private static final Set<String> IMAGE_EXTENSIONS = new HashSet<>(Arrays.asList(
        "png", "jpg", "jpeg", "gif", "bmp", "tiff", "tif", "webp"
    ));

    // Color palette
    private static final Color PRIMARY = new Color(63, 81, 181);
    private static final Color PRIMARY_DARK = new Color(40, 53, 147);
    private static final Color ACCENT = new Color(255, 193, 7);
    private static final Color BG = new Color(245, 247, 252);
    private static final Color SIDEBAR_BG = new Color(30, 32, 48);

    public MainWindow() {
        setTitle("AI-Based Resume Analyzer System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 820);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        setBackground(BG);

        initUI();
    }

    private void initUI() {
        // Main layout
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        // Header
        root.add(createHeader(), BorderLayout.NORTH);

        // Split: left sidebar + right content
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerSize(4);
        splitPane.setDividerLocation(440);
        splitPane.setBorder(null);
        splitPane.setBackground(BG);

        splitPane.setLeftComponent(createInputPanel());
        splitPane.setRightComponent(createOutputPanel());

        root.add(splitPane, BorderLayout.CENTER);
        root.add(createStatusBar(), BorderLayout.SOUTH);

        setContentPane(root);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SIDEBAR_BG);
        header.setPreferredSize(new Dimension(0, 64));
        header.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        // Logo + title
        JPanel leftSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftSection.setOpaque(false);

        JLabel iconLabel = new JLabel("🧠");
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 28));

        JPanel titleStack = new JPanel();
        titleStack.setLayout(new BoxLayout(titleStack, BoxLayout.Y_AXIS));
        titleStack.setOpaque(false);

        JLabel mainTitle = new JLabel("AI Resume Analyzer");
        mainTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        mainTitle.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Intelligent Resume Scoring & Feedback System");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 11));
        subtitle.setForeground(new Color(160, 170, 200));

        titleStack.add(mainTitle);
        titleStack.add(subtitle);
        leftSection.add(iconLabel);
        leftSection.add(titleStack);
        header.add(leftSection, BorderLayout.WEST);

        // Right side badge
        JLabel badge = new JLabel("Powered by AI Analysis Engine  v1.0");
        badge.setFont(new Font("SansSerif", Font.ITALIC, 11));
        badge.setForeground(new Color(130, 150, 200));
        header.add(badge, BorderLayout.EAST);

        return header;
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(BG);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 6));

        // Panel title
        JLabel inputTitle = new JLabel("📄  Resume Input");
        inputTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
        inputTitle.setForeground(new Color(30, 32, 60));
        inputTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

        // Role selector
        JPanel rolePanel = createRoleSelector();

        // Text area
        resumeInputArea = new JTextArea();
        resumeInputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        resumeInputArea.setForeground(new Color(30, 40, 60));
        resumeInputArea.setBackground(Color.WHITE);
        resumeInputArea.setLineWrap(true);
        resumeInputArea.setWrapStyleWord(true);
        resumeInputArea.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        resumeInputArea.setText(getSampleResume());
        resumeInputArea.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(resumeInputArea);
        scrollPane.setBorder(new LineBorder(new Color(200, 210, 230), 1, true));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Word count label
        JLabel wordCountLabel = new JLabel("Characters: 0");
        wordCountLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        wordCountLabel.setForeground(new Color(120, 130, 150));
        resumeInputArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                wordCountLabel.setText("Characters: " + resumeInputArea.getText().length());
            }
        });

        // Button panel
        JPanel btnPanel = createButtonPanel();

        JPanel topSection = new JPanel(new BorderLayout(0, 8));
        topSection.setOpaque(false);
        topSection.add(inputTitle, BorderLayout.NORTH);
        topSection.add(rolePanel, BorderLayout.CENTER);

        JPanel bottomInfo = new JPanel(new BorderLayout());
        bottomInfo.setOpaque(false);
        bottomInfo.add(wordCountLabel, BorderLayout.WEST);

        panel.add(topSection, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel southSection = new JPanel(new BorderLayout(0, 6));
        southSection.setOpaque(false);
        southSection.add(bottomInfo, BorderLayout.NORTH);
        southSection.add(btnPanel, BorderLayout.CENTER);

        panel.add(southSection, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createRoleSelector() {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        JLabel roleLabel = new JLabel("Target Role:");
        roleLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        roleLabel.setForeground(new Color(50, 60, 90));

        List<String> roles = scorer.getAvailableRoles();
        roleComboBox = new JComboBox<>(roles.toArray(new String[0]));
        roleComboBox.setFont(new Font("SansSerif", Font.PLAIN, 13));
        roleComboBox.setBackground(Color.WHITE);
        roleComboBox.setBorder(new LineBorder(new Color(200, 210, 230), 1, true));
        roleComboBox.setSelectedIndex(0);

        panel.add(roleLabel, BorderLayout.WEST);
        panel.add(roleComboBox, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 8, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));

        loadBtn = createStyledButton("📂 Load File", new Color(52, 152, 219), Color.WHITE);
        clearBtn = createStyledButton("🗑 Clear", new Color(149, 165, 166), Color.WHITE);
        analyzeBtn = createStyledButton("🔍 Analyze Resume", PRIMARY, Color.WHITE);
        analyzeBtn.setFont(new Font("SansSerif", Font.BOLD, 13));

        panel.add(loadBtn);
        panel.add(clearBtn);
        panel.add(analyzeBtn);

        // Actions
        analyzeBtn.addActionListener(e -> analyzeResume());
        clearBtn.addActionListener(e -> {
            resumeInputArea.setText("");
            statusLabel.setText("Cleared. Paste your resume text and click Analyze.");
        });
        loadBtn.addActionListener(e -> loadFile());

        return panel;
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(bg.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bg.brighter());
                } else {
                    g2.setColor(bg);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 38));
        return btn;
    }

    private JPanel createOutputPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(BG);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 6, 12, 12));

        JLabel outputTitle = new JLabel("📊  Analysis Results");
        outputTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
        outputTitle.setForeground(new Color(30, 32, 60));
        outputTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

        resultsPanel = new ResultsPanel();

        panel.add(outputTitle, BorderLayout.NORTH);
        panel.add(resultsPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(240, 242, 248));
        statusBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(210, 215, 230)),
            BorderFactory.createEmptyBorder(4, 14, 4, 14)
        ));

        statusLabel = new JLabel("Ready. Paste your resume text or load a .txt file, select a target role, and click Analyze.");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        statusLabel.setForeground(new Color(80, 90, 120));

        JLabel versionLabel = new JLabel("AI Resume Analyzer v1.0  |  Java Swing");
        versionLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        versionLabel.setForeground(new Color(130, 140, 170));

        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(versionLabel, BorderLayout.EAST);
        return statusBar;
    }

    private void analyzeResume() {
        String text = resumeInputArea.getText().trim();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please paste your resume text before analyzing.", "Empty Input", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String selectedRole = (String) roleComboBox.getSelectedItem();
        statusLabel.setText("⏳  Analyzing resume for: " + selectedRole + "...");
        analyzeBtn.setEnabled(false);

        SwingWorker<AnalysisResult, Void> worker = new SwingWorker<>() {
            @Override
            protected AnalysisResult doInBackground() {
                Resume resume = parser.parse(text);
                return scorer.analyze(resume, selectedRole);
            }
            @Override
            protected void done() {
                try {
                    AnalysisResult result = get();
                    Resume resume = parser.parse(text);
                    resultsPanel.displayResults(result, resume);
                    statusLabel.setText("✅  Analysis complete! Overall Score: " + result.getOverallScore() + "/100  |  Grade: " + result.getScoreGrade());
                } catch (Exception ex) {
                    statusLabel.setText("❌  Analysis failed: " + ex.getMessage());
                    JOptionPane.showMessageDialog(MainWindow.this, "Error during analysis: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    analyzeBtn.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private void loadFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Supported Files (*.txt, *.csv, *.html, images, etc.)",
            "txt", "csv", "html", "htm", "xml", "json", "md", "rtf", "log", "properties",
            "cfg", "ini", "yaml", "yml", "java", "py", "js", "ts", "cpp", "c", "h", "css", "sql", "sh", "bat",
            "png", "jpg", "jpeg", "gif", "bmp", "tiff", "tif", "webp"));
        chooser.addChoosableFileFilter(chooser.getAcceptAllFileFilter());
        chooser.setDialogTitle("Load Resume File");
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                String content = readFileContent(file);
                resumeInputArea.setText(content);
                resumeInputArea.setCaretPosition(0);
                statusLabel.setText("✅  File loaded: " + file.getName() + "  (" + content.length() + " characters)");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Could not read file: " + ex.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String readFileContent(File file) throws IOException {
        String name = file.getName().toLowerCase();
        String ext = name.contains(".") ? name.substring(name.lastIndexOf('.') + 1) : "";

        // Route image files to OCR
        if (IMAGE_EXTENSIONS.contains(ext)) {
            return ocrImage(file);
        }

        // Detect binary files by checking for null bytes in the first chunk
        try (InputStream is = new FileInputStream(file)) {
            byte[] bytes = new byte[Math.min((int) file.length(), 4096)];
            int read = is.read(bytes);
            for (int i = 0; i < read; i++) {
                if (bytes[i] == 0) {
                    throw new IOException("File appears to be binary (PDF, DOCX, etc.). Please provide a text-based file.");
                }
            }
        }

        // Try reading with multiple encodings
        String[] encodings = {"UTF-8", Charset.defaultCharset().name(), "ISO-8859-1", "Windows-1252"};
        for (String enc : encodings) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), enc))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                return sb.toString();
            } catch (MalformedInputException | UnsupportedEncodingException e) {
                // Try next encoding
            }
        }

        // Final fallback with system default
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        }
    }

    private String ocrImage(File file) throws IOException {
        File tempOut = new File(System.getProperty("java.io.tmpdir"), "ocr_" + System.currentTimeMillis());
        String outPrefix = tempOut.getAbsolutePath();

        ProcessBuilder pb = new ProcessBuilder(
            TESSERACT_PATH,
            file.getAbsolutePath(),
            outPrefix,
            "-l", "eng",
            "--psm", "6"
        );
        pb.redirectErrorStream(false);

        Process process = pb.start();
        try {
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                try (BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String errMsg = err.lines().collect(Collectors.joining("\n"));
                    throw new IOException("Tesseract OCR failed (exit " + exitCode + "): " + errMsg);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("OCR was interrupted", e);
        }

        File outFile = new File(outPrefix + ".txt");
        if (!outFile.exists()) {
            throw new IOException("OCR produced no output file. Is Tesseract installed at " + TESSERACT_PATH + "?");
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(outFile))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            String text = sb.toString().trim();
            if (text.isEmpty()) {
                throw new IOException("OCR extracted no text from the image. The image may be blank or unreadable.");
            }
            return text;
        } finally {
            outFile.delete();
        }
    }

    private String getSampleResume() {
        return "John Doe\njohn.doe@email.com | +91 9876543210\nLinkedIn: linkedin.com/in/johndoe | GitHub: github.com/johndoe\n\n" +
               "OBJECTIVE\nPassionate Java Developer with 3+ years of experience building scalable web applications " +
               "using Spring Boot, Microservices, and REST APIs. Seeking to leverage strong backend expertise.\n\n" +
               "EDUCATION\nBachelor of Technology in Computer Science\nVIT University, Vellore | 2018-2022 | CGPA: 8.4\n\n" +
               "WORK EXPERIENCE\nSoftware Engineer – Infosys, Bangalore | 2022 - Present\n" +
               "• Developed and maintained 5+ RESTful microservices using Spring Boot and Hibernate\n" +
               "• Reduced API response time by 40% through query optimization and Redis caching\n" +
               "• Collaborated with cross-functional teams in an Agile/Scrum environment\n" +
               "• Deployed applications to AWS EC2 using Docker and Jenkins CI/CD pipeline\n\n" +
               "Junior Developer – TCS, Pune | 2021 - 2022\n" +
               "• Implemented backend modules using Java 11 and Spring MVC\n" +
               "• Wrote unit tests with JUnit achieving 85% code coverage\n" +
               "• Worked with MySQL and MongoDB for data persistence\n\n" +
               "SKILLS\nJava, Spring Boot, Spring MVC, Hibernate, REST API, Microservices, SQL, MySQL, " +
               "MongoDB, Redis, Docker, Jenkins, Git, AWS, Agile, Scrum, Maven\n\n" +
               "CERTIFICATIONS\nAWS Certified Developer – Associate (2023)\nOracle Certified Java Programmer (2022)\n\n" +
               "PROJECTS\nE-Commerce Platform – Built a full-stack e-commerce app with Spring Boot backend, " +
               "serving 10,000+ users. Integrated payment gateway and real-time inventory management.";
    }
}

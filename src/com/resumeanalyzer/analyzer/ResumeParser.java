package com.resumeanalyzer.analyzer;

import com.resumeanalyzer.model.Resume;
import java.util.*;
import java.util.regex.*;

public class ResumeParser {

    private static final Set<String> TECH_SKILLS = new HashSet<>(Arrays.asList(
        "java", "python", "javascript", "typescript", "c++", "c#", "kotlin", "swift",
        "react", "angular", "vue", "node.js", "spring", "spring boot", "django", "flask",
        "html", "css", "sql", "mysql", "postgresql", "mongodb", "redis", "elasticsearch",
        "aws", "azure", "gcp", "docker", "kubernetes", "jenkins", "git", "github", "gitlab",
        "machine learning", "deep learning", "tensorflow", "pytorch", "scikit-learn", "nlp",
        "data science", "data analysis", "hadoop", "spark", "kafka", "rest api", "graphql",
        "microservices", "agile", "scrum", "devops", "ci/cd", "linux", "bash", "powershell",
        "android", "ios", "flutter", "react native", "php", "ruby", "rails", "scala",
        "tableau", "power bi", "excel", "r", "matlab", "opencv", "keras", "pandas", "numpy"
    ));

    private static final Set<String> SOFT_SKILLS = new HashSet<>(Arrays.asList(
        "leadership", "communication", "teamwork", "problem solving", "critical thinking",
        "time management", "adaptability", "creativity", "collaboration", "project management",
        "analytical", "detail oriented", "self motivated", "multitasking", "presentation"
    ));

    public Resume parse(String text) {
        Resume resume = new Resume();
        resume.setRawText(text);
        String lowerText = text.toLowerCase();

        resume.setCandidateName(extractName(text));
        resume.setEmail(extractEmail(text));
        resume.setPhone(extractPhone(text));
        resume.setSkills(extractSkills(lowerText));
        resume.setEducation(extractEducation(text));
        resume.setExperience(extractExperience(text));
        resume.setCertifications(extractCertifications(text));
        resume.setExperienceYears(estimateExperienceYears(text));

        return resume;
    }

    private String extractName(String text) {
        String[] lines = text.split("\\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            // First non-empty line that looks like a name (2-4 words, all letters)
            if (line.matches("[A-Z][a-zA-Z]+(\\s[A-Z][a-zA-Z]+){1,3}")) {
                return line;
            }
        }
        return "Unknown Candidate";
    }

    private String extractEmail(String text) {
        Pattern emailPattern = Pattern.compile("[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}");
        Matcher matcher = emailPattern.matcher(text);
        if (matcher.find()) return matcher.group();
        return "Not found";
    }

    private String extractPhone(String text) {
        Pattern phonePattern = Pattern.compile("(\\+?\\d{1,3}[\\s\\-]?)?(\\(?\\d{3}\\)?[\\s\\-]?\\d{3}[\\s\\-]?\\d{4})");
        Matcher matcher = phonePattern.matcher(text);
        if (matcher.find()) return matcher.group();
        return "Not found";
    }

    private List<String> extractSkills(String lowerText) {
        List<String> found = new ArrayList<>();
        for (String skill : TECH_SKILLS) {
            if (lowerText.contains(skill)) {
                found.add(capitalize(skill));
            }
        }
        for (String skill : SOFT_SKILLS) {
            if (lowerText.contains(skill)) {
                found.add(capitalize(skill));
            }
        }
        return found;
    }

    private List<String> extractEducation(String text) {
        List<String> education = new ArrayList<>();
        String[] lines = text.split("\\n");
        boolean inEducationSection = false;
        for (String line : lines) {
            String lowerLine = line.toLowerCase().trim();
            if (lowerLine.contains("education") || lowerLine.contains("academic")) {
                inEducationSection = true;
                continue;
            }
            if (inEducationSection && (lowerLine.contains("experience") || lowerLine.contains("skills") || lowerLine.contains("project"))) {
                inEducationSection = false;
            }
            if (inEducationSection && !line.trim().isEmpty() && line.trim().length() > 5) {
                education.add(line.trim());
            }
            // Always capture degree lines
            if (line.matches("(?i).*(bachelor|master|b\\.?tech|m\\.?tech|b\\.?e|m\\.?e|phd|mba|bsc|msc|b\\.?com|diploma).*")) {
                if (!education.contains(line.trim())) education.add(line.trim());
            }
        }
        return education;
    }

    private List<String> extractExperience(String text) {
        List<String> experience = new ArrayList<>();
        String[] lines = text.split("\\n");
        boolean inExpSection = false;
        for (String line : lines) {
            String lowerLine = line.toLowerCase().trim();
            if (lowerLine.matches(".*(work experience|professional experience|employment|career history).*")) {
                inExpSection = true;
                continue;
            }
            if (inExpSection && (lowerLine.contains("education") || lowerLine.contains("skills") || lowerLine.contains("certification"))) {
                inExpSection = false;
            }
            if (inExpSection && !line.trim().isEmpty() && line.trim().length() > 5) {
                experience.add(line.trim());
            }
        }
        return experience;
    }

    private List<String> extractCertifications(String text) {
        List<String> certs = new ArrayList<>();
        String[] lines = text.split("\\n");
        boolean inCertSection = false;
        for (String line : lines) {
            String lowerLine = line.toLowerCase().trim();
            if (lowerLine.contains("certification") || lowerLine.contains("certificate") || lowerLine.contains("credential")) {
                inCertSection = true;
                continue;
            }
            if (inCertSection && !line.trim().isEmpty()) {
                if (line.trim().length() > 3) certs.add(line.trim());
            }
            if (line.matches("(?i).*(aws certified|google certified|oracle certified|microsoft certified|pmp|cissp|ceh|ccna|scrum master).*")) {
                if (!certs.contains(line.trim())) certs.add(line.trim());
            }
        }
        return certs;
    }

    private int estimateExperienceYears(String text) {
        Pattern yearRange = Pattern.compile("(\\d{4})\\s*[-–]\\s*(\\d{4}|present|current)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = yearRange.matcher(text);
        int totalYears = 0;
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        while (matcher.find()) {
            try {
                int start = Integer.parseInt(matcher.group(1));
                String endStr = matcher.group(2).toLowerCase();
                int end = (endStr.equals("present") || endStr.equals("current")) ? currentYear : Integer.parseInt(matcher.group(2));
                if (end >= start && start >= 1990 && end <= currentYear + 1) {
                    totalYears += (end - start);
                }
            } catch (NumberFormatException ignored) {}
        }

        // Also look for explicit mentions like "5 years of experience"
        Pattern explicit = Pattern.compile("(\\d+)\\+?\\s*years?\\s*(of\\s*)?experience", Pattern.CASE_INSENSITIVE);
        Matcher em = explicit.matcher(text);
        if (em.find() && totalYears == 0) {
            try { totalYears = Integer.parseInt(em.group(1)); } catch (NumberFormatException ignored) {}
        }
        return totalYears;
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        String[] parts = s.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(Character.toUpperCase(p.charAt(0))).append(p.substring(1));
        }
        return sb.toString();
    }

    public Set<String> getTechSkillSet() { return TECH_SKILLS; }
    public Set<String> getSoftSkillSet() { return SOFT_SKILLS; }
}

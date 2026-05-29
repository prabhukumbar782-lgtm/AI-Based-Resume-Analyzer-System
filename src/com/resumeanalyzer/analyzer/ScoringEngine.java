package com.resumeanalyzer.analyzer;

import com.resumeanalyzer.model.Resume;
import com.resumeanalyzer.model.AnalysisResult;
import java.util.*;

public class ScoringEngine {

    // Role-based required skills map
    private static final Map<String, List<String>> ROLE_SKILLS = new LinkedHashMap<>();
    static {
        ROLE_SKILLS.put("Java Developer", Arrays.asList("Java", "Spring", "Spring Boot", "Maven", "Hibernate", "SQL", "Git", "REST API", "Microservices", "Docker"));
        ROLE_SKILLS.put("Data Scientist", Arrays.asList("Python", "Machine Learning", "Deep Learning", "Pandas", "Numpy", "Tensorflow", "Scikit-Learn", "SQL", "Data Analysis", "NLP"));
        ROLE_SKILLS.put("Frontend Developer", Arrays.asList("HTML", "CSS", "JavaScript", "React", "Angular", "Vue", "TypeScript", "Git", "REST API"));
        ROLE_SKILLS.put("Full Stack Developer", Arrays.asList("Java", "JavaScript", "React", "Spring Boot", "SQL", "REST API", "Git", "Docker", "Node.js"));
        ROLE_SKILLS.put("DevOps Engineer", Arrays.asList("Docker", "Kubernetes", "Jenkins", "AWS", "CI/CD", "Linux", "Bash", "Git", "Terraform", "Ansible"));
        ROLE_SKILLS.put("Android Developer", Arrays.asList("Java", "Kotlin", "Android", "REST API", "Git", "SQL", "Firebase", "XML"));
        ROLE_SKILLS.put("Data Analyst", Arrays.asList("SQL", "Python", "Excel", "Tableau", "Power BI", "Data Analysis", "R", "Statistics"));
        ROLE_SKILLS.put("ML Engineer", Arrays.asList("Python", "Machine Learning", "Tensorflow", "Pytorch", "Deep Learning", "Scikit-Learn", "AWS", "Docker", "Spark"));
    }

    public AnalysisResult analyze(Resume resume, String targetRole) {
        AnalysisResult result = new AnalysisResult();
        result.setJobRole(targetRole);

        // Determine experience level
        result.setExperienceLevel(determineExperienceLevel(resume.getExperienceYears()));

        // Score each category
        int skillsScore = scoreSkills(resume, targetRole, result);
        int expScore = scoreExperience(resume);
        int eduScore = scoreEducation(resume);
        int fmtScore = scoreFormatting(resume);
        int kwScore = scoreKeywords(resume, targetRole);

        result.setSkillsScore(skillsScore);
        result.setExperienceScore(expScore);
        result.setEducationScore(eduScore);
        result.setFormattingScore(fmtScore);
        result.setKeywordScore(kwScore);

        // Weighted overall score
        int overall = (int)(skillsScore * 0.35 + expScore * 0.25 + eduScore * 0.20 + fmtScore * 0.10 + kwScore * 0.10);
        result.setOverallScore(overall);

        // Category scores map for display
        result.getCategoryScores().put("Skills Match", skillsScore);
        result.getCategoryScores().put("Experience", expScore);
        result.getCategoryScores().put("Education", eduScore);
        result.getCategoryScores().put("Formatting", fmtScore);
        result.getCategoryScores().put("Keywords", kwScore);

        // ATS compatibility
        result.setAtsCompatibility(determineAtsScore(fmtScore, kwScore, skillsScore));

        // Strengths & Weaknesses
        generateStrengthsWeaknesses(result, resume);

        // Suggestions
        generateSuggestions(result, resume, targetRole);

        // Summary
        result.setSummary(generateSummary(result, resume));

        return result;
    }

    private int scoreSkills(Resume resume, String targetRole, AnalysisResult result) {
        List<String> required = ROLE_SKILLS.getOrDefault(targetRole, new ArrayList<>());
        if (required.isEmpty()) {
            result.getMatchedSkills().addAll(resume.getSkills());
            return Math.min(100, resume.getSkills().size() * 7);
        }

        List<String> resumeSkillsLower = new ArrayList<>();
        for (String s : resume.getSkills()) resumeSkillsLower.add(s.toLowerCase());

        List<String> matched = new ArrayList<>();
        List<String> missing = new ArrayList<>();

        for (String req : required) {
            boolean found = resumeSkillsLower.contains(req.toLowerCase()) ||
                            resume.getRawText().toLowerCase().contains(req.toLowerCase());
            if (found) matched.add(req);
            else missing.add(req);
        }

        result.setMatchedSkills(matched);
        result.setMissingSkills(missing);

        int baseScore = (int)(((double)matched.size() / required.size()) * 90);
        // Bonus for extra skills
        int bonus = Math.min(10, (resume.getSkills().size() - matched.size()) / 2);
        return Math.min(100, baseScore + bonus);
    }

    private int scoreExperience(Resume resume) {
        int years = resume.getExperienceYears();
        int expLines = resume.getExperience().size();

        int score = 0;
        if (years == 0) score = expLines > 2 ? 40 : 20;
        else if (years <= 1) score = 45;
        else if (years <= 3) score = 65;
        else if (years <= 5) score = 80;
        else if (years <= 8) score = 90;
        else score = 95;

        // Bonus for detailed experience section
        if (expLines > 5) score = Math.min(100, score + 5);
        return score;
    }

    private int scoreEducation(Resume resume) {
        String rawLower = resume.getRawText().toLowerCase();
        int score = 30; // base

        if (rawLower.contains("phd") || rawLower.contains("doctorate")) score = 100;
        else if (rawLower.contains("master") || rawLower.contains("m.tech") || rawLower.contains("mba") || rawLower.contains("m.e")) score = 85;
        else if (rawLower.contains("bachelor") || rawLower.contains("b.tech") || rawLower.contains("b.e") || rawLower.contains("bsc") || rawLower.contains("b.com")) score = 70;
        else if (rawLower.contains("diploma")) score = 55;

        // Bonus for reputed institutions keywords
        if (rawLower.contains("iit") || rawLower.contains("nit") || rawLower.contains("bits") || rawLower.contains("mit") || rawLower.contains("stanford")) score = Math.min(100, score + 10);

        // Bonus for GPA/CGPA
        if (rawLower.contains("cgpa") || rawLower.contains("gpa")) score = Math.min(100, score + 5);

        if (!resume.getCertifications().isEmpty()) score = Math.min(100, score + 5);
        return score;
    }

    private int scoreFormatting(Resume resume) {
        String raw = resume.getRawText();
        int score = 50;

        // Check for key sections
        String lower = raw.toLowerCase();
        if (lower.contains("objective") || lower.contains("summary") || lower.contains("profile")) score += 10;
        if (lower.contains("experience") || lower.contains("employment")) score += 10;
        if (lower.contains("education")) score += 10;
        if (lower.contains("skills")) score += 10;
        if (resume.getEmail() != null && !resume.getEmail().equals("Not found")) score += 5;
        if (resume.getPhone() != null && !resume.getPhone().equals("Not found")) score += 5;

        return Math.min(100, score);
    }

    private int scoreKeywords(Resume resume, String targetRole) {
        String rawLower = resume.getRawText().toLowerCase();
        String roleLower = targetRole.toLowerCase();

        // Count action verbs
        String[] actionVerbs = {"developed", "designed", "implemented", "built", "created", "managed", "led",
                "optimized", "improved", "increased", "reduced", "delivered", "achieved", "collaborated",
                "analyzed", "architected", "deployed", "maintained", "tested", "integrated"};
        int verbCount = 0;
        for (String verb : actionVerbs) {
            if (rawLower.contains(verb)) verbCount++;
        }

        // Check quantification
        boolean hasNumbers = rawLower.matches(".*\\d+%.*") || rawLower.matches(".*\\$\\d+.*")
                || rawLower.matches(".*\\d+\\s*(million|billion|users|customers|projects).*");

        int score = Math.min(70, verbCount * 5);
        if (hasNumbers) score += 15;
        if (rawLower.contains(roleLower.split(" ")[0])) score += 10;
        if (rawLower.contains("linkedin") || rawLower.contains("github.com")) score += 5;

        return Math.min(100, score);
    }

    private String determineExperienceLevel(int years) {
        if (years == 0) return "Fresher / Entry Level";
        if (years <= 2) return "Junior (1-2 years)";
        if (years <= 5) return "Mid-Level (3-5 years)";
        if (years <= 8) return "Senior (6-8 years)";
        return "Expert / Lead (8+ years)";
    }

    private String determineAtsScore(int fmt, int kw, int skills) {
        int ats = (int)(fmt * 0.4 + kw * 0.3 + skills * 0.3);
        if (ats >= 75) return "High ATS Compatibility (" + ats + "%)";
        if (ats >= 55) return "Moderate ATS Compatibility (" + ats + "%)";
        return "Low ATS Compatibility (" + ats + "%) – Needs Improvement";
    }

    private void generateStrengthsWeaknesses(AnalysisResult result, Resume resume) {
        if (result.getSkillsScore() >= 70) result.getStrengths().add("Strong technical skill set for the target role");
        if (result.getExperienceScore() >= 70) result.getStrengths().add("Solid professional experience");
        if (result.getEducationScore() >= 80) result.getStrengths().add("Strong educational background");
        if (!resume.getCertifications().isEmpty()) result.getStrengths().add("Industry certifications present");
        if (result.getMatchedSkills().size() >= 5) result.getStrengths().add("Good keyword alignment with job requirements");
        if (result.getFormattingScore() >= 80) result.getStrengths().add("Well-structured resume format");

        if (result.getSkillsScore() < 50) result.getWeaknesses().add("Missing many key skills for target role");
        if (result.getExperienceScore() < 50) result.getWeaknesses().add("Limited work experience");
        if (result.getEducationScore() < 60) result.getWeaknesses().add("Educational qualifications could be stronger");
        if (result.getFormattingScore() < 60) result.getWeaknesses().add("Resume lacks proper sections or structure");
        if (result.getKeywordScore() < 50) result.getWeaknesses().add("Insufficient action verbs and quantified achievements");
        if (resume.getCertifications().isEmpty()) result.getWeaknesses().add("No certifications mentioned");
    }

    private void generateSuggestions(AnalysisResult result, Resume resume, String targetRole) {
        if (!result.getMissingSkills().isEmpty()) {
            result.getSuggestions().add("🎯 Learn missing skills: " + String.join(", ", result.getMissingSkills().subList(0, Math.min(3, result.getMissingSkills().size()))));
        }
        if (result.getKeywordScore() < 60) {
            result.getSuggestions().add("💡 Add more action verbs (e.g., Developed, Designed, Optimized, Led)");
        }
        if (result.getKeywordScore() < 70) {
            result.getSuggestions().add("📊 Quantify achievements with numbers, percentages, or impact metrics");
        }
        if (resume.getCertifications().isEmpty()) {
            result.getSuggestions().add("🏆 Add relevant certifications (AWS, Google, Oracle, Scrum, etc.)");
        }
        if (result.getFormattingScore() < 70) {
            result.getSuggestions().add("📝 Ensure resume has all sections: Summary, Skills, Experience, Education");
        }
        if (resume.getExperience().isEmpty() || resume.getExperienceYears() == 0) {
            result.getSuggestions().add("🚀 Add internships, freelance projects, or open-source contributions");
        }
        if (!resume.getRawText().toLowerCase().contains("github") && !resume.getRawText().toLowerCase().contains("linkedin")) {
            result.getSuggestions().add("🔗 Include links to GitHub profile and LinkedIn URL");
        }
        result.getSuggestions().add("📋 Tailor resume keywords to match the exact job description");
        result.getSuggestions().add("🎨 Keep resume to 1-2 pages with consistent formatting");
    }

    private String generateSummary(AnalysisResult result, Resume resume) {
        String name = resume.getCandidateName();
        String grade = result.getScoreGrade();
        int score = result.getOverallScore();
        String level = result.getExperienceLevel();

        return String.format(
            "%s's resume has been analyzed for the %s role.\n\n" +
            "Overall Assessment: %s (%d/100)\n" +
            "Experience Level: %s\n\n" +
            "The candidate demonstrates %s skill alignment with %d matching and %d missing key skills. " +
            "%s",
            name, result.getJobRole(), grade, score, level,
            result.getMatchedSkills().size() > 5 ? "strong" : result.getMatchedSkills().size() > 2 ? "moderate" : "limited",
            result.getMatchedSkills().size(), result.getMissingSkills().size(),
            score >= 70 ? "This resume is well-positioned for the target role." :
            score >= 50 ? "With some improvements, this resume can be competitive." :
            "Significant improvements are needed to be competitive for this role."
        );
    }

    public List<String> getAvailableRoles() {
        return new ArrayList<>(ROLE_SKILLS.keySet());
    }
}

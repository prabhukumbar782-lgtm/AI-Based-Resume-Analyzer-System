package com.resumeanalyzer.model;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;

public class AnalysisResult {
    private int overallScore;
    private int skillsScore;
    private int experienceScore;
    private int educationScore;
    private int formattingScore;
    private int keywordScore;

    private String jobRole;
    private String experienceLevel;
    private List<String> matchedSkills;
    private List<String> missingSkills;
    private List<String> suggestions;
    private List<String> strengths;
    private List<String> weaknesses;
    private Map<String, Integer> categoryScores;
    private String summary;
    private String atsCompatibility;

    public AnalysisResult() {
        matchedSkills = new ArrayList<>();
        missingSkills = new ArrayList<>();
        suggestions = new ArrayList<>();
        strengths = new ArrayList<>();
        weaknesses = new ArrayList<>();
        categoryScores = new LinkedHashMap<>();
    }

    public int getOverallScore() { return overallScore; }
    public void setOverallScore(int overallScore) { this.overallScore = overallScore; }

    public int getSkillsScore() { return skillsScore; }
    public void setSkillsScore(int skillsScore) { this.skillsScore = skillsScore; }

    public int getExperienceScore() { return experienceScore; }
    public void setExperienceScore(int experienceScore) { this.experienceScore = experienceScore; }

    public int getEducationScore() { return educationScore; }
    public void setEducationScore(int educationScore) { this.educationScore = educationScore; }

    public int getFormattingScore() { return formattingScore; }
    public void setFormattingScore(int formattingScore) { this.formattingScore = formattingScore; }

    public int getKeywordScore() { return keywordScore; }
    public void setKeywordScore(int keywordScore) { this.keywordScore = keywordScore; }

    public String getJobRole() { return jobRole; }
    public void setJobRole(String jobRole) { this.jobRole = jobRole; }

    public String getExperienceLevel() { return experienceLevel; }
    public void setExperienceLevel(String experienceLevel) { this.experienceLevel = experienceLevel; }

    public List<String> getMatchedSkills() { return matchedSkills; }
    public void setMatchedSkills(List<String> matchedSkills) { this.matchedSkills = matchedSkills; }

    public List<String> getMissingSkills() { return missingSkills; }
    public void setMissingSkills(List<String> missingSkills) { this.missingSkills = missingSkills; }

    public List<String> getSuggestions() { return suggestions; }
    public void setSuggestions(List<String> suggestions) { this.suggestions = suggestions; }

    public List<String> getStrengths() { return strengths; }
    public void setStrengths(List<String> strengths) { this.strengths = strengths; }

    public List<String> getWeaknesses() { return weaknesses; }
    public void setWeaknesses(List<String> weaknesses) { this.weaknesses = weaknesses; }

    public Map<String, Integer> getCategoryScores() { return categoryScores; }
    public void setCategoryScores(Map<String, Integer> categoryScores) { this.categoryScores = categoryScores; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getAtsCompatibility() { return atsCompatibility; }
    public void setAtsCompatibility(String atsCompatibility) { this.atsCompatibility = atsCompatibility; }

    public String getScoreGrade() {
        if (overallScore >= 85) return "Excellent";
        if (overallScore >= 70) return "Good";
        if (overallScore >= 55) return "Average";
        if (overallScore >= 40) return "Below Average";
        return "Poor";
    }
}

#!/bin/bash
echo "=========================================="
echo "  AI-Based Resume Analyzer System"
echo "=========================================="
echo ""
if ! command -v java &> /dev/null; then
    echo "ERROR: Java is not installed."
    echo "Install with: sudo apt install default-jdk  (Ubuntu)"
    echo "           or brew install openjdk            (Mac)"
    exit 1
fi
echo "Starting Resume Analyzer..."
java -jar ResumeAnalyzer.jar

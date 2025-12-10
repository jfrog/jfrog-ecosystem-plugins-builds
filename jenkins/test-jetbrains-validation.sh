#!/bin/bash

# Test script for JetBrains Plugin Repository validation
# This script helps you test the validation locally before running in Jenkins

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
GROOVY_SCRIPT="${SCRIPT_DIR}/test-jetbrains-plugin-repo-validation.groovy"

echo "=========================================="
echo "JetBrains Plugin Repository Validation Test"
echo "=========================================="
echo ""

# Check if Groovy is installed
if ! command -v groovy &> /dev/null; then
    echo "❌ ERROR: Groovy is not installed or not in PATH"
    echo ""
    echo "Install Groovy:"
    echo "  macOS: brew install groovy"
    echo "  Ubuntu/Debian: sudo apt-get install groovy"
    echo "  Or download from: https://groovy.apache.org/download.html"
    exit 1
fi

echo "✅ Groovy found: $(groovy --version)"
echo ""

# Check if credentials file exists
CREDS_FILE="${SCRIPT_DIR}/teamcity-creds-template.json"
if [ -f "$CREDS_FILE" ]; then
    echo "📄 Found credentials template: $CREDS_FILE"
    echo "   (You can create a test credentials file based on this template)"
    echo ""
fi

# Check for token in environment or prompt
if [ -z "$JETBRAINS_PLUGIN_TOKEN" ]; then
    echo "⚠️  JETBRAINS_PLUGIN_TOKEN environment variable is not set"
    echo ""
    echo "You can either:"
    echo "  1. Set it as an environment variable:"
    echo "     export JETBRAINS_PLUGIN_TOKEN='your-token-here'"
    echo ""
    echo "  2. Or read from a credentials JSON file (create one based on teamcity-creds-template.json):"
    echo "     export JETBRAINS_PLUGIN_TOKEN=\$(cat your-creds.json | jq -r '.secrets.token')"
    echo ""
    read -p "Enter your JetBrains Plugin Repository token (or press Ctrl+C to exit): " TOKEN
    export JETBRAINS_PLUGIN_TOKEN="$TOKEN"
fi

# Optional: Set custom URL
if [ -n "$JETBRAINS_PLUGIN_URL" ]; then
    echo "Using custom URL: $JETBRAINS_PLUGIN_URL"
    export JETBRAINS_PLUGIN_URL="$JETBRAINS_PLUGIN_URL"
else
    echo "Using default URL: https://plugins.jetbrains.com"
fi

echo ""
echo "Running validation script..."
echo ""

# Run the Groovy script
groovy "$GROOVY_SCRIPT"

EXIT_CODE=$?

echo ""
if [ $EXIT_CODE -eq 0 ]; then
    echo "✅ Validation completed successfully!"
else
    echo "❌ Validation failed with exit code: $EXIT_CODE"
fi

exit $EXIT_CODE



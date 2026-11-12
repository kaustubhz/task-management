#!/bin/bash
set -e

echo "=========================================="
echo "🛡️  TaskFlow CVE & Security Scanner"
echo "=========================================="

if ! command -v trivy &> /dev/null; then
    echo "❌ Trivy is not installed. Please install it with 'brew install trivy'."
    exit 1
fi

echo ""
echo "🔍 1. Scanning Frontend dependencies (npm)..."
trivy fs frontend/ --severity HIGH,CRITICAL

echo ""
echo "🔍 2. Scanning Backend container & config misconfigurations..."
trivy config backend/ --severity HIGH,CRITICAL

echo ""
echo "🔍 3. Scanning repository secrets..."
trivy fs . --scanners secret

echo ""
echo "✅ Security scan completed successfully!"


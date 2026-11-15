#!/usr/bin/env bash
# Exporte la spec OpenAPI (API déjà démarrée sur le port 8080).
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
OUT="${ROOT}/generated/openapi.json"
mkdir -p "$(dirname "$OUT")"
curl -fsS "http://localhost:8080/v3/api-docs" -o "$OUT"
echo "Spec écrite dans ${OUT}"
echo "Régénérez les types front avec votre outil (openapi-generator, orval, etc.)"
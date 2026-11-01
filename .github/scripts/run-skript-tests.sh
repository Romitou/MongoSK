#!/usr/bin/env bash
# Runs the Skript test scripts of MongoSK with the given version of Skript, like the SkriptLang/skript-test-action does.
# Usage: run-skript-tests.sh <skript version> <test scripts directory> <plugins directory>
set -euo pipefail

SKRIPT_REF="$1"
TEST_SCRIPTS_DIR="$(realpath "$2")"
PLUGINS_DIR="$(realpath "$3")"
SKRIPT_DIR="${RUNNER_TEMP:-/tmp}/skript"

rm -rf "$SKRIPT_DIR"
git clone --quiet --depth 1 --branch "$SKRIPT_REF" --recurse-submodules --shallow-submodules \
    https://github.com/SkriptLang/Skript.git "$SKRIPT_DIR"
cd "$SKRIPT_DIR"

# Before Skript 2.12, the test platform downloads Paper with the PaperMC v2 API, which has been shut down.
# Use the test environment of Skript 2.12, which downloads it with the current API.
ENVIRONMENT_JAVA=src/main/java/ch/njol/skript/test/platform/Environment.java
if ! grep -q "fill.papermc.io" "$ENVIRONMENT_JAVA"; then
    echo "Patching the Paper download of the Skript test platform"
    curl -sSfL -o "$ENVIRONMENT_JAVA" \
        https://raw.githubusercontent.com/SkriptLang/Skript/2.12.2/src/main/java/ch/njol/skript/test/platform/Environment.java
fi

# Only run the MongoSK tests, not the vanilla Skript ones.
rm -rf src/test/skript/tests/*
cp -r "$TEST_SCRIPTS_DIR" src/test/skript/tests/custom

# Install the plugins on the test servers.
python3 - "$PLUGINS_DIR" <<'PYTHON'
import json, pathlib, sys
plugins = sorted(pathlib.Path(sys.argv[1]).iterdir())
for environment_path in pathlib.Path("src/test/skript/environments").glob("**/*.json"):
    environment = json.loads(environment_path.read_text())
    resources = environment.setdefault("resources", [])
    for plugin in plugins:
        resources.append({"source": str(plugin.resolve()), "target": f"plugins/{plugin.name}"})
    environment_path.write_text(json.dumps(environment))
PYTHON

./gradlew quickTest

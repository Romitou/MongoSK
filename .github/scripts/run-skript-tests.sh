#!/usr/bin/env bash
# Runs the Skript test scripts of MongoSK with the given version of Skript and Minecraft, like the SkriptLang/skript-test-action does.
# Usage: run-skript-tests.sh <skript version> <mc version> <test scripts directory> <plugins directory>
set -euo pipefail

SKRIPT_REF="$1"
MC_VERSION="$2"
TEST_SCRIPTS_DIR="$(realpath "$3")"
PLUGINS_DIR="$(realpath "$4")"
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

# Configure the target Minecraft environment and install the plugins on the test server.
ENV_NAME=$(python3 - "$PLUGINS_DIR" "$MC_VERSION" <<'PYTHON'
import json, pathlib, re, sys

plugins_dir = pathlib.Path(sys.argv[1])
mc_version = sys.argv[2]
env_dir = pathlib.Path("src/test/skript/environments")

# Find environments matching paper-{mc_version}
candidates = [
    f for f in env_dir.glob("**/*.json")
    if f.name == f"paper-{mc_version}.json"
    or f.name.startswith(f"paper-{mc_version}.")
    or f.name.startswith(f"paper-{mc_version}-")
]

if not candidates:
    print(f"Error: No environment matching MC {mc_version} found in {env_dir}", file=sys.stderr)
    sys.exit(1)

def version_key(p):
    m = re.search(r'paper-(\d+(?:\.\d+)*)', p.name)
    return [int(x) for x in m.group(1).split('.')] if m else []

chosen = sorted(candidates, key=version_key)[-1]
env_rel = str(chosen.relative_to(env_dir).with_suffix(""))
print(env_rel)

# Set testEnv in gradle.properties
gradle_props = pathlib.Path("gradle.properties")
if gradle_props.exists():
    lines = gradle_props.read_text().splitlines()
    new_lines = []
    found = False
    for line in lines:
        if line.startswith("testEnv="):
            new_lines.append(f"testEnv={env_rel}")
            found = True
        else:
            new_lines.append(line)
    if not found:
        new_lines.append(f"testEnv={env_rel}")
    gradle_props.write_text("\n".join(new_lines) + "\n")

# Remove other environment files so only chosen one is loaded/run
for env_file in env_dir.glob("**/*.json"):
    if env_file != chosen:
        env_file.unlink()

# Install plugins in the chosen environment
plugins = [p for p in sorted(plugins_dir.glob("*.jar")) if not p.name.endswith("-sources.jar") and not p.name.endswith("-javadoc.jar")]
if not plugins:
    plugins = sorted(plugins_dir.iterdir())
environment = json.loads(chosen.read_text())
resources = environment.setdefault("resources", [])
for plugin in plugins:
    resources.append({"source": str(plugin.resolve()), "target": f"plugins/{plugin.name}"})
chosen.write_text(json.dumps(environment, indent=2))
PYTHON
)

echo "Running Skript tests on environment: $ENV_NAME"
./gradlew quickTest -PtestEnv="$ENV_NAME"

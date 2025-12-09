#!/bin/bash
# Script to download and set up JavaFX for macOS

echo "Setting up JavaFX for macOS..."

JAVAFX_VERSION="21"
JAVAFX_DIR="./javafx-sdk-$JAVAFX_VERSION"

# Detect architecture
ARCH=$(uname -m)
if [ "$ARCH" = "arm64" ]; then
    ARCH_TYPE="osx-aarch64"
    echo "Detected Apple Silicon (ARM64) Mac"
else
    ARCH_TYPE="osx-x64"
    echo "Detected Intel (x86_64) Mac"
fi

if [ -d "$JAVAFX_DIR" ]; then
    echo "JavaFX SDK already exists at $JAVAFX_DIR"
    echo "Note: If you're having graphics issues, try removing this directory and re-running this script."
    exit 0
fi

echo "Downloading JavaFX SDK $JAVAFX_VERSION for macOS ($ARCH_TYPE)..."
cd "$(dirname "$0")"

# Download JavaFX SDK for the correct architecture
JAVAFX_URL="https://download2.gluonhq.com/openjfx/$JAVAFX_VERSION/openjfx-${JAVAFX_VERSION}_${ARCH_TYPE}_bin-sdk.zip"

echo "Downloading from: $JAVAFX_URL"
curl -L -o javafx-sdk.zip "$JAVAFX_URL"

if [ $? -ne 0 ]; then
    echo "Error: Failed to download JavaFX SDK"
    echo "Please download manually from: https://openjfx.io/"
    exit 1
fi

echo "Extracting JavaFX SDK..."
unzip -q javafx-sdk.zip
rm javafx-sdk.zip

if [ -d "$JAVAFX_DIR" ]; then
    echo "JavaFX SDK successfully installed to $JAVAFX_DIR"
    echo ""
    echo "You can now compile and run with:"
    echo "  ./compile_gui.sh"
    echo "  ./run_gui.sh"
else
    echo "Error: JavaFX SDK extraction failed"
    exit 1
fi

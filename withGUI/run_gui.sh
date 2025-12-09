#!/bin/bash
# Script to run the GUI application with JavaFX

cd "$(dirname "$0")"

JAVAFX_DIR="./javafx-sdk-21"

if [ ! -d "$JAVAFX_DIR" ]; then
    echo "JavaFX SDK not found. Running setup..."
    chmod +x setup_javafx.sh
    ./setup_javafx.sh
    if [ ! -d "$JAVAFX_DIR" ]; then
        echo "Error: JavaFX setup failed"
        exit 1
    fi
fi

JAVAFX_LIB="$JAVAFX_DIR/lib"

echo "Running Wellesley Simulator GUI..."
java --module-path "$JAVAFX_LIB" \
     --add-modules javafx.controls,javafx.graphics,javafx.base \
     -Djava.library.path="$JAVAFX_LIB" \
     -Djava.awt.headless=false \
     WellesleySimulatorGUI

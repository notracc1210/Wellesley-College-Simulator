#!/bin/bash
# Script to compile the GUI application with JavaFX

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

echo "Compiling GUI application..."
javac --module-path "$JAVAFX_LIB" --add-modules javafx.controls,javafx.graphics *.java

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
else
    echo "Compilation failed!"
    exit 1
fi

#!/bin/bash

# Check if an argument was passed
if [ -z "$1" ]; then
    echo "Error: Please specify a target platform."
    echo "Usage: ./build.sh [phone|web|desktop|server|all]"
    exit 1
fi

# Convert the argument to lowercase
TARGET=$(echo "$1" | tr '[:upper:]' '[:lower:]')

case "$TARGET" in
    "phone")
        echo " Building and installing Debug APK for Android..."
        ./gradlew :android:installDebug
        ;;
    "web")
        echo " Starting Kotlin/Wasm web development server..."
        ./gradlew wasmJsBrowserDevelopmentRun
        ;;
    "desktop")
        echo " Running Desktop application..."
        ./gradlew :desktop:run
        ;;
    "server")
        echo " Booting Vapor Server..."
        cd server && swift run
        ;;
    "all")
        echo " Building all targets..."
        ./gradlew :android:installDebug
        ./gradlew wasmJsBrowserDevelopmentRun
        ./gradlew :desktop:run
        ;;
    *)
        echo " Error: Unknown target '$1'."
        echo "Available options: phone, web, desktop, server, all"
        exit 1
        ;;
esac

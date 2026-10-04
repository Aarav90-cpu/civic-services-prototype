#!/bin/bash
# Copyright 2026 Aarav Ravindra Kharade
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.


# Check if an argument was passed
if [ -z "$1" ]; then
    echo "Error: Please specify a target platform."
    echo "Usage: ./build.sh [phone|web|weblock|desktop|server|all]"
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
    "weblock")
        echo " Upgrading Wasm yarn.lock file..."
        ./gradlew kotlinWasmUpgradeYarnLock
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
        echo "Available options: phone, web, weblock, desktop, server, all"
        exit 1
        ;;
esac

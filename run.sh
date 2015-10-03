#!/usr/bin/env bash

# Path for the various files
SRCPATH='src/'
LIBPATH='lib/'
BINPATH='bin/'

# Class path is everything in lib path
CLASSPATH="$LIBPATH"'*'

# Compiler
JAVAC='javac'
TARGET_VERSION='1.6'

# Launcher
JAVA='java'

# Main class
MAIN='ca.mcgill.ecse429.conformancetest.Main'

# Create the bin directory if missing
if [ ! -d "$BINPATH" ]; then
    mkdir -p "$BINPATH"
fi

# Find a compile the source files
echo 'Compiling...'
find "$SRCPATH" -name "*.java" -print | xargs "$JAVAC" -cp "$CLASSPATH" -d "$BINPATH" -source "$TARGET_VERSION"
echo '...Done'

echo ''

# Run the main class and pass all arguments to it
# The bin folder must be added to the classpath for java to find the main class
echo 'Running...'
$JAVA -cp "$CLASSPATH"':bin/' "$MAIN" "$@"

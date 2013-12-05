#!/bin/bash

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
pandoc "$DIR/content/$1.md" -f markdown -t odt -o "$DIR/odt/$1.odt"

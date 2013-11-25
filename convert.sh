#!/bin/bash

pandoc "content/$1.md" -f markdown -t odt -o "odt/$1.odt"

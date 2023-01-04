#!/usr/bin/env sh

rm -rf ~/tmp/brave-clojure
cp -r output ~/tmp/brave-clojure
git checkout gh-pages
cp -r ~/tmp/brave-clojure/* ./
rm -rf ./content
# git checkout -

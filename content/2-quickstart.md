--- 
title: Quick Start Guide
kind: documentation
---

# Quick Start Guide

## Install Ruby

Doc Template is an amalgamation of Ruby libraries. You don't need to
know Ruby to use it but you do have to have Ruby installed. The
[official site](http://www.ruby-lang.org/en/downloads/) has decent
instructions but you might have better luck googling instructions
specifically for your OS.

## Set Up Your Project

Ensure that bundler is installed:

``` bash
gem install bundler
```

Download and unzip the latest commit:

``` bash
curl -L -o doctemplate.zip https://github.com/flyingmachine/doctemplate/zipball/master
unzip doctemplate.zip
```

This will create a directory named "flyingmachine-doctemplate-XXXXXXX"
that you will want to move:

``` bash
mv flyingmachine-doctemplate-XXXXXXX doctemplate
cd doctemplate
```

Download gems and run the content viewer. This allows you to view the
site at [http://localhost:3000](http://localhost:3000):

``` bash
bundle
nanoc compile
nanoc view
```

## Create Content

From here, you can edit content/index.md to change the home page. You
can watch your files for changes so that they're updated automatically:

``` bash
nanoc watch
```

If you visit [http://localhost:3000](http://localhost:3000) you should see your changes.

If you change `toc:` to true at the top of index.md then a table of
contents will be generated.

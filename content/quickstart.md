--- 
title: Doc Template Quick Start Guide
kind: page
toc: true
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
curl -L -o doctemplate.tar.gz https://github.com/flyingmachine/doctemplate/zipball/master
tar zxvf doctemplate.tar.gz
```

This will create a directory named "flyingmachine-doctemplate-XXXXXXX"
that you will want to move:

``` bash
mv flyingmachine-doctemplate-XXXXXXX doctemplate
cd doctemplate
```

_Optional_ Initialize a git repo:

```
git init
```

Download gems and run the content viewer. This allows you to view the
site at [http://localhost:3000](http://localhost:3000):

``` bash
bundle
nanoc view
```

## Create Content

From here, you can edit content/index.md to change the home page. When
you're done, run

``` bash
nanoc com
```

If you visit [http://localhost:3000](http://localhost:3000) you should see your changes.

If you change `toc:` to true at the top of index.md then a table of
contents will be generated.

## Update Layouts

Update `layouts/no-toc.haml` and `layouts/default.haml` to include links
to your files. (In the future, these links will automatically be generated).

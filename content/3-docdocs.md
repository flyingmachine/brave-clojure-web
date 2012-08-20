--- 
title: Detailed Documentation
kind: documentation
---

# Detailed Documentation

This is a work in progress.

## Goals

Doc Template is meant to meet the following goals:

### Provide sensible defaults

Writers should be able to generate useful, aesthetically-pleasing
documentation without changing any settings or styles.

### Allow full control over presentation

Documentation needs vary widely from project to project, and
documentation writers should not face barriers to presenting their
content in a way that makes it more useful to their users.

This is specifically a reaction to Asciidoc, which is very difficult
to modfiy.

### Allow any markup

Writers should be able to use any markup format they want, including
HTML, HAML, Markdown, and Textile.

## Components

Doc Template is a collection of open source tools. To modify its
default settings, you will have to know those tools.

### nanoc, Static Site Generator

The core tool is nanoc, a static site generator. It has
[excellent documentation](http://nanoc.stoneship.org/docs/).

### Sass

All stylesheets are written in [SCSS](http://sass-lang.com/).

### Susy

[Susy](http://susy.oddbird.net/) is a Sass plugin that provides grid
math.

### Compass

[Compass](http://compass-style.org/) is an awesome Sass library that
provides all kinds of great stuff like vertical rhythm and CSS3
helpers.

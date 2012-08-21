--- 
title: Using Doc Template
kind: documentation
---

# Using Doc Template

## Purpose

Doc Template is a tool for creating high-level documentation. It might
be the right tool for you if you want to:

### Create high-level documentation that looks good

Doc Template excels at presenting free-form documentation, as opposed
to automatically-generated API documentation. It's great for
presenting architecture documentation, conceptual guides, and
tutorials.

The default stylesheet is designed for readability and scannability.
It's also designed to be aesthetically pleasing without being
distracting.

### Have complete control over how your documentation looks

Doc Template uses [Sass](http://sass-lang.com/),
[Compass](http://compass-style.org/), and
[Susy](http://susy.oddbird.net/) in its default
[stylesheet](https://github.com/flyingmachine/doctemplate/blob/master/content/assets/stylesheets/documentation.scss).
You have complete freedom to modify the stylesheet or add your own
stylesheets.

### Have complete control over how your documentation's generated

Would you like to pre-process your files to insert links to a
glossary? How about filtering them to dynamically generate a table of
contents (which is what this site does)?

Doc Template is built on [nanoc](http://nanoc.stoneship.org), a static
site generator written in Ruby. It gives you complete control in
generating your documentation, letting you use Ruby to describe how it
should be processed.

### Write your documentation in any format

Markdown, textile, haml, erb - the underlying
[nanoc](http://nanoc.stoneship.org) static site generator is extremely
flexible and allows you to use any file format.

## Quick Start

### Install Ruby

Doc Template is an amalgamation of Ruby libraries. You don't need to
know Ruby to use it but you do have to have Ruby installed. The
[official site](http://www.ruby-lang.org/en/downloads/) has decent
instructions but you might have better luck googling instructions
specifically for your OS.

### Set Up Your Project

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

### Create Content

From here, you can edit `content/1-home.md` to change the home page. You
can watch your files for changes so that they're updated automatically:

``` bash
nanoc watch
```

If you visit [http://localhost:3000](http://localhost:3000) you should see your changes.

If you'd like to create more files, add them to the `content`
directory. For example, `content/2-getting-started.md` would create a file
which would show up under
[http://localhost:3000/2-getting-started/](http://localhost:3000/2-getting-started/).

The numerical prefix tells Doc Template where to insert the link for
this page in the left-hand navigation.

## HOWTO

To fully take advantage of Doc Template, you should become familiar
with [nanoc](http://nanoc.stoneship.org/docs/), the underlying static site
generator.

The nanoc site has excellent documentation which will cover most of
what you need to know in order to undersetand how Doc Template works
and how its various pieces fit together.

### Update layout markup

The default layout is located under `layouts/default.haml`.

Each page's table of contents is generated using the helper at
`lib/filters/toc_filter.rb`. The filter is applied in the `Rules` file.

### Update stylesheets

Stylesheets are located under `content/assets/stylesheets/`.

### Add images

You can add images to `content/assets/images`. When your site is
generated, images will be placed in `assets/images/` relative to the
site root.

### Syntax highlighting

Currently, syntax highlighting is only available for Markdown. The
code for syntax highlighting is under
`lib/redcarpet_syntax_highlighter.rb`.

Syntax highlighting works the same way as it does on github:

    ``` {language-name}
    code line 1
    code line 2
    code line 3
    ```

For example:

    ``` ruby
    chunky_thing = "bacon"
    puts chunky_thing
    puts "this is a totally awesome example ok"
    ```

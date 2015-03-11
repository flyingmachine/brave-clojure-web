---
title: Building and Developing with Leiningen
link_title: Building and Developing with Leiningen
kind: documentation
draft: true
---

# Building and Developing with Leiningen

Writing software in any language involves generating *artifacts*,
executable files or library packages that are meant to be deployed or
shared. It also involves managing dependent artifacts, or just
*dependencies*, by ensuring that they're downloaded from a
*repository* and loaded into the project you're building.  The most
popular tool among Clojurists for handling these artifact management
tasks is Leiningen, and this chapter will show you how to use it. It
will also show you how to use Leiningen to enhancify your development
experience using *plugins*.

## The Artifact Ecosystem

Because Clojure is hosted on the JVM, Clojure artifacts are
distributed as JAR files. (JAR files are covered in Chapter 12.)
There's also an entire artifact ecosystem handling JAR files from Java
land that Clojure uses. *Artifact ecosystem* isn't an official
programming term; I use it to refer to the suite of tools, resources,
and conventions used to identify and distribute artifacts. Java's
ecosystem grew up around the Maven build tool, and since Clojure uses
this ecosystem, you'll often see references to Maven. Maven is a huge tool
that does a lot of stuff that you don't need to care about as a
Clojurist. What matters is that Maven specifies a pattern for
identifying artifacts that Clojure projects adhere to, and it also
specifies how to host these artifacts in Maven *repositories*, which
are just servers that store artifacts for distribution. 


### Identification

Maven artifacts need a *group ID*, an *artifact ID*, and a
*version*. You can specify these for your project in the `project.clj`
file. Here's what the first line of `project.clj` file looks like for
the `clojure-noob` project you created in Chapter 1:

```clojure
(defproject clojure-noob "0.1.0-SNAPSHOT"
```

`clojure-noob` is both the group ID and the artifact ID of your
project, and `"0.1.0-SNAPSHOT"` is its version. In general, versions
are permanent; if you deploy an artifact with version 0.1.0 to a
repository, then you can't make changes to the artifact and deploy it
using the same version number. You'll need to change the version
number. (Many programmers like the
[Semantic Versioning](http://semver.org/)) system. The exception is
when you append `-SNAPSHOT` to your version number. This indicates
that the version is a work in progress, and you can keep updating
it.

If you want your group ID to be different from your artifact ID, then
you can separate the two with a slash, like so:

```clojure
(defproject group-id/artifact-id "0.1.0-SNAPSHOT"
```

Often, developers will use their company name or their Github username
as the group ID.

### Dependencies

Your `project.clj` also includes a line that looks like this:

```clojure
  :dependencies [[org.clojure/clojure "1.6.0"]]
```

If you want to use a library, add it to this dependency vector using
the same naming schema that you use to name your project. For example,
if you want to easily work with dates and times, you could add the
clj-time library:

```clojure
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [clj-time "0.9.0"]]
```

The next time you start your project, either by running it or by
starting a REPL, Leiningen will automatically download clj-time and
make it available within your project. You can refer to the namespaces
and functions in the library using the tools covered in Chapter 6.

There are a lot of great Clojure libraries out there, and a good place
to start looking for them is
[The Clojure Toolbox](http://www.clojure-toolbox.com/), which
categorizes projects according to their purpose. Nearly every Clojure
library provides its identifier at the top of its README, making it
easy for you to figure out how to add it to your Leiningen
dependencies.

Often, though, you'll want to use a Java library, and the identifier
isn't as readily available. If you want to add Apache Commons Email,
for example, you have to do some searching until you find a web page
that contains something like this:


```xml
<dependency>
	<groupId>org.apache.commons</groupId>
	<artifactId>commons-email</artifactId>
	<version>1.3.3</version>
</dependency>
```

This XML is how Java projects communicate their Maven identifier. To
add it your Clojure project, you'd change your `:dependencies` vector
so that it looks like this:

```clojure
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [clj-time "0.9.0"]
                 [org.apache.commons/commons-email "1.3.3"]]
```

The main Clojure repository is [Clojars](http://clojars.org/), and the
main Java repository is
[The Central Repository](http://search.maven.org/), often referred to
as just "Central" in the same way that San Francisco residents refer
to San Francisco as "The City." You can use these sites to find
libraries and their identifiers.

To deploy your own projects to Clojars, all you have to do is create
an account there and run `lein deploy clojars` in your project. This
task generates everything necessary for a Maven artifact to be stored
in a repository, including a POM file (which I won't go into) and a
JAR file. It then uploads them to Clojars.

## Plugins

Leiningen allows *plugins*, libraries that help you when you're
writing code. For example, the
[eastwood plugin](https://github.com/jonase/eastwood) is a Clojure
lint tool; it identifies poorly-written code. You'll usually want to
specify your plugins in the file `$HOME/.lein/profiles.clj`. To add
eastwood, you'd make `projiles.clj` look like this:

```clojure
{:user {:plugins [[jonase/eastwood "0.2.1"]] }}
```

This enables an `eastwood` Leiningen task for all your projects which
you can run with `lein eastwood` at the project's root.

Leiningen's
[github project page](https://github.com/technomancy/leiningen) has
great documentation on how to use profiles and plugins, and it
includes
[a handy list of plugins](https://github.com/technomancy/leiningen/wiki/Plugins).

## Summary

This appendix focused on aspects of project management that are
important but that are hard to easily find out about, like what Maven
is and Clojure's relationship to it. It showed you how to use
Leiningen to name your project, specify dependencies, and deploy to
Clojars. Leiningen offers a lot of functionality for doing all the
software development tasks that don't involve actually writing your
code. If you want to find out more, go through the
[Leiningen tutorial](https://github.com/technomancy/leiningen/blob/stable/doc/TUTORIAL.md)
online.

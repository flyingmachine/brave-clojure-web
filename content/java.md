---
title: All the Java You Need
link_title: All the Java You Need
kind: documentation
draft: true
---

# All the Java You Need

There comes a day in every Clojurist's life when she must venture
forth from the sanctuary of pure functions and immutable data
structures into the wild and barbaric Land of Java. This treacherous
journey is necessary because Clojure is hosted on the Java Virtual
Machine (JVM), granting it two fundamental characteristics. First, a
Clojure application is a Java application when you're compiling and
running it. Second, you need to use Java objects to get a lot of stuff
done, like reading files and working with dates. In this way, Clojure
is a bit like a utopian community plunked down in the middle
non-utopian country. It's preferable to interact with other utopians,
but every once in a while you need to talk to the locals in order to
get things done.

If this chapter were a guide book for the Land of Java, it'd be
somewhere between a phrasebook ("Where is the bathroom?") and a
full-blown course on the local language and customs. It will give you
an overview of what the JVM is, how it runs programs, and how to
compile programs for it. It will also give you a tour of
frequently-used Java classes and methods and explain how to interact
with them from Clojure. More than that, it will show you how to think
about and understand Java so that you can incorporate any Java library
into your Clojure program.



# Notes

* You use local building materials (protocols, interfaces, file and
  date objs, compiling, exec jar file)
* Two perspectives:
    * the java objects perspective
        * Quick Java intro
        * instantiating
        * calling methods 
            * dot op
            * doto
        * class methods
        * navigating documentation
        * importing
        * commonly used classes
            * File
            * Date
            * Readers
            * Writers
            * System: getenv, exit, etc
    * the ops perspective
        * what it means to execute a java program (bytecode, jvm)
        * compiling to bytecode
        * classpath
        * the ecosystem around java programs

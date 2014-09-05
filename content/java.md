---
title: All the Java You Need
link_title: All the Java You Need
kind: documentation
draft: true
---

# All the Java You Need

There comes a day in every Clojurist's life when she must venture
forth from the sanctuary of pure functions and immutable data
structures into the wild and barbaric land of Java. This treacherous
journey is necessary because Clojure, as a language *hosted* on the
Java Virtual Machine (JVM), has two fundamental
characteristics. First, a Clojure application is a Java application
from when you're compiling, deploying, and running it. Second, you
need to use Java objects to get a lot of stuff done, like reading
files and working with dates. In this way, Clojure is a bit like a
utopian community plunked down in the middle non-utopian country. It's
preferable to interact with the other members of the community, but
every once in awhile you need to talk to the locals in order to get
things done.



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

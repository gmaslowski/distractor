distractor
==========

[![Join the chat at https://gitter.im/gmaslowski/distractor](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/gmaslowski/distractor?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Build Status](https://snap-ci.com/gmaslowski/distractor/branch/develop/build_image)](https://snap-ci.com/gmaslowski/distractor/branch/develop)
[![codecov.io](http://codecov.io/github/gmaslowski/distractor/coverage.svg?branch=develop)](http://codecov.io/github/gmaslowski/distractor?branch=develop)

## project information
Distractor is a continuation of [reactor](https://github.com/FutureProcessing/reactor). The aim of the project is to write
it entirely in [Scala](http://www.scala-lang.org/) with [Akka](http://akka.io/), so that reactors and transports could 
be easily distributed over network instead of being bound to one JVM.

### testing
```bash
./activator clean test
```

with coverage
```bash
./activator clean coverage test
```

### run 
```bash
./activator run
```

### create artifact and run 
```bash
./activator "project distractor-core" clean assembly
java -jar distractor-core/target/scala-2.11/distractor-assembly-0.1-SNAPSHOT.jar
```

## architecture overview
### actor system
> Note that this architecture overview may not be up to date. Code reference should be the internal actor system indicator

![Actor System Overview](http://yuml.me/diagram/scruffy/class/[note:Distractor ActorSystem {bg:wheat}],[Distractor {bg:lightskyblue}]++-1>[ReactorRegistry {bg:lightskyblue}],[Distractor {bg:lightskyblue}]++-1>[DistractorRequestHandler {bg:lightskyblue}],[Distractor {bg:lightskyblue}]++-1>[TransportRegistry {bg:lightskyblue}],[TransportRegistry {bg:lightskyblue}]<>-0..*>[*Transport {bg:lightsalmon}],[ReactorRegistry {bg:lightskyblue}]<>-0..*>[*Reactor {bg:lightsalmon}])

## short development movie
https://www.youtube.com/watch?v=8r2d7Ouwn3U

distractor
==========

[![Join the chat at https://gitter.im/gmaslowski/distractor](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/gmaslowski/distractor?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

[![Stories in Ready](https://badge.waffle.io/gmaslowski/distractor.svg?label=ready&title=Ready)](http://waffle.io/gmaslowski/distractor)
[![Stories in In Progress](https://badge.waffle.io/gmaslowski/distractor.svg?label=in%20progress&title=In%20Progress)](http://waffle.io/gmaslowski/distractor)
[![Build Status](https://snap-ci.com/gmaslowski/distractor/branch/develop/build_image)](https://snap-ci.com/gmaslowski/distractor/branch/develop)
[![codecov.io](http://codecov.io/github/gmaslowski/distractor/coverage.svg?branch=develop)](http://codecov.io/github/gmaslowski/distractor?branch=develop)

## project information
Distractor is a continuation of [reactor](https://github.com/FutureProcessing/reactor). The aim of the project is to write
it entirely in [Scala](http://www.scala-lang.org/) with [Akka](http://akka.io/), so that reactors and transports could 
be easily distributed over network instead of being bound to one JVM.

## usage

run:
```bash
sh sbt-run.sh
```

### testing

test:
```bash
./activator clean test
```

test with coverage:
```bash
./activator clean coverage test
```

### packaging

create fat jar:
```bash
./activator assembly
```

### docker

build docker images (does also the assembly):
```bash
./activator docker
```

run built image:
```bash
docker run -p 8111:8111 default/distractor
```

## architecture overview
### actor system
> Note that this architecture overview may not be up to date. Code reference should be the internal actor system indicator

![Actor System Overview](http://yuml.me/diagram/scruffy/class/[note:Distractor ActorSystem {bg:wheat}],[Distractor {bg:lightskyblue}]++-1>[ReactorRegistry {bg:lightskyblue}],[Distractor {bg:lightskyblue}]++-1>[DistractorRequestHandler {bg:lightskyblue}],[Distractor {bg:lightskyblue}]++-1>[TransportRegistry {bg:lightskyblue}],[TransportRegistry {bg:lightskyblue}]<>-0..*>[*Transport {bg:lightsalmon}],[ReactorRegistry {bg:lightskyblue}]<>-0..*>[*Reactor {bg:lightsalmon}])



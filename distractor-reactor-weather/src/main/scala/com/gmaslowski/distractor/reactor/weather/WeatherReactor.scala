package com.gmaslowski.distractor.reactor.weather

import java.nio.charset.StandardCharsets.UTF_8

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.pattern.pipe
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import akka.util.ByteString
import com.gmaslowski.distractor.core.api.DistractorApi.Register
import com.gmaslowski.distractor.core.reactor.api.ReactorApi.{ReactorRequest, ReactorResponse}
import com.typesafe.config.ConfigFactory.load

class WeatherReactor extends Actor with ActorLogging {
  import context.dispatcher

  val apiKey = "292d703e8a9dd6b9d3c45d33c8eb54c2"
  val defaultCountryCode = "pl"
  val weatherUrlFormat = "http://api.openweathermap.org/data/2.5/forecast?q=%s,%s&APPID=%s"

  implicit val materializer = ActorMaterializer(ActorMaterializerSettings(context.system))

  val http = Http(context.system)

  def receive = {
    case ReactorRequest(reactorId, data, passThrough) =>
      val params = data.split("\\s+")
      params.length match {
        case 1 | 2 => requestWeather(reactorId, params.toList, passThrough)
        case _ => sender ! ReactorResponse(reactorId, "{\"message\":\"Provide a [cityName] or [cityName countryCode] pair.\"}", passThrough)
      }
  }

  private def requestWeather(reactorId: String, params: List[String], passThrough: String): Unit = {
    val weatherUri = (params: @unchecked) match {
      case city :: Nil => String.format(weatherUrlFormat, city, defaultCountryCode, apiKey)
      case city :: country :: Nil => String.format(weatherUrlFormat, city, country, apiKey)
    }

    // TODO: response needs to be distilled - currently there's too much information to be human readable
    http.singleRequest(HttpRequest(uri = weatherUri))
      .flatMap(response => response.entity.dataBytes.runFold(ByteString(""))(_ ++ _))
      .map(bs => ReactorResponse(reactorId, bs.decodeString(UTF_8), passThrough))
      .pipeTo(sender())
  }
}

object WeatherReactor {
  def props = Props[WeatherReactor]

  def start() : Unit = {
    val system = ActorSystem("weather-reactor-system", load("weather-reactor-system.conf"))
    val registry = system.actorSelection("akka.tcp://distractor@127.0.0.1:2552/user/reactor-registry")
    registry ! Register("weather", system.actorOf(props))
  }
}

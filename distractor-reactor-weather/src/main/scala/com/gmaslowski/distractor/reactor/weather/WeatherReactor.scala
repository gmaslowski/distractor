package com.gmaslowski.distractor.reactor.weather

import akka.actor.{Actor, ActorLogging, Props}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import com.gmaslowski.distractor.core.reactor.api.ReactorApi.{ReactorRequest, ReactorResponse}
import play.api.libs.ws.ahc.AhcWSClient

object WeatherReactor {
  def props(ahcWSClient: AhcWSClient) = Props(classOf[WeatherReactor], ahcWSClient)
}

class WeatherReactor(val ahcWsClient: AhcWSClient) extends Actor with ActorLogging {

  val apiKey = "292d703e8a9dd6b9d3c45d33c8eb54c2"
  val defaultCountryCode = "pl"

  def weatherUrl(city: String, country: String) = s"http://api.openweathermap.org/data/2.5/weather?q=${city},${country}&APPID=${apiKey}"

  implicit val ec = context.dispatcher
  implicit val materializer = ActorMaterializer(ActorMaterializerSettings(context.system))

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
      case city :: Nil => weatherUrl(city, defaultCountryCode)
      case city :: country :: Nil => weatherUrl(city, country)
    }

    val sender = context.sender()

    ahcWsClient
      .url(weatherUri)
      .withHeaders("Accept" -> "application/json")
      .get()
      .onSuccess {
        case result =>
          sender forward ReactorResponse(reactorId, result.body, passThrough)
      }
  }
}

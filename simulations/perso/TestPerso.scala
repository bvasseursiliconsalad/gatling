package perso

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import io.gatling.http.Headers.Names._
import scala.concurrent.duration._
import bootstrap._
import assertions._

class TestPerso extends Simulation {

  val httpConf = http
    .baseURL("http://staging.prediction.siliconsalad.net:8001")
    .acceptCharsetHeader("ISO-8859-1,utf-8;q=0.7,*;q=0.7")
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("fr,fr-fr;q=0.8,en-us;q=0.5,en;q=0.3")
    .disableFollowRedirect

  val pio_iids = csv("pio_iids.csv").random
  val pio_uids = csv("pio_uids.csv").random
  val pio_actions = csv("pio_actions.csv").random
  val pio_notations = csv("pio_notations.csv").random

  val headers_1 = Map(
    "Keep-Alive" -> "115")

  val headers_2 = Map(
    "Keep-Alive" -> "115",
    "Content-Type" -> "application/x-www-form-urlencoded")

  val scn_get = scenario("GET")
  .feed(pio_iids)
    .exec(
      http("request_1")
        .get("/engines/itemsim/similar_item/topn.json?pio_appkey=QeMH5yW5mxYw59ii7ix5K0m12V3mByVL2BxFtRCIQQGchJ95c8Y1jlndTuNsl4JJ&pio_iid=${pio_iid}&pio_n=10")
        .headers(headers_1)
        .check(status.is(200)))
    //.pause(0 milliseconds, 100 milliseconds)

    val scn_post = scenario("POST_actions")
    .feed(pio_iids)
    .feed(pio_uids)
    .feed(pio_actions)
    .feed(pio_notations)

    .doIfOrElse(session => session("pio_action").as[String] == "rate") {
      exec(
        http("request_2")
        .post("/actions/u2i.json")
        .param("pio_appkey", "r65OOvbeoZ37cUGOlGhJlzBvUq8ZIfRxvSnGWb9HginPakvN6ZI2GbM0F0w6KSbL")
        .param("pio_uid", "${pio_uid}")
        .param("pio_iid", "${pio_iid}")
        .param("pio_action", "${pio_action}")
        .param("pio_rate", "${pio_notation}")
      )
    } {
      exec(
        http("request_2")
        .post("/actions/u2i.json")
        .param("pio_appkey", "r65OOvbeoZ37cUGOlGhJlzBvUq8ZIfRxvSnGWb9HginPakvN6ZI2GbM0F0w6KSbL")
        .param("pio_uid", "${pio_uid}")
        .param("pio_iid", "${pio_iid}")
        .param("pio_action", "${pio_action}")
        )
    }

    setUp(
      scn_post.inject(rampRate(10 usersPerSec) to(50 usersPerSec) during(5 minutes))
      //scn_get.inject(rampRate(10 usersPerSec) to(56 usersPerSec) during(15 minutes))
    ).protocols(httpConf)

}

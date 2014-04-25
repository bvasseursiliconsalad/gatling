package perso

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import io.gatling.http.Headers.Names._
import scala.concurrent.duration._
import bootstrap._
import assertions._

class NodeTestPerso extends Simulation {

  val httpConf = http
    .baseURL("http://staging.prediction.siliconsalad.net:8001")
    .acceptCharsetHeader("ISO-8859-1,utf-8;q=0.7,*;q=0.7")
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("fr,fr-fr;q=0.8,en-us;q=0.5,en;q=0.3")
    .disableFollowRedirect

  val headers_1 = Map(
    "Keep-Alive" -> "115")

  val scn_get = scenario("GET")
    .exec(
      http("request_1")
        .get("/")
        .headers(headers_1)
        .check(status.is(200)))

    val scn_post = scenario("POST")
    .exec(
      http("request_1")
        .post("/")
        .headers(headers_1)
        .check(status.is(200)))

  setUp(
      //scn_post.inject(rampRate(10 usersPerSec) to(56 usersPerSec) during(15 minutes))
      scn_post.inject(rampRate(10 usersPerSec) to(50 usersPerSec) during(5 minutes))
    ).protocols(httpConf)

}

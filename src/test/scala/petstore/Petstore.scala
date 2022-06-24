package petstore

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import java.util.concurrent.ThreadLocalRandom

class Petstore extends Simulation {

  val httpProtocol =
    http
      .baseUrl("https://petstore.octoperf.com")
      .acceptHeader(
        "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"
      )
      .acceptLanguageHeader("en-US,en;q=0.5")
      .acceptEncodingHeader("gzip, deflate")
      .userAgentHeader(
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0"
      )

  val scn = scenario("Petstore")
    .exec(
      http("Home")
        .get("/")
        .check((status is 200), substring("Welcome to JPetStore"))
    )
    .pause(1)
    .exec(
      http("Click 'enter the store' link")
        .get("/actions/Catalog.action")
        .check((status is 200), substring("Saltwater, Freshwater"))
    )
    .pause(1)
    .exec(
      http("Click on ‘Sign In’")
        .get("/actions/Account.action?signonForm=")
        .check(
          (status is 200),
          substring("Please enter your username and password.")
        )
    )
    .pause(1)
    .exec(
      http("Click on 'register now’ link")
        .get("/actions/Account.action?newAccountForm=")
        .check((status is 200), substring("User Information"))
    )
    .pause(1)

  setUp(
    scn.inject(atOnceUsers(2)).protocols(httpProtocol)
  ).protocols(httpProtocol)
}

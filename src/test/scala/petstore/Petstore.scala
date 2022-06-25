package petstore

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._
import scala.util.Random

class Petstore extends Simulation {

  def random(len: Int): String = Random.alphanumeric.filter(_.isLetter).take(len).mkString

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
        .check((status is 200), substring("Please enter your username and password."))
    )
    .pause(1)

    .exec(
      http("Click on 'register now’ link")
        .get("/actions/Account.action?newAccountForm=")
        .check((status is 200), substring("User Information"))
    )
    .pause(1)

    val staticVal = random(5)

    exec(
      http("Register Now")
        .post("/actions/Account.action")
        .formParam("username", staticVal)
        .formParam("password", "password")
        .formParam("repeatedPassword", "password" )
        .formParam("account.firstName", "aidy")
        .formParam("account.lastName", "lewis")
        .formParam("account.email", "x@x.com")
        .formParam("account.phone", "07123456789")
        .formParam("account.address", "139 Kings Road")
        .formParam("account.address2", "")
        .formParam("account.city", "Ashton")
        .formParam("account.state", "MCR")
        .formParam("account.zip", "OL68EZ")
        .formParam("account.country", "England")
        .formParam("account.languagePreference", "English")
        .formParam("account.favouriteCategoryId", "FISH")
        .formParam("account.newAccount", "Save Account Information")
        .formParam("_sourcePage", "9J-iMAXq9dG0LMgE4V3KSSKTw9srBcUe0SbJOrsq3vIUNciL00U9DRFBZpACw8IWp49AzOLT6WBBlcG8fI2ilrdMRlPVzDikCyGTNlKQI7o=")
        .formParam("__fp", "ozWOy1XVolzhao1yxtUN88Q2a14M_zok4D5hqk6q1kUfbDiktNlmCs6LZ-WlER3rZK-tPmeQ3horzSEeVE0vGQ_XaqoIGx5lOV8PdoIUljGC7qqgdLiBR58ZR9T6iF4Yjczs3K3rguWbuBRHP6p6XHoIvs74I8v7DerwfeNtWLRpO-WSLIUTJi4qD0luMle")
        .check((status is 200), substring("Saltwater, Freshwater"))
      )

  setUp(
    scn.inject(atOnceUsers(2)).protocols(httpProtocol)
  )
}

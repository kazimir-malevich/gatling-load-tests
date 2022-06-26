package petstore

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._
import scala.util.Random

class Petstore extends Simulation {

  val feederRandomValue = Iterator.continually(Map("randomValue" -> Random.alphanumeric.take(10).mkString))

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
        .check(regex("""name="_sourcePage" value=(.+?)"""").saveAs("sourcePage"),
			  regex("""name="__fp" value=(.+?)"""").saveAs("fp"))
    )
    .pause(1)

    .feed(feederRandomValue)
    .exec(
      http("Register")
        .post("/actions/Account.action")
        .formParam("username", "${randomValue}")
        .formParam("password", "password")
        .formParam("repeatedPassword", "password" )
        .formParam("account.firstName", "aidy")
        .formParam("account.lastName", "lewis")
        .formParam("account.email", "x@x.com")
        .formParam("account.phone", "07123456789")
        .formParam("account.address1", "139 Kings Road")
        .formParam("account.address2", "")
        .formParam("account.city", "Ashton")
        .formParam("account.state", "MCR")
        .formParam("account.zip", "OL68EZ")
        .formParam("account.country", "England")
        .formParam("account.languagePreference", "English")
        .formParam("account.favouriteCategoryId", "FISH")
        .formParam("newAccount", "Save Account Information")
        .formParam("_sourcePage", "#{sourcePage}")
        .formParam("__fp", "#{fp}")
        .check((status is 200), substring("Saltwater, Freshwater"))
      )
      .pause(5)

    .exec(
      http("Login")
        .post("/actions/Account.action")
        .formParam("username", "${randomValue}")
        .formParam("password", "password")
        .formParam("signon", "Login")
        .formParam("_sourcePage", "#{sourcePage}")
        .formParam("__fp", "#{fp}")
        .check((status is 200), substring("Welcome"))
      )
    .pause(2)

    .exec(
      http("Click on 'Dogs' link")
        .get("/actions/Catalog.action?viewCategory=&categoryId=DOGS")
        .check((status is 200), substring("Dogs"))
    )
    .pause(1)

    .exec(
      http("Click on 'Poodle' link")
        .get("/actions/Catalog.action?viewProduct=&productId=K9-PO-02")
        .check((status is 200), substring("Poodle"))
    )
    .pause(1)

    .exec(
      http("Add a ‘Male Puppy Poodle’ to cart")
        .get("/actions/Cart.action?addItemToCart=&workingItemId=EST-8")
        .check((status is 200), substring("Shopping Cart"))
    )
    .pause(1)

    .exec(
      http("Go to Checkout")
        .get("/actions/Order.action?newOrderForm=")
        .check((status is 200), substring("Payment Details"))
    )

    .exec(
      http("Enter Information and then continue")
        .post("/actions/Order.action")
        .formParam("order.cardType", "Visa")
        .formParam("order.creditCard", "999 9999 9999 9999")
        .formParam("order.expiryDate", "12/03")
        .formParam("order.billToFirstName", "aidy")
        .formParam("order.billToLastName", "lewis")
        .formParam("order.billAddress1", "139 Kings Road")
        .formParam("order.billAddress2", "")
        .formParam("order.billCity", "Ashton")
        .formParam("order.billState", "MCR")
        .formParam("order.billZip", "OL68EZ")
        .formParam("order.billCountry", "England")
        .formParam("newOrder", "Continue")
        .formParam("_sourcePage", "#{sourcePage}")
        .formParam("__fp", "#{fp}")
        .check((status is 200), substring("Order"))
    )
    .pause(5)

    .exec(
      http("Confirm")
        .get("/actions/Order.action?newOrder=&confirmed=true")
        .check((status is 200), substring("Thank you, your order has been submitted"))
    )
    .pause(2)

    .exec(
      http("Sign Out")
        .get("/actions/Account.action?signoff=")
        .check((status is 200), substring("Sign In"))
    )

    .pause(6 minutes)

  setUp(
    scn.inject(constantConcurrentUsers(5).during(30 minutes))
      .protocols(httpProtocol)
  )
}
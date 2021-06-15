package com.affinitycrypto.dizzy.flightstatus.web

import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.builder.RequestSpecBuilder
import io.restassured.http.ContentType
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.transaction.annotation.Transactional

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class FlightStatusControllerIT {

    @Value("\${server.servlet.context-path}")
    lateinit var basePath: String
    @LocalServerPort
    var port = 0

    @Before
    fun setup() {
        RestAssured.port = port
        RestAssured.basePath = basePath
        RestAssured.baseURI = "http://localhost"
        RestAssured.requestSpecification = RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build().log().all()
    }

    @Test
    fun failForShortDepartureAirport() {
        given().`when`()
                .get("/api/v1/flights/A/LON/dep/2018/11/22")
                .then().statusCode(400)
    }

    @Test
    fun failForShortArrivalAirport() {
        given().`when`()
                .get("/api/v1/flights/LON/BE/dep/2018/11/22")
                .then().statusCode(400)
    }

    @Test
    fun failForInvalidDate_LargeMonth() {
        given().`when`()
                .get("/api/v1/flights/LON/ATH/dep/2018/13/22")
                .then().statusCode(400)
    }

    @Test
    fun failForInvalidDate_LargeDay() {
        given().`when`()
                .get("/api/v1/flights/LON/ATH/dep/2018/11/32")
                .then().statusCode(400)
    }

    @Test
    fun failForInvalidDate() {
        given().`when`()
                .get("/api/v1/flights/LON/LON/dep/2018/02/31")
                .then().statusCode(400)
    }

    @Test
    fun failForSameAirport() {
        given().`when`()
                .get("/api/v1/flights/LON/LON/dep/2018/11/22")
                .then()
                .statusCode(400)
    }

}
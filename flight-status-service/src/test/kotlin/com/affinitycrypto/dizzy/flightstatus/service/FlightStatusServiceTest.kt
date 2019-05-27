package com.affinitycrypto.dizzy.flightstatus.service

import com.affinitycrypto.dizzy.flightstatus.model.FlightStatus
import com.affinitycrypto.dizzy.flightstatus.model.FlightStatusQuery
import com.affinitycrypto.dizzy.flightstatus.model.StatusIndicator
import com.affinitycrypto.dizzy.flightstatus.repository.FlightStatusRepository
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import java.time.ZoneId
import java.time.ZonedDateTime

@RunWith(SpringRunner::class)
@SpringBootTest
class FlightStatusServiceTest {

    @Autowired
    lateinit var repo: FlightStatusRepository
    @Autowired
    lateinit var svc: FlightStatusService


    val flight1Day1 = FlightStatus(
            flightNumber = "AA123",
            departureAirport = "LON",
            arrivalAirport = "ATH",
            scheduledDepartureDateUtc = ZonedDateTime.of(2018, 12, 10, 8, 12, 25, 9234, ZoneId.of("UTC")),
            scheduledArrivalDateUtc = ZonedDateTime.of(2018, 12, 10, 12, 0, 25, 12, ZoneId.of("UTC")),
            status = StatusIndicator.Scheduled
    )
    val flight2Day1 = FlightStatus(
            flightNumber = "BB123",
            departureAirport = "LON",
            arrivalAirport = "ATH",
            scheduledDepartureDateUtc = ZonedDateTime.of(2018, 12, 10, 22, 12, 25, 9234, ZoneId.of("UTC")),
            scheduledArrivalDateUtc = ZonedDateTime.of(2018, 12, 11, 0, 25, 25, 12, ZoneId.of("UTC"))
    )
    val flightDay2 = FlightStatus(
            flightNumber = "CC123",
            departureAirport = "LON",
            arrivalAirport = "MAD",
            scheduledDepartureDateUtc = ZonedDateTime.of(2018, 12, 22, 8, 12, 25, 9234, ZoneId.of("UTC")),
            scheduledArrivalDateUtc = ZonedDateTime.of(2018, 12, 22, 12, 0, 25, 12, ZoneId.of("UTC")),
            departureDateUtc = ZonedDateTime.of(2018, 12, 22, 10, 12, 30, 9234, ZoneId.of("UTC")),
            arrivalDateUtc = ZonedDateTime.of(2018, 12, 22, 14, 5, 26, 12, ZoneId.of("UTC")),
            status = StatusIndicator.Landed
    )

    lateinit var f1D1: FlightStatus
    lateinit var f2D1: FlightStatus
    lateinit var fD2: FlightStatus

    @Before
    fun setup() {
        f1D1 = svc.addFlight(flight1Day1)
        f2D1 = svc.addFlight(flight2Day1)
        fD2 = svc.addFlight(flightDay2)
    }

    @After
    fun tearDown() {
        repo.deleteAll()
    }

    @Test
    fun getFlights_canFindMultipleFlightsInDay() {
        // arrange
        val criteria = FlightStatusQuery(
                departureAirport = "LON",
                arrivalAirport = "ATH",
                scheduledDepartureDateUtc = ZonedDateTime.of(2018, 12, 10, 0, 0, 0, 0, ZoneId.of("UTC"))
        )
        // act
        val flights = svc.getFlights(criteria)

        // assert
        assertThat(flights.size, `is`(2))
        assertThat(flights, hasItem(f1D1))
        assertThat(flights, hasItem(f2D1))
    }

    @Test
    fun getFlights_noResultsForUnknownAirport() {
        // arrange
        val criteria = FlightStatusQuery(
                departureAirport = "LON",
                arrivalAirport = "CDG",
                scheduledDepartureDateUtc = ZonedDateTime.of(2018, 12, 10, 0, 0, 0, 0, ZoneId.of("UTC"))
        )
        // act
        val flights = svc.getFlights(criteria)

        // assert
        assertThat(flights.size, `is`(0))
    }

    @Test
    fun addFlight_timestampsAreTruncatedToMinutes() {
        // act
        val flight = repo.findById(fD2.flightId)

        // assert
        assertThat(flight.isPresent, `is`(true))
        assertThat(flight.get().scheduledDepartureDateUtc, `is`(ZonedDateTime.of(2018, 12, 22, 8, 12, 0, 0, ZoneId.of("UTC"))))
        assertThat(flight.get().scheduledArrivalDateUtc, `is`(ZonedDateTime.of(2018, 12, 22, 12, 0, 0, 0, ZoneId.of("UTC"))))
        assertThat(flight.get().departureDateUtc, `is`(ZonedDateTime.of(2018, 12, 22, 10, 12, 0, 0, ZoneId.of("UTC"))))
        assertThat(flight.get().arrivalDateUtc, `is`(ZonedDateTime.of(2018, 12, 22, 14, 5, 0, 0, ZoneId.of("UTC"))))
    }

    @Test
    fun addFlight_uuidIsOverriden() {
        // assert
        assertThat(flight1Day1.flightId, not(`is`(f1D1.flightId)))
        assertThat(flight2Day1.flightId, not(`is`(f2D1.flightId)))
    }

    @Test(expected = IllegalArgumentException::class)
    fun updateFlight_failsIfUpdatingIncorrectState() {
        // arrange
        val flight = fD2.copy(
                arrivalDateUtc = ZonedDateTime.of(2018, 11, 22, 14, 5, 26, 12, ZoneId.of("UTC")))
        // act
        svc.updateFlight(flight)
    }

    @Test
    fun updateFlight_correctUpdate_IgnoresScheduledTimes() {
        // arrange
        var flight = f1D1.copy(
                departureDateUtc = ZonedDateTime.of(2018, 12, 10, 8, 12, 25, 9234, ZoneId.of("UTC")),
                arrivalDateUtc = ZonedDateTime.of(2018, 12, 10, 12, 0, 25, 12, ZoneId.of("UTC")),
                scheduledArrivalDateUtc = ZonedDateTime.of(2019, 12, 10, 12, 0, 25, 12, ZoneId.of("UTC")),
                scheduledDepartureDateUtc = ZonedDateTime.of(2019, 12, 10, 12, 0, 25, 12, ZoneId.of("UTC"))
        )
        // act
        svc.updateFlight(flight)
        val result = repo.findById(f1D1.flightId)
        // assert
        assertThat(result.isPresent, `is`(true))
        assertThat(result.get().departureDateUtc, `is`(ZonedDateTime.of(2018, 12, 10, 8, 12, 0, 0, ZoneId.of("UTC"))))
        assertThat(result.get().arrivalDateUtc, `is`(ZonedDateTime.of(2018, 12, 10, 12, 0, 0, 0, ZoneId.of("UTC"))))
        assertThat(result.get().scheduledDepartureDateUtc, `is`(f1D1.scheduledDepartureDateUtc))
        assertThat(result.get().scheduledArrivalDateUtc, `is`(f1D1.scheduledArrivalDateUtc))
    }
}
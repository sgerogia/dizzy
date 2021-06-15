package com.affinitycrypto.dizzy.flightstatus.web

import com.affinitycrypto.dizzy.flightstatus.model.FlightStatus
import com.affinitycrypto.dizzy.flightstatus.model.FlightStatusQuery
import com.affinitycrypto.dizzy.flightstatus.service.FlightStatusService
import com.affinitycrypto.dizzy.flightstatus.validation.ValidStatus
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.hibernate.validator.constraints.Length
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindException
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.time.DateTimeException
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.validation.ConstraintViolationException
import javax.validation.constraints.Max


@RestController
@RequestMapping("/api/v1/flights",
        consumes = [APPLICATION_JSON_VALUE],
        produces = [APPLICATION_JSON_VALUE])
@Validated
class FlightStatusController(
        private val service: FlightStatusService
) {

    private val log = LoggerFactory.getLogger(FlightStatusController::class.java)

    @ApiOperation(value = "Find status of flight(s)",
            notes = "Find the status of flight for the given departure & arrival airport and departure date")
    @GetMapping("/{departureAirport}/{arrivalAirport}/dep/{year}/{month}/{day}")
    fun flightStatus(
            @ApiParam(value = "departureAirport", name = "Departure airport (3 chars)", required = true, example = "LGW")
            @Length(min = 3) @PathVariable("departureAirport", required = true) depAirport: String,

            @ApiParam(value = "arrivalAirport", name = "Arrival airport (3 chars)", required = true, example = "ATH")
            @Length(min = 3) @PathVariable("arrivalAirport", required = true) arrAirport: String,

            @ApiParam(value = "year", name = "Year of flight date", required = true, example = "2018")
            @PathVariable("year", required = true) year: Int,

            @ApiParam(value = "month", name = "Month of flight date", required = true, example = "10")
            @Max(12) @PathVariable("month", required = true) month: Int,

            @ApiParam(value = "day", name = "Day of flight date", required = true, example = "25")
            @Max(31) @PathVariable("day", required = true) day: Int
    ): List<FlightStatus> {

        val criteria = FlightStatusQuery(
                departureAirport = depAirport,
                arrivalAirport = arrAirport,
                scheduledDepartureDateUtc = ZonedDateTime.of(year, month, day, 0, 0, 0, 0, ZoneId.of("UTC"))
        )

        val flights = service.getFlights(criteria)
        log.info("Found {} matching flights", flights.size)
        return flights
    }

    @ApiOperation(value = "Add a new flight in the database",
            notes = "Flights are unique by a combination of number, airports and scheduled departure time")
    @PostMapping
    fun addFlight(
            @ApiParam(value = "The new flight status object", required = true)
            @ValidStatus @RequestBody flight: FlightStatus): FlightStatus {

        return service.addFlight(flight)
    }

    @ApiOperation(value = "Update a flight in the database",
            notes = "Intended to update the status and actual departure/arrival times of a flight")
    @PutMapping
    fun updateFlight(
            @ApiParam(value = "Updated flight status object", required = true)
            @ValidStatus @RequestBody flight: FlightStatus): FlightStatus {

        return service.updateFlight(flight)
    }

    @ExceptionHandler(ConstraintViolationException::class, BindException::class, DateTimeException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleConstraintViolationException(e: Exception): ResponseEntity<String> {
        return ResponseEntity(e.message, HttpStatus.BAD_REQUEST)
    }
}
package com.affinitycrypto.dizzy.flightstatus.service

import com.affinitycrypto.dizzy.flightstatus.model.FlightStatus
import com.affinitycrypto.dizzy.flightstatus.model.FlightStatusQuery
import com.affinitycrypto.dizzy.flightstatus.model.StatusIndicator
import com.affinitycrypto.dizzy.flightstatus.repository.FlightStatusRepository
import com.affinitycrypto.dizzy.flightstatus.validation.FlightStatusQueryValidator
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.validation.BindException
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import javax.persistence.EntityNotFoundException


@Service
class FlightStatusService(
        private val repository: FlightStatusRepository,
        private val validator: FlightStatusQueryValidator) {

    private val log = LoggerFactory.getLogger(FlightStatusService::class.java)

    fun getFlights(criteria: FlightStatusQuery): List<FlightStatus> {

        validate(criteria)
        
        log.debug("Searching for flights ", criteria)
        val dateEnd = ZonedDateTime.of(
                criteria.scheduledDepartureDateUtc.year,
                criteria.scheduledDepartureDateUtc.monthValue,
                criteria.scheduledDepartureDateUtc.dayOfMonth,
                23,
                59,
                59,
                999999,
                ZoneId.of("UTC"))
        val dateBegin = ZonedDateTime.of(
                criteria.scheduledDepartureDateUtc.year,
                criteria.scheduledDepartureDateUtc.monthValue,
                criteria.scheduledDepartureDateUtc.dayOfMonth,
                0,
                0,
                0,
                0,
                ZoneId.of("UTC"))
        return repository.findScheduledFlights(
                departureAirport = criteria.departureAirport,
                arrivalAirport = criteria.arrivalAirport,
                scheduledDepartureDateBeginUtc = dateBegin,
                scheduledDepartureDateEndUtc = dateEnd
        )
    }

    fun addFlight(flight: FlightStatus): FlightStatus {
        log.info("Inserting flight:", flight)
        val toAdd = flight.copy(
                flightId = UUID.randomUUID().toString(),
                scheduledArrivalDateUtc = flight.scheduledArrivalDateUtc.truncatedTo(ChronoUnit.MINUTES),
                scheduledDepartureDateUtc = flight.scheduledDepartureDateUtc.truncatedTo(ChronoUnit.MINUTES),
                departureDateUtc = flight.departureDateUtc?.let { it.truncatedTo(ChronoUnit.MINUTES) },
                arrivalDateUtc = flight.arrivalDateUtc?.let { it.truncatedTo(ChronoUnit.MINUTES) }
        )
        repository.save(toAdd)
        return toAdd
    }

    fun updateFlight(flight: FlightStatus): FlightStatus {
        log.info("Updating flight: ", flight.flightId)
        // make sure id exists
        val toUpdate = repository.findById(flight.flightId)
                .orElseThrow { EntityNotFoundException("Unknown flight id:" + flight.flightId) }
        // make sure we can update
        if (toUpdate.status !in arrayOf(null, StatusIndicator.Active, StatusIndicator.Scheduled)) {
            throw IllegalArgumentException("Cannot update flight with status " + toUpdate.status)
        }
        // update fields
        with(toUpdate) {
            repository.save(
                    toUpdate.copy(
                            status = flight.status,
                            departureDateUtc = flight.departureDateUtc?.let { it.truncatedTo(ChronoUnit.MINUTES) },
                            arrivalDateUtc = flight.arrivalDateUtc?.let { it.truncatedTo(ChronoUnit.MINUTES) }
                    )
            )
        }
        return toUpdate
    }

    private fun validate(criteria: FlightStatusQuery) {
        val err = BindException(criteria, "criteria")
        validator.validate(criteria, err)
        if (err.hasErrors()) {
            throw err
        }
    }
}
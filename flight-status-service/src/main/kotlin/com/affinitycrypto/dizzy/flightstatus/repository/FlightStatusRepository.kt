package com.affinitycrypto.dizzy.flightstatus.repository

import com.affinitycrypto.dizzy.flightstatus.model.FlightStatus
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import java.time.ZonedDateTime

interface FlightStatusRepository : CrudRepository<FlightStatus, String> {

    @Query("""SELECT f FROM FlightStatus f
            WHERE
                f.departureAirport = :depAir
            AND f.arrivalAirport = :arrAir
            AND f.scheduledDepartureDateUtc <= :depEnd
            AND f.scheduledDepartureDateUtc >= :depBegin""")
    fun findScheduledFlights(
            @Param("depAir")departureAirport: String,
            @Param("arrAir")arrivalAirport: String,
            @Param("depBegin")scheduledDepartureDateBeginUtc: ZonedDateTime,
            @Param("depEnd")scheduledDepartureDateEndUtc: ZonedDateTime) : List<FlightStatus>
}
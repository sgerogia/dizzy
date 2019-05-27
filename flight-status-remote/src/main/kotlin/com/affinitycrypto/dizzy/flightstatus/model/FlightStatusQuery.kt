package com.affinitycrypto.dizzy.flightstatus.model

import org.hibernate.validator.constraints.Length
import java.time.ZonedDateTime

data class FlightStatusQuery(
        @Length(min = 3) val departureAirport: String = "",
        @Length(min = 3) val arrivalAirport: String = "",
        val scheduledDepartureDateUtc: ZonedDateTime = ZonedDateTime.now()
)
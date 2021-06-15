package com.affinitycrypto.dizzy.flightstatus.validation

import com.affinitycrypto.dizzy.flightstatus.model.FlightStatusQuery
import org.springframework.stereotype.Component
import org.springframework.validation.Errors
import org.springframework.validation.Validator

@Component
class FlightStatusQueryValidator : Validator {

    override fun validate(target: Any, errors: Errors) {
        val query = target as FlightStatusQuery
        if (query.arrivalAirport == query.departureAirport) {
            errors.rejectValue(
                    "arrivalAirport",
                    "dizzy.status.airport.sameValue",
                    "Arrival and Departure airports must be different")
        }
    }

    override fun supports(clazz: Class<*>): Boolean =
        FlightStatusQuery::class.java.isAssignableFrom(clazz)
}
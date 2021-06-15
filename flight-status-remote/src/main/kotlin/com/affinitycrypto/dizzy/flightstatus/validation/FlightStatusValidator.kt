package com.affinitycrypto.dizzy.flightstatus.validation

import com.affinitycrypto.dizzy.flightstatus.model.FlightStatus
import com.affinitycrypto.dizzy.flightstatus.model.StatusIndicator
import org.springframework.stereotype.Component
import java.time.temporal.ChronoUnit
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import kotlin.math.abs

@Component
class FlightStatusValidator : ConstraintValidator<ValidStatus, FlightStatus> {

    companion object {
        @JvmStatic val CODE_AIRPORT_SAME_VALUE = "{dizzy.status.airport.sameValue}"
        @JvmStatic val CODE_SCHEDULED_ARRIVAL_TOO_SOON = "{dizzy.status.scheduledArrival.tooSoon}"
        @JvmStatic val CODE_SCHEDULED_ARRIVAL_BEFORE_DEPARTURE = "{dizzy.status.scheduledArrival.beforeScheduledDeparture}"
        @JvmStatic val CODE_ARRIVAL_BEFORE_DEPARTURE = "{dizzy.status.arrival.beforeDeparture}"
        @JvmStatic val CODE_ARRIVAL_TOO_SOON = "{dizzy.status.arrival.tooSoon}"
        @JvmStatic val CODE_STATUS_MISSING_DATES = "{dizzy.status.missingDatesForStatus}"
        @JvmStatic val CODE_STATUS_CANCELED_HAS_DATES = "{dizzy.status.canceledHasDates}"
    }

    override fun initialize(constraintAnnotation: ValidStatus?) {
    }
    
    override fun isValid(flight: FlightStatus, context: ConstraintValidatorContext): Boolean {

        var valid = true
        
        with(flight) {
            if (arrivalAirport == departureAirport) {
                valid = false
                context
                        .buildConstraintViolationWithTemplate(CODE_AIRPORT_SAME_VALUE)
                        .addPropertyNode("arrivalAirport")
                        .addPropertyNode("departureAirport")
                        .addConstraintViolation()
            }

            if (abs(ChronoUnit.HOURS.between(scheduledDepartureDateUtc, scheduledArrivalDateUtc)) < 1) {
                valid = false
                context
                        .buildConstraintViolationWithTemplate(CODE_SCHEDULED_ARRIVAL_TOO_SOON)
                        .addPropertyNode("scheduledDepartureDateUtc")
                        .addPropertyNode("scheduledArrivalDateUtc")
                        .addConstraintViolation()
            }

            if (scheduledArrivalDateUtc!!.compareTo(scheduledDepartureDateUtc) < 1) {
                valid = false
                context
                        .buildConstraintViolationWithTemplate(CODE_SCHEDULED_ARRIVAL_BEFORE_DEPARTURE)
                        .addPropertyNode("scheduledDepartureDateUtc")
                        .addPropertyNode("scheduledArrivalDateUtc")
                        .addConstraintViolation()
            }

            if (arrivalDateUtc != null && departureDateUtc != null
                    && arrivalDateUtc!!.compareTo(departureDateUtc) < 1) {
                valid = false
                context
                        .buildConstraintViolationWithTemplate(CODE_ARRIVAL_BEFORE_DEPARTURE)
                        .addPropertyNode("arrivalDateUtc")
                        .addPropertyNode("departureDateUtc")
                        .addConstraintViolation()
            }

            if (arrivalDateUtc != null && departureDateUtc != null
                    && abs(ChronoUnit.HOURS.between(departureDateUtc, arrivalDateUtc)) < 1) {
                valid = false
                context
                        .buildConstraintViolationWithTemplate(CODE_ARRIVAL_TOO_SOON)
                        .addPropertyNode("arrivalDateUtc")
                        .addPropertyNode("departureDateUtc")
                        .addConstraintViolation()
            }

            if (status !in arrayOf(null, StatusIndicator.Active, StatusIndicator.Scheduled, StatusIndicator.Canceled)
                    && (arrivalDateUtc == null || departureDateUtc == null)) {
                valid = false
                context
                        .buildConstraintViolationWithTemplate(CODE_STATUS_MISSING_DATES)
                        .addPropertyNode("arrivalDateUtc")
                        .addPropertyNode("departureDateUtc")
                        .addPropertyNode("status")
                        .addConstraintViolation()
            }

            if (status == StatusIndicator.Canceled
                    && (arrivalDateUtc == null || departureDateUtc == null)) {
                valid = false
                context
                        .buildConstraintViolationWithTemplate(CODE_STATUS_CANCELED_HAS_DATES)
                        .addPropertyNode("arrivalDateUtc")
                        .addPropertyNode("departureDateUtc")
                        .addPropertyNode("status")
                        .addConstraintViolation()
            }
            return valid
        }
    }
}
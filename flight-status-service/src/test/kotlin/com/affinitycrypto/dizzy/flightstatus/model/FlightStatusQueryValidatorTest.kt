package com.affinitycrypto.dizzy.flightstatus.model

import com.affinitycrypto.dizzy.flightstatus.validation.FlightStatusQueryValidator
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.springframework.validation.BeanPropertyBindingResult
import java.time.ZoneId
import java.time.ZonedDateTime

class FlightStatusQueryValidatorTest {

    val vld = FlightStatusQueryValidator()

    @Test
    fun rejectTwiceSameAirport() {
        // arrange
        val flight = FlightStatusQuery(
                departureAirport = "ATH",
                arrivalAirport = "ATH",
                scheduledDepartureDateUtc = ZonedDateTime.of(2018, 12, 20, 10, 12, 25, 0, ZoneId.of("UTC"))
        )
        val errors = BeanPropertyBindingResult(flight, "foo")
        // act
        vld.validate(flight, errors)
        // assert
        assertThat(errors.errorCount, `is`(1))
        assertThat(errors.getFieldError("arrivalAirport"), `is`(notNullValue()))
    }

    @Test
    fun validObject() {
        // arrange
        val flight = FlightStatusQuery(
                departureAirport = "ATH",
                arrivalAirport = "LON",
                scheduledDepartureDateUtc = ZonedDateTime.of(2018, 12, 20, 10, 12, 25, 0, ZoneId.of("UTC"))
        )
        val errors = BeanPropertyBindingResult(flight, "foo")
        // act
        vld.validate(flight, errors)
        // assert
        assertThat(errors.errorCount, `is`(0))
    }


}
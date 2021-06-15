package com.affinitycrypto.dizzy.flightstatus.validation

import com.affinitycrypto.dizzy.flightstatus.model.FlightStatus
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.validation.Validation
import javax.validation.Validator

class FlightStatusValidatorTest {

    private var validator: Validator? = null

    @BeforeAll
    fun setUp() {
        val factory = Validation.buildDefaultValidatorFactory()
        validator = factory.validator
    }

    @Test
    fun rejectTwiceSameAirport() {
        // arrange
        val flight = FlightStatus(
                flightNumber = "AA123",
                departureAirport = "ATH",
                arrivalAirport = "ATH",
                scheduledDepartureDateUtc = ZonedDateTime.of(2018, 12, 20, 10, 12, 25, 0, ZoneId.of("UTC")),
                scheduledArrivalDateUtc = ZonedDateTime.of(2018, 12, 20, 12, 12, 25, 0, ZoneId.of("UTC"))
        )
        // act
        val constraints = validator!!.validate(flight).toList()
        // assert
        MatcherAssert.assertThat(constraints.size, CoreMatchers.`is`(1))
        MatcherAssert.assertThat(constraints[0].propertyPath.toString(), CoreMatchers.`is`("arrivalAirport"))
        MatcherAssert.assertThat(constraints[0].message, CoreMatchers.`is`(FlightStatusValidator.CODE_AIRPORT_SAME_VALUE))
    }

//    @Test
//    fun rejectScheduledDepartureArrivalLessThanOneHourApart() {
//        // arrange
//        val flight = FlightStatus(
//                flightNumber = "AA123",
//                departureAirport = "ATH",
//                arrivalAirport = "LON",
//                scheduledDepartureDateUtc = ZonedDateTime.of(2018, 12, 20, 10, 12, 25, 0, ZoneId.of("UTC")),
//                scheduledArrivalDateUtc = ZonedDateTime.of(2018, 12, 20, 10, 32, 25, 0, ZoneId.of("UTC"))
//        )
//        val errors = BeanPropertyBindingResult(flight, "foo")
//        // act
//        vld.validate(flight, errors)
//        // assert
//        MatcherAssert.assertThat(errors.errorCount, CoreMatchers.`is`(1))
//        MatcherAssert.assertThat(errors.getFieldError("scheduledArrivalDateUtc"), CoreMatchers.`is`(CoreMatchers.notNullValue()))
//        MatcherAssert.assertThat(errors.getFieldError("scheduledArrivalDateUtc")!!.codes,
//                IsArrayContaining.hasItemInArray(FlightStatusValidator.CODE_SCHEDULED_ARRIVAL_TOO_SOON))
//    }
//
//    @Test
//    fun rejectScheduledArrivalBeforeDeparture() {
//        // arrange
//        val flight = FlightStatus(
//                flightNumber = "AA123",
//                departureAirport = "ATH",
//                arrivalAirport = "LON",
//                scheduledDepartureDateUtc = ZonedDateTime.of(2018, 12, 20, 10, 12, 25, 0, ZoneId.of("UTC")),
//                scheduledArrivalDateUtc = ZonedDateTime.of(2018, 12, 20, 8, 12, 25, 0, ZoneId.of("UTC"))
//        )
//        val errors = BeanPropertyBindingResult(flight, "foo")
//        // act
//        vld.validate(flight, errors)
//        // assert
//        MatcherAssert.assertThat(errors.errorCount, CoreMatchers.`is`(1))
//        MatcherAssert.assertThat(errors.getFieldError("scheduledArrivalDateUtc"), CoreMatchers.`is`(CoreMatchers.notNullValue()))
//        MatcherAssert.assertThat(errors.getFieldError("scheduledArrivalDateUtc")!!.codes,
//                IsArrayContaining.hasItemInArray(FlightStatusValidator.CODE_SCHEDULED_ARRIVAL_BEFORE_DEPARTURE))
//    }
//
//    @Test
//    fun rejectArrivalBeforeDeparture() {
//        // arrange
//        val flight = FlightStatus(
//                flightNumber = "AA123",
//                departureAirport = "ATH",
//                arrivalAirport = "LON",
//                scheduledDepartureDateUtc = ZonedDateTime.of(2018, 12, 20, 10, 12, 25, 0, ZoneId.of("UTC")),
//                scheduledArrivalDateUtc = ZonedDateTime.of(2018, 12, 20, 12, 12, 25, 0, ZoneId.of("UTC")),
//                departureDateUtc = ZonedDateTime.of(2018, 12, 20, 10, 12, 25, 0, ZoneId.of("UTC")),
//                arrivalDateUtc = ZonedDateTime.of(2018, 12, 20, 8, 12, 25, 0, ZoneId.of("UTC"))
//        )
//        val errors = BeanPropertyBindingResult(flight, "foo")
//        // act
//        vld.validate(flight, errors)
//        // assert
//        MatcherAssert.assertThat(errors.errorCount, CoreMatchers.`is`(1))
//        MatcherAssert.assertThat(errors.getFieldError("arrivalDateUtc"), CoreMatchers.`is`(CoreMatchers.notNullValue()))
//        MatcherAssert.assertThat(errors.getFieldError("arrivalDateUtc")!!.codes,
//                IsArrayContaining.hasItemInArray(FlightStatusValidator.CODE_ARRIVAL_BEFORE_DEPARTURE))
//    }
//
//    @Test
//    fun rejectDepartureArrivalLessThanOneHourApart() {
//        // arrange
//        val flight = FlightStatus(
//                flightNumber = "AA123",
//                departureAirport = "ATH",
//                arrivalAirport = "LON",
//                scheduledDepartureDateUtc = ZonedDateTime.of(2018, 12, 20, 10, 12, 25, 0, ZoneId.of("UTC")),
//                scheduledArrivalDateUtc = ZonedDateTime.of(2018, 12, 20, 12, 12, 25, 0, ZoneId.of("UTC")),
//                departureDateUtc = ZonedDateTime.of(2018, 12, 20, 10, 12, 25, 0, ZoneId.of("UTC")),
//                arrivalDateUtc = ZonedDateTime.of(2018, 12, 20, 11, 0, 25, 0, ZoneId.of("UTC"))
//        )
//        val errors = BeanPropertyBindingResult(flight, "foo")
//        // act
//        vld.validate(flight, errors)
//        // assert
//        MatcherAssert.assertThat(errors.errorCount, CoreMatchers.`is`(1))
//        MatcherAssert.assertThat(errors.getFieldError("arrivalDateUtc"), CoreMatchers.`is`(CoreMatchers.notNullValue()))
//        MatcherAssert.assertThat(errors.getFieldError("arrivalDateUtc")!!.codes,
//                IsArrayContaining.hasItemInArray(FlightStatusValidator.CODE_ARRIVAL_TOO_SOON))
//    }
//
//    @Test
//    fun rejectEmptyActualTimesWithIncorrectStatus() {
//        // arrange
//        val flight = FlightStatus(
//                flightNumber = "AA123",
//                departureAirport = "ATH",
//                arrivalAirport = "LON",
//                scheduledDepartureDateUtc = ZonedDateTime.of(2018, 12, 20, 10, 12, 25, 0, ZoneId.of("UTC")),
//                scheduledArrivalDateUtc = ZonedDateTime.of(2018, 12, 20, 12, 12, 25, 0, ZoneId.of("UTC")),
//                status = StatusIndicator.Landed
//        )
//        val errors = BeanPropertyBindingResult(flight, "foo")
//        // act
//        vld.validate(flight, errors)
//        // assert
//        MatcherAssert.assertThat(errors.errorCount, CoreMatchers.`is`(1))
//        MatcherAssert.assertThat(errors.getFieldError("status"), CoreMatchers.`is`(CoreMatchers.notNullValue()))
//        MatcherAssert.assertThat(errors.getFieldError("status")!!.codes,
//                IsArrayContaining.hasItemInArray(FlightStatusValidator.CODE_STATUS_MISSING_DATES))
//    }
//
//    @Test
//    fun rejectCanceledWithActualDates() {
//        // arrange
//        val flight = FlightStatus(
//                flightNumber = "AA123",
//                departureAirport = "ATH",
//                arrivalAirport = "LON",
//                scheduledDepartureDateUtc = ZonedDateTime.of(2018, 12, 20, 10, 12, 25, 0, ZoneId.of("UTC")),
//                scheduledArrivalDateUtc = ZonedDateTime.of(2018, 12, 20, 12, 12, 25, 0, ZoneId.of("UTC")),
//                arrivalDateUtc = ZonedDateTime.of(2018, 12, 20, 12, 12, 25, 0, ZoneId.of("UTC")),
//                status = StatusIndicator.Canceled
//        )
//        val errors = BeanPropertyBindingResult(flight, "foo")
//        // act
//        vld.validate(flight, errors)
//        // assert
//        MatcherAssert.assertThat(errors.errorCount, CoreMatchers.`is`(1))
//        MatcherAssert.assertThat(errors.getFieldError("status"), CoreMatchers.`is`(CoreMatchers.notNullValue()))
//        MatcherAssert.assertThat(errors.getFieldError("status")!!.codes,
//                IsArrayContaining.hasItemInArray(FlightStatusValidator.CODE_STATUS_CANCELED_HAS_DATES))
//    }
//
//    @Test
//    fun validObject() {
//        // arrange
//        val flight = FlightStatus(
//                flightNumber = "AA123",
//                departureAirport = "ATH",
//                arrivalAirport = "LON",
//                scheduledDepartureDateUtc = ZonedDateTime.of(2018, 12, 20, 10, 12, 25, 0, ZoneId.of("UTC")),
//                scheduledArrivalDateUtc = ZonedDateTime.of(2018, 12, 20, 12, 12, 25, 0, ZoneId.of("UTC")),
//                departureDateUtc = ZonedDateTime.of(2018, 12, 20, 10, 12, 25, 0, ZoneId.of("UTC")),
//                arrivalDateUtc = ZonedDateTime.of(2018, 12, 20, 11, 12, 25, 0, ZoneId.of("UTC")),
//                status = StatusIndicator.Diverted
//        )
//        val errors = BeanPropertyBindingResult(flight, "foo")
//        // act
//        vld.validate(flight, errors)
//        // assert
//        MatcherAssert.assertThat(errors.errorCount, CoreMatchers.`is`(0))
//    }

}
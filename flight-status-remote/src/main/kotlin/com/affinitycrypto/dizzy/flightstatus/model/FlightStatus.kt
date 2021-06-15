package com.affinitycrypto.dizzy.flightstatus.model

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.annotations.ApiModelProperty
import org.hibernate.validator.constraints.Length
import java.time.ZonedDateTime
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table
import javax.persistence.UniqueConstraint

enum class StatusIndicator {
    Active,
    Canceled,
    Diverted,
    Landed,
    Redirected,
    Scheduled
}

@Entity
@Table(
        name = "Flights",
        uniqueConstraints = [
            UniqueConstraint(
                    name = "singleFlightEachDay",
                    columnNames = [
                        "flightNumber",
                        "departureAirport",
                        "arrivalAirport",
                        "scheduledDepartureDateUtc",
                        "scheduledArrivalDateUtc"
                    ])
        ]
)
data class FlightStatus(
        @ApiModelProperty(value = "flightId", name = "Unique flight identifier", readOnly = true)
        @Id
        val flightId: String = UUID.randomUUID().toString(),
        
        @ApiModelProperty(value = "flightNumber", name = "Flight number", required = true, example = "AA123")
        @Length(min = 3) val flightNumber: String = "",
        
        @ApiModelProperty(value = "departureAirport", name = "Departure airport for this flight", required = true, example = "LGW")
        @Length(min = 3) val departureAirport: String = "",

        @ApiModelProperty(value = "arrivalAirport", name = "Arrival airport for this flight", required = true, example = "ATH")
        @Length(min = 3) val arrivalAirport: String = "",

        @ApiModelProperty(value = "scheduledDeparture", name = "Scheduled departure timestamp in UTC timezone", required = true, example = "2019-03-23T19:45:00Z")
        @JsonProperty("scheduledDeparture")
        val scheduledDepartureDateUtc: ZonedDateTime = ZonedDateTime.now(),

        @ApiModelProperty(value = "departure", name = "Actual departure timestamp in UTC timezone", required = false, example = "2019-03-23T19:53:00Z")
        @JsonProperty("departure")
        val departureDateUtc: ZonedDateTime? = null,

        @ApiModelProperty(value = "scheduledArrival", name = "Scheduled arrival timestamp in UTC timezone", required = true, example = "2019-03-24T20:05:00Z")
        @JsonProperty("scheduledArrival")
        val scheduledArrivalDateUtc: ZonedDateTime = ZonedDateTime.now(),

        @ApiModelProperty(value = "arrival", name = "Actual arrival timestamp in UTC timezone", required = false, example = "2019-03-23T19:45:00Z")
        @JsonProperty("arrival")
        val arrivalDateUtc: ZonedDateTime? = null,

        @ApiModelProperty(value = "status", name = "Status of the flight", required = false, example = "Scheduled")
        val status: StatusIndicator = StatusIndicator.Scheduled
)

Flight status service
=====================

A simple flight reference service, developed in Kotlin and SpringBoot.
It is vaguely similar to [FlightStats Status by Route API][1].

All data is stored in-memory, in an embedded [H2 db][2].

# Usage

`mvn spring-boot:run`

# Functional Endpoints

1. `GET /flightstatus/api/v1/flights/{departureAirport}/{arrivalAirport}/dep/{year}/{month}/{day}`
Return all the matching flights for that day and airport combination.

1. `POST /flightstatus/api/v1/flights`
Create a new flight entry.

1. `PUT /flightstatus/api/v1/flights`
Update an existing flight entry

# Utility Endpoints

1. `/flightstatus/h2`
Open in a browser to have access to the tables and data.
Connection string: `jdbc:h2:mem:flightsDb`
User/pwd: `sa/sa`

1. `GET /flightstatus/actuator`
Standard [SpringBoot Actuator][3] endpoint.

1. `GET /flightstatus/swagger2`
Detailed Swagger documentation for the functional endpoints.



  [1]: https://developer.flightstats.com/api-docs/flightstatus/v2/route
  [2]: http://www.h2database.com/html/main.html
  [3]: https://spring.io/guides/gs/actuator-service/
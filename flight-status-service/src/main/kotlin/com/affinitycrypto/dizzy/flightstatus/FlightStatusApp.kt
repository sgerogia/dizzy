package com.affinitycrypto.dizzy.flightstatus

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import springfox.documentation.swagger2.annotations.EnableSwagger2
import java.util.*
import javax.annotation.PostConstruct

@SpringBootApplication
@EnableSwagger2
class FlightStatusApp {

    // change default timezone for persistence layer
    // as spring.jpa.properties.hibernate.jdbc.time_zone seems to be ignored
    // ref: https://moelholm.com/2016/11/09/spring-boot-controlling-timezones-with-hibernate/
    @PostConstruct
    fun started() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(FlightStatusApp::class.java, *args)
}
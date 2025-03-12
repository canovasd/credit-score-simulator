package com.credit.score.simulator.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock
import java.time.ZoneId

@Configuration
class ClockConfig {

    @Bean
    fun systemClock(@Value("\${app.clock.zone-id}") zoneId: String): Clock {
        return Clock.system(ZoneId.of(zoneId))
    }
}

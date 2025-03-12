package com.credit.score.simulator.config

import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ResourceBundleMessageSource

const val MESSAGE_FILE_NAME = "messages"
const val DEFAULT_ENCODING = "UTF-8"

@Configuration
class MessageSourceConfig {

    @Bean
    fun messageSource(): MessageSource {
        val messageSource = ResourceBundleMessageSource()
        messageSource.setBasename(MESSAGE_FILE_NAME)
        messageSource.setDefaultEncoding(DEFAULT_ENCODING)
        return messageSource
    }
}

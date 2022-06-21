package com.example.simple_message.interfaces

import java.time.LocalDateTime
import java.time.ZonedDateTime

interface MessageInterface {
    val text: String
    val name: String
    val time: ZonedDateTime
}
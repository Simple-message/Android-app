package com.example.simple_message.factories

import com.example.simple_message.interfaces.MessageInterface
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime

class Message(override val text: String, override val time: ZonedDateTime) : MessageInterface {}
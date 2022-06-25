package com.example.simple_message.factories

import com.example.simple_message.interfaces.MessageInterface
import java.time.ZonedDateTime

class Message(override val text: String, override val name: String, override val time: ZonedDateTime) :
    MessageInterface

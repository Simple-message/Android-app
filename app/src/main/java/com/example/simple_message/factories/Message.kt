package com.example.simple_message.factories

import com.example.simple_message.interfaces.MessageInterface
import java.time.LocalDateTime

class Message(override val text: String, override val time: LocalDateTime) : MessageInterface {}
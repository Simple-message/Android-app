package com.example.simple_message.interfaces

import java.time.ZonedDateTime

interface ChatInterface {
    val text: String
    val name: String
    val time: ZonedDateTime?
    val uid: String
}
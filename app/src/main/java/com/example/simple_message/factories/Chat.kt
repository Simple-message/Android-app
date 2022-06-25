package com.example.simple_message.factories

import com.example.simple_message.interfaces.ChatInterface
import java.time.ZonedDateTime

class Chat(override val uid: String, override val text: String, override val name: String, override val time: ZonedDateTime?) : ChatInterface

package com.example.simple_message.factories

import com.example.simple_message.interfaces.FeedInterface

class Feed(override var chats: Array<Chat?>) : FeedInterface

package com.example.compose.jetchat.data

import com.example.compose.jetchat.R
import com.example.compose.jetchat.conversation.ConversationUiState
import com.example.compose.jetchat.conversation.Message
import com.example.compose.jetchat.data.EMOJIS.EMOJI_PINK_HEART
import com.example.compose.jetchat.data.EMOJIS.EMOJI_FLAMINGO
import com.example.compose.jetchat.data.EMOJIS.EMOJI_CLOUDS

val keshavSoftHrMessages = listOf(
    Message(
        "me", // Sender
        "Hello, I hope you're doing well. I wanted to follow up on the interview process with KeshavSoft.",
        "10:00 AM"
    ),
    Message(
        "KeshavSoft HR", // Sender
        "Hello! I'm doing great, thanks for asking. We're planning to schedule an interview next week. What date works for you?",
        "10:02 AM"
    ),
    Message(
        "me", // Sender
        "Next Wednesday would work perfectly for me. Could you confirm the timing for the interview?",
        "10:04 AM"
    ),
    Message(
        "KeshavSoft HR", // Sender
        "Great! We will confirm the exact time soon. Let me know if you have any questions before then. Looking forward to speaking with you!",
        "10:06 AM"
    ),
    Message(
        "me", // Sender
        "Thank you! I'm excited. Please let me know if there's anything I need to prepare.",
        "10:08 AM"
    ),
    Message(
        "KeshavSoft HR", // Sender
        "You're welcome! We'll send you an email with all the details. Have a great day! $EMOJI_PINK_HEART",
        "10:10 AM"
    ),
    Message(
        "me", // Sender
        "Thanks again! Looking forward to it. $EMOJI_FLAMINGO $EMOJI_CLOUDS",
        "10:12 AM"
    )
)

// Rename to avoid conflicts
val keshavSoftHrUiState = ConversationUiState(
    initialMessages = keshavSoftHrMessages,
    channelName = "#keshavsoft_hr",
    channelMembers = 1 // Just you and the HR in this conversation
)


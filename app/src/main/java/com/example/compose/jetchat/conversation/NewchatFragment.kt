package com.example.compose.jetchat.conversation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.compose.jetchat.databinding.FragmentNewchatBinding
import com.example.compose.jetchat.conversation.newchat.NewChatMessage
import com.example.compose.jetchat.conversation.newchat.MessageAdapter
import com.example.compose.jetchat.data.* // Import data_1.kt, which includes keshavSoftHrMessages

class NewchatFragment : Fragment() {

    private var _binding: FragmentNewchatBinding? = null
    private val binding get() = _binding!!

    // Make messages a MutableList to allow modification
    private val messages: MutableList<NewChatMessage> = keshavSoftHrMessages.map {
        NewChatMessage(it.author, it.content, it.timestamp) // Convert each Message to NewChatMessage
    }.toMutableList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewchatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up RecyclerView with messages
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = MessageAdapter(messages)  // Using the mutable list of NewChatMessage
        }

        // Set ComposeView content for UserInput
        binding.composeView.setContent {
            UserInput(
                onMessageSent = { message ->
                    // Handle message sending and update RecyclerView
                    val timeStamp = System.currentTimeMillis() // Get current timestamp
                    val formattedTime = java.text.SimpleDateFormat("hh:mm a").format(java.util.Date(timeStamp))

                    // Add the new message to the mutable list
                    messages.add(NewChatMessage("You", message, formattedTime))

                    // Notify adapter that data has changed and scroll to the new message
                    binding.recyclerView.adapter?.notifyDataSetChanged() // Update adapter
                    binding.recyclerView.scrollToPosition(messages.size - 1) // Scroll to the bottom
                }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.example.compose.jetchat

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.compose.jetchat.components.JetchatDrawer
import com.example.compose.jetchat.databinding.ContentMainBinding
import kotlinx.coroutines.launch

/**
 * Main activity for the app.
 */
@OptIn(ExperimentalMaterial3Api::class)
class NavActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { _, insets -> insets }

        setContentView(
            ComposeView(this).apply {
                consumeWindowInsets = false
                setContent {
                    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                    val drawerOpen by viewModel.drawerShouldBeOpened.collectAsStateWithLifecycle()
                    var selectedMenu by remember { mutableStateOf("composers") }

                    val scope = rememberCoroutineScope()

                    LaunchedEffect(drawerOpen) {
                        if (drawerOpen) {
                            try {
                                drawerState.open()
                            } finally {
                                viewModel.resetOpenDrawerAction()
                            }
                        }
                    }

                    JetchatDrawer(
                        drawerState = drawerState,
                        selectedMenu = selectedMenu,
                        onChatClicked = { chatId ->
                            when (chatId) {
                                "Newchat" -> navigateToDestination(R.id.nav_newchat)
                                "GpsTracker" -> navigateToDestination(R.id.gpsTrackerFragment)
                                else -> navigateToDestination(R.id.nav_home)
                            }
                            scope.launch { drawerState.close() }
                            selectedMenu = chatId
                        },
                        onProfileClicked = { userId ->
                            navigateToDestination(R.id.nav_profile, bundleOf("userId" to userId))
                            scope.launch { drawerState.close() }
                            selectedMenu = userId
                        }
                    ) {
                        AndroidViewBinding(ContentMainBinding::inflate)
                    }
                }
            }
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController().navigateUp() || super.onSupportNavigateUp()
    }

    /**
     * Handles navigation to specified destinations safely.
     */
    private fun navigateToDestination(destinationId: Int, args: Bundle? = null) {
        val navController = findNavController()
        navController.popBackStack(R.id.nav_home, false)
        navController.navigate(destinationId, args)
    }

    /**
     * See https://issuetracker.google.com/142847973
     */
    private fun findNavController(): NavController {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        return navHostFragment.navController
    }
}

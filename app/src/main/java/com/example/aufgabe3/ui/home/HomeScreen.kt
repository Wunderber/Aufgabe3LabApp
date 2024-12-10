package com.example.aufgabe3.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.aufgabe3.model.BookingEntry
import com.example.aufgabe3.viewmodel.SharedViewModel
import java.time.format.DateTimeFormatter

/**
 * Composable function to display the home screen with a list of booking entries.
 * Allows navigation to the add screen and deletion of existing bookings.
 *
 * @param navController Navigation controller for navigating to the add screen.
 * @param sharedViewModel ViewModel to manage booking entries.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    sharedViewModel: SharedViewModel
) {
    // Observes the list of booking entries from the ViewModel
    val bookingsEntries by sharedViewModel.bookingsEntries.collectAsState()
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy") // Formatter for date display

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Booking Entries") } // Screen title
            )
        },
        floatingActionButton = {
            // Floating action button to navigate to the add booking screen
            FloatingActionButton(onClick = {
                navController.navigate("add")
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add booking")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Display a message if no bookings are available
            if (bookingsEntries.isEmpty()) {
                Text(
                    text = "No bookings available.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                )
            } else {
                // LazyColumn to display the list of booking entries
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(bookingsEntries) { booking ->
                        BookingEntryItem(
                            booking = booking,
                            onDeleteClick = {
                                // Deletes the selected booking entry via ViewModel
                                sharedViewModel.deleteBookingEntry(booking)
                            },
                            dateFormatter = dateFormatter
                        )
                    }
                }
            }
        }
    }
}

/**
 * Composable function to display a single booking entry.
 * Includes the name, date range, and a delete button.
 *
 * @param booking The booking entry to display.
 * @param onDeleteClick Callback invoked when the delete button is clicked.
 * @param dateFormatter Formatter to format the booking's dates.
 */
@Composable
fun BookingEntryItem(
    booking: BookingEntry,
    onDeleteClick: () -> Unit,
    dateFormatter: DateTimeFormatter
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp) // Card elevation for shadow
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f) // Column to display the booking details
            ) {
                Text(
                    text = booking.name, // Displays the booking name
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${booking.arrivalDate.format(dateFormatter)} - ${booking.departureDate.format(dateFormatter)}", // Displays the formatted date range
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, contentDescription = "Delete booking") // Delete button
            }
        }
    }
}

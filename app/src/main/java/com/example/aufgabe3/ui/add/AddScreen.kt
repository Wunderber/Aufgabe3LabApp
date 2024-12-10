package com.example.aufgabe3.ui.add

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.aufgabe3.model.BookingEntry
import com.example.aufgabe3.viewmodel.SharedViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Composable function for adding a booking entry.
 * Provides a form for the user to enter a name and select a date range.
 * Validates input and saves the booking via the SharedViewModel.
 *
 * @param navController Navigation controller for navigating between screens.
 * @param sharedViewModel ViewModel for managing booking entries.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    navController: NavHostController,
    sharedViewModel: SharedViewModel
) {
    // State variables for user input and UI behavior
    var name by remember { mutableStateOf("") } // Name of the booking entry
    var arrivalDate by remember { mutableStateOf<LocalDate?>(null) } // Start date
    var departureDate by remember { mutableStateOf<LocalDate?>(null) } // End date
    var showDatePicker by remember { mutableStateOf(false) } // Controls the visibility of the date picker
    var errorMessage by remember { mutableStateOf("") } // Stores error messages for validation
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy") // Format for displaying dates

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Booking") }, // Screen title
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back") // Back button
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Input field for the name
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                isError = errorMessage.contains("Name") // Highlight field in case of a validation error
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Input field for the date range
            OutlinedTextField(
                value = if (arrivalDate != null && departureDate != null) {
                    "${arrivalDate!!.format(dateFormatter)} - ${departureDate!!.format(dateFormatter)}"
                } else {
                    ""
                },
                onValueChange = {},
                label = { Text("Select Date Range") },
                enabled = false,
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true }, // Opens the date picker when clicked
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface
                ),
                isError = errorMessage.contains("Date") // Highlight field in case of a validation error
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Displays error messages if present
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Button to save the booking entry
            Button(
                onClick = {
                    // Validate the input
                    if (name.isBlank()) {
                        errorMessage = "Please enter a name."
                        return@Button
                    }

                    if (arrivalDate == null || departureDate == null) {
                        errorMessage = "Please select a valid date range."
                        return@Button
                    }

                    if (departureDate!!.isBefore(arrivalDate)) {
                        errorMessage = "The end date cannot be before the start date."
                        return@Button
                    }

                    // Save the booking and reset the error message
                    sharedViewModel.addBookingEntry(
                        BookingEntry(name, arrivalDate!!, departureDate!!)
                    )
                    errorMessage = ""
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Save")
            }
        }
    }

    // Show the date picker if triggered
    if (showDatePicker) {
        DateRangePicker(
            onDateRangeSelected = { start, end ->
                arrivalDate = start
                departureDate = end
                showDatePicker = false
            },
            onDismissRequest = { showDatePicker = false }
        )
    }
}

/**
 * Composable function for displaying a date range picker in a modal bottom sheet.
 * Allows the user to select a start and end date.
 *
 * @param onDateRangeSelected Callback invoked with the selected date range.
 * @param onDismissRequest Callback invoked when the date picker is dismissed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePicker(
    onDateRangeSelected: (LocalDate, LocalDate) -> Unit,
    onDismissRequest: () -> Unit
) {
    val state = rememberDateRangePickerState() // State for managing the date picker selection

    ModalBottomSheet(
        onDismissRequest = onDismissRequest, // Closes the sheet when dismissed
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title of the date picker
            Text(
                text = "Select Date Range",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Container for the date picker component
            Box(
                modifier = Modifier
                    .height(400.dp)
                    .width(350.dp)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                DateRangePicker(
                    state = state,
                    title = { Text("Select Date Range", fontSize = 16.sp) },
                    headline = { Text("Choose Start and End Date", fontSize = 14.sp) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Button to confirm the selected date range
            Button(
                onClick = {
                    val startMillis = state.selectedStartDateMillis
                    val endMillis = state.selectedEndDateMillis

                    if (startMillis != null && endMillis != null) {
                        val selectedStartDate = LocalDate.ofEpochDay(startMillis / (24 * 60 * 60 * 1000))
                        val selectedEndDate = LocalDate.ofEpochDay(endMillis / (24 * 60 * 60 * 1000))
                        onDateRangeSelected(selectedStartDate, selectedEndDate)
                    }
                },
                enabled = state.selectedStartDateMillis != null && state.selectedEndDateMillis != null,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Save Date", color = Color.White)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Button to cancel the selection process
            TextButton(onClick = onDismissRequest) {
                Text("Cancel", color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

package com.example.aufgabe3.model

import java.time.LocalDate

/**
 * Data class representing a booking entry.
 *
 * @property name The name of the booking.
 * @property arrivalDate The date of arrival for the booking.
 * @property departureDate The date of departure for the booking.
 */
data class BookingEntry(
    val name: String,
    val arrivalDate: LocalDate,
    val departureDate: LocalDate
)


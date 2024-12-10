package com.example.aufgabe3.viewmodel

import androidx.lifecycle.ViewModel
import com.example.aufgabe3.model.BookingEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

class SharedViewModel : ViewModel() {
    // Liste der Buchungseinträge
    private val _bookingsEntries = MutableStateFlow<List<BookingEntry>>(emptyList())
    val bookingsEntries: StateFlow<List<BookingEntry>> = _bookingsEntries

    /**
     * Fügt einen neuen Buchungseintrag hinzu.
     * @param entry Das zu speichernde BookingEntry-Objekt.
     */
    fun addBookingEntry(entry: BookingEntry) {
        // Füge den neuen Eintrag zur Liste hinzu
        _bookingsEntries.value = _bookingsEntries.value + entry
    }

    /**
     * Entfernt einen Buchungseintrag.
     * @param entry Der zu entfernende BookingEntry.
     */
    fun deleteBookingEntry(entry: BookingEntry) {
        // Entferne den Eintrag aus der Liste
        _bookingsEntries.value = _bookingsEntries.value - entry
    }
}

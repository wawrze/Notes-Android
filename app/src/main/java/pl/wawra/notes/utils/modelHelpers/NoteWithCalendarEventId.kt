package pl.wawra.notes.utils.modelHelpers

import java.util.*

data class NoteWithCalendarEventId(
    var id: Long = 0L,
    var title: String = "",
    var body: String = "",
    var date: Long = Calendar.getInstance().timeInMillis,
    var isDone: Boolean = false,
    var isProtected: Boolean = false,
    var calendarEventId: String? = null
)
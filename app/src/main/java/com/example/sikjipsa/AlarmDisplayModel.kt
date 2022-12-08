package com.example.sikjipsa

data class AlarmDisplayModel (
    val hour: Int,
    val minute: Int,
    var onOff: Boolean
) {

    //Getting the hour and the minute
    val timeText: String
        get() {
            val h = "%02d".format(if(hour < 12) hour else hour - 12)
            val m = "%02d".format(minute)

            return "$h:$m"
        }

    //Getting the string AM or PM based on the time
    val ampmText: String
        get() {
            return if(hour < 12) "AM" else "PM"
        }

    //Getting the string OFF or ON based on the alarm status
    val onOffText: String
        get() {
            return if(onOff) "Alarm OFF" else "Alarm ON"
        }

    //Data for storing in the DB
    fun makeDataForDB(): String {
        return "$hour:$minute"
    }
}
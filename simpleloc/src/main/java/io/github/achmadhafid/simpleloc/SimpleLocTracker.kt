package io.github.achmadhafid.simpleloc

interface SimpleLocTracker {

    val isRunning: Boolean

    fun enable()
    fun disable()

    enum class StopState {
        PAUSED_BY_LIFECYCLE,
        DESTROYED_BY_LIFECYCLE,
        STOPPED_BY_SYSTEM,
        STOPPED_BY_USER
    }

}

//region Extensions

fun SimpleLocTracker.toggle() {
    enableOrDisable { !isRunning }
}

fun SimpleLocTracker.enableOrDisable(premise: () -> Boolean) {
    if (premise()) enable() else disable()
}

//endregion

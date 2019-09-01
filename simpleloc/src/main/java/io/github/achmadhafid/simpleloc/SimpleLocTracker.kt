package io.github.achmadhafid.simpleloc

interface SimpleLocTracker {

    val isRunning: Boolean

    fun enable(isForce: Boolean = false)
    fun disable(isForce: Boolean = false)

    enum class StopState {
        PAUSED_BY_LIFECYCLE,
        DESTROYED_BY_LIFECYCLE,
        STOPPED_BY_SYSTEM,
        STOPPED_BY_USER
    }

}

//region Extensions

fun SimpleLocTracker.toggle(force: Boolean = false) {
    enableOrDisable(force) { !isRunning }
}

fun SimpleLocTracker.enableOrDisable(force: Boolean = false, premise: () -> Boolean) {
    if (premise()) enable(force) else disable(force)
}

//endregion

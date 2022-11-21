package io.alcatraz.bloodpressure

interface AsyncInterface<T> {
    fun onDone(result: T)
}

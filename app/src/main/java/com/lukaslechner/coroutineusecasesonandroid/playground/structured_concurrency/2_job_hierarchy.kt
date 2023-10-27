package com.lukaslechner.coroutineusecasesonandroid.playground.structured_concurrency

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun main() {
    val scopeJob = Job()
    val scope = CoroutineScope(Dispatchers.Default + scopeJob)

    val passedJob = Job()
    val coroutineJob = scope.launch(passedJob) {
        println("Starting coroutine")
        delay(500)
        println("Ending coroutine")
    }

    Thread.sleep(100)
    println("passedJOb and coroutineJob are references to the same job object? => ${passedJob === coroutineJob}")

    println("Is coroutineJob is a child of scopeJOb? => ${scopeJob.children.contains(coroutineJob)}")
    Thread.sleep(1000)
}

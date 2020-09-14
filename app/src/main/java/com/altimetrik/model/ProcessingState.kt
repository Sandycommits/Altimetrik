package com.altimetrik.model

class ProcessingState(var state: State, var error: Throwable?, var extras: Any?) {

    /**
     * This static properties used to return the response state after the API call.
     */
    companion object {
        val errorState = ProcessingState(State.FAILED, Throwable(), null)
        val successState = ProcessingState(State.SUCCESS, Throwable(), Any())
        val dbSuccessState = ProcessingState(State.DB_SUCCESS, Throwable(), Any())
        val noDataState = ProcessingState(State.NO_DATA, Throwable(), null)
    }
}
package com.altimetrik.model

enum class State(val value: Int) {
    NO_DATA(0), SUCCESS(1), DB_SUCCESS(2), FAILED(-1)
}
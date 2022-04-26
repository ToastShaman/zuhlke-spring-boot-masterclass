package com.github.toastshaman.springbootmasterclass.covid.domain

import java.time.LocalDate

data class MetricEnvelope(
    val length: String,
    val maxPageLimit: String,
    val totalRecords: String,
    val data: List<DataPoint>
) {
    companion object
}

data class DataPoint(
    val date: LocalDate,
    val value: Double
) {
    companion object
}
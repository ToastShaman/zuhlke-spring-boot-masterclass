package com.github.toastshaman.springbootmasterclass.covid.client

import com.github.toastshaman.springbootmasterclass.covid.domain.MetricEnvelope
import dev.forkhandles.result4k.Result

interface CoronavirusData {
    fun newCasesByPublishDate(): Result<MetricEnvelope, Exception>
}
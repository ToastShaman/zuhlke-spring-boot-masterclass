package com.github.toastshaman.springbootmasterclass.covid.domain

import com.github.javafaker.Faker
import java.time.LocalDate
import kotlin.random.Random
import kotlin.random.asJavaRandom

fun MetricEnvelope.Companion.random(random: Random = Random(17354)): MetricEnvelope {
    val faker = Faker(random.asJavaRandom())

    return MetricEnvelope(
        length = faker.random().nextInt(100).toString(),
        maxPageLimit = faker.random().nextInt(100).toString(),
        totalRecords = faker.random().nextInt(100).toString(),
        data = IntRange(0, faker.random().nextInt(1, 10)).map { DataPoint.random(random) }
    )
}

fun DataPoint.Companion.random(random: Random = Random(17354)): DataPoint {
    val faker = Faker(random.asJavaRandom())
    return DataPoint(LocalDate.EPOCH, faker.random().nextInt(0, 100).toDouble())
}
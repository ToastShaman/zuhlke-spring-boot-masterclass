package com.github.toastshaman.springbootmasterclass.covid.domain

import com.github.javafaker.Faker
import java.time.LocalDate
import kotlin.random.Random
import kotlin.random.asJavaRandom

val rnd = Random(17354)
val defaultFaker = Faker(rnd.asJavaRandom())

fun MetricEnvelope.Companion.random(faker: Faker = defaultFaker) = MetricEnvelope(
    length = faker.random().nextInt(100).toString(),
    maxPageLimit = faker.random().nextInt(100).toString(),
    totalRecords = faker.random().nextInt(100).toString(),
    data = IntRange(0, faker.random().nextInt(1, 10)).map { DataPoint.random(faker) }
)

fun DataPoint.Companion.random(faker: Faker = defaultFaker) = DataPoint(
    date = LocalDate.EPOCH,
    value = faker.random().nextInt(0, 100).toDouble()
)
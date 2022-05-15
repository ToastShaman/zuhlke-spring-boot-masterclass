package jooq.generate

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

abstract class JooqGeneratorTask : DefaultTask() {

    @get:Input
    abstract val migrationLocations: Property<String>

    @get:Input
    abstract val outputDirectory: DirectoryProperty

    @get:Input
    abstract val outputPackageName: Property<String>

    @TaskAction
    fun execute() {
        val properties = JooqGeneratorProperties(
            migrationLocations = listOf(migrationLocations.get()),
            outputPackageName = outputPackageName.get(),
            outputDirectory = outputDirectory.get(),
            temporaryDirectory = temporaryDir
        )
        JooqGenerator(properties).generate()
    }
}
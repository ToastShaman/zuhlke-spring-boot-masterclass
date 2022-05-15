package jooq.generate

import org.flywaydb.core.Flyway
import org.gradle.api.file.Directory
import org.h2.tools.DeleteDbFiles
import org.jooq.codegen.GenerationTool
import org.jooq.meta.jaxb.Configuration
import org.jooq.meta.jaxb.Database
import org.jooq.meta.jaxb.Generate
import org.jooq.meta.jaxb.Generator
import org.jooq.meta.jaxb.Jdbc
import org.jooq.meta.jaxb.Target
import java.io.File
import java.util.*

data class JooqGeneratorProperties(
    val migrationLocations: List<String>,
    val outputPackageName: String,
    val outputDirectory: Directory,
    val temporaryDirectory: File
)

class JooqGenerator(private val properties: JooqGeneratorProperties) {

    fun generate() {
        val databaseFile = properties.temporaryDirectory
            .toPath()
            .resolve(UUID.randomUUID().toString())
            .toAbsolutePath()

        val url = "jdbc:h2:file:$databaseFile"

        val migrateResult = Flyway.configure()
            .dataSource(url, "sa", "")
            .locations(*properties.migrationLocations.toTypedArray())
            .load()
            .migrate()

        check(migrateResult.success)

        val configuration: Configuration = Configuration()
            .withJdbc(
                Jdbc()
                    .withDriver("org.h2.Driver")
                    .withUrl(url)
                    .withUser("sa")
                    .withPassword("")
            )
            .withGenerator(
                Generator()
                    .withName("org.jooq.codegen.KotlinGenerator")
                    .withGenerate(
                        Generate()
                            .withDaos(true)
                            .withPojos(true)
                            .withPojosAsKotlinDataClasses(true)
                            .withPojosToString(false)
                            .withImmutablePojos(true)
                            .withRecords(true)
                    )
                    .withDatabase(
                        Database()
                            .withName("org.jooq.meta.h2.H2Database")
                            .withIncludes(".*")
                            .withExcludes("")
                            .withInputSchema("PUBLIC")
                    )
                    .withTarget(
                        Target()
                            .withPackageName(properties.outputPackageName)
                            .withDirectory(properties.outputDirectory.asFile.absolutePath)
                    )
            )

        GenerationTool.generate(configuration)
    }
}
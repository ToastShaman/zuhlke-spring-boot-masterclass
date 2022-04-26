package setup

import org.approvaltests.namer.StackTraceNamer
import org.springframework.test.web.reactive.server.WebTestClient
import java.nio.file.Path
import kotlin.io.path.createDirectories

class PackageSettings {
    companion object {
        @JvmStatic
        var ApprovalBaseDirectory = "../resources"
    }
}

fun WebTestClient.BodyContentSpec.strictJson(expectedJson: String) = json(expectedJson, true)

fun WebTestClient.BodyContentSpec.verifyJsonStrictly(): WebTestClient.BodyContentSpec {
    val stackTraceNamer = StackTraceNamer()
    val approvedFile = stackTraceNamer.getApprovedFile(".json")
    if (!approvedFile.exists()) {
        Path.of(approvedFile.parent).createDirectories()
        approvedFile.createNewFile()
    }
    return json(approvedFile.readText(), true)
}

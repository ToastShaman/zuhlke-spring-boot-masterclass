package assertions

import com.github.toastshaman.springbootmasterclass.events.CapturingEvents
import com.github.toastshaman.springbootmasterclass.events.Event
import org.json.JSONObject
import org.skyscreamer.jsonassert.JSONCompare
import org.skyscreamer.jsonassert.JSONCompareMode
import strikt.api.Assertion
import strikt.assertions.filter
import strikt.assertions.first

fun Assertion.Builder<CapturingEvents>.containsEventWithName(expectedName: String) =
    get(CapturingEvents::captured).filter { it.name == expectedName }.first()

val Assertion.Builder<Event>.payload get() = get(Event::payload)
val Assertion.Builder<Event>.name get() = get(Event::name)
val Assertion.Builder<Event>.category get() = get(Event::category)
fun Assertion.Builder<JSONObject>.isEqualToJson(expected: String) =
    assert("is equal to JSON %s", expected) { subject ->
        val result = JSONCompare.compareJSON(expected, subject.toString(), JSONCompareMode.STRICT)
        when {
            result.failed() -> fail(subject, result.message)
            else -> pass(subject)
        }
    }
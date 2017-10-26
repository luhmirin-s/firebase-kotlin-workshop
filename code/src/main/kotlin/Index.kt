import bindings.Admin
import bindings.Date
import bindings.Functions

external val exports: dynamic

data class Rates(
    val usd: Float,
    val czk: Float,
    val pln: Float,
    val rub: Float
)

data class Conversions(
    val eur: Float,
    val usd: Float? = null,
    val czk: Float? = null,
    val pln: Float? = null,
    val rub: Float? = null,
    val timestamp: Long? = null
)

fun main(args: Array<String>) {
    Admin.initializeApp(Functions.config().firebase)

    exports.testPush = Functions.https.onRequest { req, res ->
        Admin.database().ref("/conversions")
            .push(Conversions(req.query.eur.toString().toFloat()))
            .then { res.status(200).send("done") }
    }

    exports.convert = Functions.database.ref("/conversions/{conversionID}").onWrite<Conversions> { event ->
        val input = event.data.`val`()
        if (input.timestamp != null) return@onWrite
        Admin.database().ref("/rates").once<Rates>("value")
            .then<Conversions> { ratesSnap ->
                ratesSnap.`val`().let {
                    Conversions(
                        eur = input.eur,
                        usd = input.eur * it.usd,
                        czk = input.eur * it.czk,
                        pln = input.eur * it.pln,
                        rub = input.eur * it.rub,
                        timestamp = Date.now()
                    )
                }
            }
            .then { result ->
                Admin.database().ref("/conversions")
                    .child(event.params.conversionID)
                    .set(result)
            }
    }
}

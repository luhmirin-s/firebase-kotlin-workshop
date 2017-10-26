external fun require(module: String): dynamic
external val exports: dynamic

val functions = require("firebase-functions")
val admin = require("firebase-admin")

data class Conversions(
    val eur: Float,
    val usd: Float? = null,
    val czk: Float? = null,
    val pln: Float? = null,
    val rub: Float? = null,
    val timestamp: Long? = null
)

fun main(args: Array<String>) {
    admin.initializeApp(functions.config().firebase)

    exports.testPush = functions.https.onRequest { req, res ->
        admin.database().ref("/conversions")
            .push(Conversions(req.query.eur.toString().toFloat()))
            .then { res.status(200).send("done") }
    }
}
package domain

data class Conversions(
    val eur: Float,
    val usd: Float? = null,
    val czk: Float? = null,
    val pln: Float? = null,
    val rub: Float? = null,
    val timestamp: Long? = null
)

fun Conversions.isNew() = timestamp == null

fun Conversions.convert(rates: Rates, timestamp: Long) = Conversions(
    eur = eur,
    usd = eur * rates.usd,
    czk = eur * rates.czk,
    pln = eur * rates.pln,
    rub = eur * rates.rub,
    timestamp = timestamp
)
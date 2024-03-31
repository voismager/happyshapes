package fluffy.tigerrr.happyshapes

class NumberGenerator(seed: Long) {
    companion object {
        private const val MULTIPLIER = 6364136223846793005L
    }

    private var state: Long

    init {
        this.state = seed * 2 + 1
        nextInt()
    }

    fun nextInt(): Int {
        var x = state
        val count = (x ushr 61).toInt()
        state = x * MULTIPLIER
        x = (x ushr 22) xor x
        return (x ushr (22 + count)).toInt()
    }

    fun nextBits(bits: Int): Int {
        return nextInt() ushr (32 - bits)
    }
}
package nextflow.co2footprint.utils

import java.math.RoundingMode

/**
 * A number associated with a unit.
 */
class Quantity extends BigDecimal {
    String prefix
    String unit
    String separator

    /**
     * Creator of a Quantity, combining the tracking and reporting of a number, associated with a unit.
     *
     * @param value The numerical value, saved in the quantity
     * @param scale The scale of the Quantity, defaults to ''
     * @param unit The unit of the quantity
     * @param separator The separator between value and scaled unit, defaults to ' '
     */
    Quantity(Number value, String prefix='', String unit='', String separator=' ') {
        super((value as BigDecimal).unscaledValue(), (value as BigDecimal).scale())
        this.prefix = prefix
        this.unit = unit
        this.separator = separator
    }

    /**
     * Round the value to a certain precision.
     *
     * @param precision, default: 2
     * @param roundingMode, How to round the number, default: RoundingMode.HALF_UP
     */
    Quantity round(Integer precision=2, RoundingMode roundingMode=RoundingMode.HALF_UP) {
        if (precision != null) {
            BigDecimal scaled = setScale(precision, roundingMode)
            return new Quantity(scaled, prefix, unit, separator)
        }
        return this
    }

    /**
     * Round the value to a certain precision.
     *
     * @param precision, default: 0
     */
    Quantity floor(Integer precision=0) {
        if (precision != null) {
            return new Quantity(setScale(precision, RoundingMode.FLOOR), prefix, unit, separator)
        }
        return this
    }

    /**
     * Get the readable representation of this quantity.
     * Example: '1 GB' for value = 1, scale = 'G', unit = 'B'
     *
     * @return String 'value scale+unit'
     */
    String getReadable() {
        // Remove trailing Zeros and convert to readable String
        String readable = this.stripTrailingZeros().toPlainString()

        // Add scale and unit with separator only if one of them is given
        String scaledUnit = (prefix ?: '') + (unit ?: '')
        if (scaledUnit) { readable += this.separator + scaledUnit }

        return readable
    }
}

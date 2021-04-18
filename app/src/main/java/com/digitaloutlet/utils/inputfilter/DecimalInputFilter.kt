package com.digitaloutlet.utils.inputfilter

import android.text.InputFilter
import android.text.Spanned
import com.digitaloutlet.utils.Constants
import com.digitaloutlet.utils.DOUtils
import java.util.regex.Pattern


class DecimalInputFilter : InputFilter {

    private val mPattern: Pattern
    private val digitsAfterZero: Int

    constructor(digitsBeforeZero: Int, digitsAfterZero: Int) {
        this.digitsAfterZero = digitsAfterZero
        mPattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?")
    }
    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        /*val matcher = mPattern.matcher(dest)
        if (!matcher.matches()) {
            return Constants.EMPTY
        }
        return null*/

        var returnVal: CharSequence? = null
        var hasDot = false;
        for (i in start until end) {
            dest?.let {
                if (it.contains(Constants.DOT)) {
                    hasDot = true
                }
            }
            if (DOUtils.isDigitAndDot(source?.get(i))) {
                if (!hasDot) {
                    returnVal = source
                } else if (hasDot && source != Constants.DOT) {
                    val destVal = dest ?: Constants.EMPTY
                    val indexOfDot = destVal.indexOf(Constants.DOT)
                    returnVal = if (dstart <= indexOfDot) { // This is before decimal
                        source
                    } else { // This is after decimal
                        if (destVal.substring(indexOfDot, destVal.length).length <= digitsAfterZero) {
                            source
                        } else {
                            Constants.EMPTY
                        }
                    }
                }
            }
        }
        return returnVal
    }
}
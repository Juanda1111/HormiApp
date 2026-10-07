package com.hormi.hormiapp.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/** "2000000" -> "2.000.000". Solo da formato; el valor guardado sigue siendo solo dígitos. */
fun formatThousands(digits: String): String {
    val n = digits.length
    val sb = StringBuilder()
    for (i in 0 until n) {
        if (i > 0 && (n - i) % 3 == 0) sb.append('.')
        sb.append(digits[i])
    }
    return sb.toString()
}

/** Muestra los dígitos con punto como separador de miles mientras el usuario escribe. */
class ThousandsVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val original = text.text
        val formatted = formatThousands(original)
        // Posición, dentro del texto formateado, de cada dígito original
        val digitIndex = IntArray(original.length)
        var d = 0
        formatted.forEachIndexed { index, c ->
            if (c != '.') digitIndex[d++] = index
        }
        val mapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int =
                if (offset >= original.length) formatted.length else digitIndex[offset]

            override fun transformedToOriginal(offset: Int): Int =
                formatted.take(offset).count { it != '.' }
        }
        return TransformedText(AnnotatedString(formatted), mapping)
    }
}

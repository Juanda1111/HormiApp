package com.hormi.hormiapp.util

import androidx.compose.ui.text.AnnotatedString
import org.junit.Assert.assertEquals
import org.junit.Test

class ThousandsTest {

    @Test
    fun formatThousands_agrupaDeATres() {
        assertEquals("", formatThousands(""))
        assertEquals("0", formatThousands("0"))
        assertEquals("999", formatThousands("999"))
        assertEquals("1.000", formatThousands("1000"))
        assertEquals("15.000", formatThousands("15000"))
        assertEquals("2.000.000", formatThousands("2000000"))
        assertEquals("123.456.789", formatThousands("123456789"))
    }

    @Test
    fun visualTransformation_mapeaPosicionesDelCursor() {
        val result = ThousandsVisualTransformation().filter(AnnotatedString("2000000"))
        assertEquals("2.000.000", result.text.text)
        // Cursor al final del texto original -> al final del formateado
        assertEquals(9, result.offsetMapping.originalToTransformed(7))
        // Cursor tras el primer dígito -> antes del primer punto
        assertEquals(0, result.offsetMapping.originalToTransformed(0))
        // Del texto formateado de vuelta al original: "2.0" son 2 dígitos
        assertEquals(2, result.offsetMapping.transformedToOriginal(3))
        assertEquals(7, result.offsetMapping.transformedToOriginal(9))
    }
}

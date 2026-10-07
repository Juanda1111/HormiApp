package com.hormi.hormiapp.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AccountIdTest {

    @Test
    fun normalize_ignoraMayusculasYEspaciosDeMas() {
        assertEquals("juan perez", AccountId.normalize("  Juan   Pérez".replace("é", "e")))
        assertEquals("sebas", AccountId.normalize("SEBAS"))
        assertEquals("sebas", AccountId.normalize(" sebas "))
    }

    @Test
    fun normalize_mismoNombreEscritoDistintoEsLaMismaCuenta() {
        assertEquals(AccountId.normalize("Luz María"), AccountId.normalize("  luz   maría "))
    }

    @Test
    fun normalize_nombresDistintosSonCuentasDistintas() {
        assertFalse(AccountId.normalize("Ana") == AccountId.normalize("Ana2"))
    }

    @Test
    fun demo_estaReservado() {
        assertTrue(AccountId.isReserved("Demo"))
        assertTrue(AccountId.isReserved("  demo "))
        assertTrue(AccountId.isReserved("DEMO"))
        assertFalse(AccountId.isReserved("Demos"))
        assertFalse(AccountId.isReserved("Juan"))
    }
}

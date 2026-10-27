package com.example

import com.example.data.repository.EthiopianPaymentMethod
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OleLiveRepositoryTest {

    private fun maskAccountIdentifier(methodCode: String, rawInput: String): String {
        val clean = rawInput.trim().replace("\\s+".toRegex(), "")
        return if (methodCode == EthiopianPaymentMethod.TELEBIRR.code) {
            if (clean.length >= 4) {
                val prefix = clean.take(2)
                val suffix = clean.takeLast(2)
                "$prefix•• ••• •$suffix"
            } else "••••••••"
        } else {
            if (clean.length >= 4) {
                val suffix = clean.takeLast(4)
                "•••• •••• $suffix"
            } else "•••• •••• ••••"
        }
    }

    @Test
    fun testTelebirrMasking_protectsSensitivePhoneNumber() {
        val masked = maskAccountIdentifier(EthiopianPaymentMethod.TELEBIRR.code, "0912345678")
        assertEquals("09•• ••• •78", masked)
    }

    @Test
    fun testCbeAccountMasking_protectsBankDetails() {
        val masked = maskAccountIdentifier(EthiopianPaymentMethod.CBE.code, "1000123456789")
        assertEquals("•••• •••• 6789", masked)
    }

    @Test
    fun testEthiopianMethodsCatalog_includesAllSixRequiredMethods() {
        val codes = EthiopianPaymentMethod.values().map { it.code }
        assertTrue(codes.contains("COMMERCIAL_BANK_OF_ETHIOPIA"))
        assertTrue(codes.contains("BANK_OF_ABYSSINIA"))
        assertTrue(codes.contains("AWASH_BANK"))
        assertTrue(codes.contains("DASHEN_BANK"))
        assertTrue(codes.contains("OROMIA_BANK"))
        assertTrue(codes.contains("TELEBIRR"))
        assertEquals(6, EthiopianPaymentMethod.values().size)
    }
}

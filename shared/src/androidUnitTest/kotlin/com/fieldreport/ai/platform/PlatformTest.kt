package com.fieldreport.ai.platform

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class PlatformTest {

    @Test
    fun testPlatformName() {
        val platform = getPlatformName()
        assertNotNull(platform)
        assertEquals("Android", platform)
    }
}

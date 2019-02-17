package com.simplewen.win0.wd

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented CL_Help_Login, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/Tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under CL_Help_Login.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("com.simplewen.win0.wd", appContext.packageName)
    }
}

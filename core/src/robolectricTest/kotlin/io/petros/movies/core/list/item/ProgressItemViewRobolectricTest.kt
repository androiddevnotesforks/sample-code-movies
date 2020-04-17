package io.petros.movies.core.list.item

import io.petros.movies.android_test.context.TestContextProvider
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import strikt.api.expect
import strikt.assertions.isNotNull

@RunWith(RobolectricTestRunner::class)
class ProgressItemViewRobolectricTest {

    @Test
    fun `when the progress item view is instantiated, then layout params is set`() {
        val testedClass = ProgressItemView(TestContextProvider.context())

        expect { that(testedClass.layoutParams).isNotNull() }
    }

}
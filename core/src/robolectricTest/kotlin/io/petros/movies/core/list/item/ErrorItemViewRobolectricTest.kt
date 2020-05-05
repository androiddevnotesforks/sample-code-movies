package io.petros.movies.core.list.item

import io.petros.movies.android_test.app.TestApp
import io.petros.movies.android_test.context.TestContextProvider
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import strikt.api.expect
import strikt.assertions.isNotNull

@RunWith(RobolectricTestRunner::class)
@Config(application = TestApp::class)
class ErrorItemViewRobolectricTest {

    @Test
    fun `when the error item view is instantiated, then layout params is set`() {
        val testedClass = ErrorItemView(TestContextProvider.context())

        expect { that(testedClass.layoutParams).isNotNull() }
    }

}

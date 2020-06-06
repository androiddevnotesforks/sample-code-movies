package io.petros.movies.app.robot.picker

import io.petros.movies.android_test.robot.actions.Actions
import io.petros.movies.android_test.robot.actions.ScopedActions
import io.petros.movies.android_test.robot.actions.UiActions
import io.petros.movies.android_test.robot.utils.idMatcher
import io.petros.movies.android_test.robot.utils.textMatcher
import io.petros.movies.lib.picker.R

class YearPickerRobot : ScopedActions(idMatcher(R.id.monthPicker)) {

    companion object {

        const val MOVIE_YEAR_PICKER_TITLE = "Select Movie Year"
        const val MOVIE_YEAR_PICKER_YEAR_2020 = "2020"

        private const val ACTION_OK = "OK"

    }

    fun inTitle(action: UiActions.() -> Actions) = UiActions(idMatcher(R.id.title)).action()

    fun inYear(action: UiActions.() -> Actions) = UiActions(idMatcher(R.id.year)).action()

    fun onActionOk(action: UiActions.() -> Actions) = UiActions(textMatcher(ACTION_OK)).action()

}

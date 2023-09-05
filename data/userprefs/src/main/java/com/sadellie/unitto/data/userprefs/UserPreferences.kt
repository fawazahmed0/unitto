/*
 * Unitto is a unit converter for Android
 * Copyright (c) 2022-2023 Elshan Agaev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.sadellie.unitto.data.userprefs

import androidx.compose.ui.graphics.Color
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.sadellie.unitto.core.base.OutputFormat
import com.sadellie.unitto.core.base.Separator
import com.sadellie.unitto.core.base.TopLevelDestinations
import com.sadellie.unitto.data.model.ALL_UNIT_GROUPS
import com.sadellie.unitto.data.model.AbstractUnit
import com.sadellie.unitto.data.model.UnitGroup
import com.sadellie.unitto.data.model.UnitsListSorting
import com.sadellie.unitto.data.units.MyUnitIDS
import io.github.sadellie.themmo.MonetMode
import io.github.sadellie.themmo.ThemingMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

/**
 * Represents user preferences that are user across the app
 *
 * @property themingMode [ThemingMode] from Themmo.
 * @property enableDynamicTheme Use dynamic color scheme
 * @property enableAmoledTheme Use amoled color scheme
 * @property customColor Generate custom color scheme from this color.
 * @property digitsPrecision Current [PRECISIONS]. Number of digits in fractional part
 * @property separator Current [Separator] that used to separate thousands
 * @property outputFormat Current [OutputFormat] that is applied to converted value (not input)
 * @property latestLeftSideUnit Latest [AbstractUnit] that was on the left side
 * @property latestRightSideUnit Latest [AbstractUnit] that was on the right side
 * @property shownUnitGroups [UnitGroup]s that user wants to see. Excludes other [UnitGroup]s,
 * @property enableVibrations When true will use haptic feedback in app.
 * @property enableToolsExperiment When true will enable experimental Tools screen.
 * @property radianMode AngleMode in mxParser. When true - Radian, when False - Degree.
 * @property unitConverterFavoritesOnly If true will show only units that are marked as favorite.
 * @property unitConverterFormatTime If true will format time to be more human readable.
 * @property unitConverterSorting Units list sorting mode.
 */
data class UserPreferences(
    val themingMode: ThemingMode = ThemingMode.AUTO,
    val enableDynamicTheme: Boolean = true,
    val enableAmoledTheme: Boolean = false,
    val customColor: Color = Color.Unspecified,
    val monetMode: MonetMode = MonetMode.TONAL_SPOT,
    val digitsPrecision: Int = 3,
    val separator: Int = Separator.SPACE,
    val outputFormat: Int = OutputFormat.PLAIN,
    val latestLeftSideUnit: String = MyUnitIDS.kilometer,
    val latestRightSideUnit: String = MyUnitIDS.mile,
    val shownUnitGroups: List<UnitGroup> = ALL_UNIT_GROUPS,
    val enableVibrations: Boolean = true,
    val enableToolsExperiment: Boolean = false,
    val startingScreen: String = TopLevelDestinations.Calculator.graph,
    val radianMode: Boolean = true,
    val unitConverterFavoritesOnly: Boolean = false,
    val unitConverterFormatTime: Boolean = false,
    val unitConverterSorting: UnitsListSorting = UnitsListSorting.USAGE,
    val middleZero: Boolean = false,
    val systemFont: Boolean = false,
)

data class UIPreferences(
    val themingMode: ThemingMode = ThemingMode.AUTO,
    val enableDynamicTheme: Boolean = true,
    val enableAmoledTheme: Boolean = false,
    val customColor: Color = Color.Unspecified,
    val monetMode: MonetMode = MonetMode.TONAL_SPOT,
    val startingScreen: String = TopLevelDestinations.Calculator.graph,
    val enableToolsExperiment: Boolean = false,
    val systemFont: Boolean = false,
)

data class MainPreferences(
    val digitsPrecision: Int = 3,
    val separator: Int = Separator.SPACE,
    val outputFormat: Int = OutputFormat.PLAIN,
    val latestLeftSideUnit: String = MyUnitIDS.kilometer,
    val latestRightSideUnit: String = MyUnitIDS.mile,
    val shownUnitGroups: List<UnitGroup> = ALL_UNIT_GROUPS,
    val enableVibrations: Boolean = true,
    val radianMode: Boolean = true,
    val unitConverterFavoritesOnly: Boolean = false,
    val unitConverterFormatTime: Boolean = false,
    val unitConverterSorting: UnitsListSorting = UnitsListSorting.USAGE,
    val middleZero: Boolean = false,
)

/**
 * Repository that works with DataStore
 */
class UserPreferencesRepository @Inject constructor(private val dataStore: DataStore<Preferences>) {
    /**
     * Keys for DataStore
     */
    private object PrefsKeys {
        val THEMING_MODE = stringPreferencesKey("THEMING_MODE_PREF_KEY")
        val ENABLE_DYNAMIC_THEME = booleanPreferencesKey("ENABLE_DYNAMIC_THEME_PREF_KEY")
        val ENABLE_AMOLED_THEME = booleanPreferencesKey("ENABLE_AMOLED_THEME_PREF_KEY")
        val CUSTOM_COLOR = longPreferencesKey("CUSTOM_COLOR_PREF_KEY")
        val MONET_MODE = stringPreferencesKey("MONET_MODE_PREF_KEY")
        val DIGITS_PRECISION = intPreferencesKey("DIGITS_PRECISION_PREF_KEY")
        val SEPARATOR = intPreferencesKey("SEPARATOR_PREF_KEY")
        val OUTPUT_FORMAT = intPreferencesKey("OUTPUT_FORMAT_PREF_KEY")
        val LATEST_LEFT_SIDE = stringPreferencesKey("LATEST_LEFT_SIDE_PREF_KEY")
        val LATEST_RIGHT_SIDE = stringPreferencesKey("LATEST_RIGHT_SIDE_PREF_KEY")
        val SHOWN_UNIT_GROUPS = stringPreferencesKey("SHOWN_UNIT_GROUPS_PREF_KEY")
        val ENABLE_VIBRATIONS = booleanPreferencesKey("ENABLE_VIBRATIONS_PREF_KEY")
        val ENABLE_TOOLS_EXPERIMENT = booleanPreferencesKey("ENABLE_TOOLS_EXPERIMENT_PREF_KEY")
        val STARTING_SCREEN = stringPreferencesKey("STARTING_SCREEN_PREF_KEY")
        val RADIAN_MODE = booleanPreferencesKey("RADIAN_MODE_PREF_KEY")
        val UNIT_CONVERTER_FAVORITES_ONLY = booleanPreferencesKey("UNIT_CONVERTER_FAVORITES_ONLY_PREF_KEY")
        val UNIT_CONVERTER_FORMAT_TIME = booleanPreferencesKey("UNIT_CONVERTER_FORMAT_TIME_PREF_KEY")
        val UNIT_CONVERTER_SORTING = stringPreferencesKey("UNIT_CONVERTER_SORTING_PREF_KEY")
        val MIDDLE_ZERO = booleanPreferencesKey("MIDDLE_ZERO_PREF_KEY")
        val SYSTEM_FONT = booleanPreferencesKey("SYSTEM_FONT_PREF_KEY")
    }

    val uiPreferencesFlow: Flow<UIPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val themingMode: ThemingMode = preferences[PrefsKeys.THEMING_MODE]?.let { ThemingMode.valueOf(it) }
                ?: ThemingMode.AUTO
            val enableDynamicTheme: Boolean = preferences[PrefsKeys.ENABLE_DYNAMIC_THEME] ?: true
            val enableAmoledTheme: Boolean = preferences[PrefsKeys.ENABLE_AMOLED_THEME] ?: false
            val customColor: Color = preferences[PrefsKeys.CUSTOM_COLOR]?.let { Color(it.toULong()) } ?: Color.Unspecified
            val monetMode: MonetMode = preferences[PrefsKeys.MONET_MODE]?.let { MonetMode.valueOf(it) }
                ?: MonetMode.TONAL_SPOT
            val startingScreen: String = preferences[PrefsKeys.STARTING_SCREEN] ?: TopLevelDestinations.Calculator.graph
            val enableToolsExperiment: Boolean = preferences[PrefsKeys.ENABLE_TOOLS_EXPERIMENT] ?: false
            val systemFont: Boolean = preferences[PrefsKeys.SYSTEM_FONT] ?: false

            UIPreferences(
                themingMode = themingMode,
                enableDynamicTheme = enableDynamicTheme,
                enableAmoledTheme = enableAmoledTheme,
                customColor = customColor,
                monetMode = monetMode,
                startingScreen = startingScreen,
                enableToolsExperiment = enableToolsExperiment,
                systemFont = systemFont
            )
        }

    val mainPreferencesFlow: Flow<MainPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val digitsPrecision: Int = preferences[PrefsKeys.DIGITS_PRECISION] ?: 3
            val separator: Int = preferences[PrefsKeys.SEPARATOR] ?: Separator.SPACE
            val outputFormat: Int = preferences[PrefsKeys.OUTPUT_FORMAT] ?: OutputFormat.PLAIN
            val latestLeftSideUnit: String = preferences[PrefsKeys.LATEST_LEFT_SIDE] ?: MyUnitIDS.kilometer
            val latestRightSideUnit: String = preferences[PrefsKeys.LATEST_RIGHT_SIDE] ?: MyUnitIDS.mile
            val shownUnitGroups: List<UnitGroup> =
                preferences[PrefsKeys.SHOWN_UNIT_GROUPS]?.let { list ->
                    // Everything is in hidden (nothing in shown)
                    list.ifEmpty { return@let listOf() }

                    try {
                        list.split(",").map { UnitGroup.valueOf(it) }
                    } catch (e: Exception) {
                        // Bad thing happened, return null so all units will be shown
                        null
                    }

                } ?: ALL_UNIT_GROUPS
            val enableVibrations: Boolean = preferences[PrefsKeys.ENABLE_VIBRATIONS] ?: true
            val radianMode: Boolean = preferences[PrefsKeys.RADIAN_MODE] ?: true
            val unitConverterFavoritesOnly: Boolean = preferences[PrefsKeys.UNIT_CONVERTER_FAVORITES_ONLY] ?: false
            val unitConverterFormatTime: Boolean = preferences[PrefsKeys.UNIT_CONVERTER_FORMAT_TIME] ?: false
            val unitConverterSorting: UnitsListSorting = preferences[PrefsKeys.UNIT_CONVERTER_SORTING]?.let { UnitsListSorting.valueOf(it) } ?: UnitsListSorting.USAGE
            val middleZero: Boolean = preferences[PrefsKeys.MIDDLE_ZERO] ?: false

            MainPreferences(
                digitsPrecision = digitsPrecision,
                separator = separator,
                outputFormat = outputFormat,
                latestLeftSideUnit = latestLeftSideUnit,
                latestRightSideUnit = latestRightSideUnit,
                shownUnitGroups = shownUnitGroups,
                enableVibrations = enableVibrations,
                radianMode = radianMode,
                unitConverterFavoritesOnly = unitConverterFavoritesOnly,
                unitConverterFormatTime = unitConverterFormatTime,
                unitConverterSorting = unitConverterSorting,
                middleZero = middleZero,
            )
        }

    val allPreferencesFlow = combine(
        mainPreferencesFlow, uiPreferencesFlow
    ) { main, ui ->
        return@combine UserPreferences(
            themingMode = ui.themingMode,
            enableDynamicTheme = ui.enableDynamicTheme,
            enableAmoledTheme = ui.enableAmoledTheme,
            customColor = ui.customColor,
            monetMode = ui.monetMode,
            digitsPrecision = main.digitsPrecision,
            separator = main.separator,
            outputFormat = main.outputFormat,
            latestLeftSideUnit = main.latestLeftSideUnit,
            latestRightSideUnit = main.latestRightSideUnit,
            shownUnitGroups = main.shownUnitGroups,
            enableVibrations = main.enableVibrations,
            enableToolsExperiment = ui.enableToolsExperiment,
            startingScreen = ui.startingScreen,
            radianMode = main.radianMode,
            unitConverterFavoritesOnly = main.unitConverterFavoritesOnly,
            unitConverterFormatTime = main.unitConverterFormatTime,
            unitConverterSorting = main.unitConverterSorting,
            middleZero = main.middleZero,
            systemFont = ui.systemFont,
        )
    }

    /**
     * Update precision preference in DataStore
     *
     * @param precision One of the [PRECISIONS] to change to
     */
    suspend fun updateDigitsPrecision(precision: Int) {
        dataStore.edit { preferences ->
            preferences[PrefsKeys.DIGITS_PRECISION] = precision
        }
    }

    /**
     * Update separator preference in DataStore
     *
     * @param separator One of the [Separator] to change to
     */
    suspend fun updateSeparator(separator: Int) {
        dataStore.edit { preferences ->
            preferences[PrefsKeys.SEPARATOR] = separator
        }
    }

    /**
     * Update outputFormat preference in DataStore
     *
     * @param outputFormat One of the [OutputFormat] to change to
     */
    suspend fun updateOutputFormat(outputFormat: Int) {
        dataStore.edit { preferences ->
            preferences[PrefsKeys.OUTPUT_FORMAT] = outputFormat
        }
    }

    /**
     * Update latest used pair of [AbstractUnit] in DataStore. Need it so when user restarts the app,
     * this pair will be already set.
     *
     * @param leftSideUnit [AbstractUnit] on the left
     * @param rightSideUnit [AbstractUnit] on the right
     */
    suspend fun updateLatestPairOfUnits(leftSideUnit: AbstractUnit, rightSideUnit: AbstractUnit) {
        dataStore.edit { preferences ->
            preferences[PrefsKeys.LATEST_LEFT_SIDE] = leftSideUnit.unitId
            preferences[PrefsKeys.LATEST_RIGHT_SIDE] = rightSideUnit.unitId
        }
    }

    /**
     * Update [ThemingMode]. Saves value as a string.
     *
     * @param themingMode [ThemingMode] to save.
     */
    suspend fun updateThemingMode(themingMode: ThemingMode) {
        dataStore.edit { preferences ->
            preferences[PrefsKeys.THEMING_MODE] = themingMode.name
        }
    }

    /**
     * Update preference on whether or not generate color scheme from device wallpaper.
     *
     * @param enabled True if user wants to enable this feature.
     */
    suspend fun updateDynamicTheme(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PrefsKeys.ENABLE_DYNAMIC_THEME] = enabled
        }
    }

    /**
     * Update preference on whether or not use true black colors.
     *
     * @param enabled True if user wants to enable this feature.
     */
    suspend fun updateAmoledTheme(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PrefsKeys.ENABLE_AMOLED_THEME] = enabled
        }
    }

    /**
     * Update preference on custom color scheme.
     *
     * @param color New custom color value.
     */
    suspend fun updateCustomColor(color: Color) {
        dataStore.edit { preferences ->
            preferences[PrefsKeys.CUSTOM_COLOR] = color.value.toLong()
        }
    }

    /**
     * Update [MonetMode]. Saves value as a string.
     *
     * @param monetMode [MonetMode] to save.
     */
    suspend fun updateMonetMode(monetMode: MonetMode) {
        dataStore.edit { preferences ->
            preferences[PrefsKeys.MONET_MODE] = monetMode.name
        }
    }

    /**
     * Update preference on starting screen route.
     *
     * @param startingScreen Route from [TopLevelDestinations].
     */
    suspend fun updateStartingScreen(startingScreen: String) {
        dataStore.edit { preferences ->
            preferences[PrefsKeys.STARTING_SCREEN] = startingScreen
        }
    }

    suspend fun updateShownUnitGroups(shownUnitGroups: List<UnitGroup>) {
        dataStore.edit { preferences ->
            preferences[PrefsKeys.SHOWN_UNIT_GROUPS] = shownUnitGroups.joinToString(",")
        }
    }

    /**
     * Update preference on whether or not use haptic feedback.
     *
     * @param enabled True if user wants to enable this feature.
     */
    suspend fun updateVibrations(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PrefsKeys.ENABLE_VIBRATIONS] = enabled
        }
    }

    /**
     * Update preference on where zero should be.
     *
     * @param enabled True if user wants zero button to be in the middle.
     */
    suspend fun updateMiddleZero(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PrefsKeys.MIDDLE_ZERO] = enabled
        }
    }

    /**
     * Update preference on whether or not show tools screen.
     *
     * @param enabled True if user wants to enable this feature.
     */
    suspend fun updateToolsExperiment(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PrefsKeys.ENABLE_TOOLS_EXPERIMENT] = enabled
        }
    }

    /**
     * Update angle mode for calculator.
     *
     * @param radianMode When true - Radian, when False - Degree.
     */
    suspend fun updateRadianMode(radianMode: Boolean) {
        dataStore.edit { preferences ->
            preferences[PrefsKeys.RADIAN_MODE] = radianMode
        }
    }

    /**
     * Update units list favorite filter state.
     *
     * @param enabled When true will show only favorite units.
     */
    suspend fun updateUnitConverterFavoritesOnly(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PrefsKeys.UNIT_CONVERTER_FAVORITES_ONLY] = enabled
        }
    }

    /**
     * Update [UserPreferences.unitConverterFormatTime].
     *
     * @see UserPreferences.unitConverterFormatTime
     */
    suspend fun updateUnitConverterFormatTime(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PrefsKeys.UNIT_CONVERTER_FORMAT_TIME] = enabled
        }
    }

    /**
     * Update [UserPreferences.unitConverterSorting].
     *
     * @see UserPreferences.unitConverterSorting
     */
    suspend fun updateUnitConverterSorting(sorting: UnitsListSorting) {
        dataStore.edit { preferences ->
            preferences[PrefsKeys.UNIT_CONVERTER_SORTING] = sorting.name
        }
    }

    /**
     * Update system font preference.
     *
     * @param enabled When true will use system font.
     */
    suspend fun updateSystemFont(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PrefsKeys.SYSTEM_FONT] = enabled
        }
    }
}

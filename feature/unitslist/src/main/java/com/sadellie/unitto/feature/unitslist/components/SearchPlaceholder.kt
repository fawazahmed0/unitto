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

package com.sadellie.unitto.feature.unitslist.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sadellie.unitto.core.base.R

/**
 * Placeholder that can be seen when there are no units found
 *
 * @param navigateToSettingsAction Action to perform when clicking open settings button.
 */
@Composable
internal fun SearchPlaceholder(navigateToSettingsAction: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Big icon in the middle
        Icon(
            Icons.Default.SearchOff,
            contentDescription = stringResource(R.string.empty_search_result_description),
            modifier = Modifier.size(48.dp)
        )
        // Primary text
        Text(
            text = stringResource(R.string.search_placeholder),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )
        // Secondary text with tips
        Text(
            text = stringResource(R.string.search_placeholder_secondary),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall
        )
        // Open settings button
        ElevatedButton(onClick = navigateToSettingsAction) {
            Text(text = stringResource(R.string.search_placeholder_button_label))
        }
    }
}

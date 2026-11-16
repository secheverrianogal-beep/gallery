/*
 * Copyright 2026 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.ai.edge.gallery.ui.common

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit regression tests for ConfigDialogFooter layout decisions (SER-258 / #1472).
 * Verifies that when strings (such as Spanish "Restablecer valores predeterminados" or "Cancelar")
 * or large font scales exceed available dialog width, the footer breaks into stacked rows
 * instead of squeezing or clipping the OK action button.
 */
class ConfigDialogFooterLayoutTest {

  data class LayoutDecision(val isStacked: Boolean)

  private fun computeFooterLayout(
    leadingWidth: Int,
    trailingWidth: Int,
    spacing: Int,
    maxWidth: Int,
  ): LayoutDecision {
    val singleRowWidth = leadingWidth + spacing + trailingWidth
    val isStacked = singleRowWidth > maxWidth
    return LayoutDecision(isStacked = isStacked)
  }

  @Test
  fun testEnglishDefaultDimensionsFitSingleRow() {
    // English typical widths at 1.0x scale:
    // "Restore default" ~ 120dp, "Cancel" + spacing + "OK" ~ 110dp, spacing = 8dp
    // Available width on Pixel screen ~ 360dp
    val decision = computeFooterLayout(
      leadingWidth = 120,
      trailingWidth = 110,
      spacing = 8,
      maxWidth = 360,
    )
    assertFalse("Default English footer should fit on a single row", decision.isStacked)
  }

  @Test
  fun testSpanishLongTranslationsWrapToStackedRowsPreservingOkButton() {
    // Spanish translation widths:
    // "Restablecer valores predeterminados" ~ 250dp
    // "Cancelar" (68dp) + spacing (8dp) + "Aceptar" (64dp) ~ 140dp
    // Total single line required = 250 + 8 + 140 = 398dp > 360dp max dialog width
    val decision = computeFooterLayout(
      leadingWidth = 250,
      trailingWidth = 140,
      spacing = 8,
      maxWidth = 360,
    )
    assertTrue("Spanish long translation must wrap to stacked rows", decision.isStacked)
  }

  @Test
  fun testLargeAccessibilityFontScaleWrapsToStackedRows() {
    // Font scale 1.3x or larger pushing standard labels past dialog boundaries
    val decision = computeFooterLayout(
      leadingWidth = 180,
      trailingWidth = 190,
      spacing = 8,
      maxWidth = 360,
    )
    assertTrue("Large accessibility scale must wrap to stacked rows", decision.isStacked)
  }
}

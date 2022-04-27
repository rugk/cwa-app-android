package de.rki.coronawarnapp.util.ui

import android.annotation.SuppressLint
import android.view.View
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.internal.ToolbarUtils

@SuppressLint("RestrictedApi")
fun MaterialToolbar.generateChildrenIds() {
    ToolbarUtils.getNavigationIconButton(this)?.id = View.generateViewId()
    ToolbarUtils.getTitleTextView(this)?.id = View.generateViewId()
    ToolbarUtils.getSubtitleTextView(this)?.id = View.generateViewId()
}

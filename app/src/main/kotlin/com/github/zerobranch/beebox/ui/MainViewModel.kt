package com.github.zerobranch.beebox.ui

import com.github.zerobranch.beebox.commons_app.base.BaseMainViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
) : BaseMainViewModel() {
    override val screenName: String = "Main"
}

package com.ylabz.basepro.applications.photodo.core.ui

// By making this a public data class in its own file, it becomes accessible
// to any module that depends on the main ':applications:photodo' module.
//
// NOTE: For a stricter architecture, this would live in a `:core:ui` module that
// both `:applications:photodo` and `:features:home` would implement.
// For this project, placing it here is the simplest fix.
data class FabState(val text: String, val onClick: () -> Unit)

// private data class FabState(val text: String, val onClick: () -> Unit)
package net.accelf.itc_lms_unofficial.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun <T> useState(initial: T): MutableState<T> = remember { mutableStateOf(initial) }

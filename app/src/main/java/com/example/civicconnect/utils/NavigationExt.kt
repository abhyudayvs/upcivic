package com.example.civicconnect.utils

import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions

fun NavController.safeNavigate(direction: NavDirections) {
    currentDestination?.getAction(direction.actionId)?.let {
        navigate(direction)
    }
}

fun NavController.safeNavigate(
    direction: NavDirections,
    navOptions: NavOptions
) {
    currentDestination?.getAction(direction.actionId)?.let {
        navigate(direction, navOptions)
    }
} 
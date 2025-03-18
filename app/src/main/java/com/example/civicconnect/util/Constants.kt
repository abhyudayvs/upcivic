package com.example.civicconnect.util

object Constants {
    // Firebase Collections
    const val COLLECTION_USERS = "users"
    const val COLLECTION_ISSUES = "issues"
    const val COLLECTION_POLLS = "polls"
    const val COLLECTION_REPRESENTATIVES = "representatives"

    // Firebase Fields
    const val FIELD_CREATED_AT = "createdAt"
    const val FIELD_UPDATED_AT = "updatedAt"
    const val FIELD_USER_ID = "userId"
    const val FIELD_STATUS = "status"
    const val FIELD_CATEGORY = "category"
    const val FIELD_SUPPORT_COUNT = "supportCount"
    const val FIELD_SUPPORTERS = "supporters"
    const val FIELD_END_DATE = "endDate"
    const val FIELD_HAS_VOTED = "hasVoted"
    const val FIELD_VOTES = "votes"

    // Issue Categories
    const val CATEGORY_INFRASTRUCTURE = "Infrastructure"
    const val CATEGORY_ENVIRONMENT = "Environment"
    const val CATEGORY_EDUCATION = "Education"
    const val CATEGORY_HEALTH = "Health"
    const val CATEGORY_SAFETY = "Safety"
    const val CATEGORY_OTHER = "Other"

    // Issue Status
    const val STATUS_OPEN = "Open"
    const val STATUS_IN_PROGRESS = "In Progress"
    const val STATUS_RESOLVED = "Resolved"
    const val STATUS_CLOSED = "Closed"

    // Shared Preferences
    const val PREFS_NAME = "CivicConnectPrefs"
    const val KEY_USER_ID = "userId"
    const val KEY_USER_NAME = "userName"
    const val KEY_USER_EMAIL = "userEmail"

    // Request Codes
    const val RC_SIGN_IN = 123
    const val RC_IMAGE_PICK = 456
} 
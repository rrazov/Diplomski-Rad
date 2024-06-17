package com.example.myfittracker.permissions

class PermissionHandler {

    val visiblePermissionDialogQueue = mutableListOf<String>()

    fun dismissDialog() {
        visiblePermissionDialogQueue.removeLast()
    }

    fun onPermissionResult(
        permission: String,
        isGranted: Boolean)
    {
        if (!isGranted && !visiblePermissionDialogQueue.contains(permission)) {
            visiblePermissionDialogQueue.add(0, permission)
        }
    }
}
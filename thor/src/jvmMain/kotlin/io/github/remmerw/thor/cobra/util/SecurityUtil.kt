package io.github.remmerw.thor.cobra.util

import java.security.AccessController
import java.security.PrivilegedAction

object SecurityUtil {
    fun <T> doPrivileged(action: PrivilegedAction<T?>): T? {
        val sm = System.getSecurityManager()
        if (sm == null) {
            return action.run()
        } else {
            return AccessController.doPrivileged<T?>(action)
        }
    }
}

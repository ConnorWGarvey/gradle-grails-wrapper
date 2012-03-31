/* RC_LEGAL_LEGEND */

package com.connorgarvey.gradlegrails

import java.io.File

/**
 * Works with file paths
 * @author Connor Garvey
 * @since Mar 17, 2012
 */
class Path {
    /**
     * Joins parts of a path on {@link File#separator}
     * @param items the parts of the path to be joined
     * @return a new string containing the joined path
     */
    static String join(String... items) {
        StringBuilder result = new StringBuilder()
        Iterator<String> iter = items.iterator()
        String last = null;
        if (iter.hasNext()) {
            last = iter.next()
            result.append(last)
        }
        while (iter.hasNext()) {
            String next = iter.next()
            if (!last.endsWith(File.separator) && !next.startsWith(File.separator)) {
                result.append(File.separator)
            }
            else if (last.endsWith(File.separator) && next.startsWith(File.separator)) {
                next = next.substring(1)
            }
            result.append(next)
            last = next
        }
        result.toString()
    }
}

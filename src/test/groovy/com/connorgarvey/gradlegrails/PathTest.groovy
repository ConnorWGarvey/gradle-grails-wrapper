/* RC_LEGAL_LEGEND */

package com.connorgarvey.gradlegrails

import spock.lang.Specification

/**
 * Tests {@link Path}
 * @author Connor Garvey
 * @since Mar 19, 2012
 */
class PathTest extends Specification {
    def 'path is joined correctly'() {
        when:
        def path = Path.join(args as String[])
        then:
        path == expected
        where:
        args                                             | expected
        ["home${File.separator}foo"]                     | "home${File.separator}foo"
        ["home","${File.separator}foo"]                  | "home${File.separator}foo"
        ["home${File.separator}","${File.separator}foo"] | "home${File.separator}foo"
        ["home${File.separator}","${File.separator}"]    | "home${File.separator}"
        ["home","${File.separator}"]                     | "home${File.separator}"
        ["home"]                                         | "home"
        ["home","foo"]                                   | "home${File.separator}foo"
    }
}

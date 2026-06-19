package example

import grails.testing.gorm.DataTest
import spock.lang.Specification

class RoleSpec extends Specification implements DataTest {

    Class<?>[] getDomainClassesToMock() {
        [Role] as Class[]
    }

    void 'authority must start with ROLE_ prefix in practice'() {
        expect:
        new Role(authority: 'ROLE_USER').validate()
    }

    void 'authority is required'() {
        when:
        def role = new Role(authority: '')

        then:
        !role.validate()
        role.errors['authority'].code in ['blank', 'nullable']
    }
}

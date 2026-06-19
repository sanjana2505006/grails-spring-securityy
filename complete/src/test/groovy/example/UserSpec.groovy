package example

import grails.testing.gorm.DataTest
import spock.lang.Specification

class UserSpec extends Specification implements DataTest {

    Class<?>[] getDomainClassesToMock() {
        [User, Role, UserRole] as Class[]
    }

    void 'username is required'() {
        when:
        def user = new User(username: '', password: 'secret')

        then:
        !user.validate()
        user.errors['username'].code in ['blank', 'nullable']
    }

    void 'username must be unique'() {
        given:
        new User(username: 'alice', password: 'secret').save(flush: true)

        expect:
        !new User(username: 'alice', password: 'other').validate()
    }
}

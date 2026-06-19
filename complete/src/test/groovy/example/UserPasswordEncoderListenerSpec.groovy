package example

import org.grails.datastore.mapping.core.Datastore
import org.grails.datastore.mapping.engine.EntityAccess
import org.grails.datastore.mapping.engine.event.EventType
import org.grails.datastore.mapping.engine.event.PreInsertEvent
import org.grails.datastore.mapping.engine.event.PreUpdateEvent
import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Specification

class UserPasswordEncoderListenerSpec extends Specification {

    PasswordEncoder passwordEncoder = Mock(PasswordEncoder)
    Datastore datastore = Mock(Datastore)
    UserPasswordEncoderListener listener

    def setup() {
        listener = new UserPasswordEncoderListener(datastore)
        listener.passwordEncoder = passwordEncoder
    }

    void 'encodes plain password on insert'() {
        given:
        def user = new User(username: 'alice', password: 'secret')
        def entityAccess = Mock(EntityAccess)
        def event = Mock(PreInsertEvent) {
            getEntityObject() >> user
            getEventType() >> EventType.PreInsert
            getEntityAccess() >> entityAccess
        }

        when:
        listener.onPersistenceEvent(event)

        then:
        1 * passwordEncoder.encode('secret') >> '$2a$10$encoded'
        1 * entityAccess.setProperty('password', '$2a$10$encoded')
    }

    void 'does not re-encode bcrypt password on insert'() {
        given:
        def existingHash = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'
        def user = new User(username: 'alice', password: existingHash)
        def entityAccess = Mock(EntityAccess)
        def event = Mock(PreInsertEvent) {
            getEntityObject() >> user
            getEventType() >> EventType.PreInsert
            getEntityAccess() >> entityAccess
        }

        when:
        listener.onPersistenceEvent(event)

        then:
        0 * passwordEncoder.encode(_)
        1 * entityAccess.setProperty('password', existingHash)
    }

    void 'does not re-encode bcrypt 2b password on insert'() {
        given:
        def existingHash = '$2b$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'
        def user = new User(username: 'bob', password: existingHash)
        def entityAccess = Mock(EntityAccess)
        def event = Mock(PreInsertEvent) {
            getEntityObject() >> user
            getEventType() >> EventType.PreInsert
            getEntityAccess() >> entityAccess
        }

        when:
        listener.onPersistenceEvent(event)

        then:
        0 * passwordEncoder.encode(_)
        1 * entityAccess.setProperty('password', existingHash)
    }

    void 'skips update when password field is not dirty'() {
        given:
        def user = Mock(User) {
            getPassword() >> 'secret'
            isDirty('password') >> false
        }
        def event = Mock(PreUpdateEvent) {
            getEntityObject() >> user
            getEventType() >> EventType.PreUpdate
        }

        when:
        listener.onPersistenceEvent(event)

        then:
        0 * passwordEncoder.encode(_)
    }
}

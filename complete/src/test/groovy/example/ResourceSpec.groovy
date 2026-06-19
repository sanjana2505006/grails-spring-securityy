package example

import grails.testing.gorm.DataTest
import spock.lang.Specification

class ResourceSpec extends Specification implements DataTest {

    Class<?>[] getDomainClassesToMock() {
        [Resource] as Class[]
    }

    void 'name is required'() {
        when:
        def resource = new Resource(name: '', description: 'Notes')

        then:
        !resource.validate()
        resource.errors['name'].code in ['blank', 'nullable']
    }

    void 'valid resource passes constraints'() {
        expect:
        new Resource(name: 'Public API Guide', description: 'Open read access').validate()
    }
}

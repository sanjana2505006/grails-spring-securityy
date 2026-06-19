package example

class BootStrap {

    def init = { servletContext ->
        environments {
            development {
                Resource.withTransaction {
                    if (Resource.count() == 0) {
                        new Resource(name: 'Public Overview', description: 'Readable without authentication').save(failOnError: true)
                        new Resource(name: 'Admin Console', description: 'Restricted to administrators').save(failOnError: true)
                    }
                }
            }
        }
    }

    def destroy = {
    }
}

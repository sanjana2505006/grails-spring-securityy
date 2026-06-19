package example

import grails.gorm.transactions.Transactional

class ResourceController {

    static responseFormats = ['json']
    static allowedMethods = [index: 'GET', show: 'GET', save: 'POST', update: 'PUT', delete: 'DELETE']

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Resource.list(params), model: [resourceCount: Resource.count() as Long]
    }

    def show(Long id) {
        respond Resource.get(id)
    }

    @Transactional
    def save() {
        def resource = new Resource(request.JSON as Map)
        if (!resource.validate()) {
            respond resource.errors, status: 422
            return
        }
        resource.save(failOnError: true, flush: true)
        respond resource, status: 201
    }

    @Transactional
    def update(Long id) {
        def resource = Resource.get(id)
        if (!resource) {
            render status: 404
            return
        }
        resource.properties = request.JSON
        if (!resource.validate()) {
            respond resource.errors, status: 422
            return
        }
        resource.save(failOnError: true, flush: true)
        respond resource
    }

    @Transactional
    def delete(Long id) {
        def resource = Resource.get(id)
        if (!resource) {
            render status: 404
            return
        }
        resource.delete(flush: true)
        render status: 204
    }
}

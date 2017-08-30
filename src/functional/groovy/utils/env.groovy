package utils

this.metaClass.mixin(cucumber.api.groovy.Hooks)
this.metaClass.mixin(cucumber.api.groovy.EN)

World() {
    new SharedWorld()
}

class SharedWorld {
}

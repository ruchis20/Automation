package utils

import static Configuration.*

this.metaClass.mixin(cucumber.api.groovy.Hooks)
this.metaClass.mixin(cucumber.api.groovy.EN)

World() {
    new SharedWorld()
}

class SharedWorld {
    def dir = getDestinationFolder()
}

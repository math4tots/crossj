package cjx


import cjx.JSObject


## Trait for classes that wrap JSObjects
trait JSWrapper {
    ## Gets the javascript class that this wrapper represents
    def getClass(): JSObject

    def obj(self: Self): JSObject

    ## Casts a JSObject to Self
    def from(obj: JSObject): Self {
        return obj.cast()
    }
}

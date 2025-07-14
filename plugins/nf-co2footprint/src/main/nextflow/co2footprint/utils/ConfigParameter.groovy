package nextflow.co2footprint.utils

import groovy.util.logging.Slf4j

@Slf4j
class ConfigParameter<T> {
    private final Set<Class> types
    private final String description
    private final Closure<T> defaultFunction
    private final boolean configurable
    private final boolean readable
    private final boolean writable
    private def value = null
    String name = null

    ConfigParameter(
        Set<Class> types=[],
        String description='',
        def defaultFunction=null,
        boolean configurable=true,
        boolean readable=true,
        boolean writable=true,
        String name = null
    ) {
        this.types = types
        this.description = description
        this.defaultFunction = defaultFunction instanceof Closure<T> ? defaultFunction : { -> defaultFunction }
        this.configurable = configurable
        this.readable = readable
        this.writable = writable
        this.name = name
    }

    String getDescription() { description }

    /**
     * Sets the value to the default value
     */
    void setToDefault() { this.value = this.defaultFunction() }

    /**
     * Check a given class against a list of allowed classes
     * @param queryType
     * @return
     */
    boolean checkType(def value) {
        if (types) {
            types.each { Class type ->
                if(value in type) {
                    return true
                }
            }
            return false
        }
        else {
            return true
        }
    }

    /**
     * Return the value of the parameter
     *
     * @return value
     */
    T get() {
        return this.value
    }

    /**
     * Return the value of the parameter
     *
     * @return value
     */
    T getEvaluated() {
        return (this.value instanceof Closure) ? (this.value as Closure<T>)() as T : this.value as T
    }

    void configure(def value) {
        if (configurable) {
            if(checkType(value)){
                this.value = value
            } else {
                String message = "Class `${value.getClass()}` is invalid for `${name}`. Valid types: ${types}."
                log.error(message)
                throw new ClassFormatError(message)
            }
        }
        else {
            String message = "This parameter is not configurable."
            log.error(message)
            throw new IllegalAccessError(message)
        }
    }

    void set(def value) {
        if (writable) {
            if(checkType(value)){
                this.value = value
            } else {
                String message = "Class `${value.getClass()}` is invalid for `${name}`. Valid types: ${types}."
                log.error(message)
                throw new ClassFormatError(message)
            }
        }
        else {
            String message = "This parameter is not writeable."
            log.error(message)
            throw new IllegalAccessError(message)
        }
    }

    String toString() {
        String accessString = [
                configurable ? 'configure' : null,
                readable ? 'read' : null,
                writable ? 'write' : null,
        ].join(',')

        return "${name}: ${accessString} -> ${value} ${types}\n" +
            "Description: ${description}"
    }
}

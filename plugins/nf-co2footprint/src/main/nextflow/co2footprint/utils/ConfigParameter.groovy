package nextflow.co2footprint.utils

import groovy.util.logging.Slf4j

@Slf4j
class ConfigParameter<T> {
    private final Set<Class> types
    private final String description
    private def value
    private final boolean configurable
    private final boolean readable
    private final boolean writable

    ConfigParameter(
        Set<Class> types=[],
        String description='',
        def value=null,
        boolean configurable=true,
        boolean readable=true,
        boolean writable=true
    ) {
        this.types = types
        this.description = description
        this.value = value
        this.configurable = configurable
        this.readable = readable
        this.writable = writable
    }

    String getDescription() { description }

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
                String message = "Class `${value.getClass()}` is invalid. Valid types: ${types}."
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
                String message = "Class `${value.getClass()}` is invalid. Valid types: ${types}."
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
                readable ? 'read' : null,
                writable ? 'write' : null
        ].join(',')

        return "${accessString} -> ${value} ${types}\n" +
            "Description: ${description}"
    }
}

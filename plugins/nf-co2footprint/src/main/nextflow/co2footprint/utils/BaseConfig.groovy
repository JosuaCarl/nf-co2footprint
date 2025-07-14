package nextflow.co2footprint.utils

import groovy.util.logging.Slf4j

/**
 * A class that regulated the basic properties of a config.
 * It contains a set of parameters, identified by a name, with fine-grained access configuration options.
 */
@Slf4j
class BaseConfig {
    private HashMap<String, ConfigParameter> parameters
    private String configName = 'baseConfig'

    BaseConfig(String configName, Map<String, ConfigParameter> parameters=[:]) {
        this.configName = configName
        this.parameters = parameters.each { String name, ConfigParameter parameter ->
            parameter.name = name
        } as HashMap<String, ConfigParameter>
    }

    /**
     * Enables adding parameters after initialization
     *
     * @param parameters
     */
    protected void addParameters(Map<String, ConfigParameter> parameters) {
        parameters = parameters.each { String name, ConfigParameter parameter ->
            parameter.name = name
        } as HashMap<String, ConfigParameter>
        this.parameters.putAll(parameters)
    }

    protected void setParametersToDefault(){
        this.parameters.values().each { ConfigParameter parameter -> parameter.setToDefault() }
    }


    /**
     * Find the correct parameter
     *
     * @param name
     * @return
     */
    ConfigParameter getParameter(String name) {
        ConfigParameter parameter = parameters.get(name)
        if (!parameter) {
            log.debug("Parameter `${name}` not in config `${configName}`.")
        }
        return parameter
    }

    /**
     * Get the value of a parameter
     * @param name
     * @return
     */
    def get(String name) {
        return getParameter(name).get()
    }

    /**
     * Get the value of a parameter
     *
     * @param name
     * @return
     */
    def getEvaluated(String name) {
        return getParameter(name).getEvaluated()
    }

    /**
     * Configures a parameter to an initial value
     *
     * @param name Name of the parameter to be set
     * @param value Value of the parameter to be set to
     */
    void configure(String name, def value) {
        getParameter(name).configure(value)
    }

    /**
     * Sets a parameter to a value
     *
     * @param name Name of the parameter to be set
     * @param value Value of the parameter to be set to
     */
    void set(String name, def value) {
        getParameter(name).set(value)
    }

    /**
     * Sets a parameter to a value when it is null before
     *
     * @param name Name of the parameter to be set
     * @param value Value of the parameter to be set to
     */
    void setEmpty(String name, def value) {
        ConfigParameter parameter = getParameter(name)
        if (parameter == null) {
            parameter.set(value)
        }
    }
}

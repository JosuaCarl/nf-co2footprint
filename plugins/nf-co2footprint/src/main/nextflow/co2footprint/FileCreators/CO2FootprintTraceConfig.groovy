package nextflow.co2footprint.FileCreators

import nextflow.co2footprint.utils.ConfigParameter

import java.nio.file.Path

class CO2FootprintTraceConfig extends CO2FootprintFileConfig {
    CO2FootprintTraceConfig(Map<String, ?> traceConfigMap) {
        super(traceConfigMap)

        set('name', "${name}_trace")
        set('ending', 'txt')
        setEmpty('file', Path.of(outDirectory, "${name}_${suffix}.${ending}").toString())
    }
}

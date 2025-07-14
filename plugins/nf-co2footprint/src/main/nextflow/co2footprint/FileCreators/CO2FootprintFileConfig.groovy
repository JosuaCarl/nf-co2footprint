package nextflow.co2footprint.FileCreators

import groovy.util.logging.Slf4j
import nextflow.co2footprint.utils.BaseConfig
import nextflow.co2footprint.utils.ConfigParameter
import nextflow.trace.TraceHelper

import java.nio.file.Path

/**
 * Configuration class for files in the CO2Footprint plugin.
 *
 * It extracts values from a configuration map and sets up all relevant parameters,
 * such as output file names, carbon intensity, PUE, memory and CPU power draw, and machine type.
 * Users can customize these values in the Nextflow config file under the `co2footprint` block.
 *
 * Example usage in config:
 * trace {
 *     enabled = true
 *     file = "pipeline_info/trace.txt"
 * }
 */
@Slf4j
class CO2FootprintFileConfig extends BaseConfig{
    private final HashMap<String, ConfigParameter> immutableParameters = [
            outDirectory: new ConfigParameter<String>(
                    Set.of(String),
                    'Directory for the output files.',
                    'pipeline_info',
                    false, true, false
            ),
            name: new ConfigParameter<String>(
                    Set.of(String),
                    'Name of the file.',
                    'co2footprint',
                    false, true, true
            ),
            suffix: new ConfigParameter<String>(
                    Set.of(String),
                    'Suffix / timestamp of the file.',
                    "_${TraceHelper.launchTimestampFmt()}",
                    false, true, true
            ),
            ending: new ConfigParameter<String>(
                    Set.of(String),
                    'File ending.',
                    '.txt',
                    false, true, true
            ),
    ]

    private final HashMap<String, ConfigParameter> parameters = [
            enabled: new ConfigParameter<boolean>(
                    Set.of(boolean),
                    'Should the output be enabled?',
                    true,
            ),
            file: new ConfigParameter<String>(
                    Set.of(String),
                    'Path to the file.',
                    { -> Path.of(outDirectory, "${name}${suffix}${ending}").toString() }
            ),
    ]

    CO2FootprintFileConfig(Map<String, ?> fileConfigMap) {
        super('CO2FootprintTraceConfig')
        addParameters( immutableParameters + parameters )
        setParametersToDefault()

        fileConfigMap.each { name, value -> configure(name, value) }
    }

    // Get methods
    String getOutDirectory() { get('outDirectory') }
    String getName() { get('name') }
    String getSuffix() { get('suffix') }
    String getEnding() {  get('ending')}
    boolean getEnabled() { get('enabled') }
    String getFile() { get('file') }
    String getPath() { Path.of(file) }
}

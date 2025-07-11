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
    private final static HashMap<String, ConfigParameter> immutableParameters = [
            outDirectory: new ConfigParameter<CO2FootprintTraceConfig>(
                    Set.of(CO2FootprintTraceConfig),
                    'Directory for the output files.',
                    'pipeline_info',
                    false, true, false
            ),
            name: new ConfigParameter<CO2FootprintSummaryConfig>(
                    Set.of(CO2FootprintSummaryConfig),
                    'Name of the file.',
                    'co2footprint',
                    false, true, true
            ),
            suffix: new ConfigParameter<CO2FootprintReportConfig>(
                    Set.of(CO2FootprintReportConfig),
                    'Suffix / timestamp of the file.',
                    TraceHelper.launchTimestampFmt(),
                    false, true, true
            ),
            ending: new ConfigParameter<CO2FootprintReportConfig>(
                    Set.of(CO2FootprintReportConfig),
                    'File ending.',
                    'txt',
                    false, true, true
            ),
    ]

    private final static HashMap<String, ConfigParameter> parameters = [
            enabled: new ConfigParameter<CO2FootprintReportConfig>(
                    Set.of(CO2FootprintReportConfig),
                    'Should the output be enabled?',
                    true,
            ),
            file: new ConfigParameter<CO2FootprintReportConfig>(
                    Set.of(CO2FootprintReportConfig),
                    'Path to the file.',
                    Path.of(outDirectory, "${name}_${suffix}.${ending}").toString()
            ),
    ]

    CO2FootprintFileConfig(Map<String, ?> fileConfigMap) {
        super('CO2FootprintTraceConfig', (parameters + immutableParameters))
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

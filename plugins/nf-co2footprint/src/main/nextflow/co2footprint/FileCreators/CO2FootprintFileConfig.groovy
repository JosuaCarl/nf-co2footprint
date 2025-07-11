package nextflow.co2footprint.FileCreators

import groovy.util.logging.Slf4j
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
class CO2FootprintFileConfig {
    // Immutable
    private final String outDirectory = 'pipeline_info'
    private final String name = 'co2footprint'
    private final String suffix = TraceHelper.launchTimestampFmt()
    private final String ending = 'txt'

    // Configuration parameters
    private boolean enabled = true
    private String file = Path.of(outDirectory, "${name}_${suffix}.${ending}").toString()

    CO2FootprintFileConfig(Map<String, ?> fileConfigMap) {
        fileConfigMap.each { name, value -> this.setProperty(name, value) }
    }

    // Get methods
    String getOutDirectory() { outDirectory }
    String getName() { name }
    String getSuffix() { suffix }
    String getEnding() { ending }
    boolean getEnabled() { enabled }
    String getFile() { file }
    String getPath() { Path.of(file) }
}

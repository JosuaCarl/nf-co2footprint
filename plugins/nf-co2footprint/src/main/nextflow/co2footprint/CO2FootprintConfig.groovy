package nextflow.co2footprint

import java.nio.file.Path

import groovy.util.logging.Slf4j

import nextflow.co2footprint.utils.BaseConfig
import nextflow.co2footprint.utils.ConfigParameter
import nextflow.co2footprint.DataContainers.DataMatrix
import nextflow.co2footprint.DataContainers.CIDataMatrix
import nextflow.co2footprint.DataContainers.CIValueComputer
import nextflow.co2footprint.DataContainers.TDPDataMatrix
import nextflow.co2footprint.FileCreators.CO2FootprintReportConfig
import nextflow.co2footprint.FileCreators.CO2FootprintSummaryConfig
import nextflow.co2footprint.FileCreators.CO2FootprintTraceConfig

/**
 * Configuration class for CO₂ footprint calculations.
 *
 * It extracts values from a configuration map and sets up all relevant parameters,
 * such as output file names, carbon intensity, PUE, memory and CPU power draw, and machine type.
 * Users can customize these values in the Nextflow config file under the `co2footprint` block.
 *
 * Example usage in config:
 * co2footprint {
 *     traceFile = "co2footprint_trace.txt"
 *     summaryFile = "co2footprint_summary.txt"
 *     ci = 300
 *     pue = 1.4
 *     powerdrawMem = 0.67
 * }
 *
 * @author Júlia Mir Pedrol <mirp.julia@gmail.com>, Sabrina Krakau <sabrinakrakau@gmail.com>
 */
@Slf4j
class CO2FootprintConfig extends BaseConfig {

    // Configuration parameters (can be set in Nextflow config)
    private static HashMap<String, ConfigParameter> parameters = [
        trace: new ConfigParameter<CO2FootprintTraceConfig>(
            Set.of(CO2FootprintTraceConfig),
            'Trace file, written by the plugin.'
        ),
        summary: new ConfigParameter<CO2FootprintSummaryConfig>(
            Set.of(CO2FootprintSummaryConfig),
            'Summary of the CO2 footprint in text format.'
        ),
        report: new ConfigParameter<CO2FootprintReportConfig>(
            Set.of(CO2FootprintReportConfig),
            'Report of the CO2 footprint in browser (HTML) format.'
        ),
        location: new ConfigParameter<String>(
            Set.of(String),
            'Location of the computing machine.'
        ),
        ci: new ConfigParameter<Double>(
            Set.of(Closure<Double>, Double),
            'Location-based carbon intensity (CI).'
        ),
        ciMarket: new ConfigParameter<Double>(
            Set.of(Closure<Double>, Double),
            'Market-based carbon intensity (CI).'
        ),
        emApiKey: new ConfigParameter<String>(
            Set.of(String),
            'A key/token for Electricity map\'s API.'
        ),
        pue: new ConfigParameter<Double>(
            Set.of(Double),
            'Power usage effectiveness of the computing machine.'
        ),
        powerdrawMem: new ConfigParameter<Double>(
            Set.of(Double),
            'Power usage of the memory (constant).'
        ),
        ignoreCpuModel: new ConfigParameter<Boolean>(
            Set.of(Boolean),
            'Whether to ignore the model and jump straight to a default fallback value.'
        ),
        powerdrawCpuDefault: new ConfigParameter<Double>(
            Set.of(Double),
            'Default power usage per core of the CPU [W].'
        ),
        customCpuTdpFile: new ConfigParameter<String>(
            Set.of(String),
            'Path to a custom thermal design power (TDP) table.'
        ),
        machineType : new ConfigParameter<String> (
            Set.of ( String ),
            'The type of the computing machine (local, compute cluster, cloud).'
        ),
        supportedMachineTypes : new ConfigParameter<List<String>> (
            Set.of ( List<String> ),
            'The machine types that are supported by the plugin.' ,
            [ 'local', 'compute cluster', 'cloud' ],
            true, true,false
        )
    ]

    // Getter methods for config values
    CO2FootprintTraceConfig getTrace() { get('trace') as CO2FootprintTraceConfig }
    CO2FootprintSummaryConfig getSummary() { get('summary') as CO2FootprintSummaryConfig }
    CO2FootprintReportConfig getReport() { get('report') as CO2FootprintReportConfig }
    String getLocation() { get('location') }
    Double getCi() { getEvaluated('ci') as Double }
    Double getCiMarket() { getEvaluated('ciMarket') as Double }
    String getEmApiKey() { get('emApiKey') }
    Double getPue() { get('pue') as Double}
    Boolean getIgnoreCpuModel() { get('ignoreCpuModel') }
    Double getPowerdrawCpuDefault() { get('powerdrawCpuDefault') as Double  }
    Double getPowerdrawMem() { get('powerdrawMem') as Double }
    String getCustomCpuTdpFile() { get('customCpuTdpFile') }
    String getMachineType()  { get('machineType')  }
    List<String> getSupportedMachineTypes() { get('supportedMachineTypes') as List<String> }
    /**
     * Loads configuration from a map and sets up defaults and fallbacks.
     * Also sets up CPU and CI data sources and assigns machine type and PUE.
     *
     * @param configMap   Map of configuration values (from Nextflow config)
     * @param cpuData     TDPDataMatrix with CPU power draw data
     * @param ciData      CIDataMatrix with carbon intensity data
     * @param processMap  Map with process/executor info
     */
    CO2FootprintConfig(Map<String, Object> configMap, TDPDataMatrix cpuData, CIDataMatrix ciData, Map<String, Object> processMap) {
        super('CO2FootprintConfig', parameters)

        // Ensure configMap is not null
        configMap ?= [:]

        // Assign values from map to config
        configMap.each { name, value -> configure(name, value) }

        // Determine the carbon intensity (CI) value
        if (ci == null) {
            CIValueComputer ciValueComputer = new CIValueComputer(emApiKey, location, ciData)
            // ci is either set to a Closure (in case the electricity maps API is used) or to a Double (in the other cases)
            // The closure is invoked each time the CO2 emissions are calculated (for each task) to make a new API call to update the real time ci value.
            set('ci', ciValueComputer.computeCI())
        }

        // Sets machineType and pue based on the executor if machineType is not already set
        if (machineType == null) {
            setMachineTypeAndPueFromExecutor(processMap?.get('executor') as String)
        }

        if (machineType == 'cloud') {
            log.warn(
                    'Cloud instances are not yet fully supported. ' +
                    'We are working on the seamless integration of major cloud providers. ' +
                    'In the meantime we recommend following the instructions at ' +
                    'https://nextflow-io.github.io/nf-co2footprint/usage/configuration/#cloud-computations' +
                    'to fully integrate your cloud instances into the plugin.'
            )
        }

        // Assign PUE if not already given
        setEmpty(
    'pue',
            switch (machineType) {
                case 'local' -> 1.0
                case 'compute cluster' -> 1.67
                case 'cloud' -> 1.56  // source: (https://datacenter.uptimeinstitute.com/rs/711-RIA-145/images/2024.GlobalDataCenterSurvey.Report.pdf)
                default -> 1.0 // Fallback PUE (assigned if machineType is null)
            }
        )

        // Set fallback CPU model based on machine type
        if (machineType) {
            if (supportedMachineTypes.contains(machineType)) {
                cpuData.fallbackModel = "default ${machineType}" as String
            }
            else {
                final String message = "machineType '${machineType}' is not supported." +
                        "Please chose one of ${supportedMachineTypes}."
                log.error(message)
                throw new IllegalArgumentException(message)
            }
        }

        // Set default CPU power draw if given
        if (powerdrawCpuDefault) {
            cpuData.set(powerdrawCpuDefault, cpuData.fallbackModel, 'tdp (W)')
        }

        // Throw error if both customCpuTdpFile and ignoreCpuModel are set
        if (customCpuTdpFile && ignoreCpuModel) {
            log.warn("Both 'customCpuTdpFile' and 'ignoreCpuModel=true' are set. Note: When 'ignoreCpuModel' is true, the custom TDP file will be ignored.")
        }

        // Use custom CPU TDP file if provided
        if (customCpuTdpFile) {
            cpuData.update(
                    TDPDataMatrix.fromCsv(Path.of(customCpuTdpFile as String))
            )
        }
    }

    /**
     * Sets the machine type and PUE based on the executor.
     * It reads a CSV file to get the machine type and PUE for the given executor.
     *
     * @param executor The executor name (e.g., 'awsbatch', 'local', etc.)
     */
    private void setMachineTypeAndPueFromExecutor(String executor) {
        if (executor) {
            // Read the CSV file as a DataMatrix - set RowIndex to 'executor'
            DataMatrix machineTypeMatrix = DataMatrix.fromCsv(
                    Path.of(this.class.getResource(
                            '/executor_machine_pue_mapping.csv').toURI()),
                    ',', 0, null, 'executor'
            )

            // Check if matrix contains the required columns
            machineTypeMatrix.checkRequiredColumns(['machineType', 'pue'])

            // Extract info from executor
            if (machineTypeMatrix.rowIndex.containsKey(executor)) {
                setEmpty('pue', machineTypeMatrix.get(executor, 'pue') as Double) // assign pue only if not already set
                set('machineType', machineTypeMatrix.get(executor, 'machineType') as String)
            }
            else {
                log.warn(
                    "Executor '${executor}' is not mapped to a machine type / power usage effectiveness (PUE). " +
                    "=> `machineType` <- null, `pue` <- 1.0. " +
                    "To eliminate this warning you can set `machineType` in the config to one of ${supportedMachineTypes}.")
            }
        }
        else {
            log.debug('No executor found in config under process.executor.')
        }
    }

    /**
     * Collects input file options for reporting.
     *
     * @return SortedMap of input file options
     */
    SortedMap<String, Object> collectInputFileOptions() {
        return [
                "customCpuTdpFile": customCpuTdpFile
        ].sort() as SortedMap
    }

    /**
     * Collects output file options for reporting.
     *
     * @return SortedMap of output file options
     */
    SortedMap<String, Object> collectOutputFileOptions() {
        return [
                "trace": trace,
                "summaryFile": summary,
                "reportFile": report
        ].sort() as SortedMap
    }

    /**
     * Collects CO₂ calculation options for reporting.
     *
     * @return SortedMap of calculation options
     */
    SortedMap<String, Object> collectCO2CalcOptions() {
        return [
                "location": location,
                "ci": ci,
                "pue": pue,
                "powerdrawMem": powerdrawMem,
                "powerdrawCpuDefault": powerdrawCpuDefault,
                "ignoreCpuModel": ignoreCpuModel,
        ].sort() as SortedMap
    }
}

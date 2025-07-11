package nextflow.co2footprint.FileCreators

class CO2FootprintTraceConfig extends CO2FootprintFileConfig {
    private final String name = "${super.getName()}_trace"
    private final String ending = 'txt'

    CO2FootprintTraceConfig(Map<String, ?> traceConfigMap) {
        super(traceConfigMap)
    }
}

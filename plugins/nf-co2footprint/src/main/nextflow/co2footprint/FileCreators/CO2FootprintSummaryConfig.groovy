package nextflow.co2footprint.FileCreators

class CO2FootprintSummaryConfig extends CO2FootprintFileConfig {
    private final String name = "${super.getName()}_summary"
    private final String ending = 'txt'

    CO2FootprintSummaryConfig(Map<String, ?> traceConfigMap) {
        super(traceConfigMap)
    }
}

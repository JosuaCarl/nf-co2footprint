package nextflow.co2footprint.FileCreators

class CO2FootprintReportConfig extends CO2FootprintFileConfig {
    private final String name = "${super.getName()}_report"
    private final String ending = 'html'

    CO2FootprintReportConfig(Map<String, ?> traceConfigMap) {
        super(traceConfigMap)
    }
}

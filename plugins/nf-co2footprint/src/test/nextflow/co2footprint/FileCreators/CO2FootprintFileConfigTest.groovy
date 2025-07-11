package nextflow.co2footprint.FileCreators

import spock.lang.Shared
import spock.lang.Specification

class CO2FootprintFileConfigTest extends Specification{
    @Shared
    String now = new Date().format('yyyy-MM-dd_HH-mm-ss')

    def 'Test trace config'() {
        setup:
        Map<String, ?> traceConfigMap = [:]
        if (file != null) { traceConfigMap.put('file', file) }
        if (enable != null) { traceConfigMap.put('enable', enable) }
        traceConfigMap.putAll(other)

        when:
        CO2FootprintTraceConfig traceConfig = new CO2FootprintTraceConfig(traceConfigMap)

        then:
        traceConfig.getFile() == fileExp
        traceConfig.getEnabled() == enableExp

        where:
        file            || enable       || fileExp                                          || enableExp        || other
        'test.txt'      || true         || 'test.txt'                                       || true             || [:]
        'test'          || false        || 'test'                                           || false            || [:]
        null            || null         || "pipeline_info/co2footprint_trace_${now}.txt"    || null             || [:]

    }

    def 'Test summary config'() {

    }

    def 'Test report config'() {

    }
}

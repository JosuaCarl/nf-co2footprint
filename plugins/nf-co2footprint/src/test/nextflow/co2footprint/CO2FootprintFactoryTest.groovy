/*
 * Copyright 2021, Seqera Labs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nextflow.co2footprint

import nextflow.Session
import spock.lang.Specification

/**
 *
 * @author Sabrina Krakau <sabrinakrakau@gmail.com>
 */
class CO2FootprintFactoryTest extends Specification {

    def 'should return observer' () {
        when:
        // TODO I am not sure if 'Mock(Session)' should be used instead of 'new Session()',
        // but then one would need to test 'if( session.config instanceof Map )' in CO2FootprintFactory
        // before calling the constructor of CO2FootprintConfig
        def result = new CO2FootprintFactory().create(new Session())
        then:
        result.size()==1
        result[0] instanceof CO2FootprintObserver
    }

}

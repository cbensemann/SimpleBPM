/*
 * Copyright 2014 Nomad Consulting Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nz.co.nomadconsulting.simplebpm;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;


public class ProcessVariableProducer {

    @Produces
    @ProcessVariable
    public Object getProcessVariable(final InjectionPoint ip) {
        final String processVariableName = getVariableName(ip);

        return null; // TODO get from process/task
    }


    private String getVariableName(final InjectionPoint ip) {
        String variableName = ip.getAnnotated().getAnnotation(ProcessVariable.class).value();
        if (variableName.isEmpty()) {
            variableName = ip.getMember().getName();
        }
        return variableName;
    }
}

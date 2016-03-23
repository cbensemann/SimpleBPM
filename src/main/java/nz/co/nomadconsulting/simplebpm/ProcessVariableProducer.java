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

import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.model.VariableDesc;

import java.lang.reflect.Type;
import java.util.Collection;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;


public class ProcessVariableProducer {

    @Inject
    private RuntimeDataService runtimeDataService;

    @Inject
    private BusinessProcessInstance instance;


    @Produces
    @ProcessVariable
    public Object getProcessVariable(final InjectionPoint ip) {
        final String processVariableName = getVariableName(ip);

        final Collection<VariableDesc> variablesCurrentState = runtimeDataService.getVariablesCurrentState(instance.getProcessId());
        Object resultingVariable = null;
        for (VariableDesc variableDesc : variablesCurrentState) {
            if (variableDesc.getVariableId() == processVariableName) {
                resultingVariable = variableDesc.getNewValue();
                break;
            }
        }

        return coerceToType(resultingVariable, ip.getType());
    }


    private Object coerceToType(Object resultingVariable, Type type) {
        return resultingVariable; // TODO do coercion here
    }


    private String getVariableName(final InjectionPoint ip) {
        String variableName = ip.getAnnotated().getAnnotation(ProcessVariable.class).value();
        if (variableName.isEmpty()) {
            variableName = ip.getMember().getName();
        }
        return variableName;
    }
}

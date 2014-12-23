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

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.internal.runtime.manager.context.EmptyContext;


@SuppressWarnings("serial")
public class BusinessProcessScopeContext implements Context, Serializable {

    private BeanManager manager;


    public BusinessProcessScopeContext(final BeanManager manager) {
        this.manager = manager;
    }


    @Override
    public Class<? extends Annotation> getScope() {
        return BusinessProcessScoped.class;
    }


    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(final Contextual<T> contextual, final CreationalContext<T> creationalContext) {
        final Bean<T> bean = (Bean<T>) contextual;
        final String variableName = bean.getName();

        final WorkflowProcessInstance processInstance = getProcessInstance(contextual);
        final Object variable = processInstance.getVariable(variableName);

        if (variable == null) {
            final T beanInstance = bean.create(creationalContext);
            processInstance.setVariable(variableName, beanInstance);
            return beanInstance;
        }
        else {
            return (T) variable;
        }
    }


    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(final Contextual<T> contextual) {
        final Bean<T> bean = (Bean<T>) contextual;
        final String variableName = bean.getName();

        final WorkflowProcessInstance processInstance = getProcessInstance(contextual);
        final Object variable = processInstance.getVariable(variableName);
        if (variable != null) {
            return (T) variable;
        }
        else {
            return null;
        }
    }


    private <T> WorkflowProcessInstance getProcessInstance(Contextual<T> contextual) {
        // TODO Auto-generated method stub
        final RuntimeManager runtimeManager = lookupRuntimeManager(manager);
        final RuntimeEngine runtimeEngine = runtimeManager.getRuntimeEngine(EmptyContext.get());
        return null; //runtimeEngine.getKieSession().getProcessInstance();
    }
    
    public RuntimeManager lookupRuntimeManager(BeanManager bm) {
        Set<Bean< ? >> beans = bm.getBeans(RuntimeManager.class);
        if (beans.isEmpty()) {
          throw new IllegalStateException("CDI BeanManager cannot find an instance of requested type 'RuntimeManager'");
        }
        Bean<?> bean = bm.resolve(beans);
        CreationalContext<?> ctx = bm.createCreationalContext(bean);
        // select one beantype randomly. A bean has a non-empty set of beantypes.
        Type type = (Type) bean.getTypes().iterator().next();
        return (RuntimeManager) bm.getReference(bean, type, ctx);
      }


    @Override
    public boolean isActive() {
        return true;
    }
}

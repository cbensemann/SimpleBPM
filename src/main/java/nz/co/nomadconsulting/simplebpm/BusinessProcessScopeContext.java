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

import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.BeanManager;

@SuppressWarnings("serial")
public class BusinessProcessScopeContext implements Context, Serializable {

    private BeanManager manager;

    public BusinessProcessScopeContext(BeanManager manager) {
        this.manager = manager;
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return BusinessProcessScoped.class;
    }

    @Override
    public <T> T get(Contextual<T> contextual, CreationalContext<T> creationalContext) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> T get(Contextual<T> contextual) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isActive() {
        // TODO Auto-generated method stub
        return false;
    }
}

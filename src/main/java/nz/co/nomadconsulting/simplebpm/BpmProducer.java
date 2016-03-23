/*
 * Copyright 2014 Nomad Consulting Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package nz.co.nomadconsulting.simplebpm;

import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.cdi.Kjar;
import org.jbpm.services.task.audit.JPATaskLifeCycleEventListener;
import org.jbpm.services.task.lifecycle.listeners.TaskLifeCycleEventListener;
import org.kie.internal.runtime.cdi.BootOnLoad;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;

@BootOnLoad
@ApplicationScoped
public class BpmProducer {

    public static final String DEPLOYMENT_ID = "demo.myProject:myProject:1.0";

    @Inject
    @Kjar
    private DeploymentService deploymentService;

    @PostConstruct
    public void init() {
        String[] gav = DEPLOYMENT_ID.split(":");
        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(gav[0], gav[1], gav[2]);
        deploymentService.deploy(deploymentUnit);
    }


    @PersistenceUnit(unitName = "org.jbpm.domain")
    private EntityManagerFactory emf;


    @Produces
    public EntityManagerFactory produceEntityManagerFactory() {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("org.jbpm.domain");
        }
        return emf;
    }

    @Inject
    @Kjar
    private Instance<DeploymentService> deploymentServiceInstance;


    @SuppressWarnings("serial")
    @Produces
    public DeploymentService produceDeploymentService() {
        return deploymentServiceInstance.select(new AnnotationLiteral<Kjar>() {}).get();
    }


    @Produces
    public TaskLifeCycleEventListener produceAuditListener() {
        return new JPATaskLifeCycleEventListener(true);
    }
}

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
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;


@SuppressWarnings("serial")
@Interceptor
@BusinessProcessBinding
public class BusinessProcessInterceptor implements Serializable {

    private static final Logger log = Logger.getLogger(BusinessProcessInterceptor.class.getName());

    @Inject
    private TaskService taskService;

    @Inject
    private KieSession kieSession;


    @AroundInvoke
    public Object authorisationCheck(InvocationContext ctx) throws Exception {
        final Method method = ctx.getMethod();
        if (method.isAnnotationPresent(StartTask.class)) {
            log.fine("Beginning task");
            beforeInvocation(ctx);
        }

        try {
            return ctx.proceed();
        }
        finally {
            // end task
            if (method.isAnnotationPresent(EndTask.class)) {
                afterInvocation(ctx);
            }
        }
    }


    private void beforeInvocation(InvocationContext invocationContext) {
        Method method = invocationContext.getMethod();
        if (method.isAnnotationPresent(StartTask.class)) {
            log.finest("encountered @StartTask");
            StartTask tag = method.getAnnotation(StartTask.class);
            // Long taskId = getProcessOrTaskId(tag.taskIdParameter(), tag.taskId());
            // taskService.start(taskId, userId);
        }
        // else if (method.isAnnotationPresent(ResumeProcess.class)) {
        // log.trace("encountered @ResumeProcess");
        // ResumeProcess tag = method.getAnnotation(ResumeProcess.class);
        // if (tag.processKey().equals(""))
        // {
        // Long processId = getProcessOrTaskId(tag.processIdParameter(), tag.processId());
        // return BusinessProcess.instance().resumeProcess(processId);
        // }
        // else
        // {
        // return BusinessProcess.instance().resumeProcess(tag.definition(), getProcessKey(tag.processKey()));
        // }
        // }
        // if (method.isAnnotationPresent(EndTask.class))
        // {
        // log.trace("encountered @EndTask");
        // return BusinessProcess.instance().validateTask();
        // }
    }


    private void afterInvocation(InvocationContext invocation) {
        Method method = invocation.getMethod();
        if (method.isAnnotationPresent(CreateProcess.class)) {
            log.finest("encountered @CreateProcess");
            CreateProcess tag = method.getAnnotation(CreateProcess.class);
            Map<String, Object> parameters = new HashMap<>();
            final ProcessInstance processId = kieSession.startProcess(tag.value(), parameters);
        }
        if (method.isAnnotationPresent(EndTask.class)) {
            log.finest("encountered @EndTask");
            // taskService.complete(taskId, userId, data);
        }
    }
}

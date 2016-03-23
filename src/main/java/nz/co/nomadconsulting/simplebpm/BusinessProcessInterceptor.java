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

import nz.co.nomadconsulting.simpleessentials.Expressions;
import org.jbpm.services.api.ProcessService;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Task;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;


@SuppressWarnings("serial")
@Interceptor
@BusinessProcessBinding
public class BusinessProcessInterceptor implements Serializable {

    private static final Logger log = Logger.getLogger(BusinessProcessInterceptor.class.getName());

    @Inject
    private TaskService taskService;

    @Inject
    private ProcessService processService;

    @Inject
    private BusinessProcessInstance instance;

    @Inject
    private Expressions expressions;


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
//             Long taskId = getProcessOrTaskId(tag.taskIdParameter(), tag.taskId());
//             taskService.resume(taskId, userId);

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
        try {
            Method method = invocation.getMethod();
            if (method.isAnnotationPresent(CreateProcess.class)) {
                log.finest("encountered @CreateProcess");
                CreateProcess tag = method.getAnnotation(CreateProcess.class);
                Map<String, Object> parameters = extractParameters(invocation);
                final ProcessInstance processInstance = processService.startProcess(tag.value(), parameters);
                instance.setProcessId(processInstance.getId());
            }
            if (method.isAnnotationPresent(EndTask.class)) {
                log.finest("encountered @EndTask");
                final Task taskById = taskService.getTaskById(0);
                taskService.complete(instance.getTaskId(), userId, data);
            }
        }
        catch (Exception e) {
            // TODO deal with me
        }
    }


    protected Map<String, Object> extractParameters(InvocationContext ctx) throws IllegalArgumentException, IllegalAccessException {
        Map<String, Object> variables = new HashMap<>();
        for (Field field : ctx.getMethod().getDeclaringClass().getDeclaredFields()) {
          if (!field.isAnnotationPresent(ProcessVariable.class)) {
            continue;
          }
          field.setAccessible(true);
          ProcessVariable processStartVariable = field.getAnnotation(ProcessVariable.class);
          String fieldName = processStartVariable.value();
          if (fieldName == null || fieldName.length() == 0) {
            fieldName = field.getName();
          }
          Object value = field.get(ctx.getTarget());
          variables.put(fieldName, value);
        }

        return variables;
    }
}

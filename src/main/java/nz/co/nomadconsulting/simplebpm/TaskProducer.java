package nz.co.nomadconsulting.simplebpm;

import java.util.List;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;

public class TaskProducer {

    @Inject
    private TaskService taskService;
    
    @Produces
    public void listTasksForAvailableForUser() {
//        return taskService.getTasksAssignedAsPotentialOwner(userId, language);
        // TODO make convenience producers for tasks
    }
    
    @Produces
    public List<TaskSummary> listTasksAssignedToUser() {
        return null; //taskService.getTasksOwned(userId, language);
        // TODO make convenience producers for tasks
    }
}

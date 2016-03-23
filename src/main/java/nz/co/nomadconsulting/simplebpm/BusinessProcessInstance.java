package nz.co.nomadconsulting.simplebpm;

import java.io.Serializable;

@SuppressWarnings("serial")
@BusinessProcessScoped
public class BusinessProcessInstance implements Serializable {

    private Long processId;
    private Long taskId;


    public Long getProcessId() {
        return processId;
    }


    public void setProcessId(Long processId) {
        this.processId = processId;
    }


    public Long getTaskId() {
        return taskId;
    }


    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }
}

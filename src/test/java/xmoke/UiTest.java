package xmoke;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class UiTest {

    // Positive: verifies getGoodbyeMessage returns the expected goodbye text and line separator.
    @Test
    public void getGoodbyeMessage_containsByeAndSeparator() {
        Ui ui = new Ui();
        String result = ui.getGoodbyeMessage();

        assertTrue(result.contains("Bye"));
        assertTrue(result.contains("Hope to see you again soon!"));
    }

    // Positive: verifies getTaskListMessage formats a non-empty task list with header and task lines.
    @Test
    public void getTaskListMessage_withOneTask_containsHeaderAndTask() {
        Ui ui = new Ui();
        TaskList taskList = new TaskList();
        taskList.addTask(new Task("buy milk", Task.TaskType.T));
        String result = ui.getTaskListMessage(taskList);

        assertTrue(result.contains("Here are the tasks in your list:"));
        assertTrue(result.contains("1.[T][ ] buy milk"));
        assertFalse(result.isEmpty());
    }
}

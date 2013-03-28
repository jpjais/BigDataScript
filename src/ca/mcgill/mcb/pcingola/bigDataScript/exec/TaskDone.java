package ca.mcgill.mcb.pcingola.bigDataScript.exec;

import java.util.HashMap;

import ca.mcgill.mcb.pcingola.bigDataScript.util.Gpr;

/**
 * Task done.
 * Check if a task finished, by checking if 'exitFile' exists
 * 
 * @author pcingola
 */
public class TaskDone extends Thread {

	public static final int SLEEP_TIME = 200;

	boolean debug = true;
	boolean verbose;
	HashMap<Task, Executioner> execByTask;
	boolean running;

	public TaskDone() {
		execByTask = new HashMap<Task, Executioner>();
	}

	/**
	 * Add a task to this 'timer'
	 * 
	 * @param executioner : Executioner executing this task (we must be able to kill the task)
	 * @param task : Task (timeout is inferred from task.resources)
	 */
	public synchronized void add(Executioner executioner, Task task) {
		if (debug) Gpr.debug("TaskDone: Adding task " + task.getId());
		if (task == null) return;
		execByTask.put(task, executioner);
	}

	public void kill() {
		running = false;
	}

	/**
	 * Remove task (do not monitor)
	 * @param task
	 */
	public synchronized void remove(Task task) {
		if (debug) Gpr.debug("TaskDone: Removing task " + task.getId());
		execByTask.remove(task);
	}

	@Override
	public void run() {
		Gpr.debug("RUN LOOP");
		for (running = true; running;) {
			update();
			sleep();
		}
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	/**
	 * Sleep a short time
	 */
	void sleep() {
		try {
			sleep(SLEEP_TIME);
		} catch (InterruptedException e) {
		}
	}

	/**
	 * Check is an exist file exists, update states
	 */
	void update() {
		Gpr.debug("UPDATE");
		for (Task task : execByTask.keySet()) {
			Gpr.debug("Check file " + task.getExitCodeFile());
			String exitFile = task.getExitCodeFile();
			if (Gpr.exists(exitFile)) update(task);

		}
	}

	/**
	 * Exit file exists: update states
	 */
	synchronized void update(Task task) {
		if (debug) Gpr.debug("Found exit file " + task.getExitCodeFile());

		Executioner executioner = execByTask.get(task);
		remove(task); // We don't need this any more

		// Parse exit file
		sleep();
		String exitFileStr = Gpr.readFile(task.getExitCodeFile()).trim();
		int exitVal = (exitFileStr.equals("0") ? 0 : 1);
		if (debug) Gpr.debug("Task finished '" + task.getId() + "', exit status : '" + exitFileStr + "', exit code " + exitVal);

		// Set task
		task.setStarted(true);
		task.setExitValue(exitVal);
		task.setDone(true);

		// Inform executioner that task has finished
		executioner.finished(task.getId());

	}
}
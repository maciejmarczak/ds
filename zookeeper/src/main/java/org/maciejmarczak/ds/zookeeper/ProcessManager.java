package org.maciejmarczak.ds.zookeeper;

import java.io.IOException;

class ProcessManager {

    private final ProcessBuilder processBuilder;
    private Process process;

    ProcessManager(String runCmd) {
        processBuilder = new ProcessBuilder(runCmd);
    }

    // default runCmd
    ProcessManager() {
        this("gnome-calculator");
    }

    void startProcess() throws ProcessManagementException {
        try {
            process = processBuilder.start();
        } catch (IOException e) {
            throw new ProcessManagementException(e.getMessage());
        }
    }

    void stopProcess() throws ProcessManagementException {
        if (process == null || !process.isAlive()) {
            throw new ProcessManagementException(
                    "Trying to kill a dead process.");
        }

        process.destroy();
        process = null;
    }

    boolean isRunning() {
        return process != null && process.isAlive();
    }

}

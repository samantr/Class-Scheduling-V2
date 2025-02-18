package com.esenyurt.log;

import com.esenyurt.database.MSSQLDatabaseConnector;
import com.esenyurt.entity.MemoryLog;

import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MemoryLogger {
    public static void log(int run_id) throws SQLException {
        // Start the memory logging thread
        List<MemoryLog> memoryLogs = new ArrayList<>();
        Thread memoryLogger = new Thread(() -> {
            Runtime runtime = Runtime.getRuntime();
            try {
                while (true) {
                    long totalMemory = runtime.totalMemory(); // Total memory allocated to JVM
                    long freeMemory = runtime.freeMemory();   // Free memory available
                    long usedMemory = totalMemory - freeMemory; // Used memory
                    long maxMemory = runtime.maxMemory(); // Maximum memory JVM can use
                    MemoryLog memoryLog = new MemoryLog();
                    memoryLog.freeMemMb = (int)freeMemory / (1024 * 1024);
                    memoryLog.usedMemMb = (int)usedMemory / (1024 * 1024);
                    memoryLog.maxMemMb = (int)maxMemory / (1024 * 1024);
                    LocalTime localTime = LocalTime.now();
                    memoryLog.time = Time.valueOf(localTime);
                    memoryLogs.add(memoryLog);
                    Thread.sleep(5000); // Sleep for 5 seconds

                }
            } catch (InterruptedException e) {
                System.out.println("Memory Logger Thread Interrupted!");
            }


        });

        // Set the thread as daemon so it stops when the main program exits
        memoryLogger.setDaemon(true);
        memoryLogger.start();

        // Simulating some memory usage in the main program
        for (int i = 0; i < 5; i++) {
            int[] dummyArray = new int[10_000_000]; // Allocate memory
            System.out.println("Iteration " + (i + 1) + " running...");
            try {
                Thread.sleep(7000); // Wait 7 seconds before next iteration
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        MSSQLDatabaseConnector.insertMemoryLogBatch(memoryLogs,run_id);

        System.out.println("Main program finished.");
    }
}

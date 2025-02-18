package com.esenyurt.geneticalgorithm;

import com.esenyurt.database.MSSQLDatabaseConnector;
import com.esenyurt.entity.*;
import com.esenyurt.log.MemoryLogger;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeneticAlgorithmExecutor {
    public static void main(String[] args) {
        int populationSize = 50;
        int maxGenerations = 500;
        int maxStagnation = 500;
        String desc = "Standard Algorithm";



        schedule(25, maxGenerations, maxStagnation,desc );
        schedule(50, maxGenerations, maxStagnation,desc );
        schedule(100, maxGenerations, maxStagnation,desc );
        schedule(200, maxGenerations, maxStagnation,desc );


    }


    public static void schedule(int populationSize, int maxGenerations, int maxStagnation, String desc)
    {
        // Initialize parameters
        List<Teacher> teachers = MSSQLDatabaseConnector.fetchPersons();
        List<Classroom> classrooms = MSSQLDatabaseConnector.fetchClassRooms();
        List<Subject> subjects = MSSQLDatabaseConnector.fetchSubject();
        List<TimeSlot> timeSlots = TimeSlot.generateSampleTimeSlots(); // Mock data for now

        // Insert a new run into the database and retrieve its ID
        RunEntity run = new RunEntity();
        run.populationSize = populationSize;
        run.generationCount = maxGenerations;
        long runId = MSSQLDatabaseConnector.insertRun(run,desc);
        try {
            MemoryLogger.log((int)runId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // Create initial population
        Population population = new Population(populationSize, subjects, teachers, classrooms, timeSlots);



        int bestFitness = Integer.MIN_VALUE;
        int stagnationCount = 0;
        int currentGeneration = 1;
        // Evolve the population over multiple generations
        for (int generation = 1; generation <= maxGenerations; generation++) {
            currentGeneration = generation;
            // Get the fittest chromosome of this generation
            List<Schedule> fittestChromosome = population.getFittestChromosome();
            int fitness = FitnessCalculator.calculateChromosomeFitness(fittestChromosome);


            // Generate the next generation
            population = population.evolve(subjects, teachers, classrooms, timeSlots, stagnationCount);
            if(fitness > bestFitness)
            {
                bestFitness = fitness;
                stagnationCount = 0;
            }else
                stagnationCount++;

            //.out.printf("Generation %d: Best Fitness = %d%n", generation, fitness);

            try {
                MSSQLDatabaseConnector.insertSchedulesBatch(population.getFittestChromosome(), (int) runId, currentGeneration, fitness, 1);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            if (stagnationCount > maxStagnation )
            {
                System.out.println("Exit due to stagnation ");
                break;
            }


        }

        // Save the final result
        List<Schedule> finalFittest = population.getFittestChromosome();
        int finalFitness = FitnessCalculator.calculateChromosomeFitness(finalFittest);
        System.out.printf("Final Best Fitness: %d%n", finalFitness);
        try {
            MSSQLDatabaseConnector.insertSchedulesBatch(finalFittest, (int) runId, currentGeneration, finalFitness, 1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

package com.esenyurt.geneticalgorithm;

import com.esenyurt.entity.Schedule;
import com.esenyurt.entity.Subject;
import com.esenyurt.entity.Teacher;
import com.esenyurt.entity.Classroom;
import com.esenyurt.entity.TimeSlot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Population {

    double eliteRate = 0.1;
    double selectionRate = 0.1;
    private List<List<Schedule>> population; // Each chromosome is a List<Schedule>

    public Population(int populationSize, List<Subject> subjects, List<Teacher> teachers,
                      List<Classroom> classrooms, List<TimeSlot> timeSlots) {
        population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            population.add(generateChromosome(subjects, teachers, classrooms, timeSlots));
        }
    }


    public Population evolve( List<Subject> subjects, List<Teacher> teachers,
                             List<Classroom> classrooms, List<TimeSlot> timeSlots, int stagnationCount) {
        // Selection
        List<List<Schedule>> selectedChromosomes = selection(selectionRate);

        // Elitism: Carry over the top 5% unchanged
        int eliteCount = (int) (population.size() * eliteRate);
        List<List<Schedule>> nextGeneration = new ArrayList<>(population.subList(0, eliteCount));

        // Crossover
        List<List<Schedule>> offspring = crossover(selectedChromosomes);

        // Mutation
        offspring =  mutation(offspring, subjects, teachers, classrooms, timeSlots);

        // Add offspring to the next generation
        nextGeneration.addAll(offspring);

        // Ensure next generation size matches population size
        while (nextGeneration.size() < population.size()) {
            nextGeneration.add(generateChromosome(subjects, teachers, classrooms, timeSlots));
        }
        population = nextGeneration;
        return new Population(nextGeneration);
    }


    // Generate a random chromosome (List<Schedule>)
    private List<Schedule> generateChromosome(List<Subject> subjects, List<Teacher> teachers,
                                              List<Classroom> classrooms, List<TimeSlot> timeSlots) {
        List<Schedule> chromosome = new ArrayList<>();
        Random random = new Random();
        int teacherSize = teachers.size();
        for (Subject subject : subjects) {
            Teacher randomTeacher = teachers.get(random.nextInt(teacherSize));
            Classroom randomClassroom = classrooms.get(random.nextInt(classrooms.size()));

            TimeSlot randomTimeSlot = timeSlots.get(random.nextInt(timeSlots.size()));

            while (!TimeSlotGenerator.isTeacherAndClassroomAvailable(randomTeacher,randomClassroom,randomTimeSlot.day.getValue(),randomTimeSlot.time.getValue(), subject.units,chromosome))
                randomTimeSlot = timeSlots.get(random.nextInt(timeSlots.size()));//sasa

            Schedule gene = new Schedule(subject, randomTeacher, randomClassroom, randomTimeSlot,subject.getUnits());
            chromosome.add(gene);
        }

        return chromosome;
    }


    public List<Schedule> getFittestChromosome() {
        List<Schedule> fittest = null;
        int highestFitness = Integer.MIN_VALUE;

        for (List<Schedule> chromosome : population) {
            int fitness = calculateChromosomeFitness(chromosome);
            if (fitness > highestFitness) {
                highestFitness = fitness;
                fittest = chromosome;
            }
        }

        return fittest;
    }


    private int calculateChromosomeFitness(List<Schedule> chromosome) {
        return FitnessCalculator.calculateChromosomeFitness(chromosome);
    }

    // Selection: Choose top 50% chromosomes based on fitness
    private List<List<Schedule>> selection(double selectionRate) {
        // Sort by fitness in descending order
        population.sort((c1, c2) -> Integer.compare(calculateChromosomeFitness(c2), calculateChromosomeFitness(c1)));

        // Select top 50% chromosomes
        int selectionCount = (int)(population.size() * selectionRate);
        return new ArrayList<>(population.subList(0, selectionCount));
    }

    // Crossover: Combine two parent chromosomes to create offspring
    private List<List<Schedule>> crossover(List<List<Schedule>> selectedChromosomes) {
        List<List<Schedule>> offspring = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < selectedChromosomes.size() - 1; i += 1) {
            List<Schedule> parent1 = selectedChromosomes.get(random.nextInt(selectedChromosomes.size()));
            List<Schedule> parent2 = selectedChromosomes.get(random.nextInt(selectedChromosomes.size()));

            // Single-point crossover
            int crossoverPoint = random.nextInt(parent1.size());
            List<Schedule> child1 = new ArrayList<>(parent1.subList(0, crossoverPoint));
            child1.addAll(parent2.subList(crossoverPoint, parent2.size()));

            List<Schedule> child2 = new ArrayList<>(parent2.subList(0, crossoverPoint));
            child2.addAll(parent1.subList(crossoverPoint, parent1.size()));

            offspring.add(child1);
            offspring.add(child2);

        }

        return offspring;
    }

    // Mutation: Introduce random changes in chromosomes
    private List<List<Schedule>> mutation(List<List<Schedule>> offspring, List<Subject> subjects, List<Teacher> teachers,
                          List<Classroom> classrooms, List<TimeSlot> timeSlots) {
        Random random = new Random();


        for (int i = 0; i < offspring.size(); i++) {
            List<Schedule> chromosome = offspring.get(i);

            int geneIndex = random.nextInt(chromosome.size());

            // Create a new random gene
            Teacher randomTeacher = teachers.get(random.nextInt(teachers.size()));
            Classroom randomClassroom = classrooms.get(random.nextInt(classrooms.size()));
            TimeSlot randomTimeSlot = timeSlots.get(random.nextInt(timeSlots.size()));

            Subject randomSubject = subjects.get(random.nextInt(subjects.size()));

            while (!TimeSlotGenerator.isTeacherAndClassroomAvailable(randomTeacher,randomClassroom,randomTimeSlot.day.getValue(),randomTimeSlot.time.getValue(),randomSubject.units,chromosome))
                randomTimeSlot = timeSlots.get(random.nextInt(timeSlots.size()));//sasa

            Schedule newGene = new Schedule(randomSubject, randomTeacher, randomClassroom, randomTimeSlot,randomSubject.getUnits());

            chromosome.set(geneIndex, newGene);
            offspring.remove(i);
            offspring.add(i,chromosome);
        }
        return offspring;
    }

    // Constructor for creating the next generation
    private Population(List<List<Schedule>> nextGeneration) {
        this.population = nextGeneration;
    }
}

package codeweekend;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import codeweekend.io.*;
import codeweekend.model.*;
import codeweekend.scoring.*;
import codeweekend.solvers.*;


public class Main {

    public static void main(String[] args){
        Scoring scoring = new Scoring();

        int solver = 4;
        for (int k = 0; k < 1; ++k) {
            for (int i = 50; i <= 50; ++i) {
                System.out.printf("Task %d in progress...\n", i);
                computeNew( i, scoring, solver); 
            }
        }

        scoring.showScoreUpdates();
        scoring.writeBestScores();
    }

    public static int computeNew(int task, Scoring scoring, int solver) {
        ObjectMapper objectMapper = new ObjectMapper();
        int res = 0;

        
        GameNew game = IOUtils.parseInput(objectMapper, task);
        HeroNew hero = game.getHero();

        List<Turn> turns = null;

        switch (solver) {
            case 1:
                GreedySolver greedySolver = new GreedySolver(false, 1);
                turns = greedySolver.solve(hero, game, scoring);
                break;
            case 2: 
                SecondSolver secondSolver = new SecondSolver();
                turns = secondSolver.solve(hero, game, scoring);
                break;
            case 3:
                SafestRouteSolver safeSolver = new SafestRouteSolver();
                turns = safeSolver.solve(hero, game, scoring);
                break;
            case 4:
                SpecificTasksSolver specificSolver = new SpecificTasksSolver();
                turns = specificSolver.solve(hero, game, scoring);
                break;
            case 5:
                AnnealingSolver saSolver = new AnnealingSolver();
                turns = saSolver.solve(hero, game, scoring);
                break;
            default:
                break;
        }

        if (scoring.gotImproved(task)) {
            IOUtils.writeSolution(objectMapper, task, turns);
        }

        return res;
    }
}
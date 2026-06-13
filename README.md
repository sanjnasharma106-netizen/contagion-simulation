# Contagion Zone Simulation

## Project description

Contagion Zone is a Java Swing simulation that shows how an infection can spread through a moving population on a grid. People move around randomly, infected people can spread disease to nearby healthy people, and hospitals can treat infected people or vaccinate nearby healthy people. The simulation displays live statistics and also prints starting and ending grid data to the console.

## How to run

Open a terminal in this project folder and compile all Java files:

```powershell
javac *.java
```

Then run the program:

```powershell
java ContagionMain
```

## How to use the GUI

1. Choose a disease type: `Airborne` or `Contact`.
2. Set the population size.
3. Set the number of hospitals.
4. Set the initial infected percentage.
5. Click `START`.

The simulation updates every tick. A tick is one timer update in the program.

## Legend

- Young healthy people are green circles.
- Adult healthy people are green squares.
- Elderly healthy people are green triangles.
- Infected people are red.
- Vaccinated people are blue.
- Immune people are cyan.
- Hospitalized people are magenta.
- Dead people are gray X marks.
- Hospitals are light blue blocks with a red cross.

## Console output

The console prints the initial grid and initial counts when the simulation starts. When the simulation ends or the window is closed, it prints the ending grid and final counts. This output is part of the simulation data, not debug output.

## Ending condition

The simulation ends when no infected people remain. At that point, the timer stops, the pause button is disabled, and the ending grid and final statistics are printed.

## Main files

- `ContagionMain.java`: starts the GUI.
- `ContagionGUI.java`: controls the GUI, grid, timer, statistics, and simulation steps.
- `Person.java`: stores person state such as health, infection, vaccination, immunity, movement, recovery, and death.
- `YoungPerson.java`, `AdultPerson.java`, `ElderlyPerson.java`: person types with different movement and health behavior.
- `Contagion.java`: base disease behavior.
- `AirborneContagion.java`, `ContactContagion.java`: specific disease types.
- `Hospital.java`: treats infected people and vaccinates healthy nearby people.
- `Building.java`, `Entity.java`, `Location.java`: support classes for grid objects, movement, and coordinates.

# Software Engineering Project 2023 - AM32

Java implementation of the table
game [MyShelfie](https://www.craniocreations.it/prodotto/my-shelfie)

## Team members

- [Andrea Colombo](https://github.com/AndreaTgc)
- [Alberto Castagnoli](https://github.com/albecasta)
- [Luca Castagnoli](https://github.com/lucacasta01)
- [Matteo Caspani](https://github.com/matteocaspani)

## Project state

| Component            | State  |
|----------------------|--------|
| Model                | 🟢     |
| View                 | 🟢     |
| Controller           | 🟢     |

| Advanced feature     | State |
|----------------------|-------|
| Multiple games       | 🟢    |
| Game persistence     | 🟢    |
| Player reconnection  | 🔴    |
| Chat room            | 🟢    |



### Legend:

```
🔴: Not planned
🟡: Work in progress
🟢: Ready
```

## Implementation description

MyShelfie is an engaging table game that we recreated using Java and following the Model-View-Controller (MVC) architecture, making it an organized and scalable application. 
The MVC architecture separates the game's components into three distinct roles, each with its own responsibilities.

### Model 

Firstly, the Model represents the game's data and logic. 
In MyShelfie, it manages the inventory of virtual shelves, items, and their properties. 
The Model updates and stores information while ensuring consistency and integrity throughout the game. By encapsulating the game's data and operations, the Model simplifies the overall development and maintenance process.

### View 

Secondly, the View is responsible for the game's user interface (UI). 
It presents the data from the Model to the player and captures their input. 
In MyShelfie, the View displays the virtual shelves, items, and various game elements. It provides an intuitive and visually appealing interface that enhances the player's experience.

### Controller 

Lastly, the Controller acts as an intermediary between the Model and the View. It handles user input, triggers appropriate actions, and updates the Model accordingly. 
In MyShelfie, the Controller manages interactions with the UI, such as adding or removing items from shelves or initiating any gameplay event. 
By separating the logic from the UI, the Controller ensures maintainability and flexibility.


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

| Basic Features       | State |
|----------------------|-------|
| Full rules           | 🟢    |
| TUI                  | 🟢    |
| GUI                  | 🟢    |
| RMI                  | 🟢    |
| Socket               | 🟢    |


| Advanced Features    | State |
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
We decided to implement three advanced features and the full set of rules provided with the game. (see tables above)


### Model 

Firstly, the Model represents the game's data and logic. 
In MyShelfie, it manages the inventory of virtual shelves, items, and their properties. 
The Model updates and stores information while ensuring consistency and integrity throughout the game. By encapsulating the game's data and operations, the Model simplifies the overall development and maintenance process.

In our implementation, everything revolves around the `Game` class which holds all the information about the current state of the game. This class also provides all the necessary methods to easily update its state.
At any point, a `Game` object may be read by a controller and reconstruct the entire player view.

### View 

Secondly, the View is responsible for the game's user interface (UI). 
It presents the data from the Model to the player and captures their input. 
In MyShelfie, the View displays the virtual shelves, items, and various game elements. It provides an intuitive and visually appealing interface that enhances the player's experience.

### Controller and Actions

Lastly, the Controller acts as an intermediary between the Model and the View. It handles user input, triggers appropriate actions, and updates the Model accordingly. 
In MyShelfie, the Controller manages interactions with the UI, such as adding or removing items from shelves or initiating any gameplay event. 
By separating the logic from the UI, the Controller ensures maintainability and flexibility.

Any modification to the game state (apart from initialization) is applied via `Action` objects, each action has a type that identifies the effect said action will have on the game model (some examples of types used are CHAT, PRIVATEMESSAGE, PICKTILES, etc.).

### Network

We decided to implement both TCP and RMI communication, allowing the user to choose which protocol they want to use before joining a game.
Each protocol has its own `InputHandler` class, this approach makes adding another communication protocol extremely easy since it would only take an extra InputHandler class and some adjustments to the server to make it work.
Each connected client has an associated ping thread that ensures that the client and server are connected at all times during the game. Whenever a client loses connection to the server the game is immediately stopped and its current state is saved on the server.

### Advanced features

* `Multiple games`: The server can host multiple games at once.
* `Chat room`: Players have the possibility to send private messages to another player as well as broadcast messages during the game.
* `Game persistence`: Whenever a game is stopped for any reason other than its "natural" ending, the game state is saved on the server. Whenever one of those players connects to the server, they can decide to continue playing that game instead of creating a new one.

### Running the server/client

The command to run the server is: `java -jar MyShelfie-Server.jar` and the one to run the client is: `java -jar MyShelfie-Client.jar`,
after starting the client process you'll be able to choose whether you want to play with the GUI or CLI.

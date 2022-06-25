# Simple-Server
## Short Description
Simple Android application on kotlin, which provides frontend functionality for Simple Message project. 
## Concept
Chat provides possibility to talk to people, who use the same app. Users are able to do that in separate rooms.
## Functionality
* registration;
* login;
* choose name/nickname;
* upload avatar;
* seeing all previous chats;
* separate rooms;
* seeing messages in specific rooms;
* saving in database.
## Important to know before using 
For simplicity, every login/name is unique, therefore there can not be two users with the same name.
### Simple-Server project
Frontend is based around 3 activities, which correspond to a starting screen, feed view and a chat.
To connect with backend (which is written on NodeJs) we use socket connection with help of Socket.io. To create sockets we use an socketHandler.
Also there are adapters for different views, to add needed functionality.
## Todos:
* write some android tests;  
* add new functionality for users
## Authors
[Gorbunova Yelyzaveta](https://github.com/lizardlynx)  
[Maksym Marchenko](https://github.com/kertnique)
## License
MIT © [Android-app](https://github.com/Simple-message/Android-app)



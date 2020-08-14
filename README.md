<p align="center"><img width=600px src="https://upload.wikimedia.org/wikipedia/fr/thumb/4/45/MongoDB-Logo.svg/1200px-MongoDB-Logo.svg.png"></p>
<h2 align="center">MongoSK</h2>
<p align="center">MongoSK is an add-on for Skript for data gestion with a MongoDB database.</p>

<br />

## 🚀 Download and installation
You can download MongoSK by going to actions by [clicking here](https://github.com/Romitou/MongoSK/actions?query=actor:Romitou%20is:success%20%20).
Then, you can drag `MongoSK.jar` into your `plugins` folder making sure that [Skript](https://github.com/SkriptLang/Skript) is correctly installed.

## 📖 Example
This is an example of code. You can find more on the wiki.

```py
on script load:
    connect to the mongodb server "mongodb://127.0.0.1"

    # You also can include an username and a password.
    connect to the mongodb server "mongodb://romitou:examplePassword@127.0.0.1"

on join:
    # We search a document where "player" equals to the name of
    # the player, then set "last_join" value of the document to now.
    set mongo value "last_join" where "player" is name of player in collection "player_data" and database "mongosk" to now 

    # We search a document where "player" equals to the name of
    # the player, then set {_money} to "money" value of the document.    
    set {_money} to mongo value "money" where "player" is name of player in collection "player_data" and database "mongosk"

    send "You have %{_money}%$ on your account."

command info:
    trigger:
        if mongo is connected:
            send "The mongo client is connected."
            send "List of MongoDB databases:" and all mongodb databases
        else:
            send "The mongo client isn't connected."
```




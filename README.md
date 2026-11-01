<p align="center"><img width="500" alt="MongoDB logo" src="https://upload.wikimedia.org/wikipedia/en/thumb/5/5a/MongoDB_Fores-Green.svg/1920px-MongoDB_Fores-Green.svg.png"></p>
<h1 align="center">MongoSK</h1>
<p align="center">Store and query your Skript data in MongoDB, without blocking your server.</p>
<p align="center">
  <a href="https://github.com/Romitou/MongoSK/actions/workflows/build-test.yml"><img alt="Build status" src="https://github.com/Romitou/MongoSK/actions/workflows/build-test.yml/badge.svg"></a>
  <a href="https://github.com/Romitou/MongoSK/releases/latest"><img alt="Latest release" src="https://img.shields.io/github/v/release/Romitou/MongoSK"></a>
  <a href="https://discord.com/invite/6jeQkRcMkk"><img alt="Discord" src="https://img.shields.io/badge/Discord-join-5865F2"></a>
</p>

## ✨ Features

- **Your server keeps running:** requests run in the background, and only the script waits for the response.
- **Store your Skript types:** items, players, locations, blocks and many others are converted automatically.
- **Precise queries:** combine filters, sorts, projections, limits and skips.
- **Reliable by design:** requests have a timeout, and `last mongo error` tells you when one failed.

## 📦 Installation

1. Check the requirements below.
2. Download the [latest release](https://github.com/Romitou/MongoSK/releases/latest).
3. Put the jar in your `plugins` folder, then restart your server.

New to MongoDB? Start with [the wiki](https://github.com/Romitou/MongoSK/wiki/1.-MongoDB,-what-is-that?-%F0%9F%8D%83).

### Requirements

MongoSK supports the latest three stable versions of Skript and Minecraft, and needs Java 25.

| Skript | Minecraft        |
|--------|------------------|
| 2.17   | 26.1, 26.2, 26.3 |
| 2.16   | 26.1, 26.2       |
| 2.15   | 26.1             |

## ⚡ Quick start

This script loads the data of a player when they join, and creates it the first time. The client is kept in a memory variable (`{-mongo}`), as it cannot be saved by Skript:

```applescript
on script load:
    set {-mongo} to a new mongo client with connection string "mongodb://localhost:27017/?appName=myServer"

on join:
    set {_database} to mongo database "server" of {-mongo}
    set {_collection} to mongo collection "players" of {_database}

    set {_query} to a new mongo query with:
        collection: {_collection}
        filter: a new mongosk filter where field "uuid" is "%uuid of player%"
    execute mongo query {_query} and store the result in {_doc}

    if last mongo error is set:
        send "Cannot load your data: %last mongo error%" to player
        stop

    if {_doc} is not set:
        set {_doc} to a new mongo document
        set mongo value "uuid" of {_doc} to "%uuid of player%"
        set mongo value "coins" of {_doc} to 0
        insert mongo document {_doc} into collection {_collection}

    send "You have %mongo value "coins" of {_doc}% coins." to player

on script unload:
    close mongo client {-mongo}
```

## 🔍 Querying

A query is only a description: nothing is sent to MongoDB until you execute it. Only `collection` is required.

```applescript
command /top:
    trigger:
        set {_query} to a new mongo query with:
            collection: {players}
            filter: a new mongosk filter where field "coins" is greater than 100
            sort: mongo descending sort by field "coins"
            projection: mongo projection including fields "name" and "coins"
            limit: 10
            skip: 0
        execute mongo query {_query} and store the results in {_docs::*}
        loop {_docs::*}:
            send "%mongo value "name" of loop-value%: %mongo value "coins" of loop-value%" to player
```

A list variable receives every result, and a single variable only the first one. The variable is cleared when there is no result, or when the request fails.

## ✏️ Writing

```applescript
command /addcoins:
    trigger:
        set mongo value "coins" of {_doc} to 100
        update mongo document {_doc} in collection {players}
        upsert mongo document {_doc} into collection {players}
        remove mongo document {_doc} from collection {players}

        delete all mongo documents of query {_query}
        delete first mongo document of query {_query}
        replace first mongo document of query {_query} with {_doc}
```

## 🧰 Other requests

```applescript
command /stats:
    trigger:
        count mongo documents of query {_query} and store the result in {_count}
        count estimated mongo documents of collection {players} and store the result in {_estimation}
        execute mongo distinct on field "rank" of query {_query} and store the values in {_ranks::*}
        list mongo databases of {-mongo} and store the results in {_databases::*}
        list mongo collections of {_database} and store the results in {_collections::*}
        execute mongo command "{""ping"": 1}" in database {_database} and store the result in {_result}
```

Counting uses the collection, filter, limit and skip of the query. Distinct only uses its collection and filter: the sort, projection, limit and skip are ignored.

## ⏳ How requests work

Every effect that talks to MongoDB suspends your script like the `wait` effect does: the server keeps ticking, and the script continues on the main thread once the response is received. This has a few consequences:

- **After a request, you can no longer cancel the event**, as it has already finished.
- **A function cannot return a value after a request.** Skript refuses to load the script. Store the value in a variable instead.
- **A script calling a function does not wait for the requests of this function.** The caller continues right away.
- **The state of the game may change during a request.** For example, the player may have left, like after a `wait`.
- **Two scripts can run between a read and a write.** If both update the same document, one of the updates can be lost.

When a request fails or exceeds `timeouts.request`, the error is logged and the script continues. Check it with `last mongo error`, in the same event as the request. After a timeout, a write that was already sent may still be applied by MongoDB.

When the server stops, MongoSK waits for the pending requests, but suspended scripts are not resumed.

## ⚙️ Configuration

The options are described in `plugins/MongoSK/config.yml`. The main ones are:

| Option                   | Default | Description                                              |
|--------------------------|---------|----------------------------------------------------------|
| `skript-adapters.enabled`| `true`  | Stores the Skript types which MongoDB doesn't support.   |
| `allow-drop.database`    | `false` | Enables the effect dropping a database.                  |
| `allow-drop.collection`  | `false` | Enables the effect dropping a collection.                |
| `timeouts.request`       | `30000` | Maximum time to wait for a request, in milliseconds.     |
| `debug-mode`             | `false` | Displays details about the requests in the console.      |

## 🔄 Upgrading from 2.x

Scripts must be updated, because the syntaxes which blocked the server have been removed:

| Before                                         | Now                                                                             |
|------------------------------------------------|---------------------------------------------------------------------------------|
| `first mongo document ... of collection ...`   | `execute mongo query %query% and store the result in %variable%`                |
| `all mongo documents ... of collection ...`    | `execute mongo query %query% and store the results in %variable%`               |
| `delete all mongo documents ...`               | `delete all mongo documents of query %query%`                                   |
| `set first mongo document ... to %document%`   | `replace first mongo document of query %query% with %document%`                 |
| `all mongo fields distincted by ...`           | `execute mongo distinct on field %string% of query %query% and store the values in %variable%` |
| `mongo estimated document count of ...`        | `count estimated mongo documents of collection %collection% and store the result in %variable%` |
| `all mongo databases of ...`                   | `list mongo databases of %client% and store the results in %variable%`          |
| `all mongo collections of ...`                 | `list mongo collections of %database% and store the results in %variable%`      |
| `results of mongo command ...`                 | `execute mongo command %string% in database %database% and store the results in %variable%` |

## 💬 Documentation and support

- **Help:** [Discord server](https://discord.com/invite/6jeQkRcMkk) (fastest) or [GitHub Discussions](https://github.com/Romitou/MongoSK/discussions)
- **Bugs:** [GitHub issues](https://github.com/Romitou/MongoSK/issues)
- **Syntax documentation:** [skript-mc.fr](https://skript-mc.fr/documentation/mongosk/), [SkriptHub](https://skripthub.net/docs/?addon=MongoSK) and [skUnity](https://docs.skunity.com/syntax/search/addon:mongosk)

The wiki and these sites may still describe older versions. If they differ from this page, this page is right.

## 🛠️ Building from source

With Java 25:

```bash
./gradlew shadowJar
```

The jar is created in `build/libs`.

## 📄 License

MongoSK is licensed under the [GNU General Public License v3.0](LICENSE).

It includes the [MongoDB Java Driver](https://github.com/mongodb/mongo-java-driver), unmodified, under its own [license](https://github.com/mongodb/mongo-java-driver/blob/main/LICENSE.txt) and [third-party notices](https://github.com/mongodb/mongo-java-driver/blob/main/THIRD-PARTY-NOTICES).

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
	# Create a connection to the mongo host.
	connect to the mongo host "mongodb://127.0.0.1" with name "test"
	# This is optional, but is done to clarify the example.
	set {client} to client named "test"
	set {database} to mongo database named "mongosk" of {client}
	set {collection} to collection named "example" of {database}
	

command test:
	trigger:
		# We search a document where the value "points" is 10.
		set {_search} to document where "points" is 10 in {collection}
		# We check if a document has been found.
		if {_search} is not set:
			# Otherwise, we will create a new mongo example document.
			set {_doc} to new mongo document
		else:
			# Trying to broadcast a value of the document.
			broadcast "%value ""player"" of {_doc}%"
			loop list "example" of {_doc}:
				broadcast "%loop-value%"
			set {_doc} to {_search}
		set value "title" of {_doc} to "This is a test!"
		set value "points" of {_doc} to random integer between 1 and 10
		set list "example" of {_doc} to "a", "b" and "c"
		save document {_doc} in {collection}

# This is a small example of what can be done with this add-on.
# You can find more on the documentations!
```

## 📄 Licenses

MongoSK uses the [mongo-java-driver](https://github.com/mongodb/mongo-java-driver) library. You can find its license [here](https://github.com/mongodb/mongo-java-driver/blob/master/LICENSE.txt), and third party notices [here](https://github.com/mongodb/mongo-java-driver/blob/master/THIRD-PARTY-NOTICES), being included in the project. The source code of the driver is not modified.
MongoSK is however under the GNU General Public License v3.0 license, which can be found [here](https://github.com/Romitou/MongoSK/blob/master/LICENSE) and being included in the project.

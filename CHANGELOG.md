# Changelog

## 3.0.0

### Breaking changes
- **MongoSK now only supports the latest three stable versions of Skript (2.15 to 2.17) and Minecraft (26.1 to 26.3).**
  Older versions are not supported anymore: MongoSK 2.4.1, built for Skript 2.15 and Minecraft 1.21, remains the latest
  release for them.
- **Java 25 is now required**, like Minecraft 26.1.

Your scripts don't need any change: the syntaxes remain the same.

### Bug fixes
- The Mongo client expression no longer opens a new connection pool on each evaluation: an open client is reused for
  the same connection string.
- The distinct expression declared a wrong return type.
- `Decimal128` values are no longer truncated to integers, and array values are now deserialized.
- The estimated document count no longer throws an error when the request fails.
- Updating or removing a document without `_id` field no longer targets the documents with a null `_id`: these
  documents are skipped. Upserting such a document gives it a new ID instead of storing a null one.
- Removing an out-of-range list index no longer grows the list.
- The debug mode now really displays its messages.
- The command result expression no longer fails when the JSON string is not set.
- Players (stored with the `entity` codec) are now read back as offline players when they are not online, instead of
  being returned as raw documents. The stored format doesn't change, so filters on existing documents still match.
- The `damageCause` codec was never loaded, so damage causes could not be stored.
- Reading an unknown biome now reports an error instead of returning nothing.
- MongoSK is now disabled when its syntaxes cannot be registered, instead of staying half enabled.
- Slots can now be read back (as item stacks) instead of throwing an error.
- Objects that cannot be serialized by Skript are handled according to the `safe-data` option instead of being stored
  as empty documents.

### New features
- Mongo projections, to include or exclude fields of the documents returned by a query.
- `close mongo client %mongoskservers%` effect, to release the connections of a client.
- `last mongo error` expression, to know whether the last request failed and why.
- `timeouts.request` configuration option: the maximum time to wait for a request (30 seconds by default). Before, a
  request that never completed could freeze the server forever.

### Behavior changes
- The estimated document count now returns a `Long` (it doesn't change anything for your scripts).
- Evaluating the Mongo client expression twice with the same connection string returns the same client. Closing it
  closes it for all the scripts that use this connection string.
- Mongo command events are no longer built when no script listens to them.

### Internal
- The syntaxes are registered with the modern addon API of Skript, in a module listing them explicitly, instead of
  being found by scanning the plugin jar. All the code for older versions of Skript is removed.
- Java 25 is now the compiled target (required by Minecraft 26.1, the oldest supported version).
- `api-version` is set in the `plugin.yml`, whose version is now taken from the build.
- bStats is now an external library instead of an outdated copy of its class.
- Unit tests are run by the CI, which uses a fixed MongoDB version.

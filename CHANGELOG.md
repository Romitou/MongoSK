# Changelog

## 2.5.0

### Bug fixes
- **MongoSK syntaxes were not registered at all**: MongoSK used the addon of Skript instead of registering its own one,
  so none of its syntaxes were available. MongoSK now registers its own Skript addon again.
- MongoSK couldn't be loaded with Skript 2.10 to 2.14, as it used APIs only available since Skript 2.15. The newest
  APIs are now only used when they are available.
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
- Slots can now be read back (as item stacks) instead of throwing an error.
- Objects that cannot be serialized by Skript are handled according to the `safe-data` option instead of being stored
  as empty documents.

### New features
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
- The legacy sections, only used by versions of Skript older than 2.10 (which are not supported anymore), are removed.
- Java 17 is now the compiled target (required by Skript 2.10, the oldest supported version).
- `api-version` is set in the `plugin.yml`, whose version is now taken from the build.
- Unit tests are run by the CI, which uses a fixed MongoDB version.

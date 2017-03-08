# emp-mirror-cache
[![Build Status](https://travis-ci.org/missioncommand/emp3-mirror-cache.svg?branch=master)](https://travis-ci.org/missioncommand/emp3-mirror-cache)
[![Download](https://api.bintray.com/packages/missioncommand/maven/emp3-mirror-cache/images/download.svg)](https://bintray.com/missioncommand/maven/emp3-mirror-cache/_latestVersion)

## About

Geospatial data sharing and collaboration service.

## Building

### Prerequisites

__Software__

* [JDK 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [Maven 3.3.9](https://maven.apache.org/download.cgi)

### Build

```
$ ./gradlew build publishToMavenLocal
$ cd wildfly-swarm
$ mvn clean package
```

## Running

To launch the server:
```
$ cd wildfly-swarm
$ java -jar target/wildfly-swarm-swarm.jar
```

To launch the Java client:
```
$ ./gradlew :mirrorcache-client:run
```

To launch the Web client:

> visit: [127.0.0.1:8080/client](http://127.0.0.1:8080/client)
>
> _(NOTE: The Web client assumes a channel named 'inject' exists when performing certain functions.)_

To view server state:

> visit: [127.0.0.1:8080/status.html](http://127.0.0.1:8080/status.html)

## WebSocket Endpoints

Endpoint | Description
---------|------------
ws://127.0.0.1:8080/mirrorcache | All clients interested in participating in MirrorCache message exchanges must connect to this endpoint. Only ProtoMessage objects are recognized.

## REST endpoints

Endpoint | Description
---------|------------
[/rest/status](http://127.0.0.1:8080/rest/status) | This endpoint returns the current state of the system. Returned data currently includes all active WebSocket sessions, all created channels, all created channelGroups, and outbound queues sizes.
[/rest/channels](http://127.0.0.1:8080/rest/channels) | This endpoint returns a listing of channels currently available.
[/rest/channelgroups](http://127.0.0.1:8080/rest/channelgroups) | This endpoint returns a listing of channelGroups currently avaialble.
[/rest/queues](http://127.0.0.1:8080/rest/queues) | This endpoint returns detailed information about each outbound queue.
[/rest/cache/channel](http://127.0.0.1:8080/rest/cache/channel) | This endpoint returns detailed information about each channel cache.
[/rest/cache/channelgroup](http://127.0.0.1:8080/rest/cache/channelgroup) | This endpoint returns detailed information about each channelGroup cache.
[/rest/cache/entity](http://127.0.0.1:8080/rest/cache/entity) | This endpoint returns detailed information about each cached entity.


## API Notes

### ProtoMessage

A ProtoMessage is a [Protocol Buffer](https://developers.google.com/protocol-buffers/) defined type. It is declared as follows:
```protobuf
message ProtoMessage {
    string id                    = 1;
    map<string, string> property = 2;
    int32 priority               = 3;
    OneOfCommand command         = 4;
    ProtoPayload payload         = 5;
}
```

### Channel

A channel represents the path message data travels through. A client can open a channel and produce or consume message data (or both) on it by specificying a flow pattern. Once a channel has been opened data can be published to it or consumed from it.

__Channel Types:__

Type | Description
-----|------------
TEMPORARY | A temporary channel will exist only while the owning client is still connected. 
PERSISTENT | A persistent channel will remain available until it has been explicitly deleted.

__Channel Visibility:__

Visibility | Description
-----------|------------
PUBLIC | A public channel is made available for any other client to discover and subscribe to.
PRIVATE | A private channel can only be discovered and subscribed to after being invited.

__Channel Flow:__

Flow | Description
-----|------------
INGRESS | An ingress flow denotes a client who is only interested in consuming messages.
EGRESS | An egress flow denotes a client who is only interested in producing messages.
BOTH | A both flow denotes a client interested in producing and consuming.

### ChannelGroup

A ChannelGroup represents a collection of channels. Clients who join a channelGroup will receive published data from each channel participating in the channelGroup. The owner of a channelGroup can publish data to the channelGroup, this has the effect of broadcasting data to each participating channel in the channelGroup. A channelGroup will persist until the owning session is closed.

### Server Commands

Command | Properties
--------|-----------
CREATE_CHANNEL | <ul><li>status</li><li>channelName</li><li>type</li><li>visibility</li></ul>
DELETE_CHANNEL | <ul><li>status</li><li>channelName</li></ul>
FIND_CHANNELS | <ul><li>status</li><li>filter</li><li>channel</li></ul>
CHANNEL_OPEN | <ul><li>status</li><li>channelName</li><li>flow</li><li>filter</li></ul>
CHANNEL_CLOSE | <ul><li>status</li><li>channelName</li></ul>
CHANNEL_PUBLISH | <ul><li>status</li><li>channelName</li></ul>
CHANNEL_CACHE | <ul><li>status</li><li>channelName</li><li>entityId</li></ul>
CHANNEL_HISTORY | <ul><li>status</li><li>channelName</li><li>startTime</li><li>endTime</li><li>history</li></ul>
CREATE_CHANNELGROUP | <ul><li>status</li><li>channelGroupName</li></ul>
DELETE_CHANNELGROUP | <ul><li>status</li><li>channelGroupName</li></ul>
FIND_CHANNELGROUPS | <ul><li>status</li><li>filter</li><li>channelGroup</li></ul>
CHANNELGROUP_JOIN | <ul><li>status</li><li>channelGroupName</li></ul>
CHANNELGROUP_LEAVE | <ul><li>status</li><li>channelGroupName</li></ul>
CHANNELGROUP_ADD_CHANNEL | <ul><li>status</li><li>channelGroupName</li><li>channelName</li></ul>
CHANNELGROUP_REMOVE_CHANNEL | <ul><li>status</li><li>channelGroupName</li><li>channelName</li></ul>
CHANNELGROUP_PUBLISH | <ul><li>status</li><li>channelGroupName</li></ul>
CHANNELGROUP_CACHE | <ul><li>status</li><li>channelGroupName</li><li>entityId</li></ul>
CHANNELGROUP_HISTORY | <ul><li>status</li><li>channelGroupName</li><li>entityId</li><li>startTime</li><li>endTime</li><li>history</li></ul>

---

__MirrorCacheProductManager__

Manages the list(s) of overlays that are created as products, or feeds.

__MirrorCacheCollaborationManager__

Manages all the different sessions being used for collaboration. Each collaboration session is essentially a map with numerous overlays, a shared camera, and a list of connected users.

__MirrorCacheStorage__

Similar to EMP core Storage Manager where it has a store of all the unique data elements and understands all their relationships.

__ClientMessageQueue__

When messages the user is subscribed to are processed, the result will be inserted in a first in, first out queue. If an update occurs on a piece of data that was already in the queue, that item remains in the same queue position, but has the latest update applied. Because it will likely just be a reference to the actual item in the MirrorCacheStorage, not much needs to happen to client queue if it finds that the updated item already exists in the queue.

__MirrorCacheClient__

Stores the endpoint, name, uuid, DateTime connected, reference to its ClientMessageQueue, and other user related attributes.

## License

Apache 2.0
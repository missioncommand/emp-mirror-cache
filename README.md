# emp-mirror-cache
Geospatial data sharing and collaboration service


To build and launch server:
1. gradlew clean build publishToMavenLocal
2. cd wildfly-swarm
3. mvn clean package
4. java -jar target/wildfly-swarm-swarm.jar
5. keep console window open

To launch Java client:
1. gradlew :client:run

To launch Web client:
1. visit: [127.0.0.1:8080/client](http://127.0.0.1:8080/client)

To view server state:
1. visit: [127.0.0.1:8080/status.html](http://127.0.0.1:8080/status.html)

To view specific server states:
- status -  [/rest/status](http://127.0.0.1:8080/rest/status)
- channels - [/rest/channels](http://127.0.0.1:8080/rest/channels)
- channelGroups - [/rest/channelgroups](http://127.0.0.1:8080/rest/channelgroups)
- queues - [/rest/queues](http://127.0.0.1:8080/rest/queues)
- channel cache - [/rest/cache/channel](http://127.0.0.1:8080/rest/cache/channel)
- channelGroup cache - [/rest/cache/channelgroup](http://127.0.0.1:8080/rest/cache/channelgroup)
- entity cache - [/rest/cache/entity](http://127.0.0.1:8080/rest/cache/entity)

__NOTE: The Web client assumes a channel named 'inject' exists when performing certain functions.__

__TODO - maven and gradle configuration notes for di2e access__

---

## Channel
A channel represents the path message data travels through. A client can open a channel and produce or consume message data (or both) on it by specificying a flow pattern. Once a channel has been opened data can be published to it or consumed from it.

__Channel Types:__
- Temporary - A temporary channel will exist only while the owning client is still connected. 
- Persistent - A persistent channel will remain available until it has been explicitly deleted.

__Channel Visibility:__
- Public - A public channel is made available for any other client to discover and subscribe to.
- Private - A private channel can only be discovered and subscribed to after being invited.

__Channel Flow:__
- Ingress - An ingress flow denotes a client who is only interested in consuming messages.
- Egress - An egress flow denotes a client who is only interested in producing messages.
- BOTH - A both flow denotes a client interested in producing and consuming.

---

## ChannelGroup
A ChannelGroup represents a collection of channels. Clients who join a channelGroup will receive published data from each channel participating in the channelGroup. The owner of a channelGroup can publish data to the channelGroup, this has the effect of broadcasting data to each participating channel in the channelGroup. A channelGroup will persist until the owning session is closed.

---

## WebSocket Endpoints
All clients interested in participating in MirrorCache message exchanges must connect to the following endpoint. Only ProtoMessage objects will be recognized by this endpoint.
> __ws://127.0.0.1:8080/mirrorcache__

### ProtoMessage
A ProtoMessage is a Protocol Buffer defined type. A ProtoMessage object encapsulates the following information:
- id
- properties
- priority
- command
- payload

It is defined as follows:
```protobuf
message ProtoMessage {
    string id                    = 1;
    map<string, string> property = 2;
    int32 priority               = 3;
    OneOfCommand command         = 4;
    ProtoPayload payload         = 5;
}
```


### Server Commands
The server commands and the attributes they support/provide are provided below:
- CREATE_CHANNEL
  - status
  - channelName
  - type
  - visibility
- DELETE_CHANNEL
  - status
  - channelName
- FIND_CHANNELS
  - status
  - filter
  - channel
- CHANNEL_OPEN
  - status
  - channelName
  - flow
  - filter
- CHANNEL_CLOSE
  - status
  - channelName
- CHANNEL_PUBLISH
  - status
  - channelName
- CHANNEL_CACHE
  - status
  - channelName
  - entityId
- CHANNEL_HISTORY
  - status
  - channelName
  - startTime
  - endTime
  - history 
- CREATE_CHANNELGROUP
  - status
  - channelGroupName
- DELETE_CHANNELGROUP
  - status
  - channelGroupName
- FIND_CHANNELGROUPS
  - status
  - filter
  - channelGroup
- CHANNELGROUP_JOIN
  - status
  - channelGroupName
- CHANNELGROUP_LEAVE
  - status
  - channelGroupName
- CHANNELGROUP_ADD_CHANNEL
  - status
  - channelGroupName
  - channelName
- CHANNELGROUP_REMOVE_CHANNEL
  - status
  - channelGroupName
  - channelName
- CHANNELGROUP_PUBLISH
  - status
  - channelGroupName
- CHANNELGROUP_CACHE
  - status
  - channelGroupName
  - entityId
- CHANNELGROUP_HISTORY
  - status
  - channelGroupName
  - entityId
  - startTime
  - endTime
  - history


---

## REST endpoints
- [GET] __http://127.0.0.1:8080/rest/status__
  - This endpoint returns the current state of the system. Returned data currently includes all active WebSocket sessions, all created channels, all created channelGroups, and outbound queues sizes.

- [GET] __http://127.0.0.1:8080/rest/channels__
  - This endpoint returns a listing of channels currently available.
- [GET] __http://127.0.0.1:8080/rest/channelGroups__
  - This endpoint returns a listing of channelGroups currently avaialble.
- [GET] __http://127.0.0.1:8080/rest/queues__
  - This endpoint returns detailed information about each outbound queue.
- [GET] __http://127.0.0.1:8080/rest/cache/channel__
  - This endpoint returns detailed information about each channel cache.
- [GET] __http://127.0.0.1:8080/rest/cache/channelgroup__
  - This endpoint returns detailed information about each channelGroup cache.
- [GET] __http://127.0.0.1:8080/rest/cache/entity__
  - This endpoint returns detailed information about each cached entity.

---

### EMP3 API
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
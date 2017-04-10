self.importScripts('node_modules/google-closure-library/closure/goog/base.js');
self.importScripts('node_modules/google-protobuf/google-protobuf.js');
self.importScripts('cmapi_proto.js');

goog.require('proto.cmapi.AltitudeMode');
goog.require('proto.cmapi.CacheInfo');
goog.require('proto.cmapi.ChannelCacheOperation');
goog.require('proto.cmapi.ChannelCloseOperation');
goog.require('proto.cmapi.ChannelDeleteOperation');
goog.require('proto.cmapi.ChannelGroupAddChannelOperation');
goog.require('proto.cmapi.ChannelGroupCacheOperation');
goog.require('proto.cmapi.ChannelGroupCloseOperation');
goog.require('proto.cmapi.ChannelGroupDeleteOperation');
goog.require('proto.cmapi.ChannelGroupHistoryOperation');
goog.require('proto.cmapi.ChannelGroupInfo');
goog.require('proto.cmapi.ChannelGroupOpenOperation');
goog.require('proto.cmapi.ChannelGroupPublishOperation');
goog.require('proto.cmapi.ChannelGroupRemoveChannelOperation');
goog.require('proto.cmapi.ChannelHistoryOperation');
goog.require('proto.cmapi.ChannelInfo');
goog.require('proto.cmapi.ChannelOpenOperation');
goog.require('proto.cmapi.ChannelPublishOperation');
goog.require('proto.cmapi.Circle');
goog.require('proto.cmapi.Container');
goog.require('proto.cmapi.CreateChannelGroupOperation');
goog.require('proto.cmapi.CreateChannelOperation');
goog.require('proto.cmapi.DeleteChannelGroupOperation');
goog.require('proto.cmapi.DeleteChannelOperation');
goog.require('proto.cmapi.EntityInfo');
goog.require('proto.cmapi.FillPattern');
goog.require('proto.cmapi.FindChannelGroupsOperation');
goog.require('proto.cmapi.FindChannelsOperation');
goog.require('proto.cmapi.GeoColor');
goog.require('proto.cmapi.GeoFillStyle');
goog.require('proto.cmapi.GeoLabelStyle');
goog.require('proto.cmapi.GeoPosition');
goog.require('proto.cmapi.GeoRenderable');
goog.require('proto.cmapi.GeoStrokeStyle');
goog.require('proto.cmapi.GeoTimeSpan');
goog.require('proto.cmapi.GetClientInfoCommmand');
goog.require('proto.cmapi.HistoryInfo');
goog.require('proto.cmapi.Justification');
goog.require('proto.cmapi.LogEntry');
goog.require('proto.cmapi.MemberInfo');
goog.require('proto.cmapi.MilStdSymbol');
goog.require('proto.cmapi.Modifier');
goog.require('proto.cmapi.OneOfFeature');
goog.require('proto.cmapi.OneOfOperation');
goog.require('proto.cmapi.ProtoClientInfo');
goog.require('proto.cmapi.ProtoMessage');
goog.require('proto.cmapi.ProtoPayload');
goog.require('proto.cmapi.QueueInfo');
goog.require('proto.cmapi.Status');
goog.require('proto.cmapi.SymbolStandard');
goog.require('proto.cmapi.Typeface');

function getRandomCoordinate() {
    var position = new proto.cmapi.GeoPosition();
    position.setLatitude(10 + (3 * Math.random()) - 1.5);
    position.setLongitude(20 + (3 * Math.random()) - 1.5);
    position.setAltitude(Math.random() * 16000.0);

    return position;
}

function getModifierString(key) {
    return Object.keys(proto.cmapi.Modifier)[key.toString()]
}

function createSymbol(name) {
    var symbol = new proto.cmapi.MilStdSymbol();
    symbol.setGeoId("3bce4931-6c75-41ab-afe0-2ec108a30860");
    symbol.setName(name);
    symbol.setSymbolCode("SUGP-----------");
    symbol.setSymbolStandard(proto.cmapi.SymbolStandard.MIL_STD_2525C);
    symbol.setAltitudeMode(proto.cmapi.AltitudeMode.RELATIVE_TO_GROUND);
    symbol.addPosition(getRandomCoordinate());
    symbol.getModifierMap().set(getModifierString(proto.cmapi.Modifier.UNIQUE_DESIGNATOR_1), "Maintenance Recovery Theater");

    symbol.setFillStyle(new proto.cmapi.GeoFillStyle());
    symbol.getFillStyle().setFillColor(new proto.cmapi.GeoColor(0.7, 0, 0, 255));
    symbol.getFillStyle().setFillPattern(proto.cmapi.FillPattern.hatched);
    symbol.getFillStyle().setDescription("fillStyle description..");

    symbol.setStrokeStyle(new proto.cmapi.GeoStrokeStyle());
    symbol.getStrokeStyle().setStrokeColor(new proto.cmapi.GeoColor(1.0, 0, 255, 255));
    symbol.getStrokeStyle().setStipplingPattern(0xCCCC);
    symbol.getStrokeStyle().setStipplingFactor(2);
    symbol.getStrokeStyle().setStrokeWidth(5);

    symbol.setLabelStyle(new proto.cmapi.GeoLabelStyle());
    symbol.getLabelStyle().setColor(new proto.cmapi.GeoColor(1.0, 0, 255, 255));
    symbol.getLabelStyle().setOutlineColor(new proto.cmapi.GeoColor(1.0, 255, 0, 255));
    symbol.getLabelStyle().setJustification(proto.cmapi.Justification.CENTER);
    symbol.getLabelStyle().setTypeface(proto.cmapi.Typeface.BOLD);
    symbol.getLabelStyle().setFontFamily("Arial");
    symbol.getLabelStyle().setSize(14);

    symbol.setIsSelected(true);

    return symbol;
}

function createJoinMessage(channel) {
    var channelOpenCommand = new proto.cmapi.ChannelOpenCommand();
    channelOpenCommand.setChannelName("inject");
    channelOpenCommand.setFlow("EGRESS");
    channelOpenCommand.setFilter("*");
    
    var oneOfCommand = new proto.cmapi.OneOfCommand();
    oneOfCommand.setChannelOpen(channelOpenCommand);
    
    var message = new proto.cmapi.ProtoMessage();
    message.setId("d979ba32-4c59-4c56-9676-bfe48bbb2808");
    message.setCommand(oneOfCommand);

    return message;
}

function createPublishMessage(symbol) {
    var channelPublishCommand = new proto.cmapi.ChannelPublishCommand();
    channelPublishCommand.setChannelName("inject");
    
    var oneOfCommand = new proto.cmapi.OneOfCommand();
    oneOfCommand.setChannelPublish(channelPublishCommand);
    
    var payload = new proto.cmapi.ProtoPayload();
    payload.setType("org.cmapi.primitives.proto.CmapiProto$MilStdSymbol");
    payload.setData(symbol.serializeBinary());
    
    var message = new proto.cmapi.ProtoMessage();
    message.setId("423bdda7-8675-432c-b92a-8b2c6c8ddf15");
    message.setCommand(oneOfCommand);
    message.setPayload(payload);

    return message;
}

function serialize(protoMessage) {
    var data = protoMessage.serializeBinary();
    return data; // UInt8Array
}

function deserialize(data) {
    var protoMessage = proto.cmapi.ProtoMessage.deserializeBinary(data);
    return protoMessage;
}

function testTransport(numIter, url) {
    var socket = new WebSocket(url);
    
    socket.onopen = function() {
        var totalWrite = 0;
        var totalSend = 0;

        for (i = 0; i < numIter; i++) {

            if (i % 1000 == 0) { // update UI every 1000
                self.postMessage({'count': i});
            }

            var startWrite = performance.now();
            var message = createPublishMessage(createSymbol("Unit " + i));
            var data = serialize(message);
            var endWrite = performance.now();

            totalWrite += endWrite - startWrite;

            var startSend = performance.now();
            socket.send(data);
            var endSend = performance.now();

            totalSend += endSend - startSend;
        }
        self.postMessage({'count': numIter});

        var results =
            "<p> Total write time: " + (totalWrite).toFixed(2) + "ms<br/>"
            + "Total send time: " + (totalSend).toFixed(2) + "ms</p>"
        ;
        self.postMessage({'results': results});

        socket.close();
    };
}

function testSimple(numIter) {
    var totalWrite = 0;
    var totalRead = 0;

    for (i = 0; i < numIter; i++) {

        if (i % 1000 == 0) { // update UI every 1000
            self.postMessage({'count': i});
        }

        var startWrite = performance.now();
        var message = createPublishMessage(createSymbol("Unit " + i));
        var data = serialize(message);
        var endWrite = performance.now();

        totalWrite += endWrite - startWrite;

        var startRead = performance.now();
        var newMessage = deserialize(data);
        var endRead = performance.now();

        totalRead += endRead - startRead;
    }
    self.postMessage({'count': numIter});
    
    var results =
        "<p> Total write time: " + (totalWrite).toFixed(2) + "ms<br/>"
        + "Total read time: " + (totalRead).toFixed(2) + "ms</p>"
    ;
    self.postMessage({'results': results});
}

var socket;
var flagEcho = false;

function connect(url) {

    // check if we are already connected, disconnect then reconnect
    if (socket !== undefined) {
        socket.close();
        socket = undefined;
    }

    socket = new WebSocket(url);
    socket.binaryType = 'arraybuffer';
    socket.onopen = function() {
        //join 'inject' channel
        var joinReq = createJoinMessage("inject");

        var data = serialize(joinReq);
        socket.send(data);
    }
    socket.onmessage = function(e) {

        if (!(e.data instanceof ArrayBuffer)) { // if receiving text data
            self.postMessage({'text': e.data});
            
        } else { // if receiving binary data
            var message = deserialize(e.data);

            if (message.getCommand().getCommandCase() === proto.cmapi.OneOfCommand.CommandCase.CHANNEL_OPEN) {

            } else if (message.getCommand().getCommandCase() === proto.cmapi.OneOfCommand.CommandCase.CHANNEL_PUBLISH
                        || message.getCommand().getCommandCase() === proto.cmapi.OneOfCommand.CommandCase.CHANNEL_GROUP_PUBLISH) {
                //assume its a milSymbol..
                var symbol = proto.cmapi.MilStdSymbol.deserializeBinary(message.getPayload().getData());

                var symbolStr = symbol.toString();
                self.postMessage({'symbolStr': symbolStr, 'size': e.data.byteLength});
                
                // echo back out
                if (self.flagEcho) {
                    symbol.setName(symbol.getName() + " (ECHO)");
                    
                    var data = serialize(symbol);
                    socket.send(data);
                }

            } else {
                console.log("operation: " + message.getCommand().getCommandCase());
            }
        }
    }
}

function disconnect() {
    if (socket !== undefined) {
        socket.close();
    }
}

self.onmessage = function(e) {
    switch (e.data.cmd) {
        case 'testSimple':
            testSimple(e.data.numIter);
            break;
        case 'testTransport':
            testTransport(e.data.numIter, e.data.url);
            break;
        case 'connect':
            self.connect(e.data.url);
            break;
        case 'disconnect':
            self.disconnect();
            break;
        case 'flagEcho':
            self.flagEcho = e.data.flagEcho;
            break;
    };
}
package mil.emp3.mirrorcache.impl.request;

import org.cmapi.primitives.proto.CmapiProto.GetClientInfoCommmand;
import org.cmapi.primitives.proto.CmapiProto.OneOfCommand;
import org.cmapi.primitives.proto.CmapiProto.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.emp3.mirrorcache.Message;
import mil.emp3.mirrorcache.MessageDispatcher;
import mil.emp3.mirrorcache.MirrorCacheClient.ClientInfo;
import mil.emp3.mirrorcache.MirrorCacheException;
import mil.emp3.mirrorcache.MirrorCacheException.Reason;
import mil.emp3.mirrorcache.Priority;

public class GetClientInfoRequestProcessor extends BaseRequestProcessor<Message, ClientInfo> {
    static final private Logger LOG = LoggerFactory.getLogger(GetClientInfoRequestProcessor.class);
    
    final private MessageDispatcher dispatcher;
    
    public GetClientInfoRequestProcessor(MessageDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }
    
    @Override
    public ClientInfo executeSync(Message reqMessage) throws MirrorCacheException {
        if (reqMessage == null) {
            throw new IllegalStateException("reqMessage == null");
        }

        dispatcher.dispatchMessage(reqMessage.setPriority(Priority.MEDIUM));
        
        try {
            final Message resMessage = dispatcher.awaitResponse(reqMessage);
            
            final GetClientInfoCommmand command = resMessage.getOperation().as(OneOfCommand.class).getGetClientInfo();
            if (command.getStatus() == Status.SUCCESS) {

                final String clientId = command.getClientInfo().getClientId();
                
                return new ClientInfo() {
                    @Override public String clientId() { return clientId; }
                };
                
            } else {
                throw new MirrorCacheException(Reason.GET_CLIENT_INFO_FAILURE);
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.info(Thread.currentThread().getName() + " thread interrupted.");
        }
        
        throw new IllegalStateException(new MirrorCacheException(Reason.UNKNOWN));
    }

}

package mil.emp3.mirrorcache.client.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.emp3.mirrorcache.Member;
import mil.emp3.mirrorcache.channel.ChannelGroup;
import mil.emp3.mirrorcache.client.table.ChannelTableModel.ChannelEntry;
import mil.emp3.mirrorcache.impl.channel.ChannelGroupAdaptor;
import mil.emp3.mirrorcache.impl.channel.ClientChannel;
import mil.emp3.mirrorcache.impl.channel.ClientChannelGroup;

public class ChannelGroupTreeModel implements TreeModel {
    static final private Logger LOG = LoggerFactory.getLogger(ChannelGroupTreeModel.class);

    private List<ChannelGroupEntry> root;
    private EventListenerList listenerList;

    private boolean isReloading;
    
    public ChannelGroupTreeModel() {
        root         = new ArrayList<>();
        listenerList = new EventListenerList();
    }
    
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //

    public void setRoot(List<ChannelGroup> channelGroups) {
        final List<ChannelGroupEntry> newRoot = new ArrayList<>();
        
        for (ChannelGroup channelGroup : channelGroups) {
            newRoot.add(new ChannelGroupEntry(channelGroup));
        }
        
        this.root = newRoot;
        reload();
    }


    public void reload() {
        isReloading = true;
        
        final int n = getChildCount(root);
        final int[] childIdx = new int[n];
        final Object[] children = new Object[n];

        for (int i = 0; i < n; i++) {
            childIdx[i] = i;
            children[i] = getChild(root, i);
        }

        fireTreeStructureChanged(this, new Object[] { root }, childIdx, children);
        
        isReloading = false;
    }

    public boolean isReloading() {
        return isReloading;
    }
    
    private void fireTreeStructureChanged(Object source, Object[] path, int[] childIndices, Object[] children) {
        TreeModelEvent event = new TreeModelEvent(source, path, childIndices, children);
        TreeModelListener[] listeners = getTreeModelListeners();

        for (int i = listeners.length - 1; i >= 0; --i) {
            listeners[i].treeStructureChanged(event);
        }
    }

    public TreeModelListener[] getTreeModelListeners() {
        return (TreeModelListener[]) listenerList.getListeners(TreeModelListener.class);
    }
    

    @Override
    public void addTreeModelListener(TreeModelListener listener) {
        LOG.trace("addTreeModelListener() - listener: " + listener);
        listenerList.add(TreeModelListener.class, listener);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener listener) {
        LOG.trace("removeTreeModelListener() - listener: " + listener);
        listenerList.remove(TreeModelListener.class, listener);
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        // do nothing
    }

    @Override
    public Object getChild(Object parent, int index) {
        LOG.trace("getChild() - parent: " + parent.getClass().getName() + ", index: " + index);

        if (parent instanceof List<?>) {
            return ((List<?>) parent).get(index);

        } else if (parent instanceof ChannelGroupEntry) {
            if (index == 0) {
                return ((ChannelGroupEntry) parent).getChannels();
            } else if (index == 1) {
                return ((ChannelGroupEntry) parent).getMembers();
            } else {
                throw new IllegalStateException("unexpected index: " + index);
            }

        } else if (parent instanceof ChannelSet) {
            return ((ChannelSet) parent).getChannels().get(index);
            
        } else if (parent instanceof MemberSet) {
            return ((MemberSet) parent).getMembers().get(index);
            
        } else {
            throw new RuntimeException("unexpected parent: " + parent.getClass().getName());
        }
    }

    @Override
    public int getChildCount(Object parent) {
        LOG.trace("getChildCount() - parent: " + parent.getClass().getName());

        if (parent instanceof List<?>) {
            return ((List<?>) parent).size();
            
        } else if (parent instanceof ChannelGroupEntry) {
            return 2; //channels and members
            
        } else if (parent instanceof ChannelSet) {
            return ((ChannelSet) parent).getChannels().size();
            
        } else if (parent instanceof MemberSet) {
            return ((MemberSet) parent).getMembers().size();
            
        } else {
            return 0;
        }
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        LOG.trace("getIndexOfChild() - parent: " + parent.getClass().getName() + ", child: " + child.getClass().getName());
        
        if (parent instanceof List<?>) {
            final int index = ((List<?>) parent).indexOf(child);
            return index;
            
        } else if (parent instanceof ChannelGroupEntry) {
            
            if (child instanceof ChannelSet) {
                return 0;
                
            } else if (child instanceof MemberSet) {
                return 1;
                
            } else {
                throw new RuntimeException("unexpected child object: " + child.getClass().getName());
            }
        
        } else {
            return -1;
        }
    }

    @Override
    public Object getRoot() {
        return root;
    }

    @Override
    public boolean isLeaf(Object node) {
        LOG.trace("isLeaf() - node: " + node.getClass().getName());
        
        if (node instanceof List<?> || node instanceof ChannelGroupEntry) {
            return false;
            
        } else if (node instanceof ChannelSet || node instanceof MemberSet) {
            return false;
        }
        
        return true;
    }

    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //

    static public class ChannelGroupEntry extends ChannelGroupAdaptor {
        private boolean isJoinedSelected;
        
        final private ChannelSet channelSet;
        final private MemberSet memberSet;
        
        final private ChannelGroup channelGroup;

        public ChannelGroupEntry(ChannelGroup channelGroup) {
            super(channelGroup);
            
            this.channelGroup = channelGroup;
            this.channelSet   = new ChannelSet(((ClientChannelGroup) channelGroup).getChannels());
            this.memberSet    = new MemberSet(((ClientChannelGroup) channelGroup).getMembers());
            
            this.isJoinedSelected = channelGroup.isJoined();
        }

        public ChannelSet getChannels() {
            return channelSet;
        }
        public MemberSet getMembers() {
            return memberSet;
        }
        public ChannelGroup getChannelGroup() {
            return channelGroup;
        }
        
        public void setIsJoiendSelected(boolean isJoinedSelected) {
            this.isJoinedSelected = isJoinedSelected;
        }
        
        public boolean isJoinedSelected() {
            return isJoinedSelected;
        }
        
        @Override
        public boolean isJoined() {
            return isJoinedSelected;
        }
    }
    
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    
    static public class ChannelSet {
        final private List<ChannelEntry> channels;
        
        public ChannelSet(Set<ClientChannel> clientChannels) {
            this.channels = new ArrayList<>();
            for (ClientChannel clientChannel : clientChannels) {
                channels.add(new ChannelEntry(clientChannel));
            }
        }
        public List<ChannelEntry> getChannels() {
            return Collections.unmodifiableList(channels);
        }
    }
    
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    
    static public class MemberSet {
        final private List<Member> members;
        
        public MemberSet(Set<Member> clientChannels) {
            this.members = new ArrayList<>(clientChannels);
        }
        public List<Member> getMembers() {
            return Collections.unmodifiableList(members);
        }
    }

}
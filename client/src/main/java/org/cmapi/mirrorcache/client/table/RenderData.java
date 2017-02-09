package org.cmapi.mirrorcache.client.table;

import java.util.List;

import org.cmapi.mirrorcache.Member;
import org.cmapi.mirrorcache.channel.ChannelGroup;
import org.cmapi.mirrorcache.client.table.ChannelGroupTreeModel.ChannelSet;
import org.cmapi.mirrorcache.client.table.ChannelGroupTreeModel.MemberSet;
import org.cmapi.mirrorcache.client.table.ChannelTableModel.ChannelEntry;
import org.netbeans.swing.outline.RenderDataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RenderData implements RenderDataProvider {
    static final private Logger LOG = LoggerFactory.getLogger(RenderData.class);
    
    @Override
    public String getDisplayName(Object o) {
        LOG.trace("getDisplayName() - o: " + o.getClass().getName());
        
        if (o instanceof List<?>) {
            return "---";
            
        } else if (o instanceof ChannelGroup) {
            return "ChannelGroup: " + ((ChannelGroup) o).getName();
            
        } else if (o instanceof ChannelSet) {
            return "[channels] (" + ((ChannelSet) o).getChannels().size() + ")";

        } else if (o instanceof MemberSet) {
            return "[members] (" + ((MemberSet) o).getMembers().size() + ")";
            
        } else if (o instanceof ChannelEntry) {
            return ((ChannelEntry) o).getName();
            
        } else if (o instanceof Member) {
            return ((Member) o).getSessionId();
            
        } else {
            throw new RuntimeException("unsupported object type: " + o.getClass().getName());
        }
    }

    @Override
    public java.awt.Color getForeground(Object o) {
        return null;
//        return UIManager.getColor("controlShadow");
    }
    @Override
    public java.awt.Color getBackground(Object o) {
        return null;
    }
    @Override
    public javax.swing.Icon getIcon(Object o) {
        return null;
    }
    @Override
    public String getTooltipText(Object o) {
        return null;
    }
    @Override
    public boolean isHtmlDisplayName(Object o) {
        return false;
    }

}
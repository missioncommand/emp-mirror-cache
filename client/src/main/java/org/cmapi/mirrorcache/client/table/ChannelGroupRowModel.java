package org.cmapi.mirrorcache.client.table;

import org.cmapi.mirrorcache.client.table.ChannelGroupTreeModel.ChannelGroupEntry;
import org.cmapi.mirrorcache.client.table.ChannelTableModel.ChannelEntry;
import org.netbeans.swing.outline.RowModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelGroupRowModel implements RowModel {
    static final private Logger LOG = LoggerFactory.getLogger(ChannelGroupRowModel.class);
    
    static final public int COLUMN_CHANNELGROUP_JOIN   = 0;
    static final public int COLUMN_CHANNELGROUP_DELETE = 1;
    
    final private String[] columnNames = { "Join", "" };

    @Override
    public Class<?> getColumnClass(int column) {
        switch (column) {
            case COLUMN_CHANNELGROUP_JOIN: return String.class;
            case COLUMN_CHANNELGROUP_DELETE: return String.class;
            default: throw new RuntimeException("unexpected column: " + column);
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueFor(Object node, int column) {
        LOG.trace("getValueFor() - node: " + node.getClass().getName() + ", column: " + column);
        
        switch (column) {
            case COLUMN_CHANNELGROUP_JOIN: {
                if (node instanceof ChannelGroupEntry) {
                    return Boolean.valueOf(((ChannelGroupEntry) node).isJoinedSelected());
                }
                break;
            }
            case COLUMN_CHANNELGROUP_DELETE: {
                if (node instanceof ChannelGroupEntry || node instanceof ChannelEntry) {
                    return "X";
                }
                break;
            }
        }
        
        return null;
    }

    @Override
    public void setValueFor(Object node, int column, Object value) {
        LOG.trace("setValueFor() - node: " + node.getClass().getName() + ", column: " + column);
    
        if (node instanceof ChannelGroupEntry) {
            final ChannelGroupEntry entry = (ChannelGroupEntry) node;
            
            if (column == COLUMN_CHANNELGROUP_JOIN) {
                entry.setIsJoiendSelected((Boolean) value);
            }
        }
    }
    
    @Override
    public boolean isCellEditable(Object node, int column) {
        if (node instanceof ChannelGroupEntry) { // for deleting channelGroups
            return column == COLUMN_CHANNELGROUP_JOIN || column == COLUMN_CHANNELGROUP_DELETE;
            
        } else if (node instanceof ChannelEntry) { // for removing channels from channelGroups
            return column == COLUMN_CHANNELGROUP_DELETE;
        }
        return false;
    }

}
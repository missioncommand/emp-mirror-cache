package org.cmapi.mirrorcache.client.table;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.cmapi.mirrorcache.channel.Channel;
import org.cmapi.mirrorcache.impl.channel.ChannelAdaptor;

public class ChannelTableModel extends AbstractTableModel {

    static final public int COLUMN_CHANNEL_OPEN       = 0;
    static final public int COLUMN_CHANNEL_NAME       = 1;
    static final public int COLUMN_CHANNEL_VISIBILITY = 2;
    static final public int COLUMN_CHANNEL_TYPE       = 3;
    static final public int COLUMN_CHANNEL_RCV        = 4;
    static final public int COLUMN_CHANNEL_XMIT       = 5;
    static final public int COLUMN_CHANNEL_DELETE     = 6;
    
    private List<ChannelEntry> channels;
    
    final private String[] columnNames = {
        "Open", "Name", "Visibility", "Type", "RCV", "XMIT", ""
    };
    
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Class getColumnClass(int col) {
        return getValueAt(0, col).getClass();
    }
    
    @Override
    public void setValueAt(Object value, int row, int col) {
        if (col == COLUMN_CHANNEL_OPEN) {
            getEntryAt(row).setIsOpenSelected((Boolean) value);
            fireTableCellUpdated(row, col);
        }
    }
    @Override
    public Object getValueAt(int row, int col) {
        Object value = "---";
        
        final Channel channel = getChannels().get(row);
        switch (col) {
            case COLUMN_CHANNEL_OPEN:
                value = Boolean.valueOf(channel.isOpen());
                break;
            case COLUMN_CHANNEL_NAME:
                value = channel.getName();
                break;
            case COLUMN_CHANNEL_VISIBILITY:
                value = channel.getVisibility().name();
                break;
            case COLUMN_CHANNEL_TYPE:
                value = channel.getType().name();
                break;
            case COLUMN_CHANNEL_RCV:
                value = 0;
                break;
            case COLUMN_CHANNEL_XMIT:
                value = 0;
                break;
            case COLUMN_CHANNEL_DELETE:
                value = "X";
                break;
            default:
                throw new IllegalStateException("Unknown column: " + col);
        }
        return value;
    }
    
    @Override
    public int getRowCount() {
        return getChannels().size();
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
    public boolean isCellEditable(int row, int col) {
        return col == COLUMN_CHANNEL_OPEN || col == COLUMN_CHANNEL_DELETE;
    }
    
    public void addRow(Channel channel) {
        getChannels().add(new ChannelEntry(channel));
        fireTableRowsInserted(getChannels().size() - 1, getChannels().size() - 1);
    }
    public void removeRow(int row) {
        getChannels().remove(row);
        fireTableRowsDeleted(row, row);
    }
    
    public ChannelEntry getEntryAt(int row) {
        return getChannels().get(row);
    }
    
    private List<ChannelEntry> getChannels() {
        if (channels == null) {
            channels = new ArrayList<>();
        }
        return channels;
    }
    
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
    
    static public class ChannelEntry extends ChannelAdaptor {
        private boolean isOpenSelected;
        
        final private Channel channel;
        
        public ChannelEntry(Channel channel) {
            super(channel);
            this.channel = channel;
            this.isOpenSelected = channel.isOpen();
        }
        
        public Channel getChannel() {
            return channel;
        }
        
        public void setIsOpenSelected(boolean isOpenSelected) {
            this.isOpenSelected = isOpenSelected;
        }
        
        public boolean isOpenSelected() {
            return isOpenSelected;
        }
        
        @Override
        public boolean isOpen() {
            return isOpenSelected;
        }
    }
}

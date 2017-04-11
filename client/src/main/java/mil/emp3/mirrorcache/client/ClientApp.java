package mil.emp3.mirrorcache.client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.DefaultCaret;
import javax.swing.tree.TreePath;

import org.cmapi.primitives.GeoContainer;
import org.cmapi.primitives.GeoMilSymbol;
import org.cmapi.primitives.GeoPosition;
import org.cmapi.primitives.IGeoAltitudeMode.AltitudeMode;
import org.cmapi.primitives.IGeoMilSymbol;
import org.cmapi.primitives.IGeoMilSymbol.Modifier;
import org.cmapi.primitives.IGeoMilSymbol.SymbolStandard;
import org.netbeans.swing.outline.DefaultOutlineModel;
import org.netbeans.swing.outline.Outline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mil.emp3.mirrorcache.MirrorCacheClient;
import mil.emp3.mirrorcache.MirrorCacheException;
import mil.emp3.mirrorcache.Transport.TransportType;
import mil.emp3.mirrorcache.channel.Channel;
import mil.emp3.mirrorcache.channel.Channel.Flow;
import mil.emp3.mirrorcache.channel.ChannelCache;
import mil.emp3.mirrorcache.channel.ChannelGroup;
import mil.emp3.mirrorcache.channel.ChannelGroupCache;
import mil.emp3.mirrorcache.client.table.ButtonColumn;
import mil.emp3.mirrorcache.client.table.ChannelGroupRowModel;
import mil.emp3.mirrorcache.client.table.ChannelGroupTreeModel;
import mil.emp3.mirrorcache.client.table.ChannelGroupTreeModel.ChannelGroupEntry;
import mil.emp3.mirrorcache.client.table.ChannelTableModel;
import mil.emp3.mirrorcache.client.table.ChannelTableModel.ChannelEntry;
import mil.emp3.mirrorcache.client.table.RenderData;
import mil.emp3.mirrorcache.client.utils.Publisher;
import mil.emp3.mirrorcache.event.ChannelDeletedEvent;
import mil.emp3.mirrorcache.event.ChannelEventHandler;
import mil.emp3.mirrorcache.event.ChannelGroupDeletedEvent;
import mil.emp3.mirrorcache.event.ChannelGroupEventHandler;
import mil.emp3.mirrorcache.event.ChannelGroupPublishedEvent;
import mil.emp3.mirrorcache.event.ChannelGroupUpdatedEvent;
import mil.emp3.mirrorcache.event.ChannelPublishedEvent;
import mil.emp3.mirrorcache.event.ChannelUpdatedEvent;
import mil.emp3.mirrorcache.event.ClientEventHandlerAdapter;
import mil.emp3.mirrorcache.event.ClientMessageEvent;
import mil.emp3.mirrorcache.impl.Utils;
import mil.emp3.mirrorcache.spi.MirrorCacheClientProvider;
import mil.emp3.mirrorcache.spi.MirrorCacheClientProviderFactory;
import mil.emp3.mirrorcache.support.ItemTracker;

public class ClientApp {

    public static void main(String[] args) throws Exception {
        System.out.println("here we go\n\n");
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
//                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
//                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new ClientUI().init().setVisible(true);
            }
        });
    }
    
    private static IGeoMilSymbol createGeoMilSymbol(String name) {
        /*
         * Create symbol to publish
         */
        final GeoPosition pos = new GeoPosition();
        pos.setLatitude(1.2);
        pos.setLongitude(3.4);
        pos.setAltitude(10.);
        
        final GeoMilSymbol geoSymbol = new GeoMilSymbol();
        geoSymbol.setName(name);
        geoSymbol.setSymbolCode("SUGP---------XX");
        geoSymbol.setSymbolStandard(SymbolStandard.MIL_STD_2525C);
        geoSymbol.setAltitudeMode(AltitudeMode.RELATIVE_TO_GROUND);
        geoSymbol.getPositions().add(pos);
        geoSymbol.getModifiers().put(Modifier.UNIQUE_DESIGNATOR_1, "Maintenance Recovery Theater");
        
        return geoSymbol;
    }
    
    static public class ClientUI extends JFrame {
        static final private Logger LOG = LoggerFactory.getLogger(ClientUI.class);
        
        private JCheckBox jCheckBoxEcho;
        private JCheckBox jCheckBoxLogPayload;
        private JCheckBox jCheckBoxLogStep;
        private JCheckBox jCheckBoxLogAll;
        
        private JComboBox<Channel.Type> jComboBoxChannelType;
        private JComboBox<Channel.Visibility> jComboBoxChannelVisibility;
        private JComboBox<ChannelGroup> jComboBoxChannelGroups;
        private JComboBox<Channel> jComboBoxChannels;
        
        private JTextField jTextFieldAddress;

        private JTextField jTextFieldChannelSendCount;
        private JTextField jTextFieldChannelName;
        
        private JTextField jTextFieldChannelGroupSendCount;
        private JTextField jTextFieldChannelGroupName;
        
        private JButton jButtonConnect;
        private JButton jButtonDisconnect;
        private JButton jButtonClearLogs;
        
        private JButton jButtonChannelSend;
        private JButton jButtonChannelRefresh;
        private JButton jButtonChannelCreate;
        
        private JButton jButtonChannelGroupSend;
        private JButton jButtonChannelGroupRefresh;
        private JButton jButtonChannelGroupAddChannel;
        private JButton jButtonChannelGroupCreate;
        
        private JTextArea jTextAreaSendLog;
        private JTextArea jTextAreaReceiveLog;
        
        private JPanel jPanelNorth;
        private JPanel jPanelCenter;
        private JPanel jPanelSouth;
        
        private JPanel jPanelChannelNorth;
        private JPanel jPanelChannelSouth;
        
        private JPanel jPanelChannelGroupNorth;
        private JPanel jPanelChannelGroupSouth;
        private JPanel jPanelChannelGroupAddChannel;
        private JPanel jPanelChannelGroupCreate;
        
        private JTabbedPane jTabbedPaneChannelGroup;
        
        private JTable jTableChannels;
        private ChannelTableModel tableModelChannels;
        
        private Outline jTreeTableChannelGroups;
        private ChannelGroupTreeModel treeModelChannelGroups;
        
        private MirrorCacheClient client;
        private Handler handler;
        
        private ItemTracker tracker;
        
        public ClientUI() {
            super("MirrorCache Client (protobuf)");
            
            getJTextFieldAddress().setText("ws://127.0.0.1:8080/mirrorcache?agent=java");
        }
        public ClientUI init() {
            this.tracker = new ItemTracker(new ItemTracker.LogCallback() {
                @Override public void logStart(String msg) {
                    logReceive(msg, false);
                }
                @Override public void logEnd(String msg) {
                    logReceive(msg, false);
                }
            });
            
            
            //.. THIS
            this.setSize(1200, 420);
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setLocationRelativeTo(null);
//            this.setResizable(false);
            
            this.getContentPane().setLayout(new BorderLayout());
            
            this.getContentPane().add(getJPanelNorth(), BorderLayout.NORTH);
            this.getContentPane().add(getJPanelCenter(), BorderLayout.CENTER);
            this.getContentPane().add(getJPanelSouth(), BorderLayout.SOUTH);
            
            // setup channelGroup column widths
            for (int i = 0; i < getJTreeTableChannelGroups().getColumnModel().getColumnCount(); i++) {
                final TableColumn column = getJTreeTableChannelGroups().getColumnModel().getColumn(i);
                
                if (i == 1) {
                    int width = 40;
                    column.setPreferredWidth(width);
                    column.setMaxWidth(width);
                    column.setWidth(width);
                    
                } else if (i == 2) {
                    int width = 20;
                    column.setPreferredWidth(width);
                    column.setMaxWidth(width);
                    column.setWidth(width);
                }
            }
            
            //.. EVENTS
            getJButtonConnect().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    getJButtonConnect().setEnabled(false);
                    getJTextFieldAddress().setEditable(false);
                    
                    try {
                        final URI endpointUri = new URI(getJTextFieldAddress().getText());
                        
                        client = MirrorCacheClientProviderFactory.getClient(new MirrorCacheClientProvider.ClientArguments() {
                            @Override public TransportType transportType() {
                                return TransportType.WEBSOCKET;
                            }
                            @Override public URI endpoint() {
                                return endpointUri;
                            }
                        });
                        client.init();
                        client.connect();
                        
                        // register to receive events..
                        handler = new Handler();
                        client.on(ClientMessageEvent.TYPE, handler);
                        
                        getJButtonDisconnect().setEnabled(true);
                        getJButtonChannelSend().setEnabled(true);
                        getJButtonChannelGroupSend().setEnabled(true);
                        getJTextFieldChannelSendCount().setEditable(true);
                        getJTextFieldChannelGroupSendCount().setEditable(true);
                        getJButtonChannelRefresh().setEnabled(true);
                        getJButtonChannelGroupRefresh().setEnabled(true);
                        getJButtonChannelGroupCreate().setEnabled(true);
                        getJButtonChannelGroupAddChannel().setEnabled(true);
                        getJButtonChannelCreate().setEnabled(true);
                        getJTabbedPaneChannelGroup().setEnabled(true);
                        getJButtonClearLogs().setEnabled(true);
                        
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        getJButtonConnect().setEnabled(true);
                        getJTextFieldAddress().setEditable(true);
                    }
                }
            });
            
            getJButtonDisconnect().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    getJButtonDisconnect().setEnabled(false);
                    getJButtonChannelSend().setEnabled(false);
                    getJButtonChannelGroupSend().setEnabled(false);
                    getJTextFieldChannelSendCount().setEditable(false);
                    getJTextFieldChannelGroupSendCount().setEditable(false);
                    getJButtonChannelRefresh().setEnabled(false);
                    getJButtonChannelGroupRefresh().setEnabled(false);
                    getJButtonChannelGroupCreate().setEnabled(false);
                    getJButtonChannelGroupAddChannel().setEnabled(false);
                    getJButtonChannelCreate().setEnabled(false);
                    getJTabbedPaneChannelGroup().setEnabled(false);
                    getJButtonClearLogs().setEnabled(false);
                    
                    // remove channels from table
                    while (getTableModelChannels().getRowCount() > 0) {
                        getTableModelChannels().removeRow(0);    
                    }
                    
                    // remove channelGroups from table
                    getTreeModelChannelGroups().setRoot(Collections.<ChannelGroup>emptyList());
                    
                    try {
                        if (client != null) {
                            client.shutdown();
                        }
                        
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        
                    } finally {
                        client = null;
                        getJTextFieldAddress().setEditable(true);
                        getJButtonConnect().setEnabled(true);
                    }
                }
            });
            
            getJButtonChannelSend().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    getJButtonChannelSend().setEnabled(false);
                    
                    final int selectedRow = getJTableChannels().getSelectedRow();
                    if (selectedRow == -1) {
                        JOptionPane.showMessageDialog(ClientUI.this, "Please select a channel from the table.", "Information", JOptionPane.INFORMATION_MESSAGE);
                        getJButtonChannelSend().setEnabled(true);
                        return;
                    }
                    
                    final Channel channel = getTableModelChannels().getEntryAt(selectedRow).getChannel();
                    if (!channel.isOpen()) {
                        JOptionPane.showMessageDialog(ClientUI.this, "The selected channel is not open.", "Information", JOptionPane.INFORMATION_MESSAGE);
                        getJButtonChannelSend().setEnabled(true);
                        return;
                    }
                    
                    /*
                     * Send to selected channel.
                     */
                    final int sendCount = Integer.parseInt(getJTextFieldChannelSendCount().getText());
                    if (sendCount > 0) {
                        new Thread(new Publisher<IGeoMilSymbol>("ChannelPublisher", sendCount) {
                            @Override public void log(String msg) {
                                logSend(msg);
                            }
                            @Override public void publish(IGeoMilSymbol payload) throws MirrorCacheException {
                                channel.publish(payload.getGeoId().toString(), IGeoMilSymbol.class, payload);
                            }
                            @Override public IGeoMilSymbol constructPayload(String name) {
                                final IGeoMilSymbol symbol = createGeoMilSymbol(name);
                                return symbol;
                            }
                            @Override public void statusUpdate(final int count) {
                                getJTextFieldChannelSendCount().setText(Integer.toString(count));
                            }
                            @Override public void finished() {
                                if (hasException()) {
                                    getException().printStackTrace();
                                    JOptionPane.showMessageDialog(ClientUI.this, getException().getReason().getMsg() + "\n" + getException().getDetails(), "Error", JOptionPane.ERROR_MESSAGE);
                                }
                                getJTextFieldChannelSendCount().setText(Integer.toString(sendCount));
                                getJButtonChannelSend().setEnabled(true);
                            }
                        }).start();
                    }
                }
            });
            
            getJButtonChannelGroupSend().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    getJButtonChannelGroupSend().setEnabled(false);
                    
                    final int selectedRow = getJTreeTableChannelGroups().getSelectedRow();

                    if (selectedRow == -1) {
                        JOptionPane.showMessageDialog(ClientUI.this, "Please select a channelGroup from the table.", "Information", JOptionPane.INFORMATION_MESSAGE);
                        getJButtonChannelGroupSend().setEnabled(true);
                        return;
                    }
                    
                    // NOTE: gets the selected value
                    // getJTreeTableChannelGroups().getOutlineModel().getValueAt(selectedRow, 0);

                    final TreePath path = getJTreeTableChannelGroups().getOutlineModel().getLayout().getPathForRow(selectedRow);
                    if (path.getPathCount() != 2) {
                        JOptionPane.showMessageDialog(ClientUI.this, "Please select a channelGroup from the table.", "Information", JOptionPane.INFORMATION_MESSAGE);
                        getJButtonChannelGroupSend().setEnabled(true);
                        return;
                    }
                    final ChannelGroup channelGroup = ((ChannelGroupEntry) path.getPath()[1]).getChannelGroup();
                    
                    /*
                     * Send to selected channelGroup.
                     */
                    final int sendCount = Integer.parseInt(getJTextFieldChannelGroupSendCount().getText());
                    new Thread(new Publisher<IGeoMilSymbol>("ChannelGroupPublisher", sendCount) {
                        @Override public void log(String msg) {
                            logSend(msg);
                        }
                        @Override public void publish(IGeoMilSymbol payload) throws MirrorCacheException {
                            channelGroup.publish(payload.getGeoId().toString(), IGeoMilSymbol.class, payload);
                        }
                        @Override public IGeoMilSymbol constructPayload(String name) {
                            final IGeoMilSymbol symbol = createGeoMilSymbol(name);
                            return symbol;
                        }
                        @Override public void statusUpdate(final int count) {
                            getJTextFieldChannelGroupSendCount().setText(Integer.toString(count));
                        }
                        @Override public void finished() {
                            if (hasException()) {
                                getException().printStackTrace();
                                JOptionPane.showMessageDialog(ClientUI.this, getException().getReason().getMsg() + "\n" + getException().getDetails(), "Error", JOptionPane.ERROR_MESSAGE);
                            }
                            getJTextFieldChannelGroupSendCount().setText(Integer.toString(sendCount));
                            getJButtonChannelGroupSend().setEnabled(true);
                        }
                    }).start();
                }
            });
            
            getJButtonClearLogs().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    tracker.setReceiveCount(0);
                    getJTextAreaReceiveLog().setText("");
                    getJTextAreaSendLog().setText("");
                }
            });
            
            getJButtonChannelRefresh().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        final List<Channel> channels = client.findChannels("*");
                        
                        while (getTableModelChannels().getRowCount() > 0) {
                            getTableModelChannels().removeRow(0);    
                        }
                        
                        LOG.info("[ findChannels ]");
                        for (Channel channel : channels) {
                            LOG.info("\tchannel: " + channel);
                            
                            getTableModelChannels().addRow(channel);
                        }
                        
                    } catch (MirrorCacheException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(ClientUI.this, ex.getReason().getMsg() + "\n" + ex.getDetails(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            
            getJButtonChannelCreate().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    final String name = getJTextFieldChannelName().getText();
                    if (name.length() == 0) {
                        JOptionPane.showMessageDialog(ClientUI.this, "name.length() == 0", "Invalid Input", JOptionPane.PLAIN_MESSAGE);
                        return;
                    }
                    final Channel.Type type = getJComboBoxChannelType().getItemAt(getJComboBoxChannelType().getSelectedIndex());
                    final Channel.Visibility visibility = getJComboBoxChannelVisibility().getItemAt(getJComboBoxChannelVisibility().getSelectedIndex());
                    
                    try {
                        @SuppressWarnings("unused")
                        final Channel channel = client.createChannel(name, visibility, type);
                        getJTextFieldChannelName().setText("");
                        
                        getJButtonChannelRefresh().doClick();
                        
                    } catch (MirrorCacheException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(ClientUI.this, ex.getReason().getMsg() + "\n" + ex.getDetails(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            
            /*
             * To OPEN / CLOSE channelGroups.
             */
            getJTreeTableChannelGroups().getOutlineModel().addTableModelListener(new TableModelListener() {
                @Override public void tableChanged(TableModelEvent e) {
                    
                    if (!getTreeModelChannelGroups().isReloading()) {
                        if (e.getType() == TableModelEvent.UPDATE) {
                            final int row = e.getFirstRow();
                            final int col = e.getColumn();
                            
                            if (row != -1 && (col == TableModelEvent.ALL_COLUMNS || col == 1)) { // the 'open' column
                                
                                final ChannelGroupEntry entry = (ChannelGroupEntry) getJTreeTableChannelGroups().getOutlineModel().getValueAt(row, 0);
                                try {
                                    if (entry.isOpenSelected()) {
tmpChannelGroup = entry.getChannelGroup();//TODO remove me eventually
                                        entry.getChannelGroup().open();
                                        
                                        final ChannelGroupEventHandler handler = new ChannelGroupEventHandler() {
                                            @Override public void onChannelGroupPublishedEvent(ChannelGroupPublishedEvent event) {
                                                //increment RCV column in channelGroup table
                                                System.out.println("__onChannelGroupPublishedEvent()");
                                            }
                                            @Override public void onChannelGroupUpdatedEvent(ChannelGroupUpdatedEvent event) {
                                                System.out.println("__onChannelGroupUpdatedEvent()");
                                            }
                                            @Override public void onChannelGroupDeletedEvent(ChannelGroupDeletedEvent event) {
                                                System.out.println("__onChannelGroupDeletedEvent()");
                                            }
                                        };
                                        entry.getChannelGroup().on(ChannelGroupPublishedEvent.TYPE, handler);
                                        entry.getChannelGroup().on(ChannelGroupDeletedEvent.TYPE, handler);
                                        
                                    } else {
                                        entry.getChannelGroup().close();
                                    }
                                    
                                    getJButtonChannelGroupRefresh().doClick();
                                    
                                } catch (MirrorCacheException ex) {
                                    ex.printStackTrace();
                                    JOptionPane.showMessageDialog(ClientUI.this, ex.getReason().getMsg() + "\n" + ex.getDetails(), "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        }
                    }
                }
            });
            
            /*
             * To OPEN / CLOSE channels.
             */
            getTableModelChannels().addTableModelListener(new TableModelListener() {
                @Override public void tableChanged(TableModelEvent e) {
                    
                    if (e.getType() == TableModelEvent.UPDATE) {
                        final int row = e.getFirstRow();
                        final int col = e.getColumn();
                        
                        if (row != -1 && (col == TableModelEvent.ALL_COLUMNS || col == ChannelTableModel.COLUMN_CHANNEL_OPEN)) {
                            
                            final ChannelEntry entry = getTableModelChannels().getEntryAt(row);
                            try {
                                if (entry.isOpenSelected()) {
tmpChannel = entry.getChannel();//TODO remove me eventually
                                    entry.getChannel().open(Flow.BOTH, "*");
                                    
                                    final ChannelEventHandler handler = new ChannelEventHandler() {
                                        @Override public void onChannelPublishedEvent(ChannelPublishedEvent event) {
                                            //increment RCV column in channel table
                                            System.out.println("__onChannelPublishedEvent()");
                                        }
                                        @Override public void onChannelUpdatedEvent(ChannelUpdatedEvent event) {
                                            System.out.println("__onChannelUpdatedEvent()");
                                        }
                                        @Override public void onChannelDeletedEvent(ChannelDeletedEvent event) {
                                            System.out.println("__onChannelDeletedEvent() : payloadId=" + event.getPayloadId());
                                        }
                                    };
                                    entry.getChannel().on(ChannelPublishedEvent.TYPE, handler);
                                    entry.getChannel().on(ChannelDeletedEvent.TYPE, handler);
                                    
                                } else {
                                    entry.getChannel().close();
                                }
                            } catch (MirrorCacheException ex) {
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(ClientUI.this, ex.getReason().getMsg() + "\n" + ex.getDetails(), "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                }
            });
            
            
            /*
             * To delete channels.
             */
            new ButtonColumn(
                getJTableChannels(),
                new AbstractAction() {
                    @Override public void actionPerformed(ActionEvent e) {
                        final int row = Integer.valueOf(e.getActionCommand());
                        
                        final String channelName = (String) getTableModelChannels().getValueAt(row, ChannelTableModel.COLUMN_CHANNEL_NAME);
                        try {
                            client.deleteChannel(channelName);
                            getTableModelChannels().removeRow(row);
                            
                        } catch (MirrorCacheException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(ClientUI.this, ex.getReason().getMsg() + "\n" + ex.getDetails(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                },
                ChannelTableModel.COLUMN_CHANNEL_DELETE
            ).setMargin(new Insets(0, 0, 0, 0));
            
            /*
             * To delete channelGroups and channels from channelGroups.
             */
            new ButtonColumn(
                getJTreeTableChannelGroups(),
                new AbstractAction() {
                    @Override public void actionPerformed(ActionEvent e) {
                        final int row = Integer.valueOf(e.getActionCommand());
 
                        final Object value = getJTreeTableChannelGroups().getOutlineModel().getValueAt(row, 0);
                        
                        try {
                            /*
                             * delete channelGroup
                             */
                            if (value instanceof ChannelGroupEntry) {
                                final ChannelGroupEntry entry = (ChannelGroupEntry) value;
                                
                                final String channelGroupName = entry.getName();

                                client.deleteChannelGroup(channelGroupName);
                                getJButtonChannelGroupRefresh().doClick();
                                
                            /*
                             * delete channel from channelGroup
                             */
                            } else if (value instanceof ChannelEntry) {
                                final String channelName = ((ChannelEntry) value).getName();
                                
                                final TreePath path = getJTreeTableChannelGroups().getOutlineModel().getLayout().getPathForRow(row);
                                
                                final ChannelGroup channelGroup = ((ChannelGroupEntry) path.getPath()[1]).getChannelGroup();
                                channelGroup.removeChannel(channelName);
                                
                                getJButtonChannelGroupRefresh().doClick();
                            }
                        } catch (MirrorCacheException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(ClientUI.this, ex.getReason().getMsg() + "\n" + ex.getDetails(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                },
                2 //delete column
            ).setMargin(new Insets(0, 0, 0, 0));
            
            getJButtonChannelGroupRefresh().addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    
                    // clear out the comboboxes
                    ((DefaultComboBoxModel<ChannelGroup>) getJComboBoxChannelGroups().getModel()).removeAllElements();
                    ((DefaultComboBoxModel<Channel>) getJComboBoxChannels().getModel()).removeAllElements();
                    
                    final Future<List<ChannelGroup>> futureChannelGroups = client.findChannelGroupsAsync("*");
                    
                    new SwingWorker<List<ChannelGroup>, Void>() {
                        @Override protected List<ChannelGroup> doInBackground() throws Exception {
                            try {
                                final List<ChannelGroup> channelGroups = futureChannelGroups.get();
                                return channelGroups;
                          
                            } catch (ExecutionException | InterruptedException e) {
                                LOG.error(e.getMessage(), e);
                                if (e.getCause() instanceof MirrorCacheException) {
                                    JOptionPane.showMessageDialog(ClientUI.this, ((MirrorCacheException) e.getCause()).getReason().getMsg() + "\n" + ((MirrorCacheException) e.getCause()).getDetails(), "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                            return null;
                        }
                        @Override protected void done() {
                            try {
                                final List<ChannelGroup> channelGroups = get();
                                
                                LOG.info("[ findChannelGroups ]");
                                for (ChannelGroup channelGroup : channelGroups) {
                                    LOG.info("\tchannelGroup: " + channelGroup);
                                    
                                    ((DefaultComboBoxModel<ChannelGroup>) getJComboBoxChannelGroups().getModel()).addElement(channelGroup);
                                }
                                getTreeModelChannelGroups().setRoot(channelGroups);
                            
                                // update channels
                                new SwingWorker<List<Channel>, Void>() {
                                    @Override protected List<Channel> doInBackground() throws Exception {
                                        final List<Channel> channels = client.findChannels("*");
                                        return channels;
                                    }
                                    @Override protected void done() {
                                        try {
                                            for (Channel channel : get()) {
                                                ((DefaultComboBoxModel<Channel>) getJComboBoxChannels().getModel()).addElement(channel);
                                            }
                                        } catch (Exception e) {
                                            LOG.error(e.getMessage(), e);
                                        }
                                    }
                                }.execute();
                                
                            } catch (Exception e) {
                                LOG.error(e.getMessage(), e);
                            }
                        }
                    }.execute();
                }
            });
            
            getJButtonChannelGroupCreate().addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    final String channelGroupName = getJTextFieldChannelGroupName().getText();
                    if (channelGroupName.length() == 0) {
                        JOptionPane.showMessageDialog(ClientUI.this, "channelGroupName.length() == 0", "Invalid Input", JOptionPane.PLAIN_MESSAGE);
                        return;
                    }
                    getJTextFieldChannelGroupName().setText("");
                    
                    final Future<ChannelGroup> futureChannelGroup = client.createChannelGroupAsync(channelGroupName);
                    
                    new SwingWorker<Void, Void>() {
                        @Override protected Void doInBackground() throws Exception {
                            try {
                                @SuppressWarnings("unused")
                                final ChannelGroup channelGroup = futureChannelGroup.get();
                          
                            } catch (ExecutionException | InterruptedException e) {
                                LOG.error(e.getMessage(), e);
                                if (e.getCause() instanceof MirrorCacheException) {
                                    JOptionPane.showMessageDialog(ClientUI.this, ((MirrorCacheException) e.getCause()).getReason().getMsg() + "\n" + ((MirrorCacheException) e.getCause()).getDetails(), "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                            return null;
                        }
                        @Override protected void done() {
                            getJButtonChannelGroupRefresh().doClick();
                        }
                    }.execute();
                }
            });
            
            getJButtonChannelGroupAddChannel().addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    
                    final ChannelGroup channelGroup = (ChannelGroup) getJComboBoxChannelGroups().getSelectedItem();
                    final Channel channel           = (Channel) getJComboBoxChannels().getSelectedItem();
                    
                    try {
                        channelGroup.addChannel(channel.getName());
                        
                        getJButtonChannelGroupRefresh().doClick();
                        
                    } catch (MirrorCacheException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(ClientUI.this, ex.getReason().getMsg() + "\n" + ex.getDetails(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            
            return this;
        }
        
        private JPanel getJPanelNorth() {
            if (jPanelNorth == null) {
                jPanelNorth = new JPanel();
                jPanelNorth.setBorder(BorderFactory.createTitledBorder("WebSocket:"));

                jPanelNorth.add(getJTextFieldAddress());
                jPanelNorth.add(getJButtonConnect());
                jPanelNorth.add(getJButtonDisconnect());
            }
            return jPanelNorth;
        }
        
        private JPanel getJPanelCenter() {
            if (jPanelCenter == null) {
                jPanelCenter = new JPanel(new BorderLayout());

                // logs
                final JPanel jPanelLogTop = new JPanel(new BorderLayout());
                jPanelLogTop.setBorder(BorderFactory.createTitledBorder("Send Log:"));
                final JScrollPane jScrollPaneSendLog = new JScrollPane(getJTextAreaSendLog(), JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                jPanelLogTop.add(jScrollPaneSendLog, BorderLayout.CENTER);
                
                final JPanel jPanelLogBottom = new JPanel(new BorderLayout());
                jPanelLogBottom.setBorder(BorderFactory.createTitledBorder("Receive Log:"));
                final JScrollPane jScrollPaneReceiveLog = new JScrollPane(getJTextAreaReceiveLog(), JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                jPanelLogBottom.add(new JPanel(new FlowLayout(FlowLayout.RIGHT)) {{this.add(getJCheckBoxLogStep()); this.add(getJCheckBoxLogPayload()); this.add(getJCheckBoxLogAll());}}, BorderLayout.NORTH);
                jPanelLogBottom.add(jScrollPaneReceiveLog, BorderLayout.CENTER);
                
                final JSplitPane jSplitPaneLogs = new JSplitPane(JSplitPane.VERTICAL_SPLIT, jPanelLogTop, jPanelLogBottom);
                jSplitPaneLogs.setResizeWeight(0.35);

                final JPanel jPanelLogsSouth = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                jPanelLogsSouth.add(getJCheckBoxEcho());
                jPanelLogsSouth.add(getJButtonClearLogs());
                
                final JPanel jPanelLogs = new JPanel(new BorderLayout());
                jPanelLogs.add(jSplitPaneLogs, BorderLayout.CENTER);
                jPanelLogs.add(jPanelLogsSouth, BorderLayout.SOUTH);
                

                // channels
                final JPanel jPanelChannels = new JPanel(new BorderLayout());
                jPanelChannels.setBorder(BorderFactory.createTitledBorder("Channels"));
                jPanelChannels.add(getJPanelChannelNorth(), BorderLayout.NORTH);
                jPanelChannels.add(new JScrollPane(getJTableChannels()), BorderLayout.CENTER);
                jPanelChannels.add(getJPanelChannelSouth(), BorderLayout.SOUTH);
                
                
                // channelGroups
                final JPanel jPanelChannelGroups = new JPanel(new BorderLayout());
                jPanelChannelGroups.setBorder(BorderFactory.createTitledBorder("ChannelGroups"));
                jPanelChannelGroups.add(getJPanelChannelGroupNorth(), BorderLayout.NORTH);
                jPanelChannelGroups.add(new JScrollPane(getJTreeTableChannelGroups()), BorderLayout.CENTER);
                jPanelChannelGroups.add(getJPanelChannelGroupSouth(), BorderLayout.SOUTH);
                
                
                final JSplitPane jSplitPaneChannels = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, jPanelChannels, jPanelChannelGroups);
                jSplitPaneChannels.setDividerLocation(400);
                
                final JSplitPane jSplitPaneCenter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, jPanelLogs, jSplitPaneChannels);
                jSplitPaneCenter.setDividerLocation(400);
                
                jPanelCenter.add(jSplitPaneCenter, BorderLayout.CENTER);
            }
            return jPanelCenter;
        }

        //TODO remove me thanks
Channel tmpChannel;
ChannelGroup tmpChannelGroup;
        private JPanel getJPanelSouth() {
            if (jPanelSouth == null) {
                jPanelSouth = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                
                JButton channelCacheButton = null;
                jPanelSouth.add(channelCacheButton = new JButton("channel.cache()"));
                channelCacheButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        
                        try {
                            final ChannelCache cache = tmpChannel.cache();
                            System.out.println("\n__cache(" + cache.getEntityIds().size() + "): " + cache);
                            for (Integer entityId : cache.getEntityIds()) {
                                System.out.println("\tentityId: " + entityId);
                            }
                            
                            
                        } catch (MirrorCacheException ex) {
                            LOG.error(ex.getMessage(), ex);
                        }
                        
                    }
                });
                
                JButton channelGroupCacheButton = null;
                jPanelSouth.add(channelGroupCacheButton = new JButton("channelGroup.cache()"));
                channelGroupCacheButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        
                        try {
                            final ChannelGroupCache cache = tmpChannelGroup.cache();
                            System.out.println("\n__cache(" + cache.getEntityIds().size() + "): " + cache);
                            for (Integer entityId : cache.getEntityIds()) {
                                System.out.println("\tentityId: " + entityId);
                            }
                            
                            
                        } catch (MirrorCacheException ex) {
                            LOG.error(ex.getMessage(), ex);
                        }
                        
                    }
                });
            }
            return jPanelSouth;
        }
        
        private JPanel getJPanelChannelNorth() {
            if (jPanelChannelNorth == null) {
                jPanelChannelNorth = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                
                jPanelChannelNorth.add(getJTextFieldChannelSendCount());
                jPanelChannelNorth.add(getJButtonChannelSend());
                jPanelChannelNorth.add(new JLabel("|"));
                jPanelChannelNorth.add(getJButtonChannelRefresh());
            }
            return jPanelChannelNorth;
        }
        private JPanel getJPanelChannelSouth() {
            if (jPanelChannelSouth == null) {
                jPanelChannelSouth = new JPanel();
                jPanelChannelSouth.setBorder(BorderFactory.createTitledBorder("Create"));

                final GroupLayout layout = new GroupLayout(jPanelChannelSouth);
                layout.setAutoCreateGaps(true);
                layout.setAutoCreateContainerGaps(true);
                
                jPanelChannelSouth.setLayout(layout);
                
                final JLabel jLabelName       = new JLabel("Name:");
                final JLabel jLabelVisibility = new JLabel("Visibility:");
                final JLabel jLabelType       = new JLabel("Type:");
                
                layout.setHorizontalGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jLabelName)
                        .addComponent(getJTextFieldChannelName()))
                        
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jLabelVisibility)
                        .addComponent(getJComboBoxChannelVisibility()))
                        
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jLabelType)
                        .addComponent(getJComboBoxChannelType())
                        .addComponent(getJButtonChannelCreate()))
                );
                
                layout.linkSize(SwingConstants.HORIZONTAL, getJComboBoxChannelVisibility(), getJComboBoxChannelType(), getJButtonChannelCreate());
                
                layout.setVerticalGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabelName).addComponent(jLabelVisibility).addComponent(jLabelType))
                    
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(getJTextFieldChannelName()).addComponent(getJComboBoxChannelVisibility()).addComponent(getJComboBoxChannelType()))
                    
                    .addComponent(getJButtonChannelCreate())
                );
            }
            return jPanelChannelSouth;
        }
        
        private JPanel getJPanelChannelGroupNorth() {
            if (jPanelChannelGroupNorth == null) {
                jPanelChannelGroupNorth = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                
                jPanelChannelGroupNorth.add(getJTextFieldChannelGroupSendCount());
                jPanelChannelGroupNorth.add(getJButtonChannelGroupSend());
                jPanelChannelGroupNorth.add(new JLabel("|"));
                jPanelChannelGroupNorth.add(getJButtonChannelGroupRefresh());
            }
            return jPanelChannelGroupNorth;
        }
        private JPanel getJPanelChannelGroupSouth() {
            if (jPanelChannelGroupSouth == null) {
                jPanelChannelGroupSouth = new JPanel(new BorderLayout());

                jPanelChannelGroupSouth.add(getJTabbedPaneChannelGroup(), BorderLayout.CENTER);
            }
            return jPanelChannelGroupSouth;
        }
        
        private JPanel getJPanelChannelGroupCreate() {
            if (jPanelChannelGroupCreate == null) {
                jPanelChannelGroupCreate = new JPanel();
                
                final GroupLayout layout = new GroupLayout(jPanelChannelGroupCreate);
                layout.setAutoCreateGaps(true);
                layout.setAutoCreateContainerGaps(true);
                
                jPanelChannelGroupCreate.setLayout(layout);
                
                final JLabel jLabelName = new JLabel("Name:");
                
                layout.setHorizontalGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jLabelName))
                        
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(getJTextFieldChannelGroupName()))
                        
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(getJButtonChannelGroupCreate()))
                );
                
                layout.setVerticalGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabelName).addComponent(getJTextFieldChannelGroupName()).addComponent(getJButtonChannelGroupCreate()))
                );
            }
            return jPanelChannelGroupCreate;
        }
        
        private JPanel getJPanelChannelGroupAddChannel() {
            if (jPanelChannelGroupAddChannel == null) {
                jPanelChannelGroupAddChannel = new JPanel();
                
                final GroupLayout layout = new GroupLayout(jPanelChannelGroupAddChannel);
                layout.setAutoCreateGaps(true);
                layout.setAutoCreateContainerGaps(true);
                
                jPanelChannelGroupAddChannel.setLayout(layout);
                
                final JLabel jLabelChannelGroup = new JLabel("ChannelGroup:");
                final JLabel jLabelChannel      = new JLabel("Channel:");
                
                layout.setHorizontalGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jLabelChannelGroup))
                        
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(getJComboBoxChannelGroups()))
                        
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jLabelChannel))
                    
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(getJComboBoxChannels()))
                    
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(getJButtonChannelGroupAddChannel()))
                );
                
                layout.setVerticalGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabelChannelGroup)
                        .addComponent(getJComboBoxChannelGroups())
                        .addComponent(jLabelChannel)
                        .addComponent(getJComboBoxChannels())
                        .addComponent(getJButtonChannelGroupAddChannel())
                    )
                );
            }
            return jPanelChannelGroupAddChannel;
        }
        
        private JTabbedPane getJTabbedPaneChannelGroup() {
            if (jTabbedPaneChannelGroup == null) {
                jTabbedPaneChannelGroup = new JTabbedPane();
                jTabbedPaneChannelGroup.setEnabled(false);
                
                jTabbedPaneChannelGroup.add("Create", getJPanelChannelGroupCreate());
                jTabbedPaneChannelGroup.add("Add Channel", getJPanelChannelGroupAddChannel());
            }
            return jTabbedPaneChannelGroup;
        }
        
        private Outline getJTreeTableChannelGroups() {
            if (jTreeTableChannelGroups == null) {
                jTreeTableChannelGroups = new Outline() {
                    @Override public TableCellEditor getCellEditor(int row, int column) {
                        if (getValueAt(row, column) instanceof Boolean) {
                            return super.getDefaultEditor(Boolean.class);
                        } else {
                            return super.getCellEditor(row, column);
                        }
                    }
                    @Override public TableCellRenderer getCellRenderer(int row, int column) {
                        final Object value = getValueAt(row, column);
                        
                        if (value instanceof Boolean) {
                            return super.getDefaultRenderer(Boolean.class);
                            
                        } else if (value instanceof String && value.equals("X")) {
                            return super.getCellRenderer(row, column);
                            
                        } else {
                            return super.getDefaultRenderer(Object.class);
                        }
                    }
                };
                
                jTreeTableChannelGroups.setRootVisible(false);
                jTreeTableChannelGroups.setModel(DefaultOutlineModel.createOutlineModel(getTreeModelChannelGroups(), new ChannelGroupRowModel(), true));
                jTreeTableChannelGroups.setRenderDataProvider(new RenderData());
                jTreeTableChannelGroups.setColumnHidingAllowed(false);
                jTreeTableChannelGroups.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            }
            return jTreeTableChannelGroups;
        }
        
        private JTable getJTableChannels() {
            if (jTableChannels == null) {
                jTableChannels = new JTable(getTableModelChannels());
                jTableChannels.setPreferredScrollableViewportSize(new Dimension(500, 70));
                jTableChannels.setFillsViewportHeight(true);
                
                jTableChannels.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            }
            return jTableChannels;
        }
        
        private ChannelGroupTreeModel getTreeModelChannelGroups() {
            if (treeModelChannelGroups == null) {
                treeModelChannelGroups = new ChannelGroupTreeModel();
            }
            return treeModelChannelGroups;
        }
        
        private ChannelTableModel getTableModelChannels() {
            if (tableModelChannels == null) {
                tableModelChannels = new ChannelTableModel();
            }
            return tableModelChannels;
        }
        
        private JCheckBox getJCheckBoxEcho() {
            if (jCheckBoxEcho == null) {
                jCheckBoxEcho = new JCheckBox("Echo");
                jCheckBoxEcho.setEnabled(false);
            }
            return jCheckBoxEcho;
        }
        
        private JCheckBox getJCheckBoxLogPayload() {
            if (jCheckBoxLogPayload == null) {
                jCheckBoxLogPayload = new JCheckBox("Log Payload");
            }
            return jCheckBoxLogPayload;
        }
        
        private JCheckBox getJCheckBoxLogStep() {
            if (jCheckBoxLogStep == null) {
                jCheckBoxLogStep = new JCheckBox("Log Stepping", true);
            }
            return jCheckBoxLogStep;
        }
        
        private JCheckBox getJCheckBoxLogAll() {
            if (jCheckBoxLogAll == null) {
                jCheckBoxLogAll = new JCheckBox("Log All");
            }
            return jCheckBoxLogAll;
        }
        
        private JComboBox<Channel> getJComboBoxChannels() {
            if (jComboBoxChannels == null) {
                jComboBoxChannels = new JComboBox<>();
                jComboBoxChannels.setRenderer(new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                        final JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                        if (value instanceof Channel) {
                            label.setText(((Channel) value).getName());
                        }
                        return label;
                    }
                });
            }
            return jComboBoxChannels;
        }
        
        private JComboBox<ChannelGroup> getJComboBoxChannelGroups() {
            if (jComboBoxChannelGroups == null) {
                jComboBoxChannelGroups = new JComboBox<>();
                jComboBoxChannelGroups.setRenderer(new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                        final JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                        if (value instanceof ChannelGroup) {
                            label.setText(((ChannelGroup) value).getName());
                        }
                        return label;
                    }
                });
            }
            return jComboBoxChannelGroups;
        }
        
        private JComboBox<Channel.Type> getJComboBoxChannelType() {
            if (jComboBoxChannelType == null) {
                jComboBoxChannelType = new JComboBox<>(Channel.Type.values());
                jComboBoxChannelType.setSelectedItem(Channel.Type.TEMPORARY);
            }
            return jComboBoxChannelType;
        }
        
        private JComboBox<Channel.Visibility> getJComboBoxChannelVisibility() {
            if (jComboBoxChannelVisibility == null) {
                jComboBoxChannelVisibility = new JComboBox<>(Channel.Visibility.values());
                jComboBoxChannelVisibility.setSelectedItem(Channel.Visibility.PUBLIC);
            }
            return jComboBoxChannelVisibility;
        }
        
        private JTextField getJTextFieldAddress() {
            if (jTextFieldAddress == null) {
                jTextFieldAddress = new JTextField(40);
            }
            return jTextFieldAddress;
        }
        
        private JTextField getJTextFieldChannelName() {
            if (jTextFieldChannelName == null) {
                jTextFieldChannelName = new JTextField(7);
            }
            return jTextFieldChannelName;
        }
        
        private JTextField getJTextFieldChannelSendCount() {
            if (jTextFieldChannelSendCount == null) {
                jTextFieldChannelSendCount = new JTextField("1000000", 8);
                jTextFieldChannelSendCount.setEditable(false);
            }
            return jTextFieldChannelSendCount;
        }

        private JTextField getJTextFieldChannelGroupSendCount() {
            if (jTextFieldChannelGroupSendCount == null) {
                jTextFieldChannelGroupSendCount = new JTextField("1000000", 8);
                jTextFieldChannelGroupSendCount.setEditable(false);
            }
            return jTextFieldChannelGroupSendCount;
        }
        
        private JTextField getJTextFieldChannelGroupName() {
            if (jTextFieldChannelGroupName == null) {
                jTextFieldChannelGroupName = new JTextField(7);
            }
            return jTextFieldChannelGroupName;
        }

        private JButton getJButtonChannelGroupAddChannel() {
            if (jButtonChannelGroupAddChannel == null) {
                jButtonChannelGroupAddChannel = new JButton("Add");
                jButtonChannelGroupAddChannel.setEnabled(false);
            }
            return jButtonChannelGroupAddChannel;
        }
        
        private JButton getJButtonChannelGroupCreate() {
            if (jButtonChannelGroupCreate == null) {
                jButtonChannelGroupCreate = new JButton("Create");
                jButtonChannelGroupCreate.setEnabled(false);
            }
            return jButtonChannelGroupCreate;
        }
        
        private JButton getJButtonConnect() {
            if (jButtonConnect == null) {
                jButtonConnect = new JButton("Connect");
            }
            return jButtonConnect;
        }

        private JButton getJButtonDisconnect() {
            if (jButtonDisconnect == null) {
                jButtonDisconnect = new JButton("Disconnect");
                jButtonDisconnect.setEnabled(false);
            }
            return jButtonDisconnect;
        }
        
        private JButton getJButtonClearLogs() {
            if (jButtonClearLogs == null) {
                jButtonClearLogs = new JButton("Clear Logs");
                jButtonClearLogs.setEnabled(false);
            }
            return jButtonClearLogs;
        }
        
        private JButton getJButtonChannelSend() {
            if (jButtonChannelSend == null) {
                jButtonChannelSend = new JButton("Send");
                jButtonChannelSend.setEnabled(false);
            }
            return jButtonChannelSend;
        }
        
        private JButton getJButtonChannelRefresh() {
            if (jButtonChannelRefresh == null) {
                jButtonChannelRefresh = new JButton("Refresh");
                jButtonChannelRefresh.setEnabled(false);
            }
            return jButtonChannelRefresh;
        }
        
        private JButton getJButtonChannelGroupSend() {
            if (jButtonChannelGroupSend == null) {
                jButtonChannelGroupSend = new JButton("Send");
                jButtonChannelGroupSend.setEnabled(false);
            }
            return jButtonChannelGroupSend;
        }
        
        private JButton getJButtonChannelGroupRefresh() {
            if (jButtonChannelGroupRefresh == null) {
                jButtonChannelGroupRefresh = new JButton("Refresh");
                jButtonChannelGroupRefresh.setEnabled(false);
            }
            return jButtonChannelGroupRefresh;
        }
        
        private JButton getJButtonChannelCreate() {
            if (jButtonChannelCreate == null) {
                jButtonChannelCreate = new JButton("Create");
                jButtonChannelCreate.setEnabled(false);
            }
            return jButtonChannelCreate;
        }

        private JTextArea getJTextAreaReceiveLog() {
            if (jTextAreaReceiveLog == null) {
                jTextAreaReceiveLog = new JTextArea();
                jTextAreaReceiveLog.setEditable(false);
                
                final DefaultCaret caret = (DefaultCaret) jTextAreaReceiveLog.getCaret();
                caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
            }
            return jTextAreaReceiveLog;
        }

        private JTextArea getJTextAreaSendLog() {
            if (jTextAreaSendLog == null) {
                jTextAreaSendLog = new JTextArea();
                jTextAreaSendLog.setEditable(false);
                
                final DefaultCaret caret = (DefaultCaret) jTextAreaSendLog.getCaret();
                caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
            }
            return jTextAreaSendLog;
        }

        private void logSend(String s) {
            getJTextAreaSendLog().append(s + "\n");
        }
        private void logReceive(String s) {
            logReceive(s, true);
        }
        private void logReceive(String s, boolean includeCount) {
            getJTextAreaReceiveLog().append(includeCount ? ("[" + tracker.getReceiveCount() + "] " + s + "\n") : s + "\n");
        }
        
        // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
        // -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+- //
        
        private class Handler extends ClientEventHandlerAdapter {
            @Override
            public void onMessage(ClientMessageEvent event) {
                try {
                    final boolean isPublishMessage = event.getMessage().getOperation().name().equals("CHANNEL_PUBLISH")
                                                  || event.getMessage().getOperation().name().equals("CHANNEL_GROUP_PUBLISH");
                    
                    if (isPublishMessage || getJCheckBoxLogAll().isSelected()) {
                        tracker.track();
                        
                        if (event.getMessage().hasPayload()) {
                            final Object payload = event.getMessage().getPayload().getData();
                        
                            /*
                             * deserialize
                             */
                            if (!getJCheckBoxLogStep().isSelected() || tracker.getReceiveCount() % 1000 == 0) {
                                final String payloadStr;
                                
                                if (getJCheckBoxLogPayload().isSelected()) {
                                    if (payload instanceof GeoMilSymbol) {
                                        payloadStr = Utils.asString((GeoMilSymbol) payload);
                                        
                                    } else if (payload instanceof GeoContainer) {
                                        payloadStr = Utils.asString((GeoContainer) payload);
                                        
                                    } else if (payload instanceof com.google.protobuf.Message) {
                                        payloadStr = Utils.asString((com.google.protobuf.Message) payload);
                                        
                                    } else {
                                        payloadStr = payload.getClass().getName();
                                    }
                                    
                                } else {
                                    payloadStr = event.getMessage().getOperation().toString();
                                }
                                
                                logReceive("(payload) " + payloadStr);
                            }
        
                            /*
                             * echo back out
                             */
                            if (getJCheckBoxEcho().isSelected()) {
                                if (payload instanceof GeoMilSymbol) {
                                    final GeoMilSymbol symbol = (GeoMilSymbol) payload;
                                    symbol.setName(symbol.getName() + " (ECHO)");
                                    
                                    //TODO we want to access a cached list of channels/channelGroups
                                    //     and not make a request back to the server just to publish..
                                    if (event.getMessage().getOperation().name().equals("CHANNEL_GROUP_PUBLISH")) {
                                        
                                    } else if (event.getMessage().getOperation().name().equals("CHANNEL_PUBLISH")) {
                                        
                                    }
                                }
                            }
                            
                        } else {
                            //no 'payload'
                        }
                    }

                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
    }
        
}

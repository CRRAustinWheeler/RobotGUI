/*
 * DataStreamingModule allocations
 * PIDE<name> -- Error
 * PIDO<name> -- Output
 * 
 * SynchronizedRegisterArray allocations
 * PIDP<name> -- P
 * PIDCP<name> -- denotes control of P
 * PIDI<name> -- I
 * PIDCI<name> -- denotes control of I
 * PIDD<name> -- D
 * PIDCD<name> -- denotes control of D
 * PIDD<name> -- S
 * PIDCD<name> -- denotes control of S
 */
package robotgui;

import share.DSMListener;
import share.SRAListener;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Vector;
import share.DataStreamingModule;
import share.SynchronizedRegisterArray;

/**
 *
 * @author laptop
 */
public class PID extends javax.swing.JPanel implements DSMListener, SRAListener {

    DataStreamingModule dataStreamingModule;
    SynchronizedRegisterArray synchronizedRegisterArray;
    ArrayList<String> listItems = new ArrayList();

    /**
     * Creates new form PID
     */
    public PID() {
        initComponents();
    }

    public synchronized void init(DataStreamingModule dataStreamingModule,
            SynchronizedRegisterArray synchronizedRegisterArray) {
        jList1.setListData(new String[0]);
        this.dataStreamingModule = dataStreamingModule;
        this.synchronizedRegisterArray = synchronizedRegisterArray;
        dataStreamingModule.addDSMListener(this);
        synchronizedRegisterArray.addSRAListener(this);
    }

    @Override
    public synchronized void alertToDSMUpdates() {
    }

    @Override
    public synchronized void alertToNewStreams() {
        ArrayList<String> list = new ArrayList();
        String[] names = dataStreamingModule.getStreamNames();
        for (int i = 0; i < names.length; i++) {
            if (names[i].startsWith("PIDE")) {//E for Error
                for (int j = 0; j < names.length; j++) {
                    if (names[j].charAt(3) == 'O'//O for Output
                            && (names[j].substring(0, 3) + "E"
                            + names[j].substring(4)).matches(names[i])) {
                        list.add(names[i].substring(4));
                    }
                }
            }
        }
        for (int i = 0; i < listItems.size(); i++) {
            list.remove(listItems.get(i));
        }
        if (list.size() > 0) {
            listItems.addAll(list);
            int index = jList1.getSelectedIndex();
            jList1.setListData(listItems.toArray());
            if (index != -1) {
                jList1.setSelectedIndex(index);
            }
        }
    }

    @Override
    public void alertToSRAUpdates() {
        if (jList1.getSelectedIndex() != -1) {
            String string;
            if (synchronizedRegisterArray.get("PIDCP"
                    + listItems.get(jList1.getSelectedIndex())) == 0
                    && !(string = "" + synchronizedRegisterArray.get("PIDP"
                    + listItems.get(jList1.getSelectedIndex()))).
                    matches(p.getText())) {
                p.setText("" + synchronizedRegisterArray.get("PIDP"
                        + listItems.get(jList1.getSelectedIndex())));
                p.setForeground(new Color(160, 160, 160));
            }
            if (synchronizedRegisterArray.get("PIDCI"
                    + listItems.get(jList1.getSelectedIndex())) == 0
                    && !(string = "" + synchronizedRegisterArray.get("PIDI"
                    + listItems.get(jList1.getSelectedIndex()))).
                    matches(i.getText())) {
                i.setText("" + synchronizedRegisterArray.get("PIDI"
                        + listItems.get(jList1.getSelectedIndex())));
                i.setForeground(new Color(160, 160, 160));
            }
            if (synchronizedRegisterArray.get("PIDCD"
                    + listItems.get(jList1.getSelectedIndex())) == 0
                    && !(string = "" + synchronizedRegisterArray.get("PIDD"
                    + listItems.get(jList1.getSelectedIndex()))).
                    matches(d.getText())) {
                d.setText("" + synchronizedRegisterArray.get("PIDD"
                        + listItems.get(jList1.getSelectedIndex())));
                d.setForeground(new Color(160, 160, 160));
            }
            if (synchronizedRegisterArray.get("PIDCS"
                    + listItems.get(jList1.getSelectedIndex())) == 0
                    && !(string = "" + synchronizedRegisterArray.get("PIDS"
                    + listItems.get(jList1.getSelectedIndex()))).
                    matches(s.getText())) {
                s.setText("" + synchronizedRegisterArray.get("PIDS"
                        + listItems.get(jList1.getSelectedIndex())));
                s.setForeground(new Color(160, 160, 160));
            }

            graph1.removeAllStreams();
            graph1.addStream(
                    dataStreamingModule.getStream("PIDO"
                    + listItems.get(jList1.getSelectedIndex())),
                    Color.GREEN, 0.5, 0.4, false);
            graph1.addStream(
                    dataStreamingModule.getStream("PIDE"
                    + listItems.get(jList1.getSelectedIndex())),
                    Color.RED, 0.5,
                    0.5 / Double.parseDouble(es.getText()), true);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        graph1 = new robotgui.Graph();
        jLabel1 = new javax.swing.JLabel();
        p = new javax.swing.JTextField();
        i = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        d = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        s = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        es = new javax.swing.JTextField();

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(jList1);

        javax.swing.GroupLayout graph1Layout = new javax.swing.GroupLayout(graph1);
        graph1.setLayout(graph1Layout);
        graph1Layout.setHorizontalGroup(
            graph1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        graph1Layout.setVerticalGroup(
            graph1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 104, Short.MAX_VALUE)
        );

        jLabel1.setText("P:");

        p.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                pKeyReleased(evt);
            }
        });

        i.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                iKeyReleased(evt);
            }
        });

        jLabel2.setText("I:");

        d.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                dKeyReleased(evt);
            }
        });

        jLabel3.setText("D:");

        jLabel4.setText("Setpoint:");

        s.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                sKeyReleased(evt);
            }
        });

        jLabel5.setText("ErrorScale:");

        es.setText("10");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(p, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(i, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(d, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(s, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(es, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(graph1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(graph1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel5)
                                .addComponent(es, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel3)
                                .addComponent(d, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel4)
                                .addComponent(s, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel2)
                                .addComponent(i, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel1)
                                .addComponent(p, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void pKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pKeyReleased
        if (jList1.getSelectedIndex() != -1) {
            if (!p.getText().matches("")) {
                p.setForeground(new Color(60, 60, 60));
                synchronizedRegisterArray.setRegister("PIDCP"
                        + listItems.get(jList1.getSelectedIndex()), 1);
                synchronizedRegisterArray.setRegister("PIDP"
                        + listItems.get(jList1.getSelectedIndex()),
                        Double.parseDouble(p.getText()));
            } else {
                p.setForeground(new Color(160, 160, 160));
                synchronizedRegisterArray.setRegister("PIDCP"
                        + listItems.get(jList1.getSelectedIndex()), 0);
            }
        }
    }//GEN-LAST:event_pKeyReleased

    private void iKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_iKeyReleased
        if (jList1.getSelectedIndex() != -1) {
            if (!i.getText().matches("")) {
                i.setForeground(new Color(60, 60, 60));
                synchronizedRegisterArray.setRegister("PIDCI"
                        + listItems.get(jList1.getSelectedIndex()), 1);
                synchronizedRegisterArray.setRegister("PIDI"
                        + listItems.get(jList1.getSelectedIndex()),
                        Double.parseDouble(i.getText()));
            } else {
                i.setForeground(new Color(160, 160, 160));
                synchronizedRegisterArray.setRegister("PIDCI"
                        + listItems.get(jList1.getSelectedIndex()), 0);
            }
        }
    }//GEN-LAST:event_iKeyReleased

    private void dKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dKeyReleased
        if (jList1.getSelectedIndex() != -1) {
            if (!d.getText().matches("")) {
                d.setForeground(new Color(60, 60, 60));
                synchronizedRegisterArray.setRegister("PIDCD"
                        + listItems.get(jList1.getSelectedIndex()), 1);
                synchronizedRegisterArray.setRegister("PIDD"
                        + listItems.get(jList1.getSelectedIndex()),
                        Double.parseDouble(d.getText()));
            } else {
                d.setForeground(new Color(160, 160, 160));
                synchronizedRegisterArray.setRegister("PIDCD"
                        + listItems.get(jList1.getSelectedIndex()), 0);
            }
        }
    }//GEN-LAST:event_dKeyReleased

    private void sKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_sKeyReleased
        if (jList1.getSelectedIndex() != -1) {
            if (!s.getText().matches("")) {
                s.setForeground(new Color(60, 60, 60));
                synchronizedRegisterArray.setRegister("PIDCS"
                        + listItems.get(jList1.getSelectedIndex()), 1);
                synchronizedRegisterArray.setRegister("PIDS"
                        + listItems.get(jList1.getSelectedIndex()),
                        Double.parseDouble(s.getText()));
            } else {
                s.setForeground(new Color(160, 160, 160));
                synchronizedRegisterArray.setRegister("PIDCS"
                        + listItems.get(jList1.getSelectedIndex()), 0);
            }
        }
    }//GEN-LAST:event_sKeyReleased
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField d;
    private javax.swing.JTextField es;
    private robotgui.Graph graph1;
    private javax.swing.JTextField i;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JList jList1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField p;
    private javax.swing.JTextField s;
    // End of variables declaration//GEN-END:variables
}
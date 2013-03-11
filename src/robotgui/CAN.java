/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotgui;

import communications.DSMListener;
import communications.DataStream;
import communications.DataStreamingModule;
import communications.SRAListener;
import communications.SynchronizedRegisterArray;
import java.awt.Color;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author laptop
 */
public class CAN extends javax.swing.JPanel implements DSMListener, SRAListener {

    private DataStreamingModule dataStreamingModule;
    private SynchronizedRegisterArray synchronizedRegisterArray;
    private int[] comboBoxJaguars = new int[0];
    private long lastLabelRefresh = System.currentTimeMillis();

    /**
     * Creates new form CAN
     */
    public CAN() {
        initComponents();
    }

    public void init(DataStreamingModule dataStreamingModule,
            SynchronizedRegisterArray synchronizedRegisterArray) {
        this.dataStreamingModule = dataStreamingModule;
        this.synchronizedRegisterArray = synchronizedRegisterArray;
        dataStreamingModule.addDSMListener(this);
        synchronizedRegisterArray.addSRAListener(this);
    }

    @Override
    public synchronized void alertToDSMUpdates() {
        refreshLabels();
    }

    @Override
    public synchronized void alertToNewStreams() {
        refeshComboBox();
    }

    @Override
    public synchronized void alertToSRAUpdates() {
        refreshLabels();
    }

    private void refreshGraph() {
        /*
         * Stream Names
         * 
         * Voltage Output: "CANJAGUAROV"
         * Current: "CANJAGUARI"
         * Voltage From Battery: "CANJAGUARIV"
         * Tempature: "CANJAGUART"
         */
        if (jComboBox1.getSelectedIndex() != -1) {
            graph1.removeAllStreams();
            graph1.addStream(
                    dataStreamingModule.getStream("CANJAGUAROV"
                    + comboBoxJaguars[jComboBox1.getSelectedIndex()]),
                    Color.GREEN, 0.5, 1.0 / 30.0, true);
            graph1.addStream(
                    dataStreamingModule.getStream("CANJAGUARI"
                    + comboBoxJaguars[jComboBox1.getSelectedIndex()]),
                    Color.BLUE, 0.0, 1.0 / 40.0, false);
            graph1.addStream(
                    dataStreamingModule.getStream("CANJAGUARIV"
                    + comboBoxJaguars[jComboBox1.getSelectedIndex()]),
                    Color.ORANGE, 0.0, 1.0 / 15.0, false);
            graph1.addStream(
                    dataStreamingModule.getStream("CANJAGUART"
                    + comboBoxJaguars[jComboBox1.getSelectedIndex()]),
                    Color.RED, 0.0, 1.0 / 110.0, false);
            graph1.addStream(
                    dataStreamingModule.getStream("CANJAGUARX"
                    + comboBoxJaguars[jComboBox1.getSelectedIndex()]),
                    Color.CYAN, 0.5, 0.49, false);
        }
    }

    private void refeshComboBox() {
        ArrayList<Integer> list = new ArrayList();
        String[] names = dataStreamingModule.getStreamNames();
        for (int i = 0; i < names.length; i++) {
            if (names[i].startsWith("CANJAGUAROV")) {
                list.add(new Integer(names[i].substring(11)));
            }
        }
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < comboBoxJaguars.length; j++) {
                if (list.get(i).intValue() == comboBoxJaguars[j]) {
                    list.remove(i);
                }
            }
        }
        if (list.size() > 0) {
            int[] newComboBoxJaguarsList =
                    new int[comboBoxJaguars.length + list.size()];
            for (int i = 0; i < newComboBoxJaguarsList.length; i++) {
                if (i < comboBoxJaguars.length) {
                    newComboBoxJaguarsList[i] = comboBoxJaguars[i];
                } else {
                    newComboBoxJaguarsList[i] =
                            list.get(i - comboBoxJaguars.length).intValue();
                }
            }
            comboBoxJaguars = newComboBoxJaguarsList;

            ArrayList<String> jagNames = new ArrayList<String>();
            for (int i = 0; i < comboBoxJaguars.length; i++) {
                jagNames.add("Jaguar " + comboBoxJaguars[i]);
            }
            int index = jComboBox1.getSelectedIndex();
            jComboBox1.setModel(new DefaultComboBoxModel(jagNames.toArray()));
            if (index != -1) {
                jComboBox1.setSelectedIndex(index);
            }
        } else {
        }
    }

    private synchronized void refreshLabels() {
        if (isVisible() && lastLabelRefresh + 2000 < System.currentTimeMillis() && jComboBox1.getSelectedIndex() != -1) {
            lastLabelRefresh = System.currentTimeMillis();
            int index = jComboBox1.getSelectedIndex();
            if (index!=-1) {
                int jID = comboBoxJaguars[index];
                if (jID != -1) {
                    hardwareVersion.setText("Hardware Version: "
                            + synchronizedRegisterArray.get("CANJAGUARHV" + jID));
                    firmwareVersion.setText("Firmware Version: "
                            + synchronizedRegisterArray.get("CANJAGUARFV" + jID));
                    jaguarID.setText("Jaguar ID: " + jID);
                    
                    DataStream ds;
                    ds = dataStreamingModule.getStream("CANJAGUARX" + jID);
                    if (ds != null) {
                        setpoint.setText("Setpoint: "
                                + (Math.round(ds.getLastPacket().val
                                * 100d) / 100d));
                    }
                    ds = dataStreamingModule.getStream("CANJAGUARI" + jID);
                    if (ds != null) {
                        current.setText("Current: "
                                + (Math.round(ds.getLastPacket().val
                                * 100d) / 100d));
                    }
                    ds = dataStreamingModule.getStream("CANJAGUAROV" + jID);
                    if (ds != null) {
                        outputVoltage.setText("Output Voltage: "
                                + (Math.round(ds.getLastPacket().val
                                * 100d) / 100d));
                    }
                    ds = dataStreamingModule.getStream("CANJAGUARIV" + jID);
                    if (ds != null) {
                        inputVoltage.setText("Input Voltage: "
                                + (Math.round(ds.getLastPacket().val
                                * 100d) / 100d));
                    }
                    ds = dataStreamingModule.getStream("CANJAGUART" + jID);
                    if (ds != null) {
                        tempature.setText("Tempature: "
                                + (Math.round(ds.getLastPacket().val
                                * 100d) / 100d));
                    }
                }
            }
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

        jPanel1 = new javax.swing.JPanel();
        hardwareVersion = new javax.swing.JLabel();
        firmwareVersion = new javax.swing.JLabel();
        jaguarID = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        setpoint = new javax.swing.JLabel();
        current = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        outputVoltage = new javax.swing.JLabel();
        inputVoltage = new javax.swing.JLabel();
        tempature = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        graph1 = new robotgui.Graph();

        hardwareVersion.setText("Hardware Version:");

        firmwareVersion.setText("Firmware Version:");

        jaguarID.setText("Jaguar ID:");

        jPanel3.setBackground(new java.awt.Color(90, 90, 89));

        setpoint.setForeground(java.awt.Color.cyan);
        setpoint.setText("Setpoint:");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(setpoint, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(setpoint))
        );

        current.setForeground(java.awt.Color.blue);
        current.setText("Current:");

        jPanel2.setBackground(new java.awt.Color(90, 90, 89));

        outputVoltage.setForeground(java.awt.Color.green);
        outputVoltage.setText("Output Voltage:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(outputVoltage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(outputVoltage))
        );

        inputVoltage.setForeground(java.awt.Color.orange);
        inputVoltage.setText("Input Voltage:");

        tempature.setForeground(java.awt.Color.red);
        tempature.setText("Tempature:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jaguarID, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(firmwareVersion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(hardwareVersion, javax.swing.GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE)
            .addComponent(current, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(inputVoltage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(tempature, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(hardwareVersion)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(firmwareVersion)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jaguarID)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(current)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(inputVoltage)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tempature))
        );

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "test" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout graph1Layout = new javax.swing.GroupLayout(graph1);
        graph1.setLayout(graph1Layout);
        graph1Layout.setHorizontalGroup(
            graph1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 53, Short.MAX_VALUE)
        );
        graph1Layout.setVerticalGroup(
            graph1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(graph1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(graph1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 12, Short.MAX_VALUE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        new Thread(new Runnable() {//the new thread keeps Unity from crashing
            @Override
            public void run() {
                refreshLabels();
                refreshGraph();
            }
        }).start();

    }//GEN-LAST:event_jComboBox1ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel current;
    private javax.swing.JLabel firmwareVersion;
    private robotgui.Graph graph1;
    private javax.swing.JLabel hardwareVersion;
    private javax.swing.JLabel inputVoltage;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel jaguarID;
    private javax.swing.JLabel outputVoltage;
    private javax.swing.JLabel setpoint;
    private javax.swing.JLabel tempature;
    // End of variables declaration//GEN-END:variables
}

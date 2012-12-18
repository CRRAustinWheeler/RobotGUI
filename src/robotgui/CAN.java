/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotgui;

import communications.DSMListener;
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
    private int[] comboBoxJaguars;
    //jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

    /**
     * Creates new form CAN
     */
    public CAN() {
        initComponents();
    }

    public synchronized void init(DataStreamingModule dataStreamingModule,
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
        if (jComboBox1.getSelectedIndex() != -1) {
            graph1.removeAllStreams();
            graph1.addStream(
                    dataStreamingModule.getStream("CANJAGUAROV"
                    + comboBoxJaguars[jComboBox1.getSelectedIndex()]),
                    Color.RED, 0.0, 1 / 15, false);
            graph1.addStream(
                    dataStreamingModule.getStream("CANJAGUARI"
                    + comboBoxJaguars[jComboBox1.getSelectedIndex()]),
                    Color.BLUE, 0.0, 1 / 40, false);
            graph1.addStream(
                    dataStreamingModule.getStream("CANJAGUARIV"
                    + comboBoxJaguars[jComboBox1.getSelectedIndex()]),
                    Color.ORANGE, 0.0, 1 / 15, false);
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
                jagNames.add("Jaguar " + i);
            }
            int index = jComboBox1.getSelectedIndex();
            jComboBox1.setModel(new DefaultComboBoxModel(jagNames.toArray()));
            if (index != -1) {
                jComboBox1.setSelectedIndex(index);
            }
        }
    }

    private void refreshLabels() {
        int jID = comboBoxJaguars[jComboBox1.getSelectedIndex()];
        if (jID != -1) {
            hardwareVersion.setText("Hardware Version: "
                    + synchronizedRegisterArray.get("CANJAGUARHV" + jID));
            firmwareVersion.setText("Firmware Version: "
                    + synchronizedRegisterArray.get("CANJAGUARFV" + jID));
            firmwareVersion.setText("Jaguar ID: " + jID);
            setpoint.setText("Setpoint: " + dataStreamingModule.getStream(
                    "CANJAGUARSP" + jID).getLastPacket().val);
            current.setText("Current: " + dataStreamingModule.getStream(
                    "CANJAGUARI" + jID).getLastPacket().val);
            outputVoltage.setText("Output Voltage: " + dataStreamingModule.getStream(
                    "CANJAGUAROV" + jID).getLastPacket().val);
            inputVoltage.setText("Input Voltage: " + dataStreamingModule.getStream(
                    "CANJAGUARIV" + jID).getLastPacket().val);
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
        setpoint = new javax.swing.JLabel();
        current = new javax.swing.JLabel();
        outputVoltage = new javax.swing.JLabel();
        inputVoltage = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        graph1 = new robotgui.Graph();

        hardwareVersion.setText("Hardware Version:");

        firmwareVersion.setText("Firmware Version:");

        jaguarID.setText("Jaguar ID:");

        setpoint.setText("Setpoint:");

        current.setText("Current:");

        outputVoltage.setText("Output Voltage:");

        inputVoltage.setText("Input Voltage:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(hardwareVersion)
                    .addComponent(firmwareVersion)
                    .addComponent(jaguarID)
                    .addComponent(setpoint)
                    .addComponent(current)
                    .addComponent(outputVoltage)
                    .addComponent(inputVoltage))
                .addGap(0, 67, Short.MAX_VALUE))
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
                .addComponent(setpoint)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(current)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(outputVoltage)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(inputVoltage)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout graph1Layout = new javax.swing.GroupLayout(graph1);
        graph1.setLayout(graph1Layout);
        graph1Layout.setHorizontalGroup(
            graph1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 371, Short.MAX_VALUE)
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(graph1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 53, Short.MAX_VALUE))
                    .addComponent(graph1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        refreshLabels();
        refreshGraph();
    }//GEN-LAST:event_jComboBox1ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel current;
    private javax.swing.JLabel firmwareVersion;
    private robotgui.Graph graph1;
    private javax.swing.JLabel hardwareVersion;
    private javax.swing.JLabel inputVoltage;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel jaguarID;
    private javax.swing.JLabel outputVoltage;
    private javax.swing.JLabel setpoint;
    // End of variables declaration//GEN-END:variables
}

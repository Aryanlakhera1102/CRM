import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CRMWithGraphUI extends JFrame {
    private JTextField nameField, contactField;
    private JComboBox<String> categoryBox;
    private JTextArea notesArea;
    private JLabel leadCountLabel;
    private GraphPanel graphPanel;

    private DefaultListModel<Clients> leadListModel = new DefaultListModel<>();
    private DefaultListModel<Clients> hrListModel = new DefaultListModel<>();
    private DefaultListModel<Clients> editingListModel = new DefaultListModel<>();
    private DefaultListModel<Clients> adsListModel = new DefaultListModel<>();
    private DefaultListModel<Clients> graphicListModel = new DefaultListModel<>();
    private DefaultListModel<Clients> webDevListModel = new DefaultListModel<>();
    private DefaultListModel<Clients> appDevListModel = new DefaultListModel<>();
    private DefaultListModel<Clients> softwareDevListModel = new DefaultListModel<>();

    public CRMWithGraphUI() {
        setTitle("🌐 CRM System - Digital Solutions");
        setSize(1300, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        UIManager.put("TabbedPane.selected", new Color(240, 248, 255));

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JPanel leadPanel = createLeadPanel();
        JPanel hrPanel = createHRPanel();
        JPanel editingPanel = createTeamPanel("🖋 Editing Team", editingListModel);
        JPanel adsPanel = createTeamPanel("📢 Ads Team", adsListModel);
        JPanel graphicPanel = createTeamPanel("🎨 Graphic Design", graphicListModel);
        JPanel webDevPanel = createTeamPanel("💻 Web Dev", webDevListModel);
        JPanel appDevPanel = createTeamPanel("📱 App Dev", appDevListModel);
        JPanel softwareDevPanel = createTeamPanel("🧠 Software Dev", softwareDevListModel);
        graphPanel = new GraphPanel();

        tabs.addTab("📥 Lead Generation", leadPanel);
        tabs.addTab("👥 HR & Assignment", hrPanel);
        tabs.addTab("🖋 Editing", editingPanel);
        tabs.addTab("📢 Ads", adsPanel);
        tabs.addTab("🎨 Graphics", graphicPanel);
        tabs.addTab("💻 Web Dev", webDevPanel);
        tabs.addTab("📱 App Dev", appDevPanel);
        tabs.addTab("🧠 Software Dev", softwareDevPanel);
        tabs.addTab("📊 Stats", graphPanel);

        add(tabs);
    }

    private JPanel createLeadPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("📥 Lead Entry"));

        JPanel form = new JPanel(new GridLayout(7, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        nameField = new JTextField();
        contactField = new JTextField();
        categoryBox = new JComboBox<>(new String[]{"Design/Ads", "Development"});
        notesArea = new JTextArea(3, 20);
        JButton addBtn = new JButton("➕ Add Lead");
        JButton saveBtn = new JButton("💾 Save Leads");
        leadCountLabel = new JLabel("📈 Leads Generated: 0");

        form.add(new JLabel("👤 Client Name:"));
        form.add(nameField);
        form.add(new JLabel("📞 Contact:"));
        form.add(contactField);
        form.add(new JLabel("🏷 Category:"));
        form.add(categoryBox);
        form.add(new JLabel("📝 Notes:"));
        form.add(new JScrollPane(notesArea));
        form.add(addBtn);
        form.add(saveBtn);

        JList<Clients> leadList = new JList<>(leadListModel);
        JButton sendToHR = new JButton("📤 Send to HR");

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(leadList), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(sendToHR, BorderLayout.WEST);
        bottomPanel.add(leadCountLabel, BorderLayout.EAST);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String contact = contactField.getText().trim();
            String category = (String) categoryBox.getSelectedItem();
            String notes = notesArea.getText().trim();
            if (!name.isEmpty() && !contact.isEmpty() && !notes.isEmpty()) {
                Clients client = new Clients(name, contact, category, notes);
                leadListModel.addElement(client);
                nameField.setText("");
                contactField.setText("");
                notesArea.setText("");
                updateLeadCount();
                graphPanel.repaint();
            }
        });

        saveBtn.addActionListener(e -> saveLeadsToFile());
        sendToHR.addActionListener(e -> {
            Clients selected = leadList.getSelectedValue();
            if (selected != null) {
                hrListModel.addElement(selected);
                leadListModel.removeElement(selected);
                updateLeadCount();
                graphPanel.repaint();
            }
        });

        return panel;
    }

    private JPanel createHRPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("👥 HR Panel"));

        JList<Clients> hrList = new JList<>(hrListModel);
        JPanel btns = new JPanel(new GridLayout(2, 3, 10, 10));

        JButton toEditing = new JButton("To Editing");
        JButton toAds = new JButton("To Ads");
        JButton toGraphic = new JButton("To Graphic");
        JButton toWeb = new JButton("To Web Dev");
        JButton toApp = new JButton("To App Dev");
        JButton toSoftware = new JButton("To Software Dev");

        btns.add(toEditing);
        btns.add(toAds);
        btns.add(toGraphic);
        btns.add(toWeb);
        btns.add(toApp);
        btns.add(toSoftware);

        panel.add(new JScrollPane(hrList), BorderLayout.CENTER);
        panel.add(btns, BorderLayout.SOUTH);

        toEditing.addActionListener(e -> moveTo(hrList, hrListModel, editingListModel));
        toAds.addActionListener(e -> moveTo(hrList, hrListModel, adsListModel));
        toGraphic.addActionListener(e -> moveTo(hrList, hrListModel, graphicListModel));
        toWeb.addActionListener(e -> moveTo(hrList, hrListModel, webDevListModel));
        toApp.addActionListener(e -> moveTo(hrList, hrListModel, appDevListModel));
        toSoftware.addActionListener(e -> moveTo(hrList, hrListModel, softwareDevListModel));

        return panel;
    }

    private JPanel createTeamPanel(String title, DefaultListModel<Clients> model) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        JList<Clients> list = new JList<>(model);
        JButton doneBtn = new JButton("✅ Mark Complete");
        panel.add(new JScrollPane(list), BorderLayout.CENTER);
        panel.add(doneBtn, BorderLayout.SOUTH);

        doneBtn.addActionListener(e -> {
            Clients selected = list.getSelectedValue();
            if (selected != null) {
                model.removeElement(selected);
                graphPanel.repaint();
            }
        });
        return panel;
    }

    private void moveTo(JList<Clients> sourceList, DefaultListModel<Clients> sourceModel, DefaultListModel<Clients> targetModel) {
        Clients selected = sourceList.getSelectedValue();
        if (selected != null) {
            targetModel.addElement(selected);
            sourceModel.removeElement(selected);
            graphPanel.repaint();
        }
    }

    private void updateLeadCount() {
        leadCountLabel.setText("📈 Leads Generated: " + leadListModel.size());
    }

    private void saveLeadsToFile() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("leads.dat"))) {
            ArrayList<Clients> leads = new ArrayList<>();
            for (int i = 0; i < leadListModel.size(); i++) {
                leads.add(leadListModel.getElementAt(i));
            }
            out.writeObject(leads);
            JOptionPane.showMessageDialog(this, "Leads saved successfully!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to save leads.");
        }
    }

    class GraphPanel extends JPanel {
        public GraphPanel() {
            setPreferredSize(new Dimension(400, 300));
            setBackground(Color.white);
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Map<String, Integer> data = new HashMap<>();
            data.put("Editing", editingListModel.size());
            data.put("Ads", adsListModel.size());
            data.put("Graphic", graphicListModel.size());
            data.put("Web", webDevListModel.size());
            data.put("App", appDevListModel.size());
            data.put("Software", softwareDevListModel.size());

            int total = data.values().stream().mapToInt(Integer::intValue).sum();
            if (total == 0) return;

            int startAngle = 0;
            int x = 100, y = 50, diameter = 300;
            Color[] colors = {Color.PINK, Color.CYAN, Color.YELLOW, Color.ORANGE, Color.GREEN, Color.MAGENTA};
            int i = 0;

            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                int angle = (int) ((entry.getValue() * 360.0f) / total);
                g.setColor(colors[i % colors.length]);
                g.fillArc(x, y, diameter, diameter, startAngle, angle);
                startAngle += angle;
                g.setColor(Color.BLACK);
                g.drawString(entry.getKey() + " (" + entry.getValue() + ")", x + 330, 70 + (i * 20));
                i++;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CRMWithGraphUI().setVisible(true));
    }
}

class Clients implements Serializable {
    private String name;
    private String contact;
    private String category;
    private String notes;

    public Clients(String name, String contact, String category, String notes) {
        this.name = name;
        this.contact = contact;
        this.category = category;
        this.notes = notes;
    }

    public String toString() {
        return name + " | " + contact + " | " + category + "\n" + notes;
    }
}

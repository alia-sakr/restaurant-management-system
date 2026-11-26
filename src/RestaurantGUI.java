import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class RestaurantGUI extends JFrame {

    private Order order = new Order();
    private Customer customer;
    private Staff staff;

    private static int orderCounter = 1;
    private static int customerCounter = 1;

    // phone = PRIMARY KEY
    private final Map<String, Customer> customerDatabase = new HashMap<>();

    private DefaultComboBoxModel<MenuItem> menuModel;
    private JTextArea orderArea;
    private JLabel totalLabel;

    private JTextField nameField, phoneField;
    private JTextArea addressArea;
    private JComboBox<String> staffBox;

    // Staff dashboard
    private JLabel staffCustomerLabel, staffPhoneLabel, staffAddressLabel;
    private JLabel staffTotalLabel, staffDeliveryLabel, staffTimeLabel;
    private JTextArea staffItemsArea;

    // History
    private DefaultListModel<OrderSnapshot> historyModel = new DefaultListModel<>();

    // Delivery
    private JLabel deliveryOrderLabel, deliveryCustomerLabel, deliveryAddressLabel;
    private JLabel deliveryStatusLabel, deliveryTimeLabel, mapStatusLabel;
    private JTextArea deliveryItemsArea;
    private JProgressBar deliveryBar;

    public RestaurantGUI() {
        setTitle("Restaurant Ordering & Delivery System");
        setSize(1100, 720);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Customer", createCustomerPanel());
        tabs.addTab("Staff Dashboard", createStaffPanel());
        tabs.addTab("Delivery", createDeliveryPanel());
        tabs.addTab("Customer History", createCustomerHistoryPanel());
        add(tabs);
    }

    // ================= CUSTOMER PANEL =================
    private JPanel createCustomerPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel left = new JPanel(new GridLayout(10, 1, 5, 5));
        left.setBorder(BorderFactory.createTitledBorder("Customer Info"));

        nameField = new JTextField();
        phoneField = new JTextField();
        addressArea = new JTextArea(3, 15);
        staffBox = new JComboBox<>(new String[]{"Ahmed (Chef)", "Sara (Delivery)", "Omar (Manager)"});

        JButton loadBtn = new JButton("Load Existing Customer");
        JButton registerBtn = new JButton("Register New Customer");

        left.add(new JLabel("Name"));
        left.add(nameField);
        left.add(new JLabel("Phone "));
        left.add(phoneField);
        left.add(new JLabel("Address"));
        left.add(new JScrollPane(addressArea));
        left.add(new JLabel("Staff"));
        left.add(staffBox);
        left.add(loadBtn);
        left.add(registerBtn);

        panel.add(left, BorderLayout.WEST);

        // ===== ORDER =====
        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(BorderFactory.createTitledBorder("Order"));

        menuModel = new DefaultComboBoxModel<>();
        menuModel.addElement(new MenuItem("Burger", 5.99));
        menuModel.addElement(new MenuItem("Pizza", 8.99));
        menuModel.addElement(new MenuItem("Pasta", 6.49));
        menuModel.addElement(new MenuItem("Soft Drink", 1.99));

        JComboBox<MenuItem> menuBox = new JComboBox<>(menuModel);
        JButton addBtn = new JButton("Add Item");
        JButton addProductBtn = new JButton("Add New Product");

        JPanel top = new JPanel();
        top.add(menuBox);
        top.add(addBtn);
        top.add(addProductBtn);

        orderArea = new JTextArea();
        orderArea.setEditable(false);

        center.add(top, BorderLayout.NORTH);
        center.add(new JScrollPane(orderArea), BorderLayout.CENTER);
        panel.add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        totalLabel = new JLabel("Total: $0.00");
        JComboBox<String> paymentBox = new JComboBox<>(new String[]{"Cash", "Card", "Wallet"});
        JButton payBtn = new JButton("Pay & Confirm");

        bottom.add(new JLabel("Payment"));
        bottom.add(paymentBox);
        bottom.add(totalLabel);
        bottom.add(payBtn);

        panel.add(bottom, BorderLayout.SOUTH);

        // ===== ACTIONS =====
        loadBtn.addActionListener(e -> loadCustomer());
        registerBtn.addActionListener(e -> registerCustomer());

        addBtn.addActionListener(e -> {
            MenuItem item = (MenuItem) menuBox.getSelectedItem();
        order.addItem(item);
        orderArea.append(item + "\n");
        totalLabel.setText("Total: $" + order.getTotal());

        JOptionPane.showMessageDialog(this,
            item.getName() + " added to order",
            "Item Added",
            JOptionPane.INFORMATION_MESSAGE);

        });

        addProductBtn.addActionListener(e -> addNewProduct());
        payBtn.addActionListener(e -> confirmOrder(paymentBox.getSelectedItem().toString()));

        return panel;
    }

private void loadCustomer() {
    String phone = phoneField.getText();
    if (!customerDatabase.containsKey(phone)) {
        JOptionPane.showMessageDialog(this, "Customer not found");
        return;
    }
    customer = customerDatabase.get(phone);
    nameField.setText(customer.getName());
    addressArea.setText(customer.getAddress());
    order.setCustomer(customer);

    JOptionPane.showMessageDialog(this,
            "Customer loaded successfully!\nName: " + customer.getName(),
            "Success",
            JOptionPane.INFORMATION_MESSAGE);
}

private void registerCustomer() {
    if (nameField.getText().isEmpty() || phoneField.getText().isEmpty() || addressArea.getText().isEmpty()) {
        JOptionPane.showMessageDialog(this, "All fields required");
        return;
    }

    customer = new Customer(customerCounter++, nameField.getText(),
            phoneField.getText(), addressArea.getText());
    customerDatabase.put(customer.getPhone(), customer);
    order.setCustomer(customer);

    JOptionPane.showMessageDialog(this,
            "Customer registered successfully!\nCustomer ID: " + customer.getCustomerId(),
            "Success",
            JOptionPane.INFORMATION_MESSAGE);
}

    private void confirmOrder(String payment) {
        if (customer == null || orderArea.getText().isEmpty()) return;

        OrderSnapshot s = new OrderSnapshot();
        s.orderId = orderCounter++;
        s.customerId = customer.getCustomerId();
        s.customerName = customer.getName();
        s.phone = customer.getPhone();
        s.address = customer.getAddress();
        s.itemsText = orderArea.getText();
        s.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        s.total = order.getTotal() + 20;
        s.deliveryStatus = "Preparing";

        historyModel.addElement(s);
        updateStaffDashboard(s);
        startDeliveryAnimation(s);

        order = new Order();
        orderArea.setText("");
        totalLabel.setText("Total: $0.00");
        JOptionPane.showMessageDialog(this,
        "Payment successful!\nDelivery fee: $20\nOrder confirmed.",
        "Order Confirmed",
        JOptionPane.INFORMATION_MESSAGE);

    }

    // ================= STAFF =================
    private JPanel createStaffPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        staffCustomerLabel = new JLabel();
        staffPhoneLabel = new JLabel();
        staffAddressLabel = new JLabel();
        staffTotalLabel = new JLabel();
        staffDeliveryLabel = new JLabel();
        staffTimeLabel = new JLabel();

        JPanel info = new JPanel(new GridLayout(6, 1));
        info.add(staffCustomerLabel);
        info.add(staffPhoneLabel);
        info.add(staffAddressLabel);
        info.add(staffTotalLabel);
        info.add(staffDeliveryLabel);
        info.add(staffTimeLabel);

        staffItemsArea = new JTextArea();
        staffItemsArea.setEditable(false);

        JList<OrderSnapshot> list = new JList<>(historyModel);
        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateStaffDashboard(list.getSelectedValue());
        });

        panel.add(info, BorderLayout.NORTH);
        panel.add(new JScrollPane(staffItemsArea), BorderLayout.CENTER);
        panel.add(new JScrollPane(list), BorderLayout.EAST);

        return panel;
    }

    private void updateStaffDashboard(OrderSnapshot s) {
        if (s == null) return;
        staffCustomerLabel.setText("Customer: " + s.customerName);
        staffPhoneLabel.setText("Phone: " + s.phone);
        staffAddressLabel.setText("Address: " + s.address);
        staffTotalLabel.setText("Total: $" + s.total);
        staffDeliveryLabel.setText("Status: " + s.deliveryStatus);
        staffTimeLabel.setText("Time: " + s.timestamp);
        staffItemsArea.setText(s.itemsText);
    }

    // ================= DELIVERY =================
    private JPanel createDeliveryPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        deliveryOrderLabel = new JLabel();
        deliveryCustomerLabel = new JLabel();
        deliveryAddressLabel = new JLabel();
        deliveryStatusLabel = new JLabel();
        deliveryTimeLabel = new JLabel();
        mapStatusLabel = new JLabel("📍 Restaurant → 🛵 → 🏠", JLabel.CENTER);

        JPanel info = new JPanel(new GridLayout(6, 1));
        info.add(deliveryOrderLabel);
        info.add(deliveryCustomerLabel);
        info.add(deliveryAddressLabel);
        info.add(deliveryStatusLabel);
        info.add(deliveryTimeLabel);
        info.add(mapStatusLabel);

        deliveryItemsArea = new JTextArea();
        deliveryItemsArea.setEditable(false);

        deliveryBar = new JProgressBar(0, 100);
        deliveryBar.setStringPainted(true);

        panel.add(info, BorderLayout.NORTH);
        panel.add(new JScrollPane(deliveryItemsArea), BorderLayout.CENTER);
        panel.add(deliveryBar, BorderLayout.SOUTH);

        return panel;
    }

    private void startDeliveryAnimation(OrderSnapshot s) {
        deliveryOrderLabel.setText("Order #" + s.orderId);
        deliveryCustomerLabel.setText("Customer: " + s.customerName);
        deliveryAddressLabel.setText("Address: " + s.address);
        deliveryItemsArea.setText(s.itemsText);
        deliveryTimeLabel.setText("Time: " + s.timestamp);

        String[] steps = {"Preparing", "Picked Up", "On the Way", "Near", "Delivered"};
        deliveryBar.setValue(0);

        Timer t = new Timer(5000, e -> {
            int step = deliveryBar.getValue() / 20;
            if (step < steps.length) {
                s.deliveryStatus = steps[step];
                deliveryStatusLabel.setText("Status: " + steps[step]);
                deliveryBar.setValue(deliveryBar.getValue() + 20);
            } else ((Timer) e.getSource()).stop();
        });
        t.start();
    }

    // ================= HISTORY =================
    private JPanel createCustomerHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JTextField phoneSearch = new JTextField(10);
        JTextArea area = new JTextArea();
        area.setEditable(false);

        JButton load = new JButton("Load History");

        load.addActionListener(e -> {
            area.setText("");
            for (int i = 0; i < historyModel.size(); i++) {
                OrderSnapshot s = historyModel.get(i);
                if (s.phone.equals(phoneSearch.getText())) {
                    area.append("Order #" + s.orderId + "\n" + s.itemsText + "\n\n");
                }
            }
            if (!area.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Order history loaded successfully",
                    "History Loaded",
                    JOptionPane.INFORMATION_MESSAGE);
            }

        });

        JPanel top = new JPanel();
        top.add(new JLabel("Phone:"));
        top.add(phoneSearch);
        top.add(load);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(area), BorderLayout.CENTER);
        return panel;
    }

    private void addNewProduct() {
        JTextField n = new JTextField();
        JTextField p = new JTextField();
        if (JOptionPane.showConfirmDialog(this, new Object[]{"Name", n, "Price", p},
                "Add Product", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            menuModel.addElement(new MenuItem(n.getText(), Double.parseDouble(p.getText())));
            
            JOptionPane.showMessageDialog(this,
                "New product added successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private static class OrderSnapshot {
        int orderId, customerId;
        String customerName, phone, address, itemsText, timestamp, deliveryStatus;
        double total;
        public String toString() { return "Order #" + orderId + " | " + phone; }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RestaurantGUI().setVisible(true));
    }
}

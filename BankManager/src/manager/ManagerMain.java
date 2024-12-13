package manager;

import common.CommandDTO;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ManagerMain
        extends JFrame
        implements ActionListener, BankServiceHandler {

    private JLabel labelTitle;
    private JButton btnAddCustomer;
    private JButton btnAddAccount;
    private JButton btnLogin;
    private JButton btnViewAllCustomer;
    private JButton btnViewAllAccount;
    private JButton btnExit;


    PanAddCustomer panAddCustomer;
    PanAddAccount panAddAccount;
    PanViewAllCustomer panViewAllCustomer;
    PanViewAllAccount panViewAllAccount;
    PanLogin panLogin;


    public static String userId;
    private AsynchronousChannelGroup channelGroup;
    private AsynchronousSocketChannel channel;


    
    // ManagerMain Class�� ������, ��� ����, gui �ʱ�ȭ
    
    public ManagerMain() {
        startClient();
        initGui();
        setVisible(true);
    }

    //initGui(), gui �ʱ�ȭ
    private JPanel cardPanel;
    private CardLayout cardLayout;

    private void initGui() {
        setTitle("Manager GUI");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null); // ȭ�� �߾ӿ� ��ġ
        setMinimumSize(new Dimension(800, 600));

        // ���� �г� ����
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);

        // ��� Ÿ��Ʋ
        labelTitle = new JLabel("CNU Bank Manager", SwingConstants.CENTER);
        labelTitle.setFont(new Font("Arial", Font.BOLD, 36));
        labelTitle.setForeground(new Color(0, 102, 204));
        mainPanel.add(labelTitle, BorderLayout.NORTH);

        // �г�
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // ��ư ����

        btnAddCustomer = createButton("<html>�ű� �� ���</html>", "���ο� ���� ����մϴ�.");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        buttonPanel.add(btnAddCustomer, gbc);

        btnAddAccount = createButton("<html>�ű� ���� ����</html>", "���ο� ���¸� �����մϴ�.");
        gbc.gridy = 1;
        buttonPanel.add(btnAddAccount, gbc);

        btnLogin = createButton("<html>������ �α���</html>", "������ �������� �α����մϴ�.");
        gbc.gridy = 2;
        buttonPanel.add(btnLogin, gbc);

        btnViewAllCustomer = createButton("<html>�� ����<br>��ȸ �� ����</html>", "��� �� ������ ��ȸ�ϰų� �����մϴ�.");
        gbc.gridy = 3;
        buttonPanel.add(btnViewAllCustomer, gbc);

        btnViewAllAccount = createButton("<html>���� ����<br>��ȸ �� ����</html>", "��� ���� ������ ��ȸ�ϰų� �����մϴ�.");
        gbc.gridy = 4;
        buttonPanel.add(btnViewAllAccount, gbc);

        btnExit = createButton("<html>����</html>", "���α׷��� �����մϴ�.");
        gbc.gridy = 5;
        buttonPanel.add(btnExit, gbc);

        mainPanel.add(buttonPanel, BorderLayout.WEST);

        // �߾� �г�(CardLayout) ���� �� ����
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        mainPanel.add(cardPanel, BorderLayout.CENTER);

        // �� ȭ�� �г� �߰�
        panAddCustomer = new PanAddCustomer(this);
        panAddAccount = new PanAddAccount(this);
        panViewAllCustomer = new PanViewAllCustomer(this);
        panViewAllAccount = new PanViewAllAccount(this);
        panLogin = new PanLogin(this);

        cardPanel.add(panAddCustomer, "addCustomer");
        cardPanel.add(panAddAccount, "addAccount");
        cardPanel.add(panViewAllCustomer, "ViewAllCustomer");
        cardPanel.add(panViewAllAccount, "ViewAllAccount");
        cardPanel.add(panLogin, "Login");

        cardLayout.show(cardPanel, "Login");

    }


    //*******************************************************************
    // Name : actionPerformed
    // Type : Listener
    // Description :  ManagerMain Frame�� ��ư ������Ʈ���� ������ ������ �κ�
    //                �Ʒ� �ڵ忡���� �� ��ɺ� ȭ������ ��ȯ�ϴ� �ڵ尡 �ۼ��Ǿ��ִ�.
    //*******************************************************************
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == btnAddCustomer) {
            display("addCustomer");
        } else if (source == btnAddAccount) {
            display("addAccount");
        } else if (source == btnLogin) {
            display("Login");
        } else if (source == btnViewAllCustomer) {
            display("ViewAllCustomer");
            panViewAllCustomer.GetCustomerList();
        } else if (source == btnViewAllAccount) {
            display("ViewAllAccount");
            panViewAllAccount.GetAccountList();
        } else if (source == btnExit) {
            dispose();
        }
    }

    //*******************************************************************
    // Name : display(), setFrameUI()
    // Type : Method
    // Description :  �� ȭ�麰 �г��� Visible ���θ� �ٲپ��־� ȭ�� ��ȯ�� ȿ���� �ش�.
    //*******************************************************************
    public void display(String viewName) {

        if (userId == null) {
            if (!viewName.equals("Login") && !viewName.equals("Main")) {
                JOptionPane.showMessageDialog(this, "�α����ϼ���.", "����", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // ��� �г��� ����
        panAddCustomer.setVisible(false);
        panAddAccount.setVisible(false);
        panViewAllCustomer.setVisible(false);
        panViewAllAccount.setVisible(false);
        panLogin.setVisible(false);
        // ���� UI ���
        if (viewName.equals("Main")) {
            getContentPane().getComponent(0).setVisible(true);
            return;
        }

        // Ư�� �гθ� ���̰� ����
        switch (viewName) {
            case "addCustomer":
                panAddCustomer.setVisible(true);
                break;
            case "addAccount":
                panAddAccount.setVisible(true);
                break;
            case "ViewAllCustomer":
                panViewAllCustomer.setVisible(true);
                break;
            case "ViewAllAccount":
                panViewAllAccount.setVisible(true);
                break;
            case "Login":
                panLogin.setVisible(true);
                break;
            default:
                getContentPane().getComponent(0).setVisible(true);
        }
    }

    //*******************************************************************
    // Name : startClient()
    // Type : Method
    // Description :  ManagerMain Class �� �������ִ� ������ �������Ͽ� ���ӽ�Ų��
    //*******************************************************************
    private void startClient() {
        try {
            channelGroup = AsynchronousChannelGroup.withFixedThreadPool(
                    Runtime.getRuntime().availableProcessors(),
                    Executors.defaultThreadFactory());
            channel = AsynchronousSocketChannel.open(channelGroup);
            channel.connect(new InetSocketAddress("localhost", 5002), null, new CompletionHandler<Void, Void>() {
                @Override
                public void completed(Void result, Void attachment) {
                    System.out.println("Bank Server Connected");
                }

                @Override
                public void failed(Throwable exc, Void attachment) {
                    disconnectServer();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //*******************************************************************
    // Name : stopClient(), disconnectServer()
    // Type : Method
    // Description :  ManagerMain Class �� �������ִ� ������ ������ �����Ѵ�.
    //*******************************************************************
    private void stopClient() {
        try {
            if (channelGroup != null && !channelGroup.isShutdown()) {
                channelGroup.shutdownNow();
                channelGroup.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS); // ��ٸ����� �߰�
            }
            System.out.println("���� ����");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void disconnectServer() {
        stopClient();
    }

    //*******************************************************************
    // Name : send()
    // Type : Method
    // Description :  CommandDTO�� �Ű������� �Ͽ� ������ ��û �޽����� �����ϴ� �޼ҵ�
    //                CommandDTO Class ���� ATM ���� ��û�� �ʿ��� �����͵��� ���� �Ǿ� �ִ�.
    //                ManagerMain Class�� BankServiceHandler �������̽��� ����Ͽ���.
    //*******************************************************************
    @Override
    public void send(CommandDTO commandDTO, CompletionHandler<Integer, ByteBuffer> handlers) {
        commandDTO.setId(userId);
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(commandDTO);
            objectOutputStream.flush();
            channel.write(ByteBuffer.wrap(byteArrayOutputStream.toByteArray()), null, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    ByteBuffer byteBuffer = ByteBuffer.allocate(2048);
                    channel.read(byteBuffer, byteBuffer, handlers);
                }

                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    disconnectServer();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //*******************************************************************
    // Name : createButton()
    // Type : Method
    // Description :  ���� ��ư ���� �޼ҵ��, ��Ÿ�ϰ� ������ �����Ѵ�.
    //*******************************************************************
    private JButton createButton(String text, String toolTip) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setFocusPainted(false);
        button.setBackground(new Color(51, 153, 255));
        button.setForeground(Color.BLACK);
        button.setPreferredSize(new Dimension(200, 50));
        button.setToolTipText(toolTip);
        button.addActionListener(this);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new ManagerMain();
        });
    }
}
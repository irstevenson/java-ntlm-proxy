//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//
// This file was missing from the codeset on SourceForge, so we've hacked away....

package ntlmproxy;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import ntlmproxy.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Credentials implements ActionListener {
    static Logger log;
    Properties props;
    JTextField domainField;
    JTextField usernameField;
    JPasswordField passwordField;
    JFrame frame;
    static String OK;
    static String CANCEL;

    public Credentials(Properties props) {
        this.props = props;
    }

    private void createAndShowGUI() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        this.frame = new JFrame("Enter Proxy Credentials...");
        JPanel pane = new JPanel();
        pane.setLayout(new GridLayout(0, 1));
        pane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.frame.add(pane, "Center");
        JLabel label = new JLabel("Domain");
        this.domainField = new JTextField(20);
        this.domainField.setText(this.props.getProperty(Main.PROXY_DELEGATE_DOMAIN));
        label.setLabelFor(this.domainField);
        pane.add(label);
        pane.add(this.domainField);
        label = new JLabel("Username");
        this.usernameField = new JTextField(20);
        this.usernameField.setText(this.props.getProperty(Main.PROXY_DELEGATE_USERNAME));
        label.setLabelFor(this.usernameField);
        pane.add(label);
        pane.add(this.usernameField);
        label = new JLabel("Password");
        this.passwordField = new JPasswordField(20);
        label.setLabelFor(this.passwordField);
        pane.add(label);
        pane.add(this.passwordField);
        if(this.props.getProperty(Main.PROXY_DELEGATE_DOMAIN, "").equals("")) {
            this.domainField.requestFocus();
        }

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, 2));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPane.add(Box.createHorizontalGlue());
        JButton button = new JButton("OK");
        button.setActionCommand(OK);
        button.addActionListener(this);
        buttonPane.add(button);
        this.frame.getRootPane().setDefaultButton(button);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
        button = new JButton("Cancel");
        button.setActionCommand(CANCEL);
        button.addActionListener(this);
        buttonPane.add(button);
        this.frame.getContentPane().add(buttonPane, "Last");
        this.frame.pack();
        this.frame.setResizable(false);
        this.frame.setLocationRelativeTo((Component)null);
        this.frame.setVisible(true);
        if(!this.props.getProperty(Main.PROXY_DELEGATE_DOMAIN, "").equals("")) {
            this.passwordField.requestFocusInWindow();
        }

    }

    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if(CANCEL.equals(cmd)) {
            System.exit(1);
        }

        this.props.setProperty(Main.PROXY_DELEGATE_DOMAIN, this.domainField.getText());
        this.props.setProperty(Main.PROXY_DELEGATE_USERNAME, this.usernameField.getText());
        this.props.setProperty(Main.PROXY_DELEGATE_PASSWORD, new String(this.passwordField.getPassword()));
        this.frame.dispose();
        synchronized(this) {
            this.notify();
        }
    }

    public static synchronized void getCredentials(Properties props) throws Exception {
        if(props.getProperty(Main.CREDENTIALS_METHOD, "console").equals("console")) {
            String credentials = readPassword();
            if(credentials != null) {
                props.setProperty(Main.PROXY_DELEGATE_PASSWORD, credentials);
                return;
            }
        }

        final Credentials credentials1 = new Credentials(props);
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        synchronized(credentials1) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    credentials1.createAndShowGUI();
                }
            });
            credentials1.wait();
        }
    }

    static String readPassword() {
        try {
            Class e = Class.forName("java.io.Console");
            System.out.print("Enter delegate proxy password: ");
            return new String((char[])((char[])e.getMethod("readPassword", (Class[])null).invoke(System.class.getMethod("console", (Class[])null).invoke((Object)null, (Object[])null), (Object[])null)));
        } catch (ClassNotFoundException var1) {
            ;
        } catch (NoSuchMethodException var2) {
            ;
        } catch (IllegalAccessException var3) {
            ;
        } catch (InvocationTargetException var4) {
            ;
        }

        return null;
    }

    static {
        log = LoggerFactory.getLogger(Credentials.class);
        OK = "ok";
        CANCEL = "cancel";
    }
}


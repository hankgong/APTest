package com.example.APTest;


import javax.activation.*;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Date;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: hgong
 * Date: 24/05/13
 * Time: 11:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class Email extends javax.mail.Authenticator{
    private String mUserName;
    private String mPassword;

    private String[] mRecipientAddrs;
    private String mSenderAddr;

    private String mSmtpPort;
    private String mSocketFactoryPort;

    private String mSmtpServer;

    private String mMailSubject;
    private String mMailBody;

    private boolean mIfAuthenticate;

    private boolean mIfDebuggable;

    private Multipart mMimeMultipart;


    public Email() {
        //mSmtpServer = "smtp.gmail.com"; // default smtp server
        mSmtpServer = "smtp.mail.yahoo.com"; // default smtp server
        mSmtpPort = "465"; // default smtp port
        mSocketFactoryPort = "465"; // default socketfactory port

        mUserName = "hankgong"; // username
        mPassword = "82322289"; // password
        mRecipientAddrs = new String[]{"hankgong@gmail.com", "hgong@avi-electronics.com"};
        mSenderAddr = "hankgong@yahoo.com"; // email sent from
        mMailSubject = "Test sending"; // email subject
        mMailBody = "nothing but test..."; // email body

        mIfDebuggable = false; // debug mode on or off - default off
        mIfAuthenticate = true; // smtp authentication - default on

        mMimeMultipart = new MimeMultipart();

        // There is something wrong with MailCap, javamail can not find a handler for the multipart/mixed part, so this bit needs to be added.
        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(mc);
    }

    public Email(String user, String pass) {
        this();

        mUserName = user;
        mPassword = pass;
    }

    public boolean send() throws Exception {
        Properties props = _setProperties();

        if(!mUserName.equals("") && !mPassword.equals("") && mRecipientAddrs.length > 0 && !mSenderAddr.equals("") && !mMailSubject.equals("") && !mMailBody.equals("")) {
            Session session = Session.getInstance(props, this);

            MimeMessage msg = new MimeMessage(session);

            msg.setFrom(new InternetAddress(mSenderAddr));

            InternetAddress[] addressTo = new InternetAddress[mRecipientAddrs.length];
            for (int i = 0; i < mRecipientAddrs.length; i++) {
                addressTo[i] = new InternetAddress(mRecipientAddrs[i]);
            }
            msg.setRecipients(MimeMessage.RecipientType.TO, addressTo);

            msg.setSubject(mMailSubject);
            msg.setSentDate(new Date());

            // setup message body
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(mMailBody);
            mMimeMultipart.addBodyPart(messageBodyPart);

            // Put parts in message
            msg.setContent(mMimeMultipart);

            // send email
            Transport.send(msg);

            return true;
        } else {
            return false;
        }
    }

    public void addAttachment(String filename) throws Exception {
        BodyPart messageBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(filename);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(filename);

        mMimeMultipart.addBodyPart(messageBodyPart);
    }

    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(mUserName, mPassword);
    }

    private Properties _setProperties() {
        Properties props = new Properties();

        props.put("mail.smtp.host", mSmtpServer);

        if(mIfDebuggable) {
            props.put("mail.debug", "true");
        }

        if(mIfAuthenticate) {
            props.put("mail.smtp.auth", "true");
        }

        props.put("mail.smtp.port", mSmtpPort);
        props.put("mail.smtp.socketFactory.port", mSocketFactoryPort);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");

        return props;
    }

    // the getters and setters
    public String getBody() {
        return mMailBody;
    }

    public void setBody(String _body) {
        this.mMailBody = _body;
    }

    // more of the getters and setters …..

}

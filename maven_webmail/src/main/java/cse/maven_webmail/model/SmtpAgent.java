/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cse.maven_webmail.model;

import com.sun.mail.smtp.SMTPMessage;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.NoSuchFileException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

/**
 *
 * @author jongmin
 */
public class SmtpAgent {

    private static final Logger SMTPLOG = Logger.getGlobal();

    protected String host = null;
    protected String userid = null;
    protected String to = null;              // 메일 주소
    protected String cc = null;              // 참조
    protected String subj = null;           // 제목
    protected String body = null;            // 본문
    protected String file1 = null;           // 첨부 파일

    public SmtpAgent(String host, String userid) {
        this.host = host;
        this.userid = userid;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getSubj() {
        return subj;
    }

    public void setSubj(String subj) {
        this.subj = subj;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getFile1() {
        return file1;
    }

    public void setFile1(String file1) {
        this.file1 = file1;
    }

    // LJM 100418 -  현재 로그인한 사용자의 이메일 주소를 반영하도록 수정되어야 함. - test only
    // LJM 100419 - 일반 웹 서버와의 SMTP 동작시 setFrom() 함수 사용 필요함.
    //              없을 경우 메일 전송이 송신주소가 없어서 걸러짐.
    public static final String SMTPAGENTCODE = "mail.smtp.host";

    public boolean sendMessage() throws AddressException, MessagingException, UnsupportedEncodingException {
        SMTPLOG.setLevel(Level.INFO);
        SMTPLOG.severe("severe thing");
        SMTPLOG.warning("warning");
        SMTPLOG.info("info");

        boolean status = false;           // 현재 전송되지 않았음을 나타내기 위해 false, 전송시 status가 true가 됨.
        // 1. property 설정
        Properties props = System.getProperties();
        props.put(SMTPAGENTCODE, this.host);
        SMTPLOG.info("SMTP host : " + props.get(SMTPAGENTCODE));

        // 2. session 가져오기
        Session session = Session.getDefaultInstance(props, null);
        session.setDebug(false);

        try {
            SMTPMessage msg = new SMTPMessage(session);

            msg.setFrom(new InternetAddress(this.userid));  // 200102 LJM - 테스트 목적으로 수정

            if (this.to.indexOf(';') != -1) {
                this.to = this.to.replace(";", ",");
            }
            msg.setRecipients(Message.RecipientType.TO, this.to);  // 200102 LJM - 수정

            if (this.cc.length() > 1) {
                if (this.cc.indexOf(';') != -1) {
                    this.cc = this.cc.replace(";", ",");
                }
                msg.setRecipients(Message.RecipientType.CC, this.cc);
            }

            msg.setSubject(this.subj);
            msg.setHeader("User-Agent", "LJM-WM/0.1");
            MimeBodyPart mbp = new MimeBodyPart();
            mbp.setText(this.body);
            Multipart mp = new MimeMultipart();
            mp.addBodyPart(mbp);

            if (this.file1 != null) // 첨부 파일의 존재가 확인되면
            {
                MimeBodyPart a1 = new MimeBodyPart();
                DataSource src = new FileDataSource(this.file1);
                a1.setDataHandler(new DataHandler(src));
                int index = this.file1.lastIndexOf('/');
                String fileName = this.file1.substring(index + 1);
                a1.setFileName(MimeUtility.encodeText(fileName, "UTF-8", "B"));
                mp.addBodyPart(a1);
            }
            msg.setContent(mp);
            Transport.send(msg);

            if (this.file1 != null) // 첨부 파일의 존재가 확인되면
            {
                File f = new File(this.file1);
                f.delete(); //S4042
            }
            status = true;                 // 전송 완료되었으므로 status를 true로 바꿈.
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception ex) {
            SMTPLOG.info("sendMessage() error: " + ex);
        }
        return status;
    }  // sendMessage()

    public boolean sendMessageMe() throws NoSuchFileException, DirectoryNotEmptyException, IOException {
        boolean status = false;           // 현재 전송되지 않았음을 나타내기 위해 false, 전송시 status가 true가 됨.
        // 1. property 설정
        Properties props = System.getProperties();
        props.put(SMTPAGENTCODE, this.host);
        SMTPLOG.info("SMTP host : " + props.get(SMTPAGENTCODE));

        // 2. session 가져오기
        Session session = Session.getDefaultInstance(props, null);
        session.setDebug(false);

        try {
            SMTPMessage msg = new SMTPMessage(session);

            msg.setFrom(new InternetAddress(this.userid));  // 200102 LJM - 테스트 목적으로 수정
            msg.setRecipients(Message.RecipientType.TO, this.userid);  // 200102 LJM - 수정

            if (this.cc.length() > 1) {
                if (this.cc.indexOf(';') != -1) {
                    this.cc = this.cc.replace(";", ",");
                }
                msg.setRecipients(Message.RecipientType.CC, this.cc);
            }

            msg.setSubject(this.subj);
            msg.setHeader("User-Agent", "LJM-WM/0.1");
            MimeBodyPart mbp = new MimeBodyPart();
            mbp.setText(this.body);
            Multipart mp = new MimeMultipart();
            mp.addBodyPart(mbp);

            if (this.file1 != null) // 첨부 파일의 존재가 확인되면
            {
                MimeBodyPart a1 = new MimeBodyPart();
                DataSource src = new FileDataSource(this.file1);
                a1.setDataHandler(new DataHandler(src));
                int index = this.file1.lastIndexOf('/');
                String fileName = this.file1.substring(index + 1);
                a1.setFileName(MimeUtility.encodeText(fileName, "UTF-8", "B"));
                mp.addBodyPart(a1);
            }
            msg.setContent(mp);
            Transport.send(msg);

            if (this.file1 != null) // 첨부 파일의 존재가 확인되면
            {
                File f = new File(this.file1);
                f.delete();
            }
            status = true;                 // 전송 완료되었으므로 status를 true로 바꿈.
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception ex) {
            SMTPLOG.info("sendMessage_me() error: " + ex);
        }

        return status;
    }  // sendMessageMe()

}

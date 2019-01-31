import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 *
 * @author Administrator
 */

@WebServlet("/FileUploadServlet")
@MultipartConfig(fileSizeThreshold=1024*1024*10, 	// 10 MB 
                 maxFileSize=1024*1024*50,      	// 50 MB
                 maxRequestSize=1024*1024*100)
public class mail extends HttpServlet {

  

   
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
       
        response.setContentType("text/html");
        //PrintWriter out= response.getWriter();
        String to = request.getParameter("to");
        
        ServletConfig sc = getServletConfig();
        String password = sc.getInitParameter("pass");
        javax.servlet.http.Part file1 =request.getPart("attach");
        String contentDisp = file1.getHeader("content-disposition");
                     String[] items = contentDisp.split(";");
                     String file = items[2].substring(items[2].indexOf("=")+2, items[2].length()-1);
                     
        String savePath = "C:/Users/Administrator/Documents/NetBeansProjects/mail/web/file";
        String path = savePath+"/"+file;
       
        String query = "SELECT PASSWORD FROM MY_APP WHERE EMAIL=?";
        PreparedStatement ps;
        
           
            Properties pro = System.getProperties();
            pro.put("mail.smtp.auth","true");
            pro.put("mail.smtp.starttls.enable","true");
            pro.put("mail.smtp.host","smtp.gmail.com");
            pro.put("mail.smtp.port","587");
            pro.put("mzil.smtp.socketFactory.class", "javax.net.SSlSocketFactory");
            
//            pro.put("mail.smtp.socketFactory.port","587");
//            
//            pro.put("mail.smtp.socproketFactory.fallback", "false");
        try {            
             Class.forName("oracle.jdbc.driver.OracleDriver");
             Connection con=DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","system","3101");
            
             ps = con.prepareStatement(query);
             ps.setString(1, to);
             ResultSet rs = ps.executeQuery();
             
             if (rs.next()){
                 String pass = rs.getString("PASSWORD");
                 Session s = Session.getDefaultInstance(pro, new javax.mail.Authenticator() {
                     @Override
                     protected PasswordAuthentication getPasswordAuthentication(){
                         return new PasswordAuthentication("abc@gmail.com",password);
                     }
                 });
                 file1.write(savePath+File.separator+file);
                 s.setDebug(true);
                 Message m = new MimeMessage(s);
                 m.setFrom(new InternetAddress("abc@gmail.com"));
                 m.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
                 //System.out.println("Success");
                 BodyPart BodyPart = new MimeBodyPart();
                 BodyPart.setText("Your Password Is :" + pass);
                 DataSource source = new FileDataSource(path); 
                 BodyPart textPart= new MimeBodyPart(); 
                 textPart.setDataHandler(new DataHandler(source));
                 textPart.setFileName(file);
                 
                 Multipart multipart = new MimeMultipart(); 
                 multipart.addBodyPart(BodyPart);
                 multipart.addBodyPart(textPart);
                 
                 
                 m.setSubject("Password for your mail id");
                 m.setContent(multipart);
                //m.setText("Your Password Is :" + pass);
                 Transport.send(m);
                 //out.println("mail sent successfully");
                 response.sendRedirect("http://www.google.com"); 
             }
             else{
                 //out.println("Email id not Exist");
                 response.sendRedirect("http://www.gmail.com"); 
             }
        } catch (ClassNotFoundException | SQLException | MessagingException ex) {
            Logger.getLogger(mail.class.getName()).log(Level.SEVERE, null, ex);
        }
                   
    }

    
}

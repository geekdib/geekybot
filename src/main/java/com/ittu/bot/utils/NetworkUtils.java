//package com.ittu.bot.utils;
///**
// * @author @dibyapp, Name : Dibyaprakash, Email : dibyapp@crms.app
// * @Project : Ittu AI
// */
//import java.io.BufferedReader;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.InetAddress;
//import java.net.NetworkInterface;
//import java.net.SocketException;
//import java.net.URI;
//import java.net.URLEncoder;
//import java.util.Enumeration;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.DefaultHttpClient;
//
//public class NetworkUtils {
//   public static String localIPAddress() {
//      try {
//         Enumeration en = NetworkInterface.getNetworkInterfaces();
//
//         while(en.hasMoreElements()) {
//            NetworkInterface intf = (NetworkInterface)en.nextElement();
//            Enumeration enumIpAddr = intf.getInetAddresses();
//
//            while(enumIpAddr.hasMoreElements()) {
//               InetAddress inetAddress = (InetAddress)enumIpAddr.nextElement();
//               if (!inetAddress.isLoopbackAddress()) {
//                  String ipAddress = inetAddress.getHostAddress().toString();
//                  int p = ipAddress.indexOf("%");
//                  if (p > 0) {
//                     ipAddress = ipAddress.substring(0, p);
//                  }
//
//                  System.out.println("--> localIPAddress = " + ipAddress);
//                  return "sorry";
//               }
//            }
//         }
//      } catch (SocketException var6) {
//         var6.printStackTrace();
//      }
//
//      return "sorry";
//   }
//
//   public static String responseContent(String url) throws Exception {
//      HttpClient client = new DefaultHttpClient();
//      HttpGet request = new HttpGet();
//      request.setURI(new URI(url));
//      InputStream is = client.execute(request).getEntity().getContent();
//      BufferedReader inb = new BufferedReader(new InputStreamReader(is));
//      StringBuilder sb = new StringBuilder("");
//      String NL = System.getProperty("line.separator");
//
//      String line;
//      while((line = inb.readLine()) != null) {
//         sb.append(line).append(NL);
//      }
//
//      inb.close();
//      return sb.toString();
//   }
//
//   public static String spec(String host, String botid, String custid, String input) {
//      String spec = "";
//      if (custid.equals("0")) {
//         spec = String.format("%s?botid=%s&input=%s", "http://" + host + "/pandora/talk-xml", botid, URLEncoder.encode(input));
//      } else {
//         spec = String.format("%s?botid=%s&custid=%s&input=%s", "http://" + host + "/pandora/talk-xml", botid, custid, URLEncoder.encode(input));
//      }
//
//      return spec;
//   }
//}

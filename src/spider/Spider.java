/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spider;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;

import java.util.*;

import java.net.*;

import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Spider {
    
    public static Elements url_handeler (String[] args){
        Validate.isTrue(args.length == 1, "Usage: please provide a url");
        String url = args[0];
        System.out.println("Fetching " + url );
        
        //Assignment
        Document doc = null;
        try {
            doc = Jsoup.connect(url).userAgent("Mozilla").get();
        } catch (IOException ex) {
            Logger.getLogger(Spider.class.getName()).log(Level.SEVERE, null, ex);
        }
        Elements links = doc.select("a[href]");
        return links;
    }
    
   public static void main(String[] args) throws IOException {
       
        Elements links = url_handeler(args);
           
        //Creating search settings to get correct links
        Pattern pattern = Pattern.compile(".*mp3.*");
        
        //Creating the conatiner for the desired links
        ArrayList list = new ArrayList();
        
        //Printing Links
        System.out.println("Links: " + links.size());
        for (Element link : links) {
            
            String str = link.toString();
            Matcher matcher = pattern.matcher(str);
            
            if (matcher.matches()){
                
                list.add(link.attr("abs:href"));
                //System.out.println(link.attr("abs:href"));
            }
        }
        
        //Removing duplicate links while maintaining the link order
        Set<String> s = new LinkedHashSet<>(list);
        
        Iterator iterator = s.iterator();
        URL website;
        while (iterator.hasNext()){
            String tmpstr = (String) iterator.next();
            website = new URL(tmpstr);
            System.out.println(website);
            
            String[] bits = tmpstr.split("/");
            String filename = bits[bits.length-1];
            
            File myfile = new File(filename);
            if(!myfile.exists()){
                myfile.createNewFile();
                System.out.println("File created");
                
                ReadableByteChannel rbc = Channels.newChannel(website.openStream());
                FileOutputStream fos = new FileOutputStream(filename);
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                System.out.println("File Downloaded");
            }
            else{
                System.out.println("File already exists");
            }
        }
        
        System.out.println("Done");
    }
}
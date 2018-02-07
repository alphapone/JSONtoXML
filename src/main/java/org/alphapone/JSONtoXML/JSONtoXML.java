/*
# Limited Free Public License 1.0.0+

Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted anybody 
except of following organizations: AT Consulting, DPD, Diasoft, MMVB (moex.com), USA Government.


THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR
ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS
ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
package org.alphapone.JSONtoXML;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Utility methids for conversion JSON object to XML document
 * @author alphapone{inl@yandex.com}
 */
public class JSONtoXML {
    /**
     * End of stream character ...
     */
    final static protected char EOFC=(char) -1;
    
    /**
     * JSON to XML conversion specific exception
     */
    public static class JSONtoXMLFormatException extends Exception {
        public JSONtoXMLFormatException(String s) {
            super(s);
        }
    }

    /**
     * Transalate JSON string to XML string
     * @param s A string contains JSON expression
     * @return XML from JSON
     * @throws IOException
     * @throws util.JSONtoXML.JSONtoXMLFormatException 
     */    
    public static String translate(String s) 
            throws IOException, JSONtoXMLFormatException
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        translate(s,bos);
        return bos.toString();
    }
    
    /**
     * Translate JSON string to XML stream
     * @param s A string contains JSON expression
     * @param out A stream for writing XML
     * @throws IOException
     * @throws util.JSONtoXML.JSONtoXMLFormatException 
     */    
    public static void translate(String s, OutputStream out) 
            throws IOException, JSONtoXMLFormatException            
    {
        InputStream in = new ByteArrayInputStream(s.getBytes());
        translate(in,out);
    }
    
    /**
     * Translate JSON stream to XML stream
     * @param in a stream with JSON object
     * @param out a stream with XML document (conversion result)
     * @throws IOException
     * @throws util.JSONtoXML.JSONtoXMLFormatException 
     */    
    public static void translate(InputStream in, OutputStream out) 
            throws IOException, JSONtoXMLFormatException
    {
        InputStreamReader re = new InputStreamReader(in);
        char c;
        while ((c=(char)re.read()) != EOFC) {
            switch (c) {
                case '{':
                    translateObject(re,out);
                    break;
                case '[':
                    translateArray(re,out,null);
                    break;
                default:
                    if (Character.isSpaceChar(c)) {
                        break;
                    } else {
                        throw new JSONtoXMLFormatException("invalid character:" + c + " (" + (int)c + ")");
                    }
            }
        }
    }

    protected static void translateArray(InputStreamReader re, OutputStream out, String tname) 
            throws IOException, JSONtoXMLFormatException
    {
        char c;
        while ((c=(char)re.read()) != EOFC) {
            switch (c) {
                case '{':
                        translateObject(re,out);
                        break;
                case '[':
                        translateArray(re,out,tname);
                        break;
                case ',':
                    if (tname!=null) {
                        out.write("</".getBytes());
                        out.write(tname.getBytes());
                        out.write("><".getBytes());
                        out.write(tname.getBytes());
                        out.write(">".getBytes());
                    }
                    break;
                case ']':
                        return;
                case '"':
                    String n = readStringToken(re);
                    if (n!=null) {
                        out.write(n.getBytes());
                    }
                    break;
                default:
                    if (Character.isSpaceChar(c)) {
                        break;
                    } else {
                        out.write(("" + c).getBytes());
                    }
            }
        }
    }
    
    protected static String readStringToken(InputStreamReader re) 
            throws IOException
    {
        StringBuilder b = new StringBuilder();
        char c;
        boolean esc = false;
        while ((c=(char)re.read()) != EOFC) {
            switch (c) {
                case '\\':
                   esc = true;
                   break;
                case '"':
                    if (!esc) {
                        return b.toString();
                    }
                default:
                    b.append(c);
                    esc=false;
            }
        }
        return b.toString();
    }

    protected static boolean translateValue(InputStreamReader re, OutputStream out, String tname) 
            throws IOException, JSONtoXMLFormatException
    {
        char c;
        while ((c=(char)re.read()) != EOFC) {
            switch (c) {
                case '[':
                    translateArray(re, out, tname);
                    return false;
                case '{':
                    translateObject(re, out);
                    return false;
                case '"':
                    String v = readStringToken(re);
                    outs(out,v);
                    return false;
                case 'n':
                    re.read(); // u
                    re.read(); // l
                    re.read(); // l
                    return false;
                case ' ':
                    break;
                case '}':
                    return true;
                case ',':
                    return false;
                default:
                    outc(out,c);
                    break;
            }
        }
        return false;
    }
    
    protected static void outs(OutputStream out, String s) 
            throws IOException
    {
        if (s!=null) {
            for (int i = 0, l=s.length(); i<l; i++) {
                outc(out,s.charAt(i));
            }
        }
    }
    
    protected static void outc(OutputStream out, char c) 
            throws IOException
    {
        switch(c){
            case '<': out.write("&lt;".getBytes()); break;
            case '>': out.write("&gt;".getBytes()); break;
            case '\"': out.write("&quot;".getBytes()); break;
            case '&': out.write("&amp;".getBytes()); break;
            case '\'': out.write("&apos;".getBytes()); break;
            default:
                if(c>0x7f) {
                    if (c<0x400 || c> 0x4FF) {
                        out.write(("&#"+((int)c)+";").getBytes());
                    } else {
                        out.write(("" + c).getBytes());
                    }
                } else {
                    out.write(("" + c).getBytes());
                }
        }
    }
    
    protected static void translateObject(InputStreamReader re, OutputStream out) 
            throws IOException, JSONtoXMLFormatException
    {
        char c;
        String field = null;
        while ((c=(char)re.read()) != EOFC) {
            switch (c) {
                case ':':
                    if (field!=null) {
                        out.write("<".getBytes());
                        out.write(field.getBytes());
                        out.write(">".getBytes());
                    }
                    boolean eoo = translateValue(re,out, field);
                    if (field!=null) {
                        out.write("</".getBytes());
                        out.write(field.getBytes());
                        out.write(">".getBytes());
                    }
                    if (eoo) {
                       return;
                    }
                    break;
                case '"':
                    field = readStringToken(re);
                    break;
                case '}':
                    return;
                default:
            }
        }
    }

}

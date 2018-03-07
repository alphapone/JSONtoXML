package test.alphapone.JSONtoXML;

import org.junit.Test;

import org.alphapone.JSONtoXML.JSONtoXML;


public class TestJSONtoXML {

  private static void checkTranslate(String json, String expected) 
    throws Exception
  {
    String retval = JSONtoXML.translate(json);
    System.out.println("json:" + json);
    System.out.println("xml:" + retval);
    assert (retval != null || expected == null);
    if (retval != null) {
      assert(retval.equals(expected));
    }
  }

  @Test
  public void testConversion()
    throws Exception
  {
     checkTranslate("{\"a\":\"b\"}","<a>b</a>");
     checkTranslate("{\"a\":[{\"b\":0},{\"c\":1}]}","<a><b>0</b></a><a><c>1</c></a>"); 
     checkTranslate("{\"a\":[{\"b\":\"OOO \\\"Ochki i linzy po puti\\\"\"},{\"c\":1}]}","<a><b>OOO &quot;Ochki i linzy po puti&quot;</b></a><a><c>1</c></a>");
     checkTranslate("{\"a\":[{\"b\":\"OOO \\\r\\\n\\\"Ochki i linzy po puti\\\"\"},{\"c\":1}]}","<a><b>OOO \r\n&quot;Ochki i linzy po puti&quot;</b></a><a><c>1</c></a>"); 
     checkTranslate("{\"a\":[{\"b\":\"\\u049A jj\"},{\"c\":1}]}","<a><b>\u049A jj</b></a><a><c>1</c></a>");
  }

}

package com.ssc.tc.ats.util.test;

import com.ssc.tc.ats.util.ZipSearcher;

import junit.framework.TestCase;

public class ZipSearcherTestCase extends TestCase {
  protected ZipSearcher zs;
  protected void setUp() throws Exception {
    super.setUp();
    zs = new ZipSearcher("test.zip","com.mysql.jdbc.Buffer");
  }
  public void testMatched(){
    assertEquals(zs.matched(),true);
  }

}

package cn.edu.zju.nwrt.util.test;

import cn.edu.zju.nwrt.util.ZipSearcher;

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

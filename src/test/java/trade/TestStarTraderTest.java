/*
  Copyright (C) 2012 Albert Steiner
 Copyright (C) 2022 Albert Steiner

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package trade;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import static trade.StarTrader.fatalError;
import static trade.StarTrader.mainStart;
import static trade.StarTrader.startTime;
import static trade.StarTrader.testing;
import static trade.StarTrader.itTesting;
import static trade.StarTrader.main3;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author albert steiner
 * This is a class to test the program StarTrader in a number of ways
 * Only when the tests are ok may the github be updated
 */
public class TestStarTraderTest {
  static protected E eE;
  static EM eM;
  static StarTrader st;

   /**
   * @param args the command line arguments
   */
  public static void main(String args[]) throws IOException {
    /* Set the Nimbus look and feel --change to animation*/

    // Look and feel setting code (optional) ">
    /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
     * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
     */
    /* Set the Nimbus look and feel --change to animation*/

    // Look and feel setting code (optional) ">
    /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
     * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
     */
    try {
     System.err.println("starting out in test TestStarTraderTest main " + Thread.currentThread().getName());
      //     System.exit(-25);
    mainStart(args);
   testing = true;
    itTesting = true;
    StarTrader.main3();
    
    assertEquals(false,fatalError);
     if(st.fatalError) throw new MyErr("fatal error at new StarTrader");
     System.err.println(" passed first test, StarTrader started");
      /* Create and display the form */
       /* Create and display the form */
    // java.awt.EventQueue.invokeAndWait(() -> {
      //  st.setVisible(true);
     // } //java.awt.EventQueue.invokeLater(new Runnable() {
     // );
    } finally {
      if (EM.bKeep != null) {
        EM.bKeep.close();
      }
  
    }
    System.out.println("TestStarTraderTest finished " + (st.fatalError?" fatalError": " no Error"));
    System.exit(0);
  } // main
  
  public TestStarTraderTest(){
     System.err.println("starting out in test test TestStarTraderTest main " + Thread.currentThread().getName());
  //  assertEquals(false,fatalError); I think it goes to main()
 //   System.exit(-24);  
  }
  
}

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

import java.io.IOException;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static trade.StarTrader.fatalError;
import static trade.StarTrader.itTesting;
import static trade.StarTrader.startTime;
import static trade.StarTrader.testing;

/**
 *
 * @author albert steiner
 * This is a class to test the program StarTrader in a number of ways
 * Only when the tests are ok may the github be updated
 */
public class ITStarTraderTest {
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
     System.err.println("starting out in test ITStarTraderTest main " + Thread.currentThread().getName());
      //     System.exit(-25);
    StarTrader.mainStart(args);
   testing = true;
    itTesting = true;
    StarTrader.main3();
    
    assertEquals(false,fatalError);
    assertFalse(fatalError);
     if(StarTrader.fatalError) throw new MyErr("fatal error at new StarTrader");
     System.err.println(" passed first test, StarTrader started");
     } catch (Error | Exception ex) {
      java.util.logging.Logger.getLogger(StarTrader.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      EM.flushes();
      System.err.println(EM.curEconName + " " + Thread.currentThread().getName() + new Date().toString() + (new Date().getTime() - startTime) + " cause=" + ex.getCause() + " message=" + ex.getMessage() + " string=" + ex.toString() + ", addlErr=" + eM.addlErr);
      ex.printStackTrace(System.err);
      EM.flushes();
      fatalError = true;
      assertFalse(fatalError);
      assertEquals(false,fatalError);
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
      assertFalse(fatalError);
     assertEquals(false,fatalError);
    }
    System.out.println("ITStarTraderTest finished " + (StarTrader.fatalError?" fatalError": " no Error"));
    
   // System.exit(-3);
    System.exit(0);
    System.exit(-27);
  } // main
  
  public ITStarTraderTest(){
     System.err.println("starting out in test test ITStarTraderTest main " + Thread.currentThread().getName());
     assertFalse(fatalError);
     assertEquals(false,fatalError); //I think it goes to main()
    System.exit(-24);  
  }
  
}

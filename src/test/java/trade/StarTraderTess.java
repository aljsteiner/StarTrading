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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static trade.StarTrader.fatalError;
import static trade.StarTrader.startTime;

/**
 *
 * @author albert steiner
 * This is a class to test the program StarTrader in a number of ways
 * Only when the tests are ok may the github be updated
 */
public class StarTraderTess {
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
   // System.exit(-22);
    try {
      E.sysmsg("starting out in test main " + Thread.currentThread().getName());
    StarTrader.mainStart(args);
    StarTrader.main3();
     if(StarTrader.fatalError) throw new MyErr("fatal error at new TestStarTraderTest");
     E.sysmsg(" passed first test,  started");
     assertFalse(EM.dfe());
     System.exit(0);
      System.err.println("oops passed exit");
     } catch (Error | Exception ex) {
      java.util.logging.Logger.getLogger(StarTrader.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      EM.flushes();
      System.err.println(EM.curEconName + " " + Thread.currentThread().getName() + new Date().toString() + (new Date().getTime() - startTime) + " cause=" + ex.getCause() + " message=" + ex.getMessage() + " string=" + ex.toString() + ", addlErr=" + eM.addlErr);
      ex.printStackTrace(System.err);
      EM.flushes();
      fatalError = true;
    } finally {
      if (EM.bKeep != null) {
        EM.bKeep.close();
      }
   System.exit(0);
    }
    //</editor-fold>

  } // main

  public StarTraderTess() {
    ///assertFalse(true);
    System.err.println("-----STttess---- test.StarTraderTess");
    //System.exit(-10);
  }

}

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
    try {
      System.err.println("-----MAA----starting out in aaa test main " + Thread.currentThread().getName());
      
    StarTrader.mainStart();
    StarTrader.main3();// tests are now in main3
    

     if(eM.dfe()) eM.doMyErr("fatal error at new StarTrader");
     System.err.println(" passed first test, StarTrader started");
      /* Create and display the form */
   
      if(eM.dfe()) eM.doMyErr("fatal error at second run");
      System.err.println("passed second year run");
      System.exit(0);
      System.err.println("oops passed exit");
      } catch (WasFatalError ex) {
ex.printStackTrace(EM.pw);EM.thirdStack=EM.sw.toString();
      eM.flushes();
      System.err.println("Error " + ex.toString() + " " +st.since()  + " " + EM.curEconName  + " " + Thread.currentThread().getName()  + EM.andMore());
      //ex.printStackTrace(System.err);
    System.exit(-15);
      // go to finally
    } catch (Exception | Error ex) {
ex.printStackTrace(EM.pw);EM.secondStack=EM.sw.toString();EM.firstStack = EM.secondStack+"";
      eM.flushes();
      System.err.println(EM.tError=("Error " + ex.toString() + " " + st.since()  + " " + EM.curEconName  + " " + Thread.currentThread().getName() + ", cause=" + ex.getCause() + ",  message=" + ex.getMessage() + " " + EM.andMore()));
     // ex.printStackTrace(System.err);
      eM.flushes();
      //setFatalError();
     throw new WasFatalError(EM.tError);

    } finally {
      if (EM.bKeep != null) {
        EM.bKeep.close();
      }

    }
    //</editor-fold>

  } // main
  
  public TestStarTraderTest(){
    
  }
  
}

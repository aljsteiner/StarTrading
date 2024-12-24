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

 * Display Levels:
 * 1) start and end of run, cause of death worth/health summaries
 * 2) end of each year,
 * 3) adjustments at each iteration/trades
 * 4)  major support for adjustments/trades -- limited
 + 5)  minor support for adjustments/trades -- limited
 * 6) totals of: colonists,engineers,faculty, researchers
 * 7) calculated values min, max, lim grow, rem, fert, health,well
 * 7) trade strategic values, and calculations
 * 8) future calculations
 * 9) more detailed about costs staff, guests res cargo
 * 10)
 * 11) temporary or debugging information
 * 13) auxiliary information
 * 20)lines that are column headers, the last one for 100 lines
 *      becomes the first line of the display
 */
package trade;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 *
 * @author albert steiner
 */
public class History {

  protected String pre = "#";
  protected int level;
  protected String title;
  protected String[] Ss = new String[10];
  Econ ec = EM.curEcon;
  protected int fracs = 3; // max fractions in mf
  static final int yearTotals1 = 1;  // copied to the next year
  static final int yearSubtotals2 = 2; // copied to the next year
  static final int loopIncrements3 = 3;
  static final int loopMajorConditionals4 = 4;
  static final int loopMinorConditionals5 = 5;
  static final int valuesMajor6 = 6;
  static final int valuesMinor7 = 7;
  static final int informationMajor8 = 8;
  static final int informationMinor9 = 9;
  static final int debuggingMajor10 = 10;
  static final int debuggingMinor11 = 11;
  static final int auxInfo = 12;
  static final int aux2Info = 13;
  static final int aux3Info = 14;
  static final int aux4Info = 15;

  static final int aux5Info = 16;
  static final int aux6Info = 17;
  // headers appear as the first row of a section
  static final int headers20 = 20;  // display header rows encounterd rows earlier
  static final int mostDl = 30;
  static final int dl = E.debugLogsOut ? mostDl : 1;
  static final int highestListable = dl;
  // static final int dl = mostDl;

  NumberFormat dFrac = NumberFormat.getNumberInstance();
  NumberFormat dFrac5 = NumberFormat.getNumberInstance();
  NumberFormat whole = NumberFormat.getNumberInstance();
  NumberFormat df5 = dFrac5;
  NumberFormat df = dFrac;
  NumberFormat exp = new DecimalFormat("0.######E0");

  /**
   * Class that holds the history information for the hist logs * The History
   * class generates entries that are added to a economy/environments current
   * hist file. Up to 5 years of files are kept. The files are displayed in
   * StarTrader with the displayLog method using E.logM[0] =
   * E.logEnvirn[0].logM[0]; E.logM[1] = E.logEnvirn[1].logM[1]; as pointers to
   * the 2 log files that can be displayed.
   *
   * @parm lev level for display Display Levels: 1) start and end of run, cause
   * of death worth/health summaries 2) end of each year, 3) adjustments at each
   * iteration/trades 4) major support for adjustments/trades 5) minMax values
   * min..., max... lim... grw... .rem .fun .fert. heal. .well 6) totals of:
   * colonists,engineers,faculty, researchers 7) support for #3
   * interation/trade,minMax 8) future calculations 9) more detailed about costs
   * staff, guests res cargo 10) 11) temporary or debugging information 20)lines
   * that are column headers, the last one for 100 lines becomes the first line
   * of the display ----------------------------------- division between
   * display1 and display2 end end end end end ... end of a display vector
   * @param title a longer string title for the row
   * @params a1-a5...a10 strings for display in the row
   */
  public History() {
    level = 10;
    title = "Empty Title";
    for (int i = 0; i < 10; i++) {
      Ss[i] = "";
    }

  }

  /*
   public History(int lev, String title, String a1, String a2, String a3, String a4, String a5, String a6) {
   shistory("#", lev, title, a1, a2, a3, a4, a5, a6, "", "", "", "");
   }

   public History(int lev, String title, String a1, String a2, String a3, String a4, String a5) {
   shistory("#", lev, title, a1, a2, a3, a4, a5, "", "", "", "", "");
   }

   public History(int lev, String title, String a1) {
   shistory("#", lev, title, a1, "", "", "", "", "", "", "", "", "");

   }
   */
  /**
   * convert the argument string into pieces in title S1..S10
   *
   * @param lev Level of History
   * @param str string to be pieced into the whole row
   *
   * public History(int lev, String str) { level = lev; //this.title = title;
   * int l = str.length(); int k = 10; int i = Math.min(l, 15); title =
   * str.substring(0, i); int j = Math.min(l, i = i + k); for (int m = 0; m < 10; m++, i += k) {
   * j = Math.min(l, i + k);
   * Ss[m] = (i > j ? "++" : str.substring(i, j)); //blanks at end of str } }
   */
  /**
   * store title and 3 text + number variables and a string public
   * History(String aPre,int lev, String atit,String p1, String p2, String p3,
   * String p4, String p5,String p6,String p7) { level = lev; pre = aPre; title
   * = atit; Ss[0]= p1; Ss[1] = p2; Ss[2] = p3; Ss[3] = p4; Ss[4] = p5; Ss[5] =
   * p6;
   *
   * int l = p7.length(); int k = 11; int i = Math.min(l, k); Ss[6] =
   * p7.substring(0, i); int j = Math.min(l, i = i + k); for (int m = 7; m < k; m++, i += k) {
   * j = Math.min(l, i + k);
   * Ss[m] = (i > j ? "++" : p7.substring(i, j)); //blanks at end of str }
   *
   * }
   */
  /**
   * store title and text + variables and a string if variables &lt 11
   *
   * @param aPre
   * @param lev
   * @param sss separate parameters and if length &lt 10 a longer string
   */
  public History(String apre, int alev, String... sss) {
    this.shist(apre, alev, sss);
  }

  String mf(double v) {
    return EM.mf(v);
  }

  /**
   * set title and text + string variables and a string if variables &lt 11
   *
   * @param aPre 2 char identifier of line family
   * @param lev level of the line, used to limit max line level to display
   * @param sss separate parameters and if length &lt 10 a longer string
   */
  void History(String apre, int alev, String[] aaa) {
    this.shist(apre, alev, aaa);
  }

  /**
   * set title and text + variables and a string if variables &lt 11
   *
   * @param aPre 2 char identifier of line family
   * @param lev level of the line, used to limit max line level to display
   * @param sss separate parameters and if length &lt 10 a longer string
   */
  public void shist(String apre, int alev, String[] sss) {
    pre = apre;
    level = alev;
    // int columns = sss.length;
    int sssLen = sss.length;
    int strIx = 0;
    int SsIx = 0;
    int sssIx = 0;
    int endSepColumns = sssLen - 1;// sssLen=11 eSC=10,
    //  int startString = 0;
    int strLen = 0;
    // int strColIx = 0;

    int columnStringLength = 13;
    String str = "";
    if (sssLen == 0) {   // no title no body
      title = "undefined";
    }
    else if (sssLen == 1) { // title then string segments
      strLen = sss[sssIx].length();
      strIx = Math.min(strLen, 20);
      title = sss[sssIx].substring(0, strIx);
      str = sss[sssIx].substring(strIx, strLen);
      strLen = str.length();
      strIx = 0;
      endSepColumns = 0;
    }
    else if (sssLen == 2) {
      title = sss[0];
      sssIx = 1;
      str = sss[sssIx];
      strIx = 0;
      strLen = str.length();
    }
    else if (sssLen > 10) { // do all for the full case;
      sssLen = Math.min(sssLen, 11);
      title = sss[0];
      str = "2ManyCols";
      sssIx = 1;
      endSepColumns = sssLen + 1;
    }
    else if (sssLen > 2) { // up to 11 string parameters
      sssLen = Math.min(sssLen, 11);// no more than 11 columns recognized
      title = sss[0];
      SsIx = 0;
      sssIx = 1;
      endSepColumns = sssLen - 1; // 3=2: title=sss[0],Ss[0] = col[1],sssIx=2, str=string at col[2]
    }

    if (sssLen > 0) {
      // no SepParm if sssLen <=2
      for (; sssIx < endSepColumns && SsIx < 10; sssIx++) {
        Ss[SsIx++] = sss[sssIx];  // sep Parms go to end of line
      }
      if (sssIx < sssLen) {
        String breakers = " ,:;+-_";
        str = sss[sssIx]; // the last parm is the string
        // special processing for sssLen==1 only 1 string
        if (sssLen == 1) {
          strLen = sss[sssIx].length();
          int strIx2 = Math.min(strLen, 20);
          title = sss[sssIx].substring(0, strIx2);
          str = sss[sssIx].substring(strIx2, strLen);
        }

        strLen = str.length();
        strIx = 0;
        // int stringLength = columnStringLength;
        // sssIx is now fixed
        for (; SsIx < 10;) {
          StringBuffer newStr = new StringBuffer(columnStringLength);
          boolean breaked = true;
          //now start next column
          for (int cStrIx = 0; cStrIx < columnStringLength && strIx < strLen; cStrIx++) {
            if (breaked) {
              if (breakers.indexOf(str.charAt(strIx)) < 0) { // no breakers
                newStr.append(str.charAt(strIx++));
                breaked = false;
              }
              else if (breakers.indexOf(str.charAt(strIx)) >= 0) { //breakers at 0
                newStr.append(str.charAt(strIx++));
                // breaked still true
              }
            }
            else if (str.charAt(strIx) == ' ' && newStr.length() <= columnStringLength / 2) {
              newStr.append(str.charAt(strIx++)); // allow blank before half
            }
            else if (breakers.indexOf(str.charAt(strIx)) >= 0) {
              // breaker in the middle of a column, start a new column
              cStrIx = columnStringLength; // force exit inner loop
            }
            else { // breaked == false, not breaker
              newStr.append(str.charAt(strIx++));
            }
          }// end for column filled
          Ss[SsIx++] = newStr.toString();

        }// limit sssIx <= sssLen
      }// for limit on sssLen
    } // sssLen > 0
  }

  /**
   * History Constructor all strings
   *
   * @param alev level of History object
   * @param sss varargs array exit as a created object
   */
  public History(int alev, String... sss) {
    // use a previous constructor
    this.shist(ec.aPre, alev, sss);
  }

  /**
   * a constructor that uses double values converted to strings for Ss
   *
   * @param ss
   * @param aPre
   * @param alev
   * @param atitle
   * @param ddd
   */
  public History(String aPre, int alev, String atitle, double... ddd) {
    level = alev;

    int columns = ddd.length;
    if (columns == 0 || ((alev > History.highestListable) && (alev != 20))) {
      title = "undefined";
      return;
    }
    else { // title then string segments
      pre = aPre;
      level = alev;
      title = atitle;
      boolean tt = EM.mfSS;
      EM.mfSS = true;
      for (int n = 0; n < columns; n++) {
        Ss[n] = mf(ddd[n]);
      }
      // fill out the rest with blanks
      for (int n = columns; n < 10; n++) {
        Ss[n] = "__";
      }
      EM.mfSS = tt;
    }

  }

  /**
   * a recursive sub that calls new History constructor call only if ss == true,
   * alev < highestListable or alev == 20 aPre and atitle != null
   *
   * @param ss must be true if
   * @param aPre
   * @param alev
   * @param atitle
   * @param ddd
   */
  public void dhist(Boolean ss, String aPre, int alev, String atitle, double... ddd) {
    level = alev;
    int columns = ddd.length;
    if (columns == 0 || ss == false || ((alev > History.highestListable) && (alev != 20)) || aPre == null || atitle == null) {
      title = "undefined";
      return;
    }
    else { // title then string segments
      String sss[] = new String[ddd.length + 1];

      title = atitle;
      level = alev;
      pre = aPre;
      boolean tt = EM.mfSS;
      EM.mfSS = true;
      for (int m = 1; m < ddd.length + 1; m++) {
        Ss[m] = mf(ddd[m - 1]);
      }
      EM.mfSS = tt;
    }
  }

  /**
   * a constructor creating an ARow in columns 0 to 6 column 7 min of 0-6 column
   * 8 = sum of 0-6 column 9 = sum/E.lsecs = ave list using up to 7 fraction
   * digits
   *
   * @param p the prefix of this row
   * @param lev the display level of this row
   * @param A the values in A are the first 7 after title
   */
  public History(String p, int lev, String title, ARow A) {
    level = lev;
    this.title = title;
    pre = p;
    boolean tt = EM.mfSS;
    EM.mfSS = true;
    for (int m = 0; m < 7; m++) {
      Ss[m] = mf(A.get(m));
    }
    Ss[7] = mf(A.min(0));
    Ss[8] = mf(A.sum());
    Ss[9] = mf(A.sum() / E.lsecs);
    EM.mfSS = tt;
  }

  /**
   * a constructor creating an ARow in columns 0 to 6 column 7 min of 0-6 column
   * 8 = sum of 0-6 column 9 = sum/E.lsecs = ave
   *
   * @param p the prefix of this row
   * @param lev the display level of this row
   * @param fracd the max faction digits
   * @param A the values in A are the first 7 after title
   */
  public History(String p, int fracd, int lev, String title, ARow A) {
    level = lev;
    this.title = title;
    pre = p;
    fracs = fracd;
    for (int m = 0; m < 7; m++) {
      Ss[m] = df.format(A.get(m));
    }
    Ss[7] = df.format(A.min(0));
    Ss[8] = df.format(A.sum());
    Ss[9] = df.format(A.sum() / E.lsecs);
  }

  /**
   * a constructor listing an ARow in columns 0 to 6 column 7 min of 0-6 column
   * 8 = sum of 0-6 column 9 = sum/E.lsecs = ave
   *
   * @param p
   * @param lev
   * @param A the values in A are the first 7 after title
   * @param V7 = column 7
   * @param V8 = column 8
   * @param V9 = column 9
   */
  public History(String p, int lev, String title, ARow A, String V7, String V8, String V9) {
    level = lev;
    this.title = title;
    pre = p;
    boolean tt = EM.mfSS;
    EM.mfSS = true;
    for (int m = 0; m < 7; m++) {
      Ss[m] = mf(A.get(m));
    }
    Ss[7] = V7;
    Ss[8] = V8;
    Ss[9] = V9;
    EM.mfSS = tt;
  }

  /**
   * a constructor listing an ARow in columns 0 to 6 column 7 min of 0-6 column
   * 8 = sum of 0-6 column 9 = sum/E.lsecs = ave
   *
   * @param p
   * @param lev
   * @param A the values in A are the first 7 after title
   * @param V7 = column 7
   * @param V8 = column 8
   * @param V9 = column 9
   */
  public History(String p, int lev, String title, ARow A, double V7, double V8, double V9) {
    level = lev;
    this.title = title;
    pre = p;

    boolean tt = EM.mfSS;
    EM.mfSS = true;
    for (int m = 0; m < 7; m++) {
      Ss[m] = mf(A.get(m));
    }
    Ss[7] = mf(V7);
    Ss[8] = mf(V8);
    Ss[9] = mf(V9);
    EM.mfSS = tt;
  }

  /**
   * a constructor creating an ARow in columns 0 to 6 column 7 min of 0-6 column
   * 8 = sum of 0-6 column 9 = sum/E.lsecs = ave
   *
   * @param lev
   * @param A the values in A are the first 7 after title
   */
  public History(int lev, String title, ARow A) {
    level = lev;
    this.title = title;

    boolean tt = EM.mfSS;
    EM.mfSS = true;
    for (int m = 0; m < 7; m++) {
      Ss[m] = mf(A.get(m));
    }
    Ss[7] = mf(A.min(0));
    Ss[8] = mf(A.sum());
    Ss[9] = mf(A.sum() / E.lsecs);
    EM.mfSS = tt;
  }

  /**
   * return History with a changed level
   *
   * @param h a saved History
   * @param lev the new level
   * @return History with the new level
   */
  History setLevel(History h, int lev) {
    h.level = lev;
    return h;
  }

  /**
   * change title of a History object
   *
   * @param h inpput
   * @param tit new title
   * @return History with changed title
   */
  History setTitle(History h, String tit) {
    h.title = tit;
    return h;
  }

  double getMin(double[] ar) {
    return ar[getMinIx(ar)];
  }

  int getMinIx(double[] ar) {
    makeOrderIx(ar);
    return (int) ar[7];
  }

  double getSum(double[] ar) {
    double sum = 0;
    for (int m = 0; m < E.lsecs; m++) {
      sum += ar[m];
    }
    return sum;
  }

  void makeOrderIx(double[] ar) {
    if (ar[7] > -.00001 && ar[7] < 6.00001) {
      return;
    }
    double[] min = new double[7];
    // int[] maxIx = new int[4];
    int[] minIx = new int[7];
    double minC, minO;
    double minOIx, minCIx;

    for (int g = 0; g < 7; g++) {
      minC = ar[g];
      minCIx = g;
      for (int k = 0; k < g; k++) {
        if (minC < min[k]) {
          minO = min[k];
          minOIx = ar[k + 7];
          ar[k + 7] = minCIx;
          minCIx = minOIx;
          min[k] = minC;
          minC = minO;
        }
      }
      min[g] = minC;
      ar[g + 7] = minCIx;
    }
  }
}

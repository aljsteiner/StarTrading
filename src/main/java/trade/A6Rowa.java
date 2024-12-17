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

import java.util.ArrayList;
import static trade.ABalRows.A03;
import static trade.ABalRows.BALSLENGTH;

/**
 * 1/2018 this was created to more properly be a model for multiple related rows
 * of information This uses a reference to 6 ARow objects as:
 * <ul><li>a representation of partner costs, and costs related to the 4
 * Assets.CashFlow.SubAssets.
 * <li>partner balances, than Assets.AssetsYr.SubAssets balances of one kind of
 * another, these may either be copies, or references to an ARow in
 * Assets.CashFlow.SubAssets
 * </ul><p>
 * The constructors set a title for the object and a default listing level and
 * perhaps a type. Usually the actual contents are done with a set into the
 * A6Row
 *
 *
 */
public class A6Rowa {

  EM eM = EM.eM;
  Econ ec;
  Assets as;
  String titl = "unSet";
  static final int LSECS = E.lsecs;
  static final int L2SECS = E.l2secs;
  static final int tnone = 0;
  static final int tbal = 1;
  static final int tcost = 2;
  static int d01[] = {0, 1};
  static final int[] I01 = d01;
  static final int[] A01 = d01;
  static final int d02[] = {0, 2};
  static final int A02[] = d02;
  static final int I29[] = {2, 3, 4, 5, 6, 7, 8, 9};
  static final int[] ASECS = E.ASECS;
  static final int[] A2SECS = E.A2SECS;
  static final int[] IA2SECS = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 10, 11, 12, 13};
  static int d03[] = {0, 1, 2, 3};
  static final int[] I03 = d03;
  static final int[] A03 = d03;
  static int d25[] = {2, 3, 4, 5};
  static final int[] I25 = d25;
  static final int[] A25 = d25;
  static int d05[] = {0, 1, 2, 3, 4, 5};
  static final int[] I05 = d05;
  static final int[] A05 = d05;
  static final double NZERO = -.0000001;
  static final double PZERO = .0000001;
  boolean balances = false;  // true if tbal 0 = 2+3 and 1= 4+5
  boolean costs = false; // true if tcost
  boolean costs10 = false; // only for A10Row 0=2,3,4,5 and 1= 6+7+8+9
  static boolean noChecking = false;
  static boolean didResum = false;
  //int lA = ABalRows.balsLength;
  volatile int lA = 6; // A10Row sets to 10
  volatile int dA[] = {0, 1, 2, 3, 4, 5};
  static int d29[] = {2, 3, 4, 5, 6, 7, 8, 9};
  static int BALANCESIX = 2;
  // static final int lsums = 2;// ??
  // static final int LSUMS = 4;//RCSG
  static final int lsubs = 2;
  static final int LSUBS = lsubs;
  volatile ARow[] A = new ARow[lA];
  // reqCosts for r,c,s,g   or rHealth sHealth rFertility sFertility
  volatile double sum[] = {0., 0., 0., 0., 0.}, plusSum[] = {0., 0., 0., 0., 0.}, negSum[] = {0., 0., 0., 0., 0.};
  volatile double minSum[] = {0., 0., 0., 0., 0.}, minSum2[] = {0., 0., 0., 0., 0.};
  volatile int[] aCnt = {-11, -11, -11, -11, -11, -11};// count when last reprocessed
  volatile int[] aResum = {-11, -11, -11, -11, -11, -11};
  volatile int[] dResums = {0, 1};
  int[] mResum1 = {2, 3};
  int[] mResum2 = {4, 5};
  int[][] mResum = {mResum1, mResum2};
  volatile int[] x1 = new int[E.l2secs];
  volatile int[] x2 = new int[E.l2secs];
  volatile int[] x3 = new int[E.l2secs];
  volatile int[] x4 = new int[E.l2secs];
  volatile int[] x5 = new int[E.l2secs];
  volatile int[][] iix = {x1, x2, x3, x4, x5};
  volatile double gradesA[][][]; // [s g][sector][grade]
  static final int LGRADES = 16;
  static final int[] IAGRADES = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
  static final int[] IASUBASSETS = {0, 1, 2, 3};
  static final int LSUBASSETS = 4;
  static final int RIX = ABalRows.RIX, CIX = ABalRows.CIX, SIX = ABalRows.SIX, GIX = ABalRows.GIX;
  static final int RCIX = ABalRows.RCIX;
  static final int SGIX = ABalRows.SGIX;

  String aPre;
  int blev;
  int lev;
  ArrayList<History> hist; // the owner will not change

  /**
   * primary constructor for a new balances zeroed object to be set later
   *
   * @param newEM current or newEM depending on call
   * @param nRows number of rows in object
   * @param bal t == tbal (balances), bal == tcost (costs) otherwise neither
   * @param alev level for listing with sendHist
   * @param atitl title for listing with sendHist
   */
  public A6Rowa(EM newEM, Econ ec, int nRows, int t, int alev, String atitl) {
    A6Rowa1(newEM, ec, nRows, t, alev, atitl);
    // System.out.println("A6Rowa after A.length=" + A.length + ", lA=" + lA + ", dA.length=" + dA.length);
  }

  /**
   * primary constructor for a new balances zeroed object to be set later
   *
   * @param newEM current or newEM depending on call
   * @param nRows number of rows in object
   * @param bal t == tbal (balances), bal == tcost (costs) otherwise neither
   * @param alev level for listing with sendHist
   * @param atitl title for listing with sendHist
   */
  public A6Rowa(Econ newEc, int nRows, int t, int alev, String atitl) {
    A6Rowa1(EM.eM, newEc, nRows, t, alev, atitl);
    // System.out.println("A6Rowa after A.length=" + A.length + ", lA=" + lA + ", dA.length=" + dA.length);
  }

  /**
   * The no parameter constructor
   */
  public A6Rowa(Econ newEc) {
    A6Rowa1(EM.eM, newEc, lA, tbal, History.informationMajor8, "A6Rowa");
  }

  /**
   * constructor helper for a new balances zeroed object to be set later
   *
   * @param newEM current or newEM depending on call
   * @param newEC ec supplied in call to constructor
   * @param nRows number of rows in object
   * @param bal t == tbal (balances), bal == tcost (costs) otherwise neither
   * @param alev level for listing with sendHist
   * @param atitl title for listing with sendHist
   */
  public void A6Rowa1(EM newEM, Econ newEc, int nRows, int t, int alev, String atitl) {
    eM = newEM;
    ec = newEc;
    as = ec.as;
    aPre = ec.aPre = ec.aPre == null ? "&V" : ec.aPre;
    blev = ec.blev = ec.blev == 0 ? History.debuggingMinor11 : ec.blev;
    hist = ec.getHist();
    lA = nRows;
    dA = new int[lA];
    A = new ARow[lA];
    aCnt = new int[lA];
    aResum = new int[lA];
    ec.lev = lev = alev;
    balances = t == tbal;
    costs = t == tcost;
    titl = atitl;
    gradesA = new double[4][][]; // only 2 && 3 should have grades

    // try leaving the rest empty until used
    int ll = lA;
    for (int n = 0; n < ll; n++) {
      dA[n] = n;
      A[n] = new ARow(ec).zero();
      aCnt[n] = -11;
      aResum[n] = -11;
    }
    if (balances == true && (nRows == 6 || nRows == ABalRows.BALSLENGTH)) {
      // zero the subassets 2,3 staff,guests
      gradesA = new double[4][][];
      for (int i = 2; i < LSUBASSETS; i++) {
        gradesA[i] = new double[LSECS][];
        // construct the sectors
        for (int m : ASECS) {
          // copy the sectors
          // construct the grades vector
          gradesA[i][m] = new double[LGRADES];
          // zero the grades
          for (int n : IAGRADES) {
            gradesA[i][m][n] = 0.;
          }
        }
      }
    }

    //  System.out.println("A6Rowa1 before A.length =" + A.length + ", lA=" + lA + ", lev=" + lev + ", title=" + titl);
  }

  /**
   * make a balances subClone with the balances part of ABalRow The rows
   * reference the same ARow instances as in bals
   *
   * @return
   */
  A6Row makeA6() {
    A6Row ret = new A6Row(ec);
    for (int i = 0; i < 6; i++) {
      ret.A[i] = A[i];
    } // copy row references
    ret.aResum = aResum;
    ret.balances = true;
    ret.gradesA = gradesA;
    ret.titl = "Bal6s";
    return ret;
  }

  /**
   * set sendHist hist listing type balances, costs or neither
   *
   * @param t t == tbal for balances, t=tcost for costs otherwise neither for
   * balances identify the six rows rc,sg,r,c,s,g for costs identify the six
   * rows as rc,sg,r.rc,s.rc,r.sg,s.sg otherwise identify the six rows as
   * rc,sg,2,3,4,5
   * @return t
   */
  int setType(int t) {
    balances = t == tbal;
    costs = t == tcost;
    return t;
  }

  /**
   * make sure all values are slightly more than zero set only values < UNZERO
   * to UNZERO
   *
   * @param tit
   * @param start
   * @param number
   * @return
   */
  A6Rowa unzero(String tit, int start, int number) {
    for (int rowIx = 0; rowIx < number; rowIx++) {
      String tt = tit + start + rowIx;
      A[start + rowIx].unzero(tt);
    }
    return this;
  }

  double doubleTrouble(Double trouble) {
    if (trouble.isNaN()) {
      if (E.debugDouble) {
        throw new MyErr(String.format("Not a number found, term%d, i%d, j%d, m%d, n%d, rowIx%d", as.term, as.i, as.j, as.m, as.n, as.rowIx));
      }
      else {
        return 0.0;
      }
    }
    if (trouble.isInfinite()) {
      if (E.debugDouble) {
        throw new MyErr(String.format("Infinite number found, term%d,i%d,j%d,m%d,n%d", as.term, as.i, as.j, as.m, as.n));
      }
      else {
        return 100.0;
      }
    }
    return (double) trouble;
  }

  /**
   * make a copy of the grades array
   *
   * @param gr old grades array
   * @return new copy of gr;
   */
  double[][] copyGrades(double[][] gr) {// Assets.CashFlow.SubAsset
    double[][] ret = new double[E.lsecs][];
    for (int m : ASECS) {
      ret[m] = new double[LGRADES];
      for (int n = 0; n < LGRADES; n++) {
        ret[m][n] = gr[m][n];
      }
    }
    return ret;
  }

  /**
   * return reference to copy of a set of grades probably unused
   *
   * @param n index of which grades 2=staff,3=guests
   * @return a reference to gradesA[n]
   */
  double[][] getGradesCopy(int n) {

    // zero the subassets 2,3 staff,guests
    // only zero null 2,3
    if (gradesA[n] == null && n > 1 && n < 4) {
      gradesA[n] = new double[E.lsecs][];
      // construct the sectors
      for (int m : ASECS) {
        // copy the sectors
        if (gradesA[n][m] == null) {
          // construct the grades vector
          gradesA[n][m] = new double[LGRADES];
          // zero the grades
          for (int i : IAGRADES) {
            gradesA[n][m][i] = 0.;
          }
        }
      }
      A[n].addCnt();   // into the ARow
    }
    return gradesA[n]; // return reference
  }

  /**
   * return reference to a set of grades staff & guests
   *
   * @param n index of which grades 2=staff,3=guests
   * @return a reference to gradesA[n]
   */
  double[][][] getGrades() {
    // double bGrades[][][] = gradesA;
    // bGrades[sIx] = myGrades[;
    return gradesA;
  }

  /**
   * get the grades reference for Guests
   *
   * @return guest grades array
   */
  double[][] getGuestGrades() {
    return gradesA[3];
  }

  /**
   * get the grades reference for Staff
   *
   * @return staff grades array
   */
  double[][] getStaffGrades() {
    return gradesA[2];
  }

  /**
   * set level to be used in listing in hist with sendHist in object
   *
   * @param l listing level
   * @return listing level
   */
  int setLevel(int l) {
    return ec.lev = lev = l;
  }

  /**
   * set title in object
   *
   * @param t string for title
   * @return titl
   */
  String setTitle(String t) {
    return titl = t;
  }

  /**
   * zero all values in the A6Rowa
   *
   * @return this
   */
  public A6Rowa zero() {
    for (int m = 0; m < lA; m++) {
      for (int n : ASECS) {
        set(m, n, 0.);
      }
    }

    // zero the subassets 2,3 staff,guests
    for (int i = 2; i < LSUBASSETS; i++) {
      if (gradesA[i] == null) {
        gradesA[i] = new double[LSECS][];
      }
      // construct the sectors
      for (int m : ASECS) {
        // copy the sectors
        if (gradesA[i][m] == null) {
          // construct the grades vector
          gradesA[i][m] = new double[LGRADES];
        }
        // zero the grades
        for (int n : IAGRADES) {
          gradesA[i][m][n] = 0.;
        }
      }
    }
    return this;
  }

  /**
   * copy A6Rowa object, copy each by each of calling A6Row use as A6Row b =
   * a.copy(); no change to a, rtn is a new object lev,titl,balances,costs,blev
   * are all copied as well as A[] values
   *
   * @return new object copy of this object
   */
  public A6Rowa copy(Econ newEc) {
    ec = newEc;
    String tit = titl + ""; // force a new reference
    return this.copy(lev, tit, EM.eM, newEc);
  }

  /**
   * copy A6Rowa object, copy each by each of calling A6Rowa use as A6Rowa b =
   * a.copy(); no change to a, rtn is a new object lev,titl,balances,costs,blev
   * are all copied as well as A[] values
   *
   * @param lev the display level for this A6Rowa routine
   * @return new object copy of this object
   */
  public A6Rowa copy(int lev, Econ newEc) {
    ec = newEc;
    String tit = titl + ""; // force a new reference
    return copy(lev, tit, EM.eM, newEc);
  }

  /**
   * copy A6Rowa object, copy each by each of calling A6Rowa use as A6Rowa b =
   * a.copy(); no change to a, rtn is a new object lev,titl,balances,costs,blev
   * are all copied as well as A[] values
   *
   * @param lev the display level for this A6Rowa routine
   * @param tit the new title for the copy
   * @return new object copy of this object
   */
  public A6Rowa copy(int lev, String tit, EM newEM, Econ newEc) {
    int t = balances ? tbal : costs ? tcost : tnone;
    A6Rowa rtn = new A6Rowa(newEM, newEc, lA, t, lev, tit + "");
    rtn.blev = blev;
    rtn.eM = newEM;
    for (int m = 0; m < lA; m++) {
      if (A[m] != null) {
        rtn.A[m] = A[m].copy();
      }
      else {
        rtn.A[m] = new ARow(ec).zero();
      }
    }
    // check about doing gradesA
    if (gradesA != null) {
      // construct number of subassets
      rtn.gradesA = new double[LSUBASSETS][][];
      for (int ii = 2; ii < LSUBASSETS; ii++) {
        rtn.gradesA[ii] = new double[E.LSECS][];
        for (int mm = 0; mm < E.LSECS; mm++) {
          rtn.gradesA[ii][mm] = new double[E.LGRADES];
        }
      }

      // copy the subassets 2,3 if they exist
      for (int i = 2; i < LSUBASSETS; i++) {
        if (gradesA[i] != null) {
          // construct the sectors
          for (int m : ASECS) {
            // copy the sectors
            if (gradesA[i][m] != null) {
              // copy the grades
              for (int n : IAGRADES) {
                if (E.debugDouble) {
                  rtn.gradesA[i][m][n] = doubleTrouble(gradesA[i][m][n]);
                }
                else {
                  rtn.gradesA[i][m][n] = gradesA[i][m][n];
                }
              }
            }
          }
        }
      }

    }
    rtn.mResum[0] = rtn.mResum1;
    rtn.mResum[1] = rtn.mResum2;

    // now move values
    for (int i = 0; i < iix.length; i++) {
      rtn.sum[i] = doubleTrouble(sum[i]);
      rtn.plusSum[i] = doubleTrouble(plusSum[i]);
      rtn.negSum[i] = doubleTrouble(negSum[i]);
      rtn.minSum[i] = doubleTrouble(minSum[i]);
      rtn.minSum2[i] = doubleTrouble(minSum2[i]);
      rtn.aCnt[i] = aCnt[i];
      rtn.aResum[i] = aResum[i];
      rtn.aResum[i] = aResum[i];
      //now copy ix
      for (int j = 0; j < E.L2SECS; j++) {
        if (iix[i] != null) {
          rtn.iix[i][j] = iix[i][j];
        }
      }
    }
    return rtn;
  }

  public A6Rowa set(A6Rowa aa) {
    int t = aa.balances ? tbal : aa.costs ? tcost : tnone;
    for (int m = 0; m < lA; m++) {
      if (aa.A[m] != null) {
        this.A[m] = new ARow(ec);
      }
    }
    // check about doing gradesA
    if (aa.gradesA != null) {
      // construct number of subassets
      gradesA = new double[LSUBASSETS][][];

      // copy the subassets 2,3 if they exist
      for (int i = 2; i < LSUBASSETS; i++) {
        if (aa.gradesA[i] != null) {
          // construct the sectors
          gradesA[i] = new double[E.LSECS][];
          for (int m : ASECS) {
            // copy the sectors
            if (gradesA[i][m] != null) {
              gradesA[i][m] = new double[E.lgrades];
              // copy the grades
              for (int n : IAGRADES) {
                if (E.debugDouble) {
                  gradesA[i][m][n] = doubleTrouble(aa.gradesA[i][m][n]);
                }
                else {
                  gradesA[i][m][n] = aa.gradesA[i][m][n];
                }
              }
            }
          }
        }
      }
    }
    mResum[0] = aa.mResum1;
    mResum[1] = aa.mResum2;

    noChecking = true;
    // now move values
    for (int i = 0; i < iix.length; i++) {
      sum[i] = doubleTrouble(aa.sum[i]);
      plusSum[i] = doubleTrouble(aa.plusSum[i]);
      negSum[i] = doubleTrouble(aa.negSum[i]);
      minSum[i] = doubleTrouble(aa.minSum[i]);
      minSum2[i] = doubleTrouble(aa.minSum2[i]);
      aCnt[i] = aa.aCnt[i];
      aResum[i] = aa.aResum[i];
      aResum[i] = aa.aResum[i];
      //now copy ix
      for (int j = 0; j < E.L2SECS; j++) {
        if (iix[i] != null) {
          iix[i][j] = aa.iix[i][j];
        }
      }
    }
    noChecking = false;
    return this;
  }

  public ABalRows copyBals(Econ ec, int lev, String tit, EM newEM) {
    ABalRows rtn = new ABalRows(ec);
    rtn.blev = blev;
    rtn.eM = newEM;
    for (int m = 0; m < lA; m++) {
      if (A[m] != null) {
        rtn.A[m] = A[m].copy();
      }
    }
    // check about doing gradesA
    if (gradesA != null) {
      // construct number of subassets
      rtn.gradesA = new double[LSUBASSETS][][];

      // copy the subassets 2,3 if they exist
      for (int i = 2; i < LSUBASSETS; i++) {
        if (gradesA[i] != null) {
          // construct the sectors
          rtn.gradesA[i] = new double[E.LSECS][];
          for (int m : ASECS) {
            // copy the sectors
            if (gradesA[i][m] != null) {
              rtn.gradesA[i][m] = new double[E.lgrades];
              // copy the grades
              for (int n : IAGRADES) {
                if (E.debugDouble) {
                  rtn.gradesA[i][m][n] = doubleTrouble(gradesA[i][m][n]);
                }
                else {
                  rtn.gradesA[i][m][n] = gradesA[i][m][n];
                }
              }
            }
          }
        }
      }

    }
    rtn.mResum[0] = rtn.mResum1;
    rtn.mResum[1] = rtn.mResum2;

    // now move values
    for (int i = 0; i < iix.length; i++) {
      rtn.sum[i] = doubleTrouble(sum[i]);
      rtn.plusSum[i] = doubleTrouble(plusSum[i]);
      rtn.negSum[i] = doubleTrouble(negSum[i]);
      rtn.minSum[i] = doubleTrouble(minSum[i]);
      rtn.minSum2[i] = doubleTrouble(minSum2[i]);
      rtn.aCnt[i] = aCnt[i];
      rtn.aResum[i] = aResum[i];
      rtn.aResum[i] = aResum[i];
      //now copy ix
      for (int j = 0; j < E.L2SECS; j++) {
        if (iix[i] != null) {
          rtn.iix[i][j] = iix[i][j];
        }
      }
    }
    return rtn;
  }

  /**
   * copy calling instance to a new instance with new references
   *
   * @param newEM possible change EM or use existing EM
   * @return
   */
  public A6Rowa newCopy(EM newEM, Econ newEc) {
    return copy(lev, titl, newEM, newEc);
  }

  /**
   * copy the values of prev into this, particularly do not change the reference
   * of each ARow
   *
   * @param prev a previous copy of this A6Row
   * @return this with updated values and setCnt
   */
  public A6Rowa copyValues(A6Rowa prev) {
    if (prev != null) {
      int len = A.length;
      for (int m = 0; m < len; m++) {
        for (int n = 0; n < E.LSECS; n++) {
          if (E.debugDouble) {
            set(m, n, doubleTrouble(prev.get(m, n)));
          }
          else {
            set(m, n, prev.get(m, n));
          }
        }
      }// end m     
    }
    return this;
  }

  /**
   * copy to A6Row
   *
   * @param startIx //location of in this AxRow, 2 if this is A6Row
   * @param aLev // new A6Row level
   * @param aTitl // new A6Row title
   * @return A6Row with 0,1 fixed
   */
  A6Row copy6(int startIx, int aLev, String aTitl) {
    A6Row rtn = new A6Row(ec, aLev, aTitl).zero();
    rtn.balances = true;
    rtn.titl = aTitl + "";
    rtn.lev = aLev;
    rtn.balances = true;

    for (int m : A03) {
      for (int n : E.ASECS) {
        if (E.debugDouble) {
          rtn.A[m + LSUBS].set(n, doubleTrouble(this.A[startIx + m].get(n)));
        }
        else {
          rtn.A[m + LSUBS].set(n, this.A[startIx + m].get(n));
        }
      }
    }
    return rtn;
  }

  /**
   * return a copy of rows 0,1
   *
   * @param lev level of new A2Row
   * @param tit title of new A2Row
   * @return a new A2Row each value copied from this A10Row
   */
  public A2Row copy2(int lev, String tit) {
    A2Row rtn = new A2Row(ec, lev, tit);
    resum(0);
    resum(1);
    rtn.A[0].set(A[0]);
    rtn.A[1].set(A[1]);
    return rtn;
  }

  /**
   * create an ordered least to most set of indexes in ix for rows with indexes
   * rowIx1 and rowIx2
   *
   * @param ix the ordered array of index pointers into rowIx1(0-6) and
   * rowIx2(7-13) where
   * @param x index of sums, minSum, minSum2,negSum,plusSum 0,1,2,3,4
   * @param sum sum of the values of A[rowIx1] and A[
   * @param rowIx1 index of first row
   * @param rowIx2 index of second row
   */
  synchronized void makeOrderIx(int[] ix, int x, double[] sum, int rowIx1, int rowIx2) {
    E.myTest(A[rowIx1] == null, " ARow a=%2d not defined", rowIx1);
    E.myTest(A[rowIx2] == null, " ARow b=%2d not defined", rowIx2);
    E.myTest(x < 0 || x > 4, "x index=%2d is < 0 or > 4", x);
    double[] min = new double[E.l2secs];
    int[] minIx = new int[E.l2secs];
    if (x < 0 || x > 4) {
      eM.aErr("x index=%2d not > -1 and < 5", x);
    }
    double minC, minO;
    int minOIx, minCIx;
    aCnt[rowIx1] = A[rowIx1].getSetCnt();
    aCnt[rowIx2] = A[rowIx2].getSetCnt();
    minSum[x] = minSum2[x] = negSum[x] = plusSum[x] = sum[x] = 0.;
    for (int g = 0; g < L2SECS; g++) {
      if (g < LSECS) {
        minC = get(rowIx1, g); //A[rowIx1].values[g];
      }
      else {
        minC = get(rowIx2, g - E.LSECS); //A[rowIx2].values[g - LSECS];
      }
      sum[x] += minC;
      if (minC < NZERO) {
        negSum[x] += minC;
      }
      if (minC > PZERO) {
        plusSum[x] += minC;
      }
      minCIx = g;
      // insert minC into the slot before the first higher value
      for (int k = 0; k < g; k++) {
        if (minC < min[k]) {
          minO = min[k];
          minOIx = ix[k];
          ix[k] = minCIx;
          minCIx = minOIx;
          min[k] = minC;
          minC = minO;
        }
      }
      min[g] = minC;
      ix[g] = minCIx;
    }
    for (int g = 0; g < eM.minSumCnt; g++) {
      minSum[x] += min[g];
    }
    for (int g = 0; g < eM.minSum2Cnt; g++) {
      minSum2[x] += min[g];
    }
  }

  /**
   * again sum rc or sg if m %eq; one of them aResum contains the sum of setCnts
   * for the 2 ARows sumed to this sum Row aResum is set to the sum of setCnts
   * when a resum is dome
   *
   * @param m The number of the index in A for ARow for to be a sum, if m is not
   * a sum row ignore
   *
   */
  void resum(int m) {
    if (m < 100) {
      return; // disable resum
    }//    System.out.println("into resum title=" + titl + " la=" + lA + ", length A=" + A.length + ", m=" + m);
    if (A[m] == null) {
      A[m] = new ARow(ec);
    }
    int ma = m < 2 ? m : (m - lsubs) / lsubs; // find proper rc or sg
    for (int mm : dResums) { // iterate over arrays of iX values and ARows
      if (mm == ma) {
        // check for a change since the last calculation of mResum[mm]
        int sumr = 0;
        for (int nn : mResum[mm]) {  // check the full iX array for changes
          sumr += A[nn].getSetCnt();
        }
        didResum = false;
        if (sumr != aResum[mm]) {  // check for changes since last resum
          // resum A[mm], by adding in all mResum rows
          ARow bb = new ARow(ec).set(A[mm]); // save prev value
          A[mm].zero();  //zero so we can do adds of subvalues
          //    System.out.printf("do resum %s ARow %2d add", titl, mm);
          for (int rr : mResum[mm]) { // set by sub class
            //       System.out.printf(" ARow %2d", rr);
            for (int q : E.ASECS) {// add to rc or sg
              if (E.debugDouble) {
                A[mm].add(q, doubleTrouble(A[rr].get(q)));  // add in appropriate values
              }
              else {
                A[mm].add(q, A[rr].get(q));  // add in appropriate values 
              }
            }
          }
          //       System.out.println("");
          // now add the new sum for aResum sum of setCnt s
          aResum[mm] = 0;
          for (int nn : mResum[mm]) {
            aResum[mm] += A[nn].getSetCnt();
          }
          didResum = true;
        }
      }
    }
  }

  /**
   * check whether the base pair has been changed since the last makeOrderIx and
   * call makOrderIx if a value has been potentially changed.
   *
   * @param x
   */
  void checkIx(int x) {
    resum(x * 2);   // do resum if necessary
    resum(1 + x * 2);
    if (aCnt[x * 2] != A[x * 2].getSetCnt() || aCnt[1 + x * 2] != A[1 + x * 2].getSetCnt()) {
      makeOrderIx(iix[x], x, sum, x * 2, 1 + x * 2);
    }
  }

  /**
   * find the n from the min value of rows 0,1
   *
   * @param x index generated from some other object
   * @return the min n'th index
   */
  int findMinIx(int x) {
    checkIx(0);
    for (int m : IA2SECS) {
      if (A[m] == null) {
        A[m] = new ARow(ec);
      }
      if (x == iix[0][m]) {
        return m;
      }
    }
    return L2SECS - 1;

  }

  /**
   * find the min index from the value of rows 0,1
   *
   * @param x some value in rows 0,1
   * @return the min n'th index
   */
  int findMinIx(double x) {
    checkIx(0);
    for (int m : IA2SECS) {
      if (A[m] == null) {
        A[m] = new ARow(ec);
      }
      if (x == curGet(iix[0][m])) {
        return m;
      }
    }
    return L2SECS - 1;

  }

  /**
   * get the index of a ordered A2Row: cur
   *
   * @param nn the n'th from minimum
   * @return min+ix of the selected orderedpair
   */
  int get01Ix(int nn) {
    checkIx(0);
    return iix[0][nn];
  }

  /**
   * check for some screwup that loses the connection to real balances and
   * grades
   *
   * @param cr
   */
  public void checkBalances(Assets.CashFlow cr) {
    E.myTest(cr.r.balance != A[2], "r connection lost");
    E.myTest(cr.r.balance != cr.as.bals.A[2], "bals r connection lost");
    E.myTest(cr.c.balance != cr.as.bals.A[3], "bals c connection lost");
    E.myTest(cr.c.balance != A[3], "c connection lost");
    E.myTest(cr.s.balance != A[4], "s connection lost");
    E.myTest(cr.s.balance != cr.as.bals.A[4], "bals s connection lost");
    E.myTest(cr.g.balance != A[5], "g connection lost");
    E.myTest(cr.g.balance != cr.as.bals.A[5], "bals g connection lost");
    E.myTest(cr.c.partner.balance != A[2], "c.partner connection lost");
    E.myTest(cr.r.partner.balance != A[3], "r.partner connection lost");
    E.myTest(cr.g.partner.balance != A[4], "g.partner connection lost");
    E.myTest(cr.s.partner.balance != A[5], "s.partner connection lost");
    // so a test for null pointer
    double[][][] bb = as.bals.gradesA;
    if (E.debugSumGrades) {
      if (as.bals.gradesA[2]
          != as.cur.s.grades) {
        throw new MyErr(String.format("bals grades != s.grades, term%d, i%d, j%d, m%d, n%d", as.term, as.i, as.j, as.m, as.n));
      }
      if (as.bals.gradesA[3]
          != as.cur.g.grades) {
        throw new MyErr(String.format("bals grades != g.grades, term%d, i%d, j%d, m%d, n%d", as.term, as.i, as.j, as.m, as.n));
      }
      as.cur.s.checkSumGrades();
      as.cur.g.checkSumGrades();
    }
  }

  /**
   * sum of rows 0,1
   *
   * @return sum of rows 0,1
   */
  double curSum() {
    checkIx(0);
    return sum4();
  }

  /**
   * find the average of rows 0,1
   *
   * @return curSum() * E.invL2secs
   */
  double curAve() {
    checkIx(0);
    return sum4() * E.invL2secs;
  }

  /**
   * get sum of plus values in rows 0,1
   *
   * @return
   */
  double curPlusSum() {
    checkIx(0);
    return plusSum[0];
  }

  /**
   * get the sum of the {@value Assets#minSumCnt} least values rows 0,1
   *
   * @return
   */
  double curMinSum() {
    checkIx(0);
    return minSum[0];
  }

  /**
   * get the sum of the {@value Assets#minSum2Cnt} least values rows 0,1
   *
   * @return
   */
  double curMinSum2() {
    checkIx(0);
    return minSum2[0];
  }

  /**
   * get sum of neg values in rows 0,1
   *
   * @return
   */
  double curNegSum() {
    checkIx(0);
    return negSum[0];
  }

  /**
   * sum the unit balances r c s g all sectors returns the same value as curSum,
   * but always sums the individual SubAsset balances
   *
   * @param bias the bias to the first element
   *
   * @return sum each value in rows 9 1 2 3;
   */
  double sum4(int bias) {
    double sum = 0;
    for (int m : d03) {
      if (A[bias + m] != null) {
      for (int n : ASECS) {
        sum += get(bias + m, n);
        }
      }
    }
    return sum;
  }

  /**
   * copy the values ref from 4 rows starting at b to rows starting at c
   *
   * @param b bias of source rows
   * @param c bias of destination rows
   */
  public void copy4BtoC(int b, int c) {
    for (int rowIx : A03) {
      if (this.A[b + rowIx] != null) {
        if (this.A[c + rowIx] == null) {
          this.A[c + rowIx] = new ARow(ec);
        }
        for (int secIx : E.ASECS) {
          this.A[c + rowIx].values[secIx] = this.A[b + rowIx].values[secIx];
        }
      }
    }
  }

  /**
   * copy the values from ABalRows prev to this but do not change any
   * references. This is used for swap redo and must not change the references
   * of this
   *
   * @param prev new values from a HSwaps previous value
   * @return the revised values for this with no this references changed
   */
  public A6Rowa copyValues(ABalRows prev) {
    int m = 0;
    for (m = 0; m < BALSLENGTH; m++) {
      if (A[m] != null) {
        if (prev.A[m] == null) {
          prev.A[m] = new ARow(ec);
        }
        for (int n : E.ASECS) {
          {
            //  A[m].set(n, prev.A[m].get(n));
            if (prev.A[m] != null) {
              this.A[m].values[n] = prev.A[m].values[n];
            }
          }
        }
      }
    }// end m
    // ABalRows always has these grades define with some values
    for (int i = 2; i < 4; i++) {
      for (m = 0; m < LSECS; m++) {
        for (int n = 0; n < LGRADES; n++) {
          this.gradesA[i][m][n] = prev.gradesA[i][m][n];
        }
      }
    }// end i
    return this;
  }

  /**
   * sum the unit balances rc sg all sectors returns the same value as curSum,
   * but always sums the individual SubAsset balances
   *
   * @param bias the bias to the first element
   *
   * @return sum each value in rows 0 2;
   */
  double sum2(int bias) {
    double sum = 0;
    for (int m : d01) {
      for (int n : ASECS) {
        sum += get(bias + m, n);
      }
    }
    return sum;
  }

  /**
   * sum the unit balances r c s g all sectors returns the same value as curSum,
   * but always sums the individual SubAsset balances
   *
   * @return sum each value in rows 2-5;
   */
  double sum4() {
    return sum4(BALANCESIX);
  }

  /**
   * copy 1 row of values from rows biasA to biasB
   *
   * @param biasA the index of the row of the sources
   * @param biasB the index of the row of targets
   */
  void copy1AtoB(int biasA, int biasB) {
    for (int secIx : E.ASECS) {
      // A[biasB + rowIx].set(secIx, A[biasA + rowIx].get(secIx));
      A[biasB].values[secIx] = A[biasA].values[secIx];
    }
  }

  /**
   * get the n'th value from rows 0 and 1 row1 0-6, row2 7-13
   *
   * @param nn
   * @return n'th value
   */
  double get01(int nn) {
    int m = (nn / E.lsecs) % 2;
    if (A[m] == null) {
      A[m] = new ARow(ec);
    }
    int n = nn % E.lsecs;
    return get(m, n);
  }

  /**
   * get the n'th value from rows 0 and 1 row1 0-6, row2 7-13
   *
   * @param nn
   * @return n'th value
   */
  double curGet(int nn) {
    int m = (nn / E.lsecs) % 2;
    if (A[m] == null) {
      A[m] = new ARow(ec);
    }
    resum(m);
    int n = nn % E.lsecs;
    return get(m, n);
  }

  /**
   * get index from rows 01, row m, sector n
   *
   * @param m
   * @param n
   * @return
   */
  int get01Ix(int m, int n) {
    return get01Ix((m % 2) * E.lsecs + n % E.lsecs);
  }

  /**
   * get only in rows 0 and 1 m,n
   *
   * @param m
   * @param n
   * @return value n in row m
   */
  double get01(int m, int n) {
    return get(m, n);
    // return get((m % 2), n % E.lsecs);
  }

  /**
   * get index of least value
   *
   * @see getIx(n) get n'th from the least value
   * @return index of least value
   */
  int curMinIx() {
    return get01Ix(0);
  }

  /**
   * return Ix of the n'th min value of rows 0,1
   *
   * @param n
   * @return Ix of min
   */
  int curMinIx(int n) {
    return get01Ix(n);
  }

  /**
   * return Ix of the n'th max of rows 9,1
   *
   * @param n
   * @return
   */
  int curMaxIx(int n) {
    return get01Ix(E.l2secs - 1 - n);
  }

  /**
   * return the n'th less the the largest value of rows 0,1 (cur)
   *
   * @param n
   * @return
   */
  double curMax(int n) {
    return curMin(E.l2secs - 1 - n);
  }

  protected Object clone() throws CloneNotSupportedException {
    return super.clone(); //To change body of generated methods, choose Tools | Templates.
  }

  /**
   * get least value of rows 0,1
   *
   * @return value
   */
  double curMin() {
    return curMin(0);
  }

  /**
   * get the nn'th greater than the least value in rows 0,1
   *
   * @param nn
   * @return
   */
  double curMin(int nn) {
    int x = get01Ix(nn);  // find index of lowest element
    int m = x / LSECS;  // index of row
    int n = x % LSECS;  // index within the row
    return get(m, n);
  }

  /**
   * get internal ARow m
   *
   * @param m index of row to get
   * @return reference to ARow n
   */
  public ARow getRow(int m) {
    if (A[m] == null) {
      A[m] = new ARow(ec);
    }
    resum(m);
    return A[m];
  }

  /**
   * get value of internal ARow n, sector m
   *
   * @param m row in the object
   * @param n sector in the balances
   * @return A[n].get(m)
   */
  public double get(int m, int n) {
    if (A[m] == null) {
      A[m] = new ARow(ec);
    }
    if (m > 1) {
      if (E.debugDouble) {
        return doubleTrouble(A[m].values[n]);
      }
      else {
        return A[m].values[n];
      }
    }
    else if (m == 0) {
      if (E.debugDouble) {
        return doubleTrouble(A[0].values[n] = A[2].values[n] + A[3].values[n]);
      }
      else {
        return A[0].values[n] = A[2].values[n] + A[3].values[n];
      }
    }
    else if (m == 1) {
      if (E.debugDouble) {
        return doubleTrouble(A[1].values[n] = A[4].values[n] + A[5].values[n]);
      }
      else {
        return A[1].values[n] = A[4].values[n] + A[5].values[n];
      }
    }
    else {
      int al = A.length;
      assert m < A.length : "get(m,n) error m" + m + " indexes more than length" + al;
      int mm = m < 2 ? m : (m - lsubs) / lsubs; // find proper rc or sg
      resum(m);
      boolean ignoreIf = !(al == 6 || al == ABalRows.BALSLENGTH || al == 10) || !(balances && costs10) || m < 0 || costs10 ? m > 9 : balances ? m > 5 : false;
      double bal1 = 0.;
      double both = 0.;
      if (E.debugResumP && !ignoreIf && !noChecking) {
        bal1 = gett(mm, n);
        both = gett(lsubs + 0 + mm * lsubs, n) + gett(lsubs + 1 + mm * lsubs, n);
        // add in the 10row if needed
        both += ignoreIf ? 0.0 : costs10 ? gett(lsubs + 2 + mm * lsubs, n) + gett(lsubs + 3 + mm * lsubs, n) : 0.0;
        // ignore test if ignoreIf is true

        double dif = bal1 - both;
        boolean badDif = E.PZERO < dif || E.NZERO > -dif; // trouble if true
        // assert error only if ignoreIf is false and badDif is true , costs10 both has 4 values
        assert !badDif : "resum error sector" + n + " length" + al + " lsubs" + lsubs + (noChecking ? " noChecking" : " !noChecking") + (didResum ? " didResum" : " !didResum") + " m" + m + " mm" + mm + "=" + EM.mf(bal1) + " noteq dif" + dif + " both" + EM.mf(both) + (costs10 ? " r" + EM.mf(gett(2 + mm * lsubs, n)) + " c" + EM.mf(gett(3 + mm * lsubs, n)) + " s" + EM.mf(gett(4 + mm * lsubs, n)) + " g" + EM.mf(gett(5 + mm * lsubs, n)) : " working" + EM.mf(gett(2 + mm * lsubs, n)) + " reserve" + EM.mf(gett(3 + mm * 2, n)));
      }
      if (noChecking) {
        noChecking = true;
      }
      if (E.debugDouble) {
        return doubleTrouble(A[m].get(n));
      }
      else {
        return A[m].get(n);
      }
    }
  }

  /**
   * get the nn'th element in the values of the object
   *
   * @param nn the number of the element
   * @return
   */
  public double get(int nn) {
    return get(nn / E.LSECS, nn % E.LSECS);
  }

  /**
   * get the value of rows 0,1 by sector m = m%2 resum m%2
   *
   * @param m index of row to use
   * @param n index of value in selected row
   * @return the value of value n in the selected row
   */
  public double gett(int m, int n) {
    if (A[m] == null) {
      A[m] = new ARow(ec);
    }
    if (m > 1) {
      if (E.debugDouble) {
        return doubleTrouble(A[m].values[n]);
      }
      else {
        return A[m].values[n];
      }
    }
    else if (m == 0) {
      if (E.debugDouble) {
        return doubleTrouble(A[0].values[n] = A[2].values[n] + A[3].values[n]);
      }
      else {
        return A[0].values[n] = A[2].values[n] + A[3].values[n];
      }
    }
    else if (m == 1) {
      if (E.debugDouble) {
        return doubleTrouble(A[1].values[n] = A[4].values[n] + A[5].values[n]);
      }
      else {
        return A[1].values[n] = A[4].values[n] + A[5].values[n];
      }
    }
    return 0;
  }

  /**
   * get the value of balances working row by sector resum m%2
   *
   * @param m index of working row
   * @param n index of value in selected row
   * @return the value of value n in the selected row
   */
  public double gett1(int m, int n) {
    if (A[m] == null) {
      A[m] = new ARow(ec);
    }
    if (balances) {
      resum((m % 2 * 2) + 2);
      return get((m % 2 * 2) + 2, n); //0r = 2,s1 = 4;
    }
    else {
      resum((m % 2) + 2);
      return get((m % 2) + 2, n); // r0=2, s1=3
    }
  }

  /**
   * get the value of balances reserve row by sector resum m%2
   *
   * @param m index of reserve row
   * @param n index of value in selected row
   * @return the value of value n in the selected row
   */
  public double gett2(int m, int n) {
    if (A[m] == null) {
      A[m] = new ARow(ec);
    }
    if (balances) {
      resum((m % 2 * 2) + 3);
      return get((m % 2 * 2) + 3, n); // 0r =4,1s=5
    }
    else {
      resum((m % 2 * 2) + 2);
      return get((m % 2 * 2) + 2, n); // 0r =3,1s=5
    }
  }

  /**
   * set this to joint balances, and references of SubAsset balances
   *
   * @param alev used by sendHist as send if History.dl &gt alev, lev = alev
   * @param atitl title for all sendHist rows
   * @param rb r.balance
   * @param cb c.balance
   * @param sb s.balance
   * @param gb g.balance
   * @return 0=rc sum(rb,cb),1=sg sum(sb,gb),2-5=pointers to SubAssets r,c,s,g
   */
  public A6Rowa setUseBalances(int alev, String atitl, ARow rb, ARow cb, ARow sb, ARow gb) {
    balances = true;
    costs = false;
    ec.lev = alev;
    titl = atitl;
    A[2] = (rb);  // or costs r r + c r
    A[3] = (cb);  // r s + c s
    A[4] = (sb);  // s r + g r
    A[5] = (gb);  // s s + g s
    for (int n : ASECS) {
      if (E.debugDouble) {
        set(0, n, doubleTrouble(get(2, n) + get(3, n)));
        set(1, n, doubleTrouble(get(4, n) + get(5, n)));
      }
      else {
        set(0, n, get(2, n) + get(3, n));
        set(1, n, get(4, n) + get(5, n));
      }
    }
    return this;
  }

  /**
   * set an A6Rowa instance to the references r0-r5, use this for worths, use
   * values set up by Assets.CashFlow.totalWorth(), set balances true
   *
   * @param r0 like newKnowledge worth + cash
   * @param r1 like commonKnowledge worth + manualsWorth
   * @param r2 like resource worth
   * @param r3 like cargo worth
   * @param r4 like staff worth
   * @param r5 like guests worth
   * @return
   */
  public A6Rowa setUse(ARow r0, ARow r1, ARow r2, ARow r3, ARow r4, ARow r5) {
    balances = true;
    costs = false;
    A[0] = r0;
    A[1] = r1;
    A[2] = r2;
    A[3] = r3;
    A[4] = r4;
    A[5] = r5;
    return this;
  }

  /**
   * set internal row m to reference and use a
   *
   * @param m index of row to be changed
   * @param a an ARow reference to set into m
   * @return this with row m using reference a
   */
  public A6Rowa setUse(int m, ARow a) {
    A[m] = a;
    return this;
  }

  /**
   * set internal row rowIx to ARow B testing for a Double error
   *
   * @param rowIx number of row to be set
   * @param b ARow to be set into that row of object
   * @return ARow B
   */
  public ARow set2(int rowIx, ARow b) {
    if (rowIx < 6) {
      assert rowIx > 1 : "tried to set" + rowIx + " sum balance not working or reserve";
      if (A[rowIx] == null) {
        A[rowIx] = new ARow(ec);
      }
      return A[rowIx].set2(b, this.titl, rowIx);
    }
    if (A[rowIx] == null) {
      A[rowIx] = new ARow(ec);
    }
    return A[rowIx].set2(b, this.titl, rowIx);
  }

  /**
   * set values in 4 rows in ABalRows starting at bias from an A10Row 2-9
   *
   * @param bias index of the start of rows in an ABalRows
   * @param b the A10 row form which 8 rows 2 -9 are taken
   */
  public void set4(int bias, A10Row b) {
    assert (b.A[0] != null) : "b.A[0] == null";
    for (int subIx : I03) {
      if (this.A[bias + subIx] == null) {
        this.A[bias + subIx] = new ARow(ec);
      }
      for (int secIx : E.ASECS) {
        assert (b.A[6 + subIx] != null) : "b.A[6 + subIx] == null";
        double aa, bb, cc;
        assert ((aa = b.A[2 + subIx].values[secIx]) == aa) : " not (aa = b.A[2 + subIx].values[secIx]) == aa";
        // assert ((aa = b.A[2 + subIx].values[secIx]) == aa) : " not (aa = b.A[2 + subIx].values[secIx]) == aa";
        // assert ((bb = b.A[6 + subIx].values[secIx]) == bb) : " not (bb = b.A[6 + subIx].values[secIx]) == bb";
        double k
                = b.A[2 + subIx].values[secIx]
                  + b.A[6 + subIx].values[secIx];
        A[subIx + bias].values[secIx]
                = k;
      }
    }
  }

  /**
   * use the references from A6Row 2-5 into ABalRows[bias+0-3]
   *
   * @param bias index into the start of rows in ABalRows
   * @param b A6Row from which which reference for row2-5 are moved
   */
  public void useRef4(int bias, A6Row b) {
    for (int rowIx : A03) {
      A[bias + rowIx] = b.A[2 + rowIx];
    }
  }

  /**
   * set internal ARow m to B and C
   *
   * @param m row to be stored
   * @param B row to be added each by each
   * @param C second row to be added each by each
   * @return return each B + C
   */
  public ARow set(int m, ARow B, ARow C) {
    noChecking = true;

    ARow ret = A[m].set(B, C);
    noChecking = false;
    resum(0);
    resum(1);
    return ret;
  }

  /**
   * set internal ARow bias, sector n to val if balances && m <%lt; 6 then
   * assert m * %gt; 1 and do resum @param m selector of row numbe
   *
   * r
   * @param bias selector of entry in row * @param val value to be tested as a
   * Double
   *
   * t
   *
   * hen
   *
   * stored
   * @param n sector to set
   * @return val
   */
  public double set(int bias, int n, Double val) {
    ec.doubleTrouble(val, "in A6Rowa title=" + this.titl + "A[" + bias + "][" + n + "]");
    if (A[bias] == null) {
      A[bias] = new ARow(ec);
    }
    int m = bias;
    int al = A.length;
    // decide whether to add to  0 or 1=mm
    int mm = m < 2 ? m : m < 6 ? (m - lsubs) / lsubs : (m - 6) / lsubs; //  0:rc or 1:sg
    // if don't add to 0 or 1
    boolean ignoreIf = !(al == 6 || al == ABalRows.BALSLENGTH || al == 10) || !(balances || costs10) || m < 0 || costs10 ? m > 9 : balances ? m > 5 : false;
    double bal1 = 0.;
    double both = 0.;
    // is test ignored
    double ret = A[m].set(n, val);
    if (E.debugResumP && !ignoreIf && !noChecking) {
      bal1 = gett(mm, n);//get 0 or 1
      //both is sum for 0 or 1  first 2345 =>0 1
      both = gett(lsubs + 0 + mm * lsubs, n) + gett(lsubs + 1 + mm * lsubs, n);//23 or 45
      // if A10Row add in the 6789 => 0 1
      both += (!costs10) ? 0.0 : gett(6 + mm * lsubs, n) + gett(7 + mm * lsubs, n); //67 or 89
      boolean c10 = costs10;
      double dif = bal1 - both; // 01 - both
      boolean badDif = E.PZERO < dif || E.NZERO > -dif; // dif too much
      // assert error only if ignoreIf is false and badDif is true , costs10 both has 4 values
      assert !badDif : "resum error sector" + n + " length" + al + " m" + m + " mm" + mm + "="
                       + EM.mf2(bal1, "bal1")
                       + EM.mf2(dif, "dif") + EM.mf2(both, "both") + EM.mf2(gett(2, n), "pr") + EM.mf2(gett(3, n), "pc") + EM.mf2(gett(4, n), "ps") + EM.mf2(gett(5, n), "pg")
                       + (c10 ? EM.mf2(gett(6, n), "sr") + EM.mf2(gett(7, n), "sc") + EM.mf2(gett(8, n), "ss") + EM.mf2(gett(9, n), "sg") : "");
      A[mm].set(both);
    }
    return ret;
  }

  /**
   * set internal ARow m, sector n to val if balances && m <%lt; 6 then assert m
   * %gt; 1 and do resum @param m selector of row numbe
   *
   * r
   * @param n selector of entry in row @param val value to be tested as a Double
   * th
   *

   *
   * e
   * n stored
   * @param desc description of set
   * @return val
   */
  public double set(int m, int n, Double val, String desc) {
    return set(m, n, val);
  }

  /**
   * set internal ARow m, sector n to val, evaluate m as 0 or 1 do not test for
   * previous error in sum of 0,1
   *
   * @param m
   * @param n
   * @param val
   * @return val
   */
  public double sett(int m, int n, double val) {
    return set(m, n, val);
  }

  /**
   * add to row m, sector n value val test for Double problems and if balances
   * set row 0 or 1
   *
   * @param m row to object to be set
   * @param n sector of row to be added to
   * @param val
   * @return return the result in A[m].get(n) value after add
   */
  double add(int m, int n, double val) {
    //  E.myTestDouble(val, "in A6Rowa " + this.titl, " A[%1d][%1d] ", m, n);
    E.myTestDouble(val, "in A6Rowa title=" + this.titl + "A[" + m + "][" + n + "]");
    if (A[m] == null) {
      A[m] = new ARow(ec);
    }
    double ret = A[m].add(n, val);
    int al = A.length;
    // decide whether to add to  0 or 1=mm
    int mm = m < 2 ? m : m < 6 ? (m - lsubs) / lsubs : (m - 6) / lsubs; //  0:rc or 1:sg
    // if don't add to 0 or 1
    boolean ignoreIf = !(al == 6 || al == ABalRows.BALSLENGTH || al == 10) || !(balances || costs10) || m < 0 || costs10 ? m > 9 : balances ? m > 5 : false;
    double bal1 = 0.;
    double both = 0.;
    // is test ignored
    if (E.debugResumP && !ignoreIf && !noChecking) {
      add(mm, n, val);// add to rc or sg
      bal1 = gett(mm, n);//get 0 or 1
      //both is sum for 0 or 1  first 2345 =>0 1
      both = gett(lsubs + 0 + mm * lsubs, n) + gett(lsubs + 1 + mm * lsubs, n);//23 or 45
      // if A10Row add in the 6789 => 0 1
      both += (!costs10) ? 0.0 : gett(6 + mm * lsubs, n) + gett(7 + mm * lsubs, n); //67 or 89
      boolean c10 = costs10;
      double dif = bal1 - both; // 01 - both
      boolean badDif = E.PZERO < dif || E.NZERO > -dif; // dif too much
      // assert error only if ignoreIf is false and badDif is true , costs10 both has 4 values
      assert !badDif : "resum error sector" + n + " length" + al + " m" + m + " mm" + mm + "="
                       + EM.mf2(bal1, "bal1")
                       + EM.mf2(dif, "dif") + EM.mf2(both, "both") + EM.mf2(gett(2, n), "pr") + EM.mf2(gett(3, n), "pc") + EM.mf2(gett(4, n), "ps") + EM.mf2(gett(5, n), "pg")
                       + (c10 ? EM.mf2(gett(6, n), "sr") + EM.mf2(gett(7, n), "sc") + EM.mf2(gett(8, n), "ss") + EM.mf2(gett(9, n), "sg") : "");
      A[mm].set(both);
    }

    if (noChecking) {
      noChecking = true;
    }
    return ret;

  }

  /**
   * multiply raw growth by a fertility
   *
   * @param a raw growth
   * @param f a 2 row r s fertility
   * @return return the A6Row with r,c,s,g multiplied, a resum needed
   */
  public A6Rowa setAmultF(A6Row a, A2Row f) {
    noChecking = true;
    noChecking = true;
    int mm;
    for (int m : A25) {
      mm = (m - LSUBS) / LSUBS;//23=0,45=1
      for (int n : ASECS) {
        this.set(m, n, a.get(m, n) * f.get(mm, n));
      }
    }
    noChecking = false;
    //   resum(0);
    //  resum(1);
    return this;
  }

  /**
   * subtract each B times V from the calling object
   *
   * @param B input A6Rowa
   * @param V
   * @return each this - each B * V
   */
  public A6Rowa setSubBmultV(A6Rowa B, double V) {
    noChecking = true;
    for (int m : E.d6) {
      for (int n : ASECS) {
        // separate each operation to localize null object errors
        if (E.debugDouble) {
          set(m, n, doubleTrouble(this.get(m, n)
                                  - (V
                                     * B.get(m, n))));
        }
        else {
          set(m, n, this.get(m, n)
                    - (V
                       * B.get(m, n)));
          if (balances && m < 6) {
            int mm = (int) ((m - 2) / 2);  // get row 0 or 1 rc  or sg
            assert m > 1 : "error must set only working or reserve values";
            //resum((int)((m -2)/2)); // 0=2,3,1=4,5
            // local resum
            A[mm].set(n, get(mm * 2 + 2, n) + get(3 + mm * 2));
          }
        }
      }
    }
    noChecking = true;
    resum(0);
    resum(1);
    return this;
  }

  /**
   * set to Min of each by each B,C
   *
   * @param B the first A6Rowa
   * @param C the second A6Rowa
   * @return min each by each B,C, 0 = min(2,4),1=min(3,5)
   */
  public A6Rowa setMin(A6Rowa B, A6Rowa C) {
    noChecking = true;
    double b = 1., c = 1.;

    int al = A.length;
    for (int m = 0; m < al; m++) {
      for (int n = 0; n < E.LSECS; n++) {
        // do the rr,rs,sr,ss sets first
        b = doubleTrouble(B.get(m, n));
        c = doubleTrouble(C.get(m, n));
        if (A[m] == null) {
          A[m] = new ARow(ec);
        }
        A[m].set(n, b < c ? b : c);
        if (balances && m < 6) {
          if (m == 5) { //the last balance row
            int mm = (int) ((m - 2) / 2);
            A[0].set(n, get(2, n) + get(3, n));
            A[1].set(n, get(4, n) + get(5, n));
          }
        }
      }
    }
    noChecking = false;
    resum(0);
    resum(1);
    return this;
  }

  /**
   * set to Max of each by each B,C
   *
   * @param B first A6Rowa
   * @param C second A6Rowa
   * @return min each by each B,C, 0 = min(2,4),1=min(3,5)
   */
  public A6Rowa setMax(A6Rowa B, A6Rowa C) {
    noChecking = true;
    double b = 1., c = 1.;

    for (int m = 2; m < lA; m++) {
      for (int n : ASECS) {
        // do the rr,rs,sr,ss sets first
        b = doubleTrouble(B.get(m, n));
        c = doubleTrouble(C.get(m, n));
        if (A[m] == null) {
          A[m] = new ARow(ec);
        }
        A[m].set(n, b > c ? b : c);
        if (balances && m < 6) {
          if (m == 5) { //the last balance row
            int mm = (int) ((m - 2) / 2);
            A[0].set(n, get(2, n) + get(3, n));
            A[1].set(n, get(4, n) + get(5, n));
          }
        }
      }
    }
    noChecking = false;
    resum(0);
    resum(1);
    return this;
  }

  /**
   * set to Max of each sector of a,b,c
   *
   * @param a first A6Rowa
   * @param b second A6Rowa
   * @param c third A6Rowa
   * @return max each by each a,B,C,
   */
  public A6Rowa setMax(A6Rowa a, A6Rowa b, A6Rowa c) {
    noChecking = true;
    double ab = 1, abc = 1, al = A.length;
    for (int m = 0; m < al; m++) {
      for (int n = 0; n < E.LSECS; n++) {
        // do the rr,rs,sr,ss etc
        ab = doubleTrouble(a.get(m, n)) > doubleTrouble(b.get(m, n)) ? doubleTrouble(a.get(m, n)) : doubleTrouble(b.get(m, n));
        abc = ab > doubleTrouble(c.get(m, n)) ? ab : doubleTrouble(c.get(m, n));
        //set(m, n, abc);
        if (A[m] == null) {
          A[m] = new ARow(ec);
        }
        A[m].set(n, abc);
        if (balances && m < 6) {
          if (m == 5) { //the last balance row
            int mm = (int) ((m - 2) / 2);
            A[0].set(n, get(2, n) + get(3, n));
            A[1].set(n, get(4, n) + get(5, n));
          }
        }
      }
    }
    noChecking = false;
    resum(0);
    resum(1);
    return this;
  }

  /**
   * set to Max of each sector of a,b,c,d
   *
   * @param a
   * @param b
   * @param c
   * @param d
   * @return max of each sector of a,b,c,d,
   */
  public A6Rowa setMax(A6Rowa a, A6Rowa b, A6Rowa c, A6Rowa d) {
    noChecking = true;
    double ab = 1, abc = 1, abcd = 1;
    for (int m = 2; m < lA; m++) {
      for (int n : ASECS) {
        // do the rr,rs,sr,ss etc
        ab = doubleTrouble(a.get(m, n)) > doubleTrouble(b.get(m, n)) ? doubleTrouble(a.get(m, n)) : doubleTrouble(b.get(m, n));
        abc = ab > doubleTrouble(c.get(m, n)) ? ab : doubleTrouble(c.get(m, n));
        abcd = abc > doubleTrouble(d.get(m, n)) ? abc : doubleTrouble(d.get(m, n));
        if (A[m] == null) {
          A[m] = new ARow(ec);
        }
        set(m, n, abcd);
      }
    }
    noChecking = false;
    resum(0);
    resum(1);
    return this;
  }

  /**
   * set to Min of each by each B,C,D
   *
   * @param B
   * @param C
   * @param D
   * @return Min of each by each B,C,D
   */
  public A6Rowa setMin(A6Rowa B, A6Rowa C, A6Rowa D) {
    noChecking = true;
    double b = 1., c = 1., d = 1.;
    for (int m = 2; m < lA; m++) {
      for (int n : ASECS) {
        // do the rr,rs,sr,ss sets first
        b = B.get(m, n);
        c = C.get(m, n);
        d = D.get(m, n);
        if (A[m] == null) {
          A[m] = new ARow(ec);
        }
        set(m, n, b < c ? b : c < d ? c : d);
      }
    }
    noChecking = false;
    resum(0);
    resum(1);
    return this;
  }

  /**
   * set instance to each ARow A mult by B
   *
   * @param A
   * @param B
   * @return each by each A * B
   */
  public A6Rowa setAmultB(A6Rowa A, A6Rowa B) {
    noChecking = true;
    // mult each member set of 2 rows each A by corresponding B
    for (int m = 2; m < lA; m++) {
      for (int n : ASECS) {
        // separate each operation to localize null object errors
        set(m, n,
            doubleTrouble(A.get(m, n))
            * doubleTrouble(B.get(m, n)));
      }
    }
    noChecking = false;
    resum(0);
    resum(1);
    return this;
  }

  /**
   * set object to each A mult by V
   *
   * @param a the ARowA object being multiplied
   * @param v value of multiplier
   * @return each entry in each row multiplied by v
   */
  public A6Rowa setAmultV1(A6Rowa a, double v) {
    noChecking = true;
    // mult each member set of 2 rows each A by corresponding B
    int al = a.A.length;
    for (int m = 2; m < al; m++) {
      for (int n : ASECS) {
        // separate each operation to localize null object errors
        set(m, n,
            doubleTrouble(a.get(m, n))
            * doubleTrouble(v));
      }
    }
    noChecking = true;
    resum(0);
    resum(1);
    return this;
  }

  /**
   * set object to each A mult by V
   *
   * @param a the ARowA object being multiplied
   * @param v value of multiplier
   * @return each entry in each row multiplied by v
   */
  public A10Row notsetAmultV(A10Row a, Double v) {
    return (A10Row) setAmultV1((A6Rowa) a, v);
  }

  /**
   * set object to each A mult by V
   *
   * @param a the ARowA object being multiplied
   * @param v value of multiplier
   * @return each entry in each row multiplied by v
   */
  public A6Rowa setAmultV(A6Rowa a, Double v) {
    return (A10Row) setAmultV1((A6Rowa) a, v);
  }

  /**
   * set instance to the each by each sum of A +B + C
   *
   * @param A
   * @param B
   * @param C
   * @return this = each by each A + B + C
   */
  public A6Rowa setAdd(A6Rowa A, A6Rowa B, A6Rowa C) {
    noChecking = true;
    for (int m = 2; m < lA; m++) {
      for (int n : ASECS) {
        // separate each operation to localize null object errors
        this.set(m, n,
                 doubleTrouble(A.get(m, n))
                 + doubleTrouble(B.get(m, n))
                 + doubleTrouble(C.get(m, n)));
      }
    }
    noChecking = false;
    resum(0);
    resum(1);
    return this;
  }

  /**
   * set instance to the each by each sum of A +B
   *
   * @param A
   * @param B
   * @return this = each by each A + B + C
   */
  public A6Rowa setAdd(A6Rowa A, A6Rowa B) {
    noChecking = true;
    for (int m = 2; m < lA; m++) {
      for (int n : ASECS) {
        this.set(m, n, doubleTrouble(A.get(m, n)) + doubleTrouble(B.get(m, n)));
      }
    }
    noChecking = false;
    resum(0);
    resum(1);
    return this;
  }

  /**
   * sum the designated row
   *
   * @param row to be summed
   * @return sum of sector values in row
   */
  double rowSum(int row) {
    return getRow(row).sum();
  }

  /**
   * send rowStart to rowEnd balance rows to hist if lev &le; bLev if econCnt
   * for this Econ &gt; E.maxEconHist =@value(E.maxEconHist) clear hist and exit
   *
   * @param bLev if aLev &gt; bLev, no list
   * @param aPre prefix for the listing rows
   * @param aLev level for listing, if lev &lt; 1 use predefined level
   * @param at first part of title if null use the predefined title
   * @param rowStart row to start listing
   * @param rowEnd row to finish listing
   */
  void sendHistBal(int bLev, String aPre, int aLev, String at, int rowStart, int rowEnd) {
    if (ec.clearHist()) {
      return;
    }
    if (bLev > History.dl) {
      bLev = History.dl;
    }
    if (aLev < 1) {
      aLev = lev;
    }
    if (aLev > bLev) {
      return;
    } // no listing
    String[] ts = {"rc", "sg", "r", "c", "s", "g"};
    if (rowStart < 0) {
      rowStart = 0;
    }
    if (rowStart > 5) {
      rowStart = 5;
    }
    if (rowEnd < 0) {
      rowEnd = 5;
    }
    if (rowEnd > 5) {
      rowEnd = 5;
    }
    aPre = this.aPre = ec.aPre = aPre != null ? aPre : ec.aPre;
    ec.lev = aLev;  // set global this econ only
    ec.blev2 = ec.blev = bLev;

    for (int n = rowStart; n <= rowEnd; n++) {
      hist.add(new History((aPre == null ? "Z#" : aPre), lev, (at == null ? titl : at) + " " + ts[n], A[n]));
    }

  }

  /**
   * semd History of this A6Rowa instance as a bals
   *
   * @param none not used
   * @param aPre prefix for this set of History's
   */
  void sendHist(ArrayList<History> none, String aPre) {
    sendHistBal(ec.blev, aPre, lev, titl, 0, 5);
  }

  /**
   * send listing to hist for all 5 balance rows if econCnt for this Econ &gt;
   * E.maxEconHist =@value(E.maxEconHist) clear hist and exit
   *
   * @param bLev if lev &gt; bLev skip listing;
   * @param aPre
   */
  void sendHist(int lev, String aPre) {
    sendHistBal(ec.blev, aPre, lev, titl, 0, 5);
  }

  /**
   * send listing to hist for all 5 balance rows if econCnt for this Econ &gt;
   * E.maxEconHist =@value(E.maxEconHist) clear hist and exit
   *
   */
  void sendHist() {
    sendHistBal(ec.blev, aPre, lev, titl, 0, 5);
  }

  /**
   * create a history line from a String use econ aPre and lev use title from
   * the title of the calling A6Rowa
   *
   * @param aString this string will be broken into 13 character columns
   */
  void sendHist(String aString) {
    int alen = aString.length();
    String atitl = aString.substring(0, Math.min(alen, 10));
    String bbb = " ";
    if (alen > 10) {
      bbb = aString.substring(11, alen);
    }
    hist.add(new History(ec.aPre, ec.lev, " " + ec.name + " " + atitl, bbb));
  }

  /**
   * send listing to hist for the first 2 balance rows if econCnt for this Econ
   * &gt; E.maxEconHist =@value(E.maxEconHist) clear hist and exit
   *
   * @param bLev if lev &gt; bLev skip listing;
   * @param aPre
   */
  void sendHist2(int lev, String aPre) {
    sendHistBal(ec.blev, aPre, lev, titl, 0, 1);
  }

  /**
   * send listing to hist for the last 4 balance rows if econCnt for this Econ
   * &gt; E.maxEconHist =@value(E.maxEconHist) clear hist and exit
   *
   * @param bLev if lev &gt; bLev skip listing;
   * @param aPre
   */
  void sendHist4() {
    sendHistBal(ec.blev, aPre, lev, titl, 2, 5);
  }

}

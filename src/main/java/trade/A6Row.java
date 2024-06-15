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

/**
 * 2/2016 this was created to more properly represent related rows of
 * information This uses a reference to 6 ARow objects as:
 * <ul><li>a representation of partner costs, and costs related to the 4
 * Assets.CashFlow.SubAssets.
 * <li>partner balances, than Assets.CashFlow.SubAssets balances of one kind of
 * another, these may either be copies, or references to an ARow in
 * Assets.CashFlow.SubAssets
 * </ul><p>
 * The constructors set a title for the object and a default listing level and
 * perhaps a type. Usually the actual contents are done with a set into the
 * A6Row
 *
 *
 */
public class A6Row extends A6Rowa {

 // EM eM = EM.eM;
  //Econ ec = EM.curEcon;

  // int blev = History.debuggingMinor11;
//  int lev = History.debuggingMinor11;
  // String titl = "aName";
// boolean balances = false;
  // boolean costs = true;
//  ARow[] A = new ARow[10];
  // int lA = 6;
  // reqCosts for r,c,s,g   or rHealth sHealth rFertility sFertility
  // double sum[] = {0., 0., 0., 0., 0.}, plusSum[] = {0., 0., 0., 0., 0.}, negSum[] = {0., 0., 0., 0., 0.};
//  double minSum[] = {0., 0., 0.}, minSum2[] = {0., 0., 0.};
//  int[] aCnt = {-11, -11, -11, -11, -11, -11};
  // int[] x1 = new int[E.l2secs];
  // int[] x2 = new int[E.l2secs];
  // int[] x3 = new int[E.l2secs];
  /**
   * The no parameter constructor
   */
  public A6Row(Econ ec) {
    super(ec, 6, tbal, History.informationMajor8, "unset");
    int ag = ec.age;// create null error if null ec
    eM = StarTrader.eM;
    lev = 9;
    balances = false;
    costs = false;
    titl = "unset";
    A[0] = new ARow(ec).zero();
    A[1] = new ARow(ec).zero();
    A[2] = new ARow(ec).zero();
    A[3] = new ARow(ec).zero();
    A[4] = new ARow(ec).zero();
    A[5] = new ARow(ec).zero();

  }

  public A6Row(Econ ec, int t) {
    super(ec, 6, tbal, History.informationMajor8, "unset");
    titl = "unset";
  }

  /**
   * constructor for new costs object 0RC=2+4,1SG=3+5,2r-r,3r - s,4s -r,5s-s
   *
   * @param alev
   * @param atitl
   */
  public A6Row(Econ ec, int alev, String atitl) {
    super(ec, 6, tbal, alev, atitl);
    lev = alev;
    balances = true;
  }

  /**
   * constructor for a new balances zeroed object to be set later
   *
   * @param ec reference to the econ instance in which this object is created
   * @param t type t == tbal (balances), bal == tcost (costs) otherwise neither
   * @param alev level for listing with sendHist
   * @param atitl title for listing with sendHist
   */
  public A6Row(Econ ec, int t, int alev, String atitl) {
    super(ec, 6, t, alev, atitl);
    A[0] = new ARow(ec).zero();
    A[1] = new ARow(ec).zero();
    A[2] = new ARow(ec).zero();
    A[3] = new ARow(ec).zero();
    A[4] = new ARow(ec).zero();
    A[5] = new ARow(ec).zero();
  }

  /**
   * constructor to copy the first 2 rows of 3 A6Row objects to form a new A6Row
   * object
   *
   * @param lev level for lising with sendHist
   * @param titl title for listing with sendHist
   * @param a 0,1 to rows 0,1
   * @param b 0,1 to rows 2,3
   * @param c 0,1 to rows 4,5
   */
  public A6Row(Econ ec, int lev, String titl, A6Row a, A6Row b, A6Row c) {
    super(ec, 6, tnone, lev, titl);
    this.lev = lev;
    this.titl = titl;
    for (int n : E.alsecs) {
      this.set(0, n, a.get(0, n));
      this.set(1, n, a.get(1, n));
      this.set(2, n, b.get(0, n));
      this.set(3, n, b.get(1, n));
      this.set(4, n, c.get(0, n));
      this.set(5, n, c.get(1, n));
    }
  }

  /**
   * constructor to copy references to rows 2-5 for type t A6Row object
   *
   * @param t set type of instance tbal or tcost
   * @param lev level for listing with sendHist
   * @param titl title for listing with sendHist
   * @param a row reference for Row 2 resources
   * @param b row reference for Row 3 cargo
   * @param c row reference for Row 4 staff
   * @param d row reference for Row 5 guests
   */
  public A6Row(Econ ec, int t, int lev, String titl, ARow a, ARow b, ARow c, ARow d) {
    super(ec, 6, tbal, lev, titl);
    A[2] = a;
    A[3] = b;
    A[4] = c;
    A[5] = d;
  }

  /**
   * copy A6Row object, copy each by each of calling A6Row a.copy(); no change
   * to a, rtn is a new object lev,titl,balances,costs,blev are all copied as
   * well as A[] values
   *
   * @param newEc calling object root Econ
   * @return new object copy of this object
   */
  public A6Row copy(Econ newEc) {
    int t = balances ? tbal : costs ? tcost : tbal;
    A6Row rtn = new A6Row(newEc);
    rtn.titl = this.titl;
    rtn.lev = this.lev;
    rtn.balances = this.balances;
    rtn.costs = this.costs;
    rtn.blev = blev;
    rtn.eM = EM.eM;
    for (int m = 0; m < lA; m++) {
      if (A[m] != null) {
        rtn.A[m] = A[m].copy();
      } else {
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
                } else {
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
   * set all the values in b to this
   *
   * @param b values to be copied
   * @return the new this with the values in b
   */
  A6Row set(A6Row b) {
    lev = b.lev;
    balances = b.balances;
    costs = b.costs;
    titl = b.titl;
    blev = b.blev;
    for (int m = 2; m < 6; m++) {
      for (int n = 0; n < E.LSECS; n++) {
        set(m, n, b.get(m, n));
      }
    }
    return this;
  }

  /**
   * compress the values in an A10Row to an A6Row calculate costs for each
   * SubAsset
   *
   * @param b A20Row file to compress to A6Row
   * @return the reference to the owning A6Row
   */
  A6Row set(A10Row b) {
    lev = b.lev;
    balances = b.balances;
    costs = b.costs;
    titl = b.titl;
    blev = b.blev;
    for (int m = 2; m < 6; m++) {
      for (int n = 0; n < E.LSECS; n++) {
        set(m, n, b.get(m, n) + b.get(m + 4, n));
      }
    }
    return this;
  }

  /**
   * copy an A6Row to another A6Row and return it
   *
   * @param leva level of the new A6Row
   * @param titl title of the new A6Row
   * @return a new A6Row with the values of this A6Row
   */
  public A6Row copy(int leva, String titl) {
    return this.copy6(2, leva, titl);
  }

  /**
   * copy the values of prev into this, particularly do not change the reference
   * of each ARow
   *
   * @param prev a previous copy of this A6Row
   * @return this with updated values and setCnt
   */
  public A6Row copyValues(A6Row prev) {
    if (prev != null) {
      for (int m = 0; m < 6; m++) {
        A[m].setCnt++;
        for (int n = 0; n < E.LSECS; n++) {
          A[m].values[n] = prev.A[m].values[n];
        }
      }// end m
    }
    return this;
  }

  /**
   * copy values to rows 2-5 for type costs A6Row object
   *
   * @param lev level for listing with sendHist
   * @param t type tcost, tbal, tnone for cost type, or balances or neither
   * @param titl title for listing with sendHist
   * @param a row reference for Row 2 resources
   * @param b row reference for Row 3 cargo
   * @param c row reference for Row 4 staff
   * @param d row reference for Row 5 guests
   */
  public A6Row copy(int lev, int t, String titl, ARow a, ARow b, ARow c, ARow d) {
    A6Row tmp = new A6Row(a.ec, tcost, lev, titl);
    tmp.A[2] = a.copy();
    tmp.A[3] = b.copy();
    tmp.A[4] = c.copy();
    tmp.A[5] = d.copy();
    return tmp;
  }

  /**
   * copy the values of prev into this, particularly do not change the reference
   * of each ARow
   *
   * @param sartIx the row to start to copy 4 rows
   * @param prev a previous copy of this A6Rowa grades are not copied
   * @return this with updated values and setCnt
   */
  public A6Row copyValues(int startIx, A6Rowa prev) {
    int prevIx = 0;
    for (int m = 0; m < 4; m++) {
      A[m + 2].setCnt++; // only copy to r,c,s,g
      prevIx = m + startIx;
      for (int n = 0; n < E.LSECS; n++) {
        A[m + 2].values[n] = prev.A[prevIx].values[n];
      }
    }// end m
    return this;
  }

  /**
   * flip the signs of the values in both ARows part of this A2Row
   *
   * @return this the flipped ARows
   */
  A6Row flip() {
    for (int m = 0; m < 6; m++) {
      A[m].flip();
    }
    return this;
  }

  /**
   * flip the signs of the values in both ARows part of this A2Row
   *
   * @return this the flipped ARows
   */
  A6Row flip(A6Row prev) {
    for (int m = 0; m < 6; m++) {
      A[m].flip(prev.A[m]);
    }
    return this;
  }

  /**
   * set to all the references in AABalRows b
   *
   * @param b
   * @return the invoking A6Row with copied values
   */
  @Override
  public A6Row set(A6Rowa b) {
    return copyValues(2, b);
  }

  /**
   * set rows 0,1 to the rows in b zero the rest of the rows
   *
   * @param b source of rows 0,1
   * @return this rows 0=b0,1=b1,2,3,4,5 all zero
   */
  A6Row set(A2Row a) {
    A[0].set(a.A[0]);
    A[1].set(a.A[1]);
    A[2].zero();
    A[3].zero();
    A[4].zero();
    A[5].zero();
    return this;
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
   * set level to be used in listing in hist with sendHist in object
   *
   * @param l listing level
   * @return listing level
   */
  int setLevel(int l) {
    return lev = l;
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
   * re-add row 0 = 2 + 4, row 1 = 3 + 5
   *
   * @return this row 0 = 2 + 4, row 1 = 3 + 5
   */
  public A6Row addJointCosts() {
    costs = true;
    balances = false;
    for (int m : d01) {
      for (int n : ASECS) {
        set(m, n, get(2 + m, n) + get(4 + m, n));
      }
    }
    return this;
  }

  /**
   * use A6Rowa resum, notreSum the rows 0,1 depending on flag balances
   *
   * @return balances?0=2+3,1=4+5:0=2+4,1=3+5;
   */
  public A6Row notreSum() {
    for (int m : d01) {
      for (int n : ASECS) {
        set(m, n, balances ? get(2 + m * 2, n) + get(3 + m * 2, n) : get(2 + m, n) + get(4 + m, n));
      }
    }

    return this;
  }

  /**
   * zero all values in the A6Row
   *
   * @return this
   */
  public A6Row zero() {

    for (int m = 0; m < lA; m++) {
      A[m].setCnt++;
      for (int n : E.alsecs) {
        A[m].values[n] = 0.;
      }
    }
    return this;
  }

  /**
   * set doNot for duo index nIx to nYears years
   *
   * @param duo
   * @param ixSrc
   * @param srcIx
   * @param nYears
   * @return this
   */
  public A6Row setDoNot(int duo, int ixSrc, int srcIx, double nYears) {
    A[duo * 2 + ixSrc].values[srcIx] = nYears;
    A[duo * 2 + ixSrc].setCnt++;
    return this;
  }

  /**
   * age the doNot object
   *
   * @return this
   */
  public A6Row age() {
    for (int m : dA) {
      for (int n : E.alsecs) {
        if (A[m].values[n] > E.pzero) {
          A[m].values[n] -= 1.;
          A[m].setCnt++;
        }
      }
    }
    return this;
  }

  /**
   * check if any doNot is set
   *
   * @return true is yes, else false
   */
  boolean checkDoNot() {
    for (int m : dA) {
      for (int n : E.alsecs) {
        if (A[m].values[n] > E.pzero) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * filter avail set very negative any value for which the doNot has positive
   * years left
   *
   * @param group 0,1,2 incr, delr, xfer of doNot
   * @param avail
   * @return avail with doNots negated
   */
  A2Row filterByDoNot(int group, A2Row avail) {
    for (int m : E.d2) {
      for (int n : E.alsecs) {
        if (A[m + 2 * group].values[n] > E.pzero) {
          avail.A[m].values[n] = -9999.;
          avail.A[m].setCnt++;
        }
      }
    }
    return avail;
  }

  /**
   * re_add balances
   *
   * @return row 0 = 2,3, row 1 = 4,5
   */
  public A6Row addJointBalances() {
    balances = true;
    for (int m : d01) {
      for (int n : ASECS) {
        set(m, n, get(2 * m + 2, n) + get(2 * m + 3, n));
      }
    }
    return this;
  }

  /** moved A6Rowa
   * check for some screwup that loses the connection to real balances and
   * grades
   *
   * @param cr

  public void checkBalances(Assets.CashFlow cr) {
    E.myTest(cr.r.balance != A[2], "r connection lost");
    E.myTest(cr.c.balance != A[3], "c connection lost");
    E.myTest(cr.s.balance != A[4], "s connection lost");
    E.myTest(cr.g.balance != A[5], "g connection lost");
    E.myTest(cr.resource.balance != A[2], "resource connection lost");
    E.myTest(cr.cargo.balance != A[3], "cargo connection lost");
    E.myTest(cr.staff.balance != A[4], "staff connection lost");
    E.myTest(cr.guests.balance != A[5], "gguests connection lost");
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
   */

  /**
   * copy A6Row object, copy each by each of calling A6Row use as A6Row b =
   * a.copy(); no change to a, b is a new object lev,titl,balances,costs,blev
   * are all copied as well as A[] values
   *
   * @return new object copy new references for the values
   *
   * public A6Row copy() { return this.copy(this.lev); }
   */
  /**
   * copy A6Row object, copy each by each of calling A6Row use as A6Row b =
   * a.copy(); no change to a, b is a new object lev,titl,balances,costs,blev
   * are all copied as well as A[] values
   *
   * @param lev level to display the copy
   *
   * @return new object copy new references for the values
   *
   * public A6Row copy(int lev) { A6Row rtn = new A6Row(); rtn.lev = lev;
   * rtn.titl = titl; rtn.balances = balances; rtn.costs = costs; rtn.blev =
   * blev; int l = A.length; for (int m = 0; m < l; m++) { for (int n : ASECS) {
   * rtn.set(m, n, get(m, n)); } } return rtn; }
   */
  /**
   * copy A6Row object, copy each by each of calling A6Row use as A6Row b =
   * a.copy(); no change to a, b is a new object lev,titl,balances,costs,blev
   * are all copied as well as A[] values
   *
   * @param lev level to display the copy
   * @param atit title of copy A6Row
   *
   * @return new object copy new references for the values
   *
   * @Override public A6Row copy(int lev, String atit) { A6Row rtn = new
   * A6Row(); rtn.lev = lev; rtn.titl = atit + ""; rtn.balances = balances;
   * rtn.costs = costs; rtn.blev = blev; int l = A.length; for (int m = 0; m <
   * 6; m++) { for (int n : ASECS) { rtn.set(m, n, get(m, n)); } } return rtn; }
   */
  /**
   * Calculate the min res+cargo and staff+cargo re-min row 0 = min(2,4), row 1
   * = min(3,5)
   *
   * @return this row 0 = min(2,4), row 1 = min(3,5)
   */
  public A6Row minCosts() {
    for (int m : E.d2) {
      for (int n : E.alsecs) {
        set(m, n, Math.min(get(2 + m, n), get(4 + m, n)));
      }
    }
    return this;
  }

  /**
   * Calculate maxes set each in rows 0,1 to the corresponding max of each in
   * rows 2,3 and 4,5
   *
   * @return each row 0=max(2,4), row 1=max(3,5)
   */
  public A6Row maxCosts() {
    for (int m : E.d2) {
      for (int n : E.alsecs) {
        set(m, n, Math.max(get(2 + m, n), get(4 + m, n)));
      }
    }
    return this;
  }

  @Override
  protected Object clone() throws CloneNotSupportedException {
    return super.clone(); //To change body of generated methods, choose Tools | Templates.
  }

  /**
   * set object copy from 8 costs, and convert staff work to staff balance
   * values set balances false and costs true
   *
   * @param alev used by sendHist as send if History.dl &gt alev, lev = alev
   * @param aTitl title for all sendHist rows
   * @param RR the cost to r for the r SubAsset
   * @param RS the cost to s for the r SubAsset
   * @param CR the cost to r for the c SubAsset
   * @param CS the cost to s for the c SubAsset
   * @param SR the cost to r for the s SubAsset
   * @param SS the cost to s for the s SubAsset
   * @param GR the cost to r for the g SubAsset
   * @param GS the cost to s for the g SubAsset
   * @param sBal staff balance
   * @param sWork staff work
   * @param phe poor health effect after health for maint,travel,growth else 1.
   * @param years lightYears traveled if travel else 1.
   * @return instance 0=2+4 r costs,`1=3+5 s costs, 2+RR+CR,
   * 3=(RS+CS)*sBal/sWork, 4+SR,GR, 5=(SS+GS)*sBal/sWork copy of costs
   */
  public A6Row setCopyCosts(int alev, String aTitl, ARow RR, ARow RS, ARow CR, ARow CS, ARow SR, ARow SS, ARow GR, ARow GS, ARow sBal, ARow sWork, double phe, double years) {
    balances = false;
    costs = true;
    lev = alev;
    titl = aTitl;
    for (int n : E.alsecs) {
      double sw = sWork.get(n);
      set(2, n, (RR.get(n) + CR.get(n)) * phe * years);
      Double t1 = sw < E.pzero ? 0 : (RS.get(n) + CS.get(n)) * phe * years * sBal.get(n) / sw;
      set(3, n, t1.isInfinite() || t1.isNaN() ? 0 : t1);
      set(4, n, (SR.get(n) + GR.get(n)) * phe * years);
      t1 = sw < E.pzero ? 0 : (SS.get(n) + GS.get(n)) * phe * years * sBal.get(n) / sw;
      set(5, n, t1.isInfinite() || t1.isNaN() ? 0 : t1);
      set(0, n, get(2, n) + get(4, n));
      set(1, n, get(3, n) + get(5, n));
    }
    return this;
  }

  /**
   * set object copy from 8 costs, and convert staff work to staff balance
   * values
   *
   * @param alev used by sendHist as send if History.dl &gt alev, lev = alev
   * @param aTitl title for all sendHist rows
   * @param RR the cost to r for the r SubAsset
   * @param RS the cost to s for the r SubAsset
   * @param CR the cost to r for the c SubAsset
   * @param CS the cost to s for the c SubAsset
   * @param SR the cost to r for the s SubAsset
   * @param SS the cost to s for the s SubAsset
   * @param GR the cost to r for the g SubAsset
   * @param GS the cost to s for the g SubAsset
   * @param sBal staff balance
   * @param sWork staff work
   * @param sBal staff balance
   * @param sWork staff work
   * @param phe poor health effect after health for maint,travel,growth else 1.
   *
   * @return instance 0=2+4 r costs,`1=3+5 s costs, 2+RR+CR,
   * 3=(RS+CS)*sBal/sWork, 4+SR,GR, 5=(SS+GS)*sBal/sWork copy of costs
   */
  public A6Row setCopyCosts(int alev, String aTitl, ARow RR, ARow RS, ARow CR, ARow CS, ARow SR, ARow SS, ARow GR, ARow GS, ARow sBal, ARow sWork, double phe) {

    return this.setCopyCosts(alev, aTitl, RR, RS, CR, CS, SR, SS, GR, GS, sBal, sWork, phe, 1.);
  }

  /**
   * set object copy from 8 costs, and convert staff work to staff balance
   * values
   *
   * @param alev used by sendHist as send if History.dl &gt alev, lev = alev
   * @param aTitl title for all sendHist rows
   * @param RR the cost to r for the r SubAsset
   * @param RS the cost to s for the r SubAsset
   * @param CR the cost to r for the c SubAsset
   * @param CS the cost to s for the c SubAsset
   * @param SR the cost to r for the s SubAsset
   * @param SS the cost to s for the s SubAsset
   * @param GR the cost to r for the g SubAsset
   * @param GS the cost to s for the g SubAsset
   * @param sBal staff balance
   * @param sWork staff work
   * @return instance 0=2+4 r costs,`1=3+5 s costs, 2+RR+CR,
   * 3=(RS+CS)*sBal/sWork, 4+SR,GR, 5=(SS+GS)*sBal/sWork copy of costs
   */
  public A6Row setCopyCosts(int alev, String atitl, ARow RR, ARow RS, ARow CR, ARow CS, ARow SR, ARow SS, ARow GR, ARow GS, ARow sBal, ARow sWork) {

    return this.setCopyCosts(alev, atitl, RR, RS, CR, CS, SR, SS, GR, GS, sBal, sWork, 1., 1.);
  }

  /**
   * set this to joint rows references of SubAsset
     *
   * @param alev used by sendHist as send if History.dl &gt alev, lev = alev
   * @param atitl title for all sendHist rows
   * @param rb r SubAsset reference
   * @param cb c SubAsset reference
   * @param sb s SubAsset reference
   * @param gb g SubAsset reference
   * @return 0=rc sum(rb,cb),1=sg sum(sb,gb),2-5=pointers to SubAssets r,c,s,g
   */
  public A6Row setUseBalances(int alev, String atitl, ARow rb, ARow cb, ARow sb, ARow gb) {
    balances = true;
    costs = false;
    lev = alev;
    titl = atitl;
    A[2] = (rb);  // or costs r r + c r
    A[3] = (cb);  // r s + c s
    A[4] = (sb);  // s r + g r
    A[5] = (gb);  // s s + g s
    if (A[0] == null) {
      A[0] = new ARow(ec);
      A[1] = new ARow(ec);
    }
    for (int n : E.alsecs) {
      sett(0, n, gett(2, n) + get(3, n));
      sett(1, n, gett(4, n) + get(5, n));
    }
    return this;
  }

  /**
   * set and A6 instance to the reference r0-r5, use this for worths, use values
   * set up by Assets.CashFlow.totalWorth(), set balances true
   *
   * @param r0 like newKnowledge worth + cash
   * @param r1 like commonKnowledge worth + manualsWorth
   * @param r2 like resource worth
   * @param r3 like cargo worth
   * @param r4 like staff worth
   * @param r5 like guests worth
   * @return
   */
  public A6Row setUse(ARow r0, ARow r1, ARow r2, ARow r3, ARow r4, ARow r5) {
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
   * set internal ARow m to ARow B
   *
   * @param m
   * @param B
   * @return ARow B
   */
  public ARow set(int m, ARow B) {
    return A[m].set(B);
  }

  /**
   * set internal ARow m to B and C
   *
   * @param m
   * @param B
   * @param C
   * @return return each B + C
   */
  public ARow set(int m, ARow B, ARow C) {
    return A[m].set(B, C);
  }

  /**
   * set internal ARow m, sector n to val
   * force use of A6Rowa set
   *
   * @param m
   * @param n
   * @param val
   * @return val
   */
  public double notSet(int m, int n, double val) {
    return A[m].set(n, val);
  }

  /**
   * set internal ARow m, sector n to val, evaluate m as 0 or 1
   *
   * @param m
   * @param n
   * @param val
   * @return val
   */
  public double notsett(int m, int n, double val) {
    return set(m % 2, n, val);
  }

  /**
   * add to row m, sector n value val
   * force use of A6Rowa Set
   *
   * @param m
   * @param n
   * @param val
   * @return return the result in A[m].get(n)
   */
  double notAdd(int m, int n, double val) {
    return A[m].add(n, val);
  }

  /**
   * set the hist variable in A6Row
   *
   * @param hist
   */
  void setHist(ArrayList<History> hist) {
    this.hist = hist;
  }

  /**
   * subtrace each B time V from the calling object
   *
   * @param B
   * @param V
   * @return each this - each B * V
   */
  public A6Row setSubBmultV(A6Row B, double V) {
    for (int m : E.d6) {
      for (int n : E.alsecs) {
        // separate each operation to localize null object errors
        set(m, n, this.get(m, n)
                - (V
                * B.get(m, n)));
      }
    }
    return this;
  }

  /**
   * set the remnant of balance - costs
   *
   * @param b balances, charges only to r,s
   * @param c costs
   * @return
   */
  A6Row setRem(ABalRows b, A10Row c) {
    for (int n : ASECS) {
      for (int m : A01) {
        A[2 + 4 * m].set(n, b.get(2 + 2 * m, n)); // set r s
        //   A[2+2*m].set(n,B.get(2+2*m,n); // set r,s
        for (int mm : A03) {
          A[2 + 4 * m + mm].add(n, -c.get(2 + 4 * m + mm, n));
        }
      }
    }
    return this;
  }

  /**
   * set to need the negative of remnant, needs=costs-balances
   *
   * @param b the balance
   * @param c the costs
   * @return positive needs(costs-balances), only r,s pay for needs
   */
  A6Row setNeed(ABalRows b, A10Row c) {
    for (int n : ASECS) {
      for (int m : A01) {
        A[2 + 2 * m].set(n, -b.get(2 + 2 * m, n)); // set r s

        for (int mm : A03) {
          A[2 + 2 * m + mm / 2].add(n, c.get(2 + 4 * m + mm, n));
        }
      }
    }
    return this; // do auto resum when needed
  }

  /**
   * set to Min of each by each B,C
   *
   * @param B
   * @param C
   * @return min each by each B,C, 0 = min(2,4),1=min(3,5)
   */
  public A6Row setMin(A6Row B, A6Row C) {
    double b = 1., c = 1.;
    for (int m : E.d2) {
      for (int n : E.alsecs) {
        // do the rr,rs,sr,ss sets first
        b = B.get(m + 2, n);
        c = C.get(m + 2, n);
        set(m + 2, n, b < c ? b : c);
        b = B.get(m + 4, n);
        c = C.get(m + 4, n);
        set(m + 4, n, b < c ? b : c);
        // finally do the joint
        b = get(2 + m, n);
        c = get(4 + m, n);
        set(m, n, b < c ? b : c);
      }
    }
    return this;
  }

  /**
   * set to Max of each by each B,C
   *
   * @param B
   * @param C
   * @return min each by each B,C, 0 = min(2,4),1=min(3,5)
   */
  public A6Row setMax(A6Row B, A6Row C) {
    double b = 1., c = 1.;
    for (int m : E.d2) {
      for (int n : E.alsecs) {
        // do the rr,rs,sr,ss sets first
        b = B.get(m + 2, n);
        c = C.get(m + 2, n);
        set(m + 2, n, b > c ? b : c);
        b = B.get(m + 4, n);
        c = C.get(m + 4, n);
        set(m + 4, n, b > c ? b : c);
        // finally do the joint
        b = get(2 + m, n);
        c = get(4 + m, n);
        set(m, n, b > c ? b : c);
      }
    }
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
  public A6Row setMin(A6Row B, A6Row C, A6Row D) {
    double b = 1., c = 1., d = 1.;
    for (int m : E.d2) {
      for (int n : E.alsecs) {
        // do the rr,rs,sr,ss sets first
        b = B.get(m + 2, n);
        c = C.get(m + 2, n);
        d = D.get(m + 2, n);
        set(m + 2, n, b < c ? b : c < d ? c : d);
        b = B.get(m + 4, n);
        c = C.get(m + 4, n);
        d = D.get(m + 4, n);
        set(m + 4, n, b < c ? b : c < d ? c : d);
        // finally do the joint
        b = get(2 + m, n);
        c = get(4 + m, n);
        set(m, n, b < c ? b : c);
      }
    }
    return this;
  }

  /**
   * set instance to each ARow A mult by B
   *
   * @param A
   * @param B
   * @return each by each A * B
   */
  public A6Row setAmultB(A6Row A, A6Row B) {
    // mult each member set of 2 rows each A by corresponding B
    for (int m : d01) {
      for (int n : ASECS) {
        // separate each operation to localize null object errors
        set(m, n,
                A.get(m, n)
                * B.get(m, n));
        set(m + 2, n, A.get(m + 2, n) * B.get(m + 2, n));
        set(m + 4, n, A.get(m + 4, n) * B.get(m + 4, n));
      }
    }
    return this;
  }

  /**
   * multiply raw growth by a fertility
   *
   * @param a raw growth
   * @param f a 2 row r s fertility
   * @return return the A6Row with r,c,s,g multiplied, a resum needed
   */
  public A6Row setAmultF(A6Row a, A2Row f) {
    noChecking=true;
    for (int n : ASECS) {
      for (int m : A01) {
        for (int mm : A01) {
          set(2 + 2 * m + mm, n, a.get(2 + 2 * m + mm, n) * f.get(m, n));
        }
      }
    }
    noChecking=false;resum(0);resum(1);
    return this;
  }

  /**
   * multiply raw growth by a fertility but set a min
   *
   * @param a raw growth
   * @param f a 2 row r s fertility
   * @param min the growth always has a min
   * @return return the A6Row with r,c,s,g multiplied, a resum needed
   */
  public A6Row setAmultFminM(A6Row a, A2Row f, double min) {
    noChecking = true;
    double t = 0., mined = 0.;
    for (int secIx : ASECS) {
      for (int sumIx : A01) {
        this.A[sumIx].values[secIx] = 0.0;
        for (int rowIx : A01) {
          // sett(2 + 2 * m + mm, n, a.get(2 + 2 * m + mm, n) * f.get(m, n));
          t = a.A[2 + sumIx + sumIx + rowIx].values[secIx]
              * f.A[sumIx].values[secIx];
          mined = t < min ? min : t;
          this.A[sumIx].values[secIx]
                  += this.A[2 + sumIx + sumIx + rowIx].values[secIx] = mined;
          this.A[2 + sumIx + sumIx + rowIx].setCnt++; //flag a change made
        }
      }
    }
    noChecking = false;
    // resum(0);
    //  resum(1);
    return this;
  }

  /**
   * apply low and high limits to a copy of values from b
   *
   * @param b source of values
   * @param low low limit
   * @param high high limit
   * @return revised this
   */
  A6Row setLimits(A6Row b, double low, double high) {
    for (int n=0;n < E.LSECS; n++) {
      for (int m=0 ; m<6;m++) {
        double d = b.get(m, n);
        set(m, n, d < low ? low : d > high ? high : d);
      }
    }
    return this;
  }

  /**
   * set object to each A mult by V
   *
   * @param A
   * @param V
   * @return
   */
  public A6Row setAmultV(A6Row A, double V) {
    // mult each member set of 2 rows each A by corresponding B
    for (int m : d01) {
      for (int n : ASECS) {
        // separate each operation to localize null object errors
        set(m, n,
                A.get(m, n)
                * V);
        set(m + 2, n, A.get(m + 2, n) * V);
        set(m + 4, n, A.get(m + 4, n) * V);
      }
    }
    return this;
  }

  public A6Row setAmultB(A4Row A, A6Row B) {
    A2Row joint = new A2Row(ec);
    for (int m : E.d2) {
      for (int n : E.alsecs) {
        // separate each operation to localize null object errors
        joint.set(m, n,
                A.get(m, n)
                + A.get(m + 1, n));
        set(m, n, joint.get(m, n) * B.get(m, n));
        set(m + 2, n, joint.get(m, n) * B.get(m + 2, n));
        set(m + 4, n, joint.get(m, n) * B.get(m + 4, n));
      }
    }
    return this;
  }

  /**
   * set instance to the each by each sum of A +B + C
   *
   * @param a an adden
   * @param b an adden
   * @param c an adden
   * @return this = each by each a + b + c
   */
  public A6Row setAdd(A6Row a, A6Row b, A6Row c) {
    for (int m : E.d6) {
      for (int n : E.alsecs) {
        // separate each operation to localize null object errors
        this.set(m, n,
                a.get(n, m)
                + b.get(n, m)
                + c.get(n, m));
      }
    }
    return this;
  }

  /**
   * set to Max of each sector of a,b,c
   *
   * @param a first A6Row
   * @param b second A6Row
   * @param c third A6Row
   * @return max each by each a,B,C,
   */
  public A6Row setMax(A6Row a, A6Row b, A6Row c) {
    double ab = 1, abc = 1;
    for (int m = 2; m < lA; m++) {
      for (int n = 0; n < E.LSECS; n++) {
        if (E.debugDouble) {
          ab = doubleTrouble(a.get(m, n)) > doubleTrouble(b.get(m, n)) ? doubleTrouble(a.get(m, n)) : doubleTrouble(b.get(m, n));
          abc = ab > doubleTrouble(c.get(m, n)) ? ab : doubleTrouble(c.get(m, n));
        } else {
          ab = a.get(m, n) > b.get(m, n) ? a.get(m, n) : b.get(m, n);
          abc = ab > c.get(m, n) ? ab : c.get(m, n);
        }
        set(m, n, abc);
      }
    }
    return this;
  }

  /**
   * add 3 costs and , sum as costs to 0,1
   *
   * @param A
   * @param B
   * @param C
   * @return
   */
  public A6Row setAddJointCosts(A6Row A, A6Row B, A6Row C) {
    for (int m : d25) {
      for (int n : E.alsecs) {
        // separate each operation to localize null object errors
        this.set(m, n,
                A.get(m, n)
                + B.get(m, n)
                + C.get(m, n));
        if (m > 3) {
          //now set rows 0,1 as sums of costs 0 = 2+4, 1=3+5
          this.set(m - 4, n, this.get(m - 2, n) + this.get(m, n));
        }
      }
    }
    return this;
  }

  /**
   * set instance to the each by each sum of A +B
   *
   * @param A
   * @param B
   * @return this = each by each A + B + C
   */
  public A6Row setAdd(A6Row A, A6Row B) {
    for (int m : E.d6) {
      for (int n : E.alsecs) {
        this.set(m, n, A.get(m, n) + B.get(m, n));
      }
    }
    return this;
  }

  /**
   * send 6 rows to the hist log unless hist.lev >= History.dl
   *
   * @param hist
   * @param aPre
   */
  void sendHist(ArrayList<History> hist, String aPre) {
    if (ec.clearHist()) {
      return;
    }
    if (History.dl > lev) {
      lev = Math.min(lev, History.auxInfo);
      hist.add(new History(aPre, lev, " RC " + this.titl, A[0]));
      hist.add(new History(aPre, lev, " SG " + this.titl, A[1]));
      hist.add(new History(aPre, lev, (balances ? " r " + titl : costs ? " r.rc" + titl : " 2 " + titl), A[2]));
      hist.add(new History(aPre, lev, (balances ? " c " + titl : costs ? "s.rc" + titl : " 3 " + titl), A[3]));
      hist.add(new History(aPre, lev, (balances ? " s " + titl : costs ? " r.sg" + titl : " 4 " + titl), A[4]));
      hist.add(new History(aPre, lev, (balances ? " g " + titl : costs ? " s.sg" + titl : " 5 " + titl), A[5]));
    }
  }

  /**
   * sum the designated row
   *
   * @param row
   * @return
   */
  double rowSum(int row) {
    return this.A[row].sum();
  }

  /**
   * send 6 rows to the hist log if lev &le; blev
   *
   * @param lev
   * @param aPre
   */
  void sendHist(int lev, String aPre) {
    sendHist(blev, aPre, lev, titl);
  }

  /**
   * send 6 rows to the hist log if lev &le; blev
   *
   * @param bLev if bLev &lt lev do not list
   * @param lev
   * @param aPre
   * @param titl base title for these lists
   */
  void sendHist(int bLev, String aPre, int lev, String titl) {
    if (ec.clearHist()) {
      return;
    }
    if (lev <= blev) {
      lev = Math.min(lev, History.auxInfo);
      resum(0);
      resum(1);
      hist.add(new History(aPre, lev, " RC " + this.titl, A[0]));
      hist.add(new History(aPre, lev, " SG " + this.titl, A[1]));
      hist.add(new History(aPre, lev, (balances ? " r " + titl : costs ? " r.rc" + titl : " 2 " + titl), A[2]));
      hist.add(new History(aPre, lev, (balances ? " c " + titl : costs ? "s.rc" + titl : " 3 " + titl), A[3]));
      hist.add(new History(aPre, lev, (balances ? " s " + titl : costs ? " r.sg" + titl : " 4 " + titl), A[4]));
      hist.add(new History(aPre, lev, (balances ? " g " + titl : costs ? " s.sg" + titl : " 5 " + titl), A[5]));
    }
  }

  /**
   * send 6 rows to the hist log if lev &le; blev
   *
   * @param lev
   * @param aPre
   */
  void sendHist(String aPre, int alev) {
    sendHist(blev, aPre, alev, titl);
  }

  /**
   * send rows 2-5 to hist if lev &le; blev
   *
   * @param hist
   * @param blev
   * @param aPre
   * @param lev
   * @param titl2
   * @param titl3
   * @param titl4
   * @param titl5
   */
  void sendHist4(ArrayList<History> hist, int blev, String aPre, int lev, String titl2, String titl3, String titl4, String titl5) {
    this.hist = hist;
    String titl = " " + this.titl;
    sendHist4(blev, aPre, lev, titl2, titl3, titl4, titl5);
  }

  /**
   * send rows 2-5 to hist if lev &le; blev
   *
   * @param blev
   * @param aPre
   * @param lev
   * @param titl2
   * @param titl3
   * @param titl4
   * @param titl5
   */
  void sendHist4(int blev, String aPre, int lev, String titl2, String titl3, String titl4, String titl5) {
    if (ec.clearHist()) {
      return;
    }
    String titl = " " + this.titl;
    if (lev <= blev) {
      hist.add(new History(aPre, lev, " " + titl2, A[2]));
      hist.add(new History(aPre, lev, " " + titl3, A[3]));
      hist.add(new History(aPre, lev, " " + titl4, A[4]));
      hist.add(new History(aPre, lev, " " + titl5, A[5]));
    }
  }

  /**
   * send rows 2-5 to hist if lev &le; blev
   *
   * @param hist
   * @param blev
   * @param aPre
   */
  void sendHist4(ArrayList<History> hist, int blev, String aPre) {
    if (ec.clearHist()) {
      return;
    }
    String titl = " " + this.titl;
    this.hist = hist;
    if (lev <= blev) {
      lev = Math.min(lev, History.auxInfo);
      hist.add(new History(aPre, lev, (balances ? " r " + titl : costs ? " r.rc" + titl : " 2 " + titl), A[2]));
      hist.add(new History(aPre, lev, (balances ? " c " + titl : costs ? "s.rc" + titl : " 3 " + titl), A[3]));
      hist.add(new History(aPre, lev, (balances ? " s " + titl : costs ? " r.sg" + titl : " 4 " + titl), A[4]));
      hist.add(new History(aPre, lev, (balances ? " g " + titl : costs ? " s.sg" + titl : " 5 " + titl), A[5]));
    }
  }

  /**
   * send rows 2-5 to hist if lev &le; blev
   *
   * @param blev
   * @param aPre
   */
  void sendHist4(int blev, String aPre) {
    if (ec.clearHist()) {
      return;
    }
    String titl = " " + this.titl;
    if (lev <= blev) {
      lev = Math.min(lev, History.auxInfo);
      hist.add(new History(aPre, lev, (balances ? " r " + titl : costs ? " r.rc" + titl : " 2 " + titl), A[2]));
      hist.add(new History(aPre, lev, (balances ? " c " + titl : costs ? "s.rc" + titl : " 3 " + titl), A[3]));
      hist.add(new History(aPre, lev, (balances ? " s " + titl : costs ? " r.sg" + titl : " 4 " + titl), A[4]));
      hist.add(new History(aPre, lev, (balances ? " g " + titl : costs ? " s.sg" + titl : " 5 " + titl), A[5]));
    }
  }

  /**
   * send2 rows to the hist log if lev &le; blev
   *
   * @param hist
   * @param aPre
   */
  void sendHist2(ArrayList<History> hist, String aPre) {
    if (ec.clearHist()) {
      return;
    }
    this.hist = hist;
    if (lev <= blev) {
      lev = Math.min(lev, History.auxInfo);
      resum(0);
      resum(1);
      hist.add(new History(aPre, lev, " RC " + this.titl, A[0]));
      hist.add(new History(aPre, lev, " SG " + this.titl, A[1]));
    }
  }

  /**
   * send to history the first 2 rows if lev &le; blev
   *
   * @param hist
   * @param bLev log only if bLev &lt History.dl
   * @param aPre pre characters
   * @param lev listing level
   * @param tit0
   * @param tit1
   */
  void sendHist2(ArrayList<History> hist, int bLev, String aPre, int lev, String tit0, String tit1) {

    this.hist = hist;
    sendHist2(blev, aPre, lev, tit0, tit1);
  }

  /**
   * send to history the first 2 rows if lev &le; blev
   *
   * @param bLev log only if bLev &lt History.dl
   * @param aPre pre characters
   * @param lev listing level
   * @param tit0
   * @param tit1
   */
  void sendHist2(int bLev, String aPre, int lev, String tit0, String tit1) {
    //  lev = Math.min(lev, History.auxInfo);
    if (ec.clearHist()) {
      return;
    }
    if (lev <= bLev) {
      resum(0);
      resum(1);
      hist.add(new History(aPre, lev, " " + tit0, A[0]));
      hist.add(new History(aPre, lev, " " + tit1, A[1]));
    }
  }

  /**
   * send to history the 2 & 4 rows if lev &le; blev
   *
   * @param bLev log only if bLev &lt History.dl
   * @param aPre pre characters
   * @param lev listing level
   * @param tit0
   * @param tit1
   */
  void sendHist24(int bLev, String aPre, int lev, String tit0, String tit1) {
    if (ec.clearHist()) {
      return;
    }
    lev = Math.min(lev, History.auxInfo);
    if (lev <= bLev) {
      hist.add(new History(aPre, lev, " " + tit0, A[2]));
      hist.add(new History(aPre, lev, " " + tit1, A[4]));
    }
  }

  /**
   * send to history the 0 & 1 rows if lev &le; blev
   *
   * @param bLev log only if bLev &lt History.dl
   * @param aPre pre characters
   * @param lev listing level
   * @param tit0
   * @param tit1
   */
  void sendHist01(int bLev, String aPre, int lev, String tit0, String tit1) {
    if (ec.clearHist()) {
      return;
    }
    lev = Math.min(lev, History.auxInfo);
    if (lev <= bLev) {
      resum(0);
      resum(1);
      hist.add(new History(aPre, lev, " " + tit0, A[0]));
      hist.add(new History(aPre, lev, " " + tit1, A[1]));
    }
  }

}

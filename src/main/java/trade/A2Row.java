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
 *
 * @author albert
 */
public class A2Row {

  EM eM = EM.eM;
  E eE = EM.eE;
  Econ ec;
  static final int ASECS[] = E.alsecs;
  static final int A2SECS[] = E.a2lsecs;
  static int d01[] = {0, 1};
  String titl = "unset";
  volatile private int[] ix;    // array min to max
  volatile ARow A[] = new ARow[2];
  volatile double asum = 0., anegSum = 0., aplusSum = 0., minSum = 0., minSum2 = 0.;
  volatile int[] aCnt = {-11, -11, -11, -11, -11, -11};
  
  String aPre;
  int blev;
  int lev;
  ArrayList<History> hist; // the owner will not change

  /**
   * new an A2Row from ARows a and b
   *
   * @param a
   * @param b
   */
  A2Row(ARow a, ARow b) {
    ec = a.getEc();
    aPre = ec.aPre = ec.aPre == null ? "&V" : ec.aPre;
    blev = ec.blev = ec.blev == 0 ? History.debuggingMinor11 : ec.blev;
   lev = ec.lev = ec.lev == 0 ? History.informationMinor9 : ec.lev;
   hist = ec.getHist(); // the owner will not change
    this.A[0] = a;
    this.A[1] = b;
    ix = new int[2 * E.lsecs];
  }

  /**
   * constructor
   *
   * @param leva level when sendHist used
   * @param titla title when sendHist used
   */
  public A2Row(Econ myEc,int leva, String titla) {
    ec = myEc;
    ec.lev = lev = leva;
    titl = titla;
    aPre = ec.aPre = ec.aPre == null ? "&V" : ec.aPre;
    blev = ec.blev = ec.blev == 0 ? History.debuggingMinor11 : ec.blev;
   hist = ec.getHist(); // the owner will not change
    this.A[0] = new ARow(ec).zero();
    this.A[1] = new ARow(ec).zero();
    ix = new int[2 * E.lsecs];
  }

  /**
   * new an A2Row with zero'd new ARows
   *
   */
  A2Row(Econ myEc) {
    ec = myEc;
    aPre =  ec.aPre;
    blev = ec.blev;
   lev = ec.lev ;
   hist = ec.getHist(); // the owner will not change
    this.A[0] = new ARow(ec).zero();
    this.A[1] = new ARow(ec).zero();
    ix = new int[2 * E.lsecs];
  }
  static int AaddB = 2;
  static int AsubB = 3;
  static int AmultB = 4;
  static int AdivbyB = 5;

  public A2Row(int op, int leva, String title, A6Row A, A6Row B) {
    ec = A.ec;
    aPre = ec.aPre = ec.aPre == null ? "&V" : ec.aPre;
    blev = ec.blev = ec.blev == 0 ? History.debuggingMinor11 : ec.blev;
   hist = ec.getHist(); // the owner will not change
    titl = title;
    for (int m : E.d2) {
      for (int n : E.alsecs) {
        if (op == AaddB && A.costs) {
          this.A[m].set(n, A.get(2 + m, n) + B.get(2 + m, n) + A.get(4 + m, n) + B.get(4 + m, n));
        }
        else if (op == AaddB) {
          this.A[m].set(n, A.get(2 + m * 2, n) + B.get(2 + m * 2, n) + A.get(3 + m * 2, n) + B.get(3 + m * 2, n));
        }
        else if (op == AsubB && A.costs) {
          this.A[m].set(n, A.get(2 + m, n) - B.get(2 + m, n) + A.get(4 + m, n) - B.get(4 + m, n));
        }
        else if (op == AsubB) {
          this.A[m].set(n, A.get(2 + m * 2, n) - B.get(2 + m * 2, n) + A.get(3 + m * 2, n) - B.get(3 + m * 2, n));
        }
      }
    }
  }

  /**
   * double format to string for putting into hist using eM.df
   *
   * @param v input
   * @return String value of v using
   */
  String df(double v) {
    return eM.df(v);
  }

  String setTitle(String title) {
    return titl = title;
  }

  double adjust2Fertility(A2Row adjReqFertilities, A2Row rawReqFertilities, int pors, int clan) {
//
    for (int m : E.alsecs) {
      adjReqFertilities.A[0].set(m, (rawReqFertilities.A[0].get(m) * (1. - eM.reqGrowthFertilityMinMult[pors][clan])) + rawReqFertilities.A[0].min() * eM.reqGrowthFertilityMinMult[pors][clan]);
      adjReqFertilities.A[1].set(m, (rawReqFertilities.A[1].get(m) * (1. - eM.reqGrowthFertilityMinMult[pors][clan])) + rawReqFertilities.A[1].min() * eM.reqGrowthFertilityMinMult[pors][clan]);
    }
    adjReqFertilities.A[0].revalueAtoMinMax(adjReqFertilities.A[0], eM.minFertility[pors], eM.maxFertility[pors]);
    adjReqFertilities.A[1].revalueAtoMinMax(adjReqFertilities.A[1], eM.minFertility[pors], eM.maxFertility[pors]);
    return (adjReqFertilities.A[0].ave() + adjReqFertilities.A[1].ave()) / 2.;
  }

  /**
   * order the ix from min to max of values then set valid
   */
  synchronized void makeOrderIx() {
    E.myTest(A[0] == null, " ARow A[0] not defined");
    E.myTest(A[1] == null, " ARow A[1] not defined");
    double[] min = new double[2 * E.lsecs];
    int[] minIx = new int[2 * E.lsecs];
    double minC, minO;
    int minOIx, minCIx;
    aCnt[0] = A[0].getSetCnt();
    aCnt[1] = A[1].getSetCnt();
    minSum2 = minSum = anegSum = aplusSum = asum = 0.;
    for (int g = 0; g <  E.L2SECS; g++) {
      if (g < E.lsecs) {
        minC = get(0,g);
      }
      else {
        minC = get(1,g-E.LSECS); //A[1].values[g - E.LSECS];
      }
      asum += minC;
      if (minC < E.NZERO) {
        anegSum += minC;
      }
      if (minC > E.PZERO) {
        aplusSum += minC;
      }
      minCIx = g;
      // now insert g into the array ordered min to max value
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
    for (int h = 0; h < eM.minSumCnt; h++) {
      // use array left above;
      minSum += min[h];
    }
    for (int h = 0; h < eM.minSum2Cnt; h++) {
      // use array left above;
      minSum2 += min[h];
    }
  }

  /**
   * check whether order has been set since either A0 or A1 were changed If they
   * were changed call makeOrderIx()
   */
  void checkIx() {
    if (aCnt[0] != A[0].getSetCnt() || aCnt[1] != A[1].getSetCnt()) {
      makeOrderIx();
    }
  }

  /**
   * zero all values in the A2Row
   *
   * @return this
   */
  public void zero() {

    for (int m = 0; m < 2; m++) {
      for (int n : E.alsecs) {
        set(m,n,0.); //A[m].values[n] = 0.;
      }
    }
  }

  /**
   * find the place in the order that matches 01x m
   *
   * @param m index into 01values to match
   * @return matching position in min to max order
   */
  int getCurN(int m) {
    checkIx();
    for (int p : A2SECS) {
      if (ix[p] == m) {
        return p;
      }
    }
    return -1;
  }

  /**
   * get the index of entry n order least to most
   *
   * @param n
   * @return
   */
  int get01Ix(int n) {
    checkIx();
    return ix[n];
  }

  /**
   * find the n from the min value of rows 0,1
   *
   * @param x index generated from some other object
   * @return the min n'th index
   */
  int findMinIx(int x) {
    checkIx();
    for (int m = 0; m < E.l2secs; m++) {
      if (x == ix[m]) {
        return m;
      }
    }
    return E.l2secs - 1;

  }

  /**
   * find the n from the min value of rows 0,1
   *
   * @param x some value in rows 0,1
   * @return the min n'th index
   */
  int findMinIx(double x) {
    checkIx();
    for (int m = 0; m < E.l2secs; m++) {
      if (x == get(ix[m])) {
        return m;
      }
    }
    return E.l2secs - 1;

  }

  /**
   * return the index of entry m,n
   *
   * @param m 0,1 row number
   * @param n 0-E.lsecs-1 column number in row
   * @return getIx(m*E.lsecs + n%E.lsecs);
   */
  int get01Ix(int m, int n) {
    return get01Ix(m * E.lsecs + n % E.lsecs);
  }

  /**
   * get index of least value of rows 0,1
   *
   * @see getIx(n) get n'th from the least value
   * @return index of least value
   */
  int minIx() {
    return get01Ix(0);
  }

  /**
   * get index of least value of rows 0,1
   *
   * @see getIx(n) get n'th from the least value
   * @return index of least value
   */
  int curMinIx() {
    return get01Ix(0);
  }

  /**
   * get pos index of least value of rows 0,1
   *
   * @param pos positions above the least min
   * @return index of least + pos value
   */
  int curMinIx(int pos) {
    return get01Ix(pos);
  }

  /**
   * return the index of the n'th from least value
   *
   * @depreciated see curMinIx
   * @param n n to return
   * @return return the index of the n'th from least value
   */
  int minIx(int n) {
    return get01Ix(n);
  }

  /**
   * get double least +1 index
   *
   * @return the index of the 1 more than the least
   */
  int curMin2Ix() {
    return get01Ix(1);
  }

  /**
   * get double least + 2 index
   *
   * @return the index of the 1 more than the least
   */
  int curMin3Ix() {
    return get01Ix(2);
  }

  /**
   * get the index of the highest (max) value
   *
   * @depreciated see curMaxIx
   * @return index of the max value
   */
  int maxIx() {
    return get01Ix(E.l2secs - 1);
  }

  /**
   * get the index of the highest -- max value
   *
   * @return index of max value
   */
  int curMaxIx() {
    return maxIx();
  }

  /**
   * get the index of the n'th highest max index
   *
   * @param n the number less than the max to return
   * @return the n'th max index
   */
  int curMaxIx(int n) {
    return maxIx(n);
  }

  /**
   * get the index of the n'th from max value
   *
   * @param n n of values less than the max value
   * @return the index of the n'th from the max value
   */
  int maxIx(int n) {
    return get01Ix(E.l2secs - 1 - n);
  }

  /**
   * return the least value
   *
   * @return
   */
  double min() {
    int aix = get01Ix(0);
    if (aix < E.lsecs) {
      return A[0].get(aix);
    }
    else {
      return A[1].get(aix - E.lsecs);
    }
  }

  /**
   * return minvalue in both rows
   *
   * @return
   */
  double curMin() {
    return min();
  }

  /**
   * return the n'th from larger than min
   *
   * @param n
   * @return
   */
  double curMin(int n) {
    return min(n);
  }

  /**
   * return the max value in this A2Row
   *
   * @return
   */
  double curMax() {
    return max();
  }

  /**
   * get the sum of positive values in rows 0,1
   *
   * @return
   */
  double curPlusSum() {
    checkIx();
    return aplusSum;
  }

  /**
   * get the sum of negative values in rows 0,1
   *
   * @return
   */
  double curNegSum() {
    checkIx();
    return anegSum;
  }

  /**
   * get the sum of the {@value Assets#minSumCnt} least values
   *
   * @return
   */
  double curMinSum() {
    checkIx();
    return minSum;
  }

  /**
   * get the sum of the {@value Assets#minSum2Cnt} least value
   *
   * @return
   */
  double curMinSum2() {
    checkIx();
    return minSum2;
  }

  /**
   * return the n'th less than max value
   *
   * @param n
   * @return
   */
  double curMax(int n) {
    checkIx();
    return max(n);
  }

  /**
   * return sum of both rows
   *
   * @return sum of both rows
   */
  double curSum() {
    checkIx();
    return asum;
  }

  /**
   * return the average of both rows together
   *
   * @return average of values in both rows
   */
  double curAve() {
    return curSum() * E.INVL2SECS;
  }

  /**
   * ordered get of a value from one of the ARow parts
   *
   * @param n the n'th ordered entry from min
   * @return
   */
  double min(int n) {
    int aix = get01Ix(n);
    if (aix < E.lsecs) {
      return get(0,aix);
    }
    else {
      return A[1].get(aix - E.lsecs);
    }
  }

  /**
   * get entry n in the 2 rows
   *
   * @param n
   * @return va double curGet(int n){ return get(n);} /** get entry n in the 2
   * rows
   *
   * @param n
   * @return value
   */
  double get(int n) {
    if (n < E.lsecs) {
      return A[0].get(n);
    }
    else {
      return A[1].get(n - E.lsecs);
    }
  }
  
  /** return sum of positive values selected by indexes in getIxs
   * 
   * @param getIxs  an array of index values
   * @return positive sum of indexed values
   */
  double arrayIxPlusSum(int[] getIxs){
    double rtn=0.;
   double t=0;
    for(int n:getIxs){
       if (n < E.lsecs) {
          
         rtn += ((t=get(0,n)) > 0.0) ? t : 0.0;//rtn += (A[0].values[n] > -0.0 ? A[0].values[n]:0.0);
        }
        else {
          rtn += ((t=get(1,n-E.LSECS)) > 0.0) ? t : 0.0;// get(1(A[1].values[n-E.lsecs]> -0.0 ? A[1].values[n-E.lsecs] : 0.0) ;
        }
    }
    return rtn;
  }

 /** return sum of negative values selected by indexes in getIxs
   * 
   * @param getIxs  an array of index values
   * @return positive sum of indexed values
   */
  double arrayIxNegSum(int[] getIxs){
    double rtn=0.,t=0.;
   
    for(int n:getIxs){
       if (n < E.lsecs) {
          rtn += rtn += ((t=get(0,n)) < 0.0) ? t : 0.0;
        }
        else {
          rtn += ((t=get(1,n-E.LSECS)) < 0.0) ? t : 0.0;
        }
    }
    return rtn;
  }
 

  /**
   * get entry n in the 2 rows compatible with other multARows
   *
   * @param n
   * @return va double curGet(int n){ return get(n);} /** get entry n in the 2
   * rows
   *
   * @param n
   * @return value
   */
  double curGet(int n) {
    return get(n);
  }

  double get(int m, int n) {
    assert m > -1 && m < 2: "error m=" +m + " value out of range" ;
    return A[m].get(n);
  }

  double set(int m, int n, double v) {
    E.myTestDouble(v, "in A2Row " + this.titl, " A[%1d][%1d] ", m % 2, n);
    return A[m % 2].set(n, v);
  }

  /**
   * set the corresponding ARow entry unordered
   *
   * @param n index < E.lsecs ARow A[0].set(n,v) else A[1].set(n%E.lsecs,v)
   * @param v value to set @return value set
   */
  double set(int n, double v) {
    E.myTestDouble(v, "in A2Row " + this.titl, " A[%1d][%1d] ", (int) (n / E.lsecs), (int) n % E.lsecs);
    if (n < E.lsecs) {
      return A[0].set(n, v);
    }
    else {
      return A[1].set(n - E.lsecs, v);
    }
  }

  /**
   * add to the corresponding ARow entry unordered
   *
   * @param n index &lt E.2lsecs
   * @param v value to add
   * @return sector value after addition
   */
  double add(int n, double v) {
    E.myTestDouble(v, "in A2Row " + this.titl, " A[%1d][%1d] ", (int) (n / E.lsecs), (int) n % E.lsecs);
    if (n < E.lsecs) {
      return A[0].add(n, v);
    }
    else {
      return A[1].add(n - E.lsecs, v);
    }
  }

  /**
   * add value to the ARow m sector n
   *
   * @param m select ARow m%2
   * @param n select sector n%E.lsecs
   * @param v value to add
   * @return resulting value of A2Row m,n
   */
  double add(int m, int n, double v) {
    E.myTestDouble(v, "in A2Row " + this.titl, " A[%1d][%1d] ", (int) (n / E.lsecs), (int) n % E.lsecs);
    return A[m % 2].add(n % E.lsecs, v);
  }

  /**
   * copy: make A2Row with new instances of the 2 ARows from the invoking A2Row
   *
   * @return the copy of of this
   */
  A2Row copy() {
    A2Row rtn = new A2Row(this.A[0].copy(),this.A[1].copy());
   // ARow nCargo = new ARow(ec).set(getARow(0));
   // ARow nGuests = new ARow(ec).set(getARow(1));
   // A2Row tmp = new A2Row(nCargo, nGuests);
    return rtn;
  }
  
  /** return a new A2Row normalized by moving the min to base
   * 
   * @param base the new min for this object
   * @param lev level of the new A2Row
   * @param tit title of the new A2Row
   * @return each row min moved to base
   */
  A2Row normalize(double base, int lev, String tit){
    A2Row rtn = new A2Row(ec,lev,tit);
  
    for(int m=0; m< A.length;m++){
      //double mAve = A[0].ave();
      double norm = base - A[m].min();
      for(int n=0;n<E.LSECS;n++){
        rtn.set(m,n,get(m,n) + norm);
      }
    } 
    return rtn;
  }

  /**
   * copy the values of prev into this, particularly do not change the reference
   * of each ARow
   *
   * @param prev a previous copy of this A6Row
   * @return this with updated values and setCnt
   */
  public A2Row copyValues(A2Row prev) {
    if (prev != null) {
      for (int m = 0; m < 2; m++) {
        for (int n = 0; n < E.LSECS; n++) {
      //    A[m].values[n] = prev.A[m].values[n];
          set(m,n,prev.get(m,n));
        }
      }// end m     
    }
    return this;
  }

  /**
   * make a new copy of A2Row with a new EM
   *
   * @param newEM the reference to the new EM
   * @return an A2Row with all new references, but copied values
   */
  A2Row newCopy(EM newEM) {
    A2Row ret = new A2Row(ec);
    String titl = "unset";
    ret.A[0] = A[0].newCopy(eM);
    ret.A[1] = A[1].newCopy(eM);
    eM = newEM;
    int siz = ix.length;
    for (int i = 0; i < siz; i++) {
      ret.ix[i] = ix[i];
    }
    ret.asum = asum;
    ret.anegSum = anegSum;
    ret.aplusSum = aplusSum;
    ret.minSum = minSum;
    ret.minSum2 = minSum2;
    siz = aCnt.length;
    for (int i = 0; i < siz; i++) {
      ret.aCnt[i] = aCnt[i];
    }
    return ret;
  }

  /**
   * set a new A2Row copying rin
   *
   * @param rin the A2Row to be copied
   * @return A2Row pointing to the revised A2Row
   */
  A2Row set(A2Row rin) {
    // A2Row tmp = new A2Row(rin.getARow(0), rin.getARow(E.lsecs));
    // set new values into the existing ARows
    for (int m : E.alsecs) {
      A[0].set(m, rin.getARow(0).get(m));
      A[1].set(m, rin.getARow(1).get(m));
    }
    return this;
  }

  /**
   * flip the signs of the values in both ARows part of this A2Row
   *
   * @return this the flipped ARows
   */
  A2Row flip() {
    A[0].flip();
    A[1].flip();
    return this;
  }

  /**
   * get the ARow for this min ordered n
   *
   * @param n index to ordered values 0=min,1=min2 , 15=max
   * @return ARow associated with this n
   */
  ARow minARow(int n) {
    int ii = get01Ix(n);
    if (ii < E.lsecs) {
      return A[0];
    }
    else {
      return A[1];
    }
  }

  /**
   * get the ARow for this un ordered ix
   *
   * @param ix 0 = A[0], other = A[1]
   * @return ix < E.lsecs ARow a else b
   */
  ARow getARow(int ix) {
    if (ix < 1) {
      return A[0];
    }
    else {
      return A[1];
    }
  }

  /**
   * ret reference to row a or b
   *
   * @param n
   * @return a if n == 0 else b
   */
  ARow getRow(int n) {
    if (n == 0) {
      return A[0];
    }
    else {
      return A[1];
    }
  }

  /**
   * find the product of the parameters
   *
   * @param A
   * @param B
   * @param C
   * @param D
   * @param F
   * @return
   */
  A2Row mult(A2Row A, A2Row B, A2Row C, A2Row D, A2Row F) {
    for (int m : E.d2) {
      for (int n : E.alsecs) {
        this.set(m, n, A.get(m, n) * B.get(m, n) * C.get(m, n) * D.get(m, n) * F.get(m, n));
      }
    }
    return this;
  }

  /**
   * find the product of the parameters
   *
   * @param A
   * @param B
   * @param C
   * @param D
   * @param V
   * @return
   */
  A2Row mult(A2Row A, A2Row B, A2Row C, A2Row D, double V) {
    for (int m : E.d2) {
      for (int n : E.alsecs) {
        this.set(m, n, A.get(m, n) * B.get(m, n) * C.get(m, n) * D.get(m, n) * V);
      }
    }
    return this;
  }

  /**
   * find the product of the parameters
   *
   * @param A
   * @param V
   * @return
   */
  A2Row mult(A2Row A, double V) {
    for (int m : E.d2) {
      for (int n : E.alsecs) {
        this.set(m, n, A.get(m, n) * V);
      }
    }
    return this;
  }

  /**
   * OBSOLETE calculate the possible move for 4 different combinations There is
   * a move source and resource and staff assesment This process provides the
   * files that permits 1) find the sector with the worst/min value in
   *
   * @param sAvail output move staff resource and staff assesments
   * @param rRevAvail output move resource reverse r and s assesments
   * @param sRevAvail output move staff revers r and s assesments
   * @param balFrac fraction of each SubAsset balance available to move
   * @param availFrac fraction of working Subassets available
   * @param maxMove maxMov generally a fraction of the rc or sg balance sum
   * @param balances the balances of each resource
   * @param needs = need = -available units
   * @param aaDiv resource to ? resource unit cost
   * @param abDiv resource to ? staff unit cost
   * @param baDiv staff to ? resource unit cost
   * @param bbDiv staff to ? staff unit cost
   * @return straight rAvail is r avail move and s avail move, chhose min
   */
  A2Row setAvailableToExchange(A2Row sAvail, A2Row rRevAvail, A2Row sRevAvail, double balFrac, double availFrac, double maxMove, A6Row balances, A6Row needs, double aaDiv, double abDiv, double baDiv, double bbDiv) {
    ec.lev = lev = History.dl + 5;// can change to .aux5Info
    ec.aPre = aPre = "S^";
    String tit = "rmov rasmt";
    if (lev < History.dl) {
      hist.add(new History(aPre, lev, "pre r rmov", needs.getRow(0)));
      hist.add(new History(aPre, lev, "pre s rmov", needs.getRow(1)));
    }
    // amount rc move with  high rc cost to rc r to r rcost
    A[0].xsetAvailableSwap(tit, balFrac, availFrac, maxMove, balances.getRow(0), needs.getRow(0), 1.09 + aaDiv);
    if (lev < History.dl) {
      hist.add(new History(aPre, lev, tit, A[0]));
    }
    // amount rc move with low sg cost to sg
    tit = "      sasmt";
    A[1].xsetAvailableSwap(tit, balFrac, availFrac, maxMove, balances.getRow(1), needs.getRow(1), .2 + abDiv);
    if (lev < History.dl) {
      hist.add(new History(aPre, lev, tit, A[1]));
    }
    // find amount sg move with rc cost to rc

    tit = "     rasmt";
    sAvail.A[0].xsetAvailableSwap(tit, balFrac, availFrac, maxMove, balances.getRow(0), needs.getRow(0), .2 + baDiv);
    if (lev < History.dl) {
      hist.add(new History(aPre, lev, tit, sAvail.A[0]));
    }
    tit = "smov sasmt";
    // amount sg move with sg cost to sg
    sAvail.A[1].xsetAvailableSwap(tit, balFrac, availFrac, maxMove, balances.getRow(1), needs.getRow(1), 1.09 + bbDiv);
    if (lev < History.dl) {
      hist.add(new History(aPre, lev, tit, sAvail.A[1]));
    }
    // ***************** now do the reversed availability  s has the high cost for xfer
    // amount rc move iwth sg cost to rc
    tit = "rmov sasmt";
    rRevAvail.A[0].xsetAvailableSwap(tit, balFrac, availFrac, maxMove, balances.getRow(0), needs.getRow(0), 1.09 + abDiv);
    if (lev < History.dl) {
      hist.add(new History(aPre, lev, tit, rRevAvail.A[0]));
    }
    // amount rc move with rc cost to sg
    tit = "    rasmt";
    rRevAvail.A[1].xsetAvailableSwap(tit, balFrac, availFrac, maxMove, balances.getRow(1), needs.getRow(1), .2 + aaDiv);
    if (lev < History.dl) {
      hist.add(new History(aPre, lev, tit, rRevAvail.A[1]));
    }
    // amount sg move with sg cost to rc
    tit = "     sasmt";
    sRevAvail.A[0].xsetAvailableSwap(tit, balFrac, availFrac, maxMove, needs.getRow(0), needs.getRow(0), .2 + bbDiv);
    if (lev < History.dl) {
      hist.add(new History(aPre, lev, tit, sRevAvail.A[0]));
    }
    // amount sg move with rc cost to sg
    tit = "smov rasmt";
    sRevAvail.A[1].xsetAvailableSwap(tit, balFrac, availFrac, maxMove, needs.getRow(1), needs.getRow(1), 1.09 + baDiv);
    if (lev < History.dl) {
      hist.add(new History(aPre, lev, tit, sRevAvail.A[1]));
    }
    return this;
  }

  /**
   * OBSOLETE calculate the amount available to exchange by taking the
   * appropriate costs against the sources are RC and SG, return 2 different
   * variations set rc and sg as straight r and s availability
   *
   * @param revCostsAvail reverse the r & s costs for both rc and sg
   * @param balFrac fraction of source balance to be considered
   * @param availFrac fraction of needs availability to be considered
   * @param maxMove maxMov generally a fraction of the rc or sg balance sum
   * @param needs = need = -available units
   * @param aaDiv resource to resource resource unit cost
   * @param abDiv resource to resource staff unit cost
   * @param baDiv staff to staff resource unit cost
   * @param bbDiv staff to staff staff unit cost
   * @return straight avail using straight r & s costs
   */
  A2Row setAvailableToExchange(A2Row revCostsAvail, double balFrac, double availFrac, double maxMove, A6Row needs, double aaDiv, double abDiv, double baDiv, double bbDiv) {
    ec.lev = lev = History.debuggingMinor11;// can change to .aux5Info
    ec.aPre = aPre = "X^";
    if (lev < History.dl) {
      hist.add(new History(aPre, lev, "xneeds rc", needs.getRow(0)));
      hist.add(new History(aPre, lev, "xneeds sg", needs.getRow(1)));
    }
    // amount rc move with  high rc cost to rc r to r rcost
    A[0].setAvailableSwap(balFrac, availFrac, maxMove, needs.getRow(0), needs.getRow(0), needs.getRow(1), 1.05 + aaDiv, .05 + abDiv);
    if (lev < History.dl) {
      hist.add(new History(aPre, lev, "xst r rtn rAvail", A[0]));
    }
    // amount sg move with straight rc & sg costs
    A[1].setAvailableSwap(balFrac, availFrac, maxMove, needs.getRow(1), needs.getRow(0), needs.getRow(1), .05 + baDiv, 1.05 + bbDiv);
    if (lev < History.dl) {
      hist.add(new History(aPre, lev, "xst r rtn sFind", A[1]));
    }
    // amount rc avail for r to r with s cost, than r cost
    revCostsAvail.A[0].setAvailableSwap(balFrac, availFrac, maxMove, needs.getRow(0), needs.getRow(0), needs.getRow(1), 1.05 + abDiv, .05 + aaDiv);
    if (lev < History.dl) {
      hist.add(new History(aPre, lev, "revCostsAvail r", revCostsAvail.A[0]));
    }
    // amount rc avail for s to s  with s cost, than r cost
    revCostsAvail.A[1].setAvailableSwap(balFrac, availFrac, maxMove, needs.getRow(1), needs.getRow(0), needs.getRow(1), .05 + bbDiv, 1.05 + baDiv);
    if (lev < History.dl) {
      hist.add(new History(aPre, lev, "\"revCostsAvail s", revCostsAvail.A[1]));
    }

    return this;
  }

  /**
   * calculate the possible moves of resource or staff to increase health and
   * growth. Finds the possible move and cost Frac for each sector when: 1. r to
   * r normal r cost frac + rmov 1a. r to r normal s cost frac 2. s to s normal
   * r cost frac + smov 2a. s to s normal s cost frac 3 r to r normal s swap r
   * cost frac + rmov 3a r to r normal s swap s cost frac 4 s to s normal r swap
   * r cost frac + smov 4a s to s normal r swap s cost frac
   *
   * @param mtgAvails6 input amount available for each sector in each row
   * @param sAvail output move staff resource and staff assesments
   * @param rRevAvail output move resource reverse r and s assesments
   * @param sRevAvail output move staff revers r and s assesments
   * @param balFrac fraction of each SubAsset balance available to move
   * @param availFrac fraction of working Subassets available
   * @param maxMove maxMov generally a fraction of the rc or sg balance sum
   * @param avails the avails not needs
   * @param aaDiv resource to ? resource unit cost
   * @param abDiv resource to ? staff unit cost
   * @param baDiv staff to ? resource unit cost
   * @param bbDiv staff to ? staff unit cost
   * @return straight rAvail is r avail move and s avail move, chhose min
   */
  A2Row setAvailableToExchange2(A2Row sAvail, A2Row rRevAvail, A2Row sRevAvail, double balFrac, double availFrac, double maxMove, A6Row avails, double aaDiv, double abDiv, double baDiv, double bbDiv) {
    ec.lev = lev = History.dl + 5;// can change to .aux5Info
    ec.aPre = aPre = "^s";

    // amount r mov ignore s, s will be chosen independently A[1]
    A[0].setAvailableSwapi(balFrac, availFrac, maxMove, avails.getRow(2), avails.getRow(2), avails.getRow(4), 1.05 + aaDiv, .05 * abDiv); // r to r mov r cost rfrac + rmov
    if (lev < History.dl) {
      hist.add(new History(aPre, lev, "rMov", A[0]));
    }
    // amount r move with low s cost to s, max independent of r A[0}
    A[1].setAvailableSwapi(balFrac, availFrac, maxMove, avails.getRow(4), avails.getRow(2), avails.getRow(4), .05 * aaDiv, .05 + abDiv); // r to r mov s cost sfrac
    if (lev < History.dl) {
      hist.add(new History(aPre, lev, "s for rmov", A[1]));
    }
    // find amount s to s move with r cost frac
    sAvail.A[0].setAvailableSwapi(balFrac, availFrac, maxMove, avails.getRow(4), avails.getRow(2), avails.getRow(4), .05 + baDiv, .05 * bbDiv); // s to s mov r cost rfrac
    if (lev < History.dl) {
      hist.add(new History(aPre, lev, "smov rcst", sAvail.A[0]));
    }
    // amount smov with s cost independent of A[0]
    sAvail.A[1].setAvailableSwapi(balFrac, availFrac, maxMove, avails.getRow(4), avails.getRow(2), avails.getRow(4), 05 * baDiv, 1.05 + bbDiv); // s to s mov s cost sfrac + smov
    if (lev < History.dl) {
      hist.add(new History(aPre, lev, "sMov sCst", sAvail.A[1]));
    }
    // ***************** now do the reversed availability  sCst now the high cost rdiv=aaDiv
    // amount rmov but reverse r rCost as s rFrac
    rRevAvail.A[0].setAvailableSwapi(balFrac, availFrac, maxMove, avails.getRow(2), avails.getRow(2), avails.getRow(4), 1.05 + baDiv, .05 * bbDiv);// r to r mov s cost rfrac + mov
    if (lev < History.dl) {
      hist.add(new History(aPre, lev, "rev rM sC", rRevAvail.A[0]));
    }
    // amount rMov but reverse rFrac cost for s
    rRevAvail.A[1].setAvailableSwapi(balFrac, availFrac, maxMove, avails.getRow(4), avails.getRow(2), avails.getRow(4), .05 * aaDiv, 1.05 + abDiv); // r to r mov s cost sfrac
    if (lev < History.dl) {
      hist.add(new History(aPre, lev, "rev sMov rC", rRevAvail.A[1]));
    }
    // amount sg move with sg cost to rc
    sRevAvail.A[0].setAvailableSwapi(balFrac, availFrac, maxMove, avails.getRow(4), avails.getRow(2), avails.getRow(4), .05 + aaDiv, .05 * abDiv); // s to s mov r cost r frac
    if (lev < History.dl) {
      hist.add(new History(aPre, lev, "sToS rCrFrac", sRevAvail.A[0]));
    }
    // amount s move with r cost to s
    sRevAvail.A[1].setAvailableSwapi(balFrac, availFrac, maxMove, avails.getRow(4), avails.getRow(2), avails.getRow(4), .05 * aaDiv, 1.05 + abDiv); // s to s mov r cost sfrac + mov
    if (lev < History.dl) {
      hist.add(new History(aPre, lev, "sTOs rCsFrac", sRevAvail.A[1]));
    }
    return this;
  }

  /**
   * OBSOLETE calculate the amount available to exchange by taking the
   * appropriate costs against the sources are RC and SG, return 2 different
   * variations set rc and sg as straight r and s availability
   *
   * @param revCostsAvail reverse the r & s costs for both rc and sg
   * @param balFrac fraction of source balance to be considered
   * @param availFrac fraction of needs availability to be considered
   * @param maxMove maxMov generally a fraction of the rc or sg balance sum
   * @param bals = actual balances
   * @param aaDiv resource to resource resource unit cost
   * @param abDiv resource to resource staff unit cost
   * @param baDiv staff to staff resource unit cost
   * @param bbDiv staff to staff staff unit cost
   * @return straight avail using straight r & s costs
   */
  A2Row setAvailableToExchange(A2Row revCostsAvail, double balFrac, double availFrac, double maxMove, ABalRows bals, double aaDiv, double abDiv, double baDiv, double bbDiv) {
    ec.lev = lev = History.debuggingMinor11;// can change to .aux5Info
    ec.aPre = aPre = "X^";
    if (lev < History.dl) {
      hist.add(new History(aPre, lev, "xneeds rc", bals.getRow(0)));
      hist.add(new History(aPre, lev, "xneeds sg", bals.getRow(1)));
    }
    // amount r move with  high r cost to r r to r rcost
    A[0].setAvailableSwap2(balFrac, availFrac, maxMove, bals.getRow(2), bals.getRow(2), bals.getRow(4), 1.05 + aaDiv, .05 + abDiv);
    if (lev < History.dl) {
      hist.add(new History(aPre, lev, "xst r rtn rAvail", A[0]));
    }
    // amount s move with straight r & s costs
    A[1].setAvailableSwap2(balFrac, availFrac, maxMove, bals.getRow(4), bals.getRow(2), bals.getRow(4), .05 + baDiv, 1.05 + bbDiv);
    if (lev < History.dl) {
      hist.add(new History(aPre, lev, "xst r rtn sFind", A[1]));
    }
    // amount r avail for r to r with s cost, than r cost
    revCostsAvail.A[0].setAvailableSwap2(balFrac, availFrac, maxMove, bals.getRow(2), bals.getRow(4), bals.getRow(4), 1.05 + abDiv, .05 + aaDiv);
    if (lev < History.dl) {
      hist.add(new History(aPre, lev, "revCostsAvail r", revCostsAvail.A[0]));
    }
    // amount rc avail for s to s  with s cost, than r cost
    revCostsAvail.A[1].setAvailableSwap2(balFrac, availFrac, maxMove, bals.getRow(4), bals.getRow(2), bals.getRow(1), .05 + bbDiv, 1.05 + baDiv);
    if (lev < History.dl) {
      hist.add(new History(aPre, lev, "\"revCostsAvail s", revCostsAvail.A[1]));
    }

    return this;
  }

  /**
   * calculate the reserve amount available to swap by taking the appropriate
   * costs the sources are c and g
   *
   * @param balFrac fraction of source balance to use in move
   * @param availFrac fraction of resource,staff (avails) available for costs
   * @param maxMove maximum move regardless of other factors
   * @param avails = avails for moves and costs
   * @param aaDiv cargo to resource resource cost divisor
   * @param abDiv cargo to resource staff cost divisor
   * @param baDiv quests to staff resource cost divisor
   * @param bbDiv guests to staff staff cost divisor
   * @return
   */
  A2Row setAvailableToIncr(double balFrac, double otherFrac, double maxMove, A6Row avails, double aaDiv, double abDiv, double baDiv, double bbDiv) {
    ec.lev = lev = History.debuggingMinor11;// can change to .aux5Info
    if (lev < History.aux5Info) {
      hist.add(new History("sAv", lev, "inc bal c", avails.getRow(3)));
      hist.add(new History("sAv", lev, "inc bal g", avails.getRow(5)));
      hist.add(new History("sAv", lev, "inc bal rc", avails.getRow(0)));
      hist.add(new History("sAv", lev, "inc bal sg", avails.getRow(1)));
    }
    // c to r by c and g
    A[0].setAvailableSwapi(balFrac, otherFrac, maxMove, avails.getRow(3), avails.getRow(2), avails.getRow(4), .05 + aaDiv, .05 + abDiv);

    // g to s  by c and g
    A[1].setAvailableSwapi(balFrac, otherFrac, maxMove, avails.getRow(5), avails.getRow(2), avails.getRow(4), .05 + baDiv, .05 + bbDiv);
    return this;
  }

  /**
   * OBSOLETE calculate the reserve amount available to swap by taking the
   * appropriate costs the sources are c and g
   *
   * @param balFrac fraction of source balance to use in move
   * @param availFrac fraction of resource,staff available for costs
   * @param maxMove maximum move regardless of other factors
   * @param bals = balances for moves and costs
   * @param aaDiv cargo to resource resource cost divisor
   * @param abDiv cargo to resource staff cost divisor
   * @param baDiv quests to staff resource cost divisor
   * @param bbDiv guests to staff staff cost divisor rMov + rMov * rCostFrac =
   * balRSrc rMov *(1.+ rCostFrac+.05) = balRSrc rrMov = balRSrc/(1.05 +
   * rCostFrac), rsMov = balSSrc/(.05+sCostFrac) mov = Math.min(rrMov,rsMov);
   * @return largest move for r and s
   */
  A2Row setAvailableToIncr(double balFrac, double otherFrac, double maxMove, ABalRows bals, double aaDiv, double abDiv, double baDiv, double bbDiv) {
    ec.lev = lev = History.debuggingMinor11;// can change to .aux5Info
    if (lev < History.aux5Info) {
      hist.add(new History("sAv", lev, "inc bal c", bals.getRow(3)));
      hist.add(new History("sAv", lev, "inc bal g", bals.getRow(5)));
      hist.add(new History("sAv", lev, "inc bal rc", bals.getRow(0)));
      hist.add(new History("sAv", lev, "inc bal sg", bals.getRow(1)));
    }
    // c to r by c and s
    A[0].setAvailableSwapi(balFrac, otherFrac, maxMove, bals.getRow(3), bals.getRow(2), bals.getRow(4), .05 + aaDiv, .05 + abDiv); //move limited by bal limit and limits on 2 and 4

    // g to s  by r and g
    A[1].setAvailableSwapi(balFrac, otherFrac, maxMove, bals.getRow(5), bals.getRow(2), bals.getRow(4), .05 + baDiv, .05 + bbDiv);// move limited by bal limit and limits on 2 and 4
    return this; // A[0],A[1]...
  }

  /**
   * calculate the working amount available to decrement by taking the
   * appropriate costs the sources are r and s
   *
   * @param balFrac fraction of source balance to use
   * @param availFrac fraction of r + 3 available
   * @param maxMove max move
   * @param avails = available all sectors available
   * @param aaDiv resource to ? resource cost
   * @param abDiv resource to ? staff cost
   * @param baDiv staff to ? resource cost
   * @param bbDiv staff to ? staff cost
   * @return
   */
  A2Row setAvailableToDecrement(double balFrac, double availFrac, double maxMove, A6Row avails, double aaDiv, double abDiv, double baDiv, double bbDiv) {
    ec.lev = lev = History.debuggingMinor11;// can change to .aux5Info
    ec.aPre = aPre = "s@";
    if (lev < History.aux5Info) {
      hist.add(new History(aPre, lev, "-bal rc", avails.getRow(0)));
      hist.add(new History(aPre, lev, "-resource", avails.getRow(0)));
      hist.add(new History(aPre, lev, "-staff", avails.getRow(1)));
    }
    // r  by r and s
    A[0].setAvailableSwapi(balFrac, availFrac, maxMove, avails.getRow(2), avails.getRow(2), avails.getRow(4), 1.05 + aaDiv, .05 + abDiv);
    if (lev < History.aux5Info) {
      hist.add(new History(aPre, lev, "-bal sg", avails.getRow(1)));
      hist.add(new History(aPre, lev, "-resource", avails.getRow(0)));
      hist.add(new History(aPre, lev, "-staff", avails.getRow(1)));
    }
    // s  by rc and sg
    A[1].setAvailableSwap(balFrac, availFrac, maxMove, avails.getRow(4), avails.getRow(2), avails.getRow(4), .05 + baDiv, 1.05 + bbDiv);
    return this;
  }

  /**
   * find the product of the parameters
   *
   * @param A
   * @param B
   * @param C
   * @param D
   * @return
   */
  A2Row mult(A2Row A, A2Row B, A2Row C, A2Row D) {
    for (int m : E.d2) {
      for (int n : E.alsecs) {
        this.set(m, n, A.get(m, n) * B.get(m, n) * C.get(m, n) * D.get(m, n));
      }
    }
    return this;
  }

  /**
   * find the product of the parameters
   *
   * @param A
   * @param B
   * @param C
   * @return
   */
  A2Row mult(A2Row A, A2Row B, A2Row C) {
    for (int m : E.d2) {
      for (int n : E.alsecs) {
        this.set(m, n, A.get(m, n) * B.get(m, n) * C.get(m, n));
      }
    }
    return this;
  }

  /**
   * set instance to the each by each sum of a,b,c
   *
   * @param a first instance to add
   * @param b second instance to add
   * @param c third instance to add
   * @return sum of each by each
   */
  A2Row add(A2Row a, A2Row b, A2Row c) {
    for (int m : E.d2) {
      for (int n : E.alsecs) {
        this.set(m, n, a.get(m, n) + b.get(m, n) + c.get(m, n));
      }
    }
    return this;
  }

  /**
   * set to multiple each by each of rows A and B
   *
   * @param A  multiplicand
   * @param B multiplier
   * @return each by each A * B
   */
  A2Row mult(A2Row A, A2Row B) {
    for (int m : E.A01) {
      for (int n : E.alsecs) {
        this.set(m, n, A.get(m, n) * B.get(m, n));
      }
    }
    return this;
  }
  
  /** multiply strategic values to get the equivalent strategic worth from r and s SubAssets
   * 
   * @param stratVar  the raw strategic values
   * @param rWorth   r.worth
   * @param sWorth   s.worth
   * @return strategic worths according to r and s SubAssets
   */
  A2Row setMultStratByWorth(A2Row stratVar,ARow rWorth,ARow sWorth){
    
      ARow rStrat = stratVar.getRow(0);
      ARow sStrat = stratVar.getRow(1);
      double rMult = rWorth.sum()/rStrat.sum();
      double sMult = sWorth.sum()/sStrat.sum();
     E.myTestDouble(rMult, "r sum =" + eM.mf(rWorth.sum()) + ", rStrat sum=" + eM.mf(rStrat.sum()));
     E.myTestDouble(sMult, "s sum=" + eM.mf(sWorth.sum()) + ", sStrat sum=" + eM.mf(sStrat.sum()));
      for(int n = 0; n < E.LSECS; n++){
          set(0,n,rStrat.get(n) * rMult);
          set(1,n,sStrat.get(n) * sMult);
      }
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
  A2Row setLimits(A2Row b, double low, double high) {
    for (int n : E.ASECS) {
      for (int m : E.A01) {
        double d = b.get(m, n);
        set(m, n, d < low ? low : d > high ? high : d);
      }
    }
    return this;
  }

  /**
   * set each value to the min of the corresponding a,b,c
   *
   * @param a input
   * @param b input
   * @param c input
   * @return min a,b,c
   */
  A2Row setMin(A2Row a, A2Row b, A2Row c) {
    double aa, bb, cc, dd;
    for (int n : ASECS) {
      for (int m : E.A01) {
        aa = a.get(m, n);
        bb = b.get(m, n);
        cc = c.get(m, n);
        dd = aa < bb ? aa : bb;
        dd = dd < cc ? dd : cc;
        this.set(m, n, dd);
      }
    }
    return this;
  }

  /**
   * set each value to the min of the corresponding a,b
   *
   * @param a input for min
   * @param b input for min
   * @return min of a,b
   */
  A2Row setMin(A2Row a, A2Row b) {
    double aa, bb, dd;
    for (int n : ASECS) {
      for (int m : E.A01) {
        aa = a.get(m, n);
        bb = b.get(m, n);
        dd = aa < bb ? aa : bb;
        this.set(m, n, dd);
      }
    }
    return this;
  }

  /**
   * derive a strategic trade value using mult limited by limLow and its
   * reciprocal. The results center around 1 so that they can be multiplied
   * together and leave a result around 1 in this. mult is divided by the
   * average to keep results the same as the total values grow year by year
   *
   * @param A Input
   * @param mult0 use mult/A.A[0]ave() as proportional multiplier
   * @param mult1 use mult/A.A[1]ave() as proportional multiplier
   * @param limLow lowest faction permitted for result. limHigh is its
   * reciprocal
   * @return calling ARow new this value
   */
  A2Row strategicValAbyMultLim(A2Row A, double mult0, double mult1, double lowlim) {
    this.A[0].strategicRecipValAbyMultLim(A.titl, A.A[0], mult0, lowlim);
    this.A[1].strategicRecipValAbyMultLim(A.titl, A.A[1], mult1, lowlim);
    return this;
  }

  /**
   * derive a strategic trade value using mult limited by limLow and its
   * reciprocal. The results center around 1 so that they can be multiplied
   * together and leave a result around 1 in this. mult is divided by the
   * average to keep results the same as the total values grow year by year
   *
   * @param A Input
   * @param mult0 use mult/A.A[0]ave() as proportional multiplier
   * @param mult1 use mult/A.A[1]ave() as proportional multiplier
   * @param limLow lowest faction permitted for result. limHigh is its
   * reciprocal
   * @return calling ARow new this value
   */
  A2Row strategicValAbyMultLim(A6Row A, double mult0, double mult1, double lowlim) {
    this.A[0].strategicRecipValAbyMultLim(A.titl, A.A[0], mult0, lowlim);
    this.A[1].strategicRecipValAbyMultLim(A.titl, A.A[1], mult1, lowlim);
    return this;
  }

  /**
   * derive a reciprocal strategic trade value using mult limited by limLow and
   * its reciprocal. The results center around 1 so that they can be multiplied
   * together and leave a result around 1 in this. mult is divided by the
   * average to keep results the same as the total values grow year by year
   *
   * @param A Input
   * @param mult0 use mult/A.A[0]ave() as proportional multiplier
   * @param mult1 use mult/A.A[1]ave() as proportional multiplier
   * @param limLow lowest faction permitted for result. limHigh is its
   * reciprocal
   * @return calling ARow new this value
   */
  A2Row strategicRecipValAbyMultLim(A2Row A, double mult0, double mult1, double lowlim) {
    this.A[0].strategicRecipValAbyMultLim(A.titl, A.A[0], mult0, lowlim);
    this.A[1].strategicRecipValAbyMultLim(A.titl, A.A[1], mult1, lowlim);
    return this;
  }

  /**
   * derive a reciprocal strategic trade value using mult limited by limLow and
   * its reciprocal. The results center around 1 so that they can be multiplied
   * together and leave a result around 1 in this. mult is divided by the
   * average to keep results the same as the total values grow year by year
   *
   * @param A Input
   * @param mult0 use mult/A.A[0]ave() as proportional multiplier
   * @param mult1 use mult/A.A[1]ave() as proportional multiplier
   * @param limLow lowest faction permitted for result. limHigh is its
   * reciprocal
   * @return calling ARow new this value
   */
  A2Row strategicRecipValAbyMultLim(A6Row A, double mult0, double mult1, double lowlim) {
    this.A[0].strategicRecipValAbyMultLim(A.titl, A.A[0], mult0, lowlim);
    this.A[1].strategicRecipValAbyMultLim(A.titl, A.A[1], mult1, lowlim);
    return this;
  }

  /**
   * derive a reciprocal strategic trade value using mult limited by limLow and
   * its reciprocal. The results center around 1 so that they can be multiplied
   * together and leave a result around 1 in this. mult is divided by the
   * average to keep results the same as the total values grow year by year if
   * (a-median)/(min-median) = -1.75 &lt 0, -1.75/-2.5 = 1/.7 = 1.4285 --2 &lt
   * 0, -2./-2.5= 1/1 if (a-median)/(min-median) = .7 then result = 1 + 1/.7
   *
   * @param A Input @param mult use mult/A.ave() as proportional multiplier
   *
   * e
   * r
   *
   * @param limLow lowest faction permitted for result. limHigh is its
   * reciprocal @return a new ARow with strategic values
   */
  A2Row strategicRecipValBbyMultLim(String titla, A2Row B, double mult, double alimLow) {
    double amax = B.max();
    double amin = B.min();
    double ave = (amax + amin) / 2.;  // ave = m
    double adif = (amax - amin); //size of a_most - a_least
    double athlf = amax - ave;  // amt > ave
    double alhlf = amin - ave;  // amt <= ave
    double dVal = 0;
    double aVal = 1;
//   double amultlr = 1.3;  // lower mult
//    double amulthr = 1.3;  // upper mult
    double fmult = mult;  // original mult
    // force mult to be > 1.
    mult = mult < 1. ? 1 / mult : mult;
    //  mult = (mult - 1.);   // amount above 1/
    double amultr = 1.;   // temp of amultlr or amulthr
    //   double aMult = mult * amulthr / ahlf; // .4/5 = .08  or -.08 if -.4
    //   double mVal = aMult * dVal;
    double tVal = 1., tVal1 = 1., tVal2 = 1., tVal3 = 1.;
    double limHigh = alimLow < 1. ? 1. / alimLow : alimLow;
    double limLow = alimLow > 1. ? 1. / alimLow : alimLow;
    // set fudge to aPrevent creating values beyound limits
    double fudge = (1. + limLow) * (.3 + mult);
    double ahlf = alhlf * fudge; // lower half
    EM.curEcon.hist.add(new History(History.debuggingMinor11, "n=" + EM.curEcon.as.n + " " + titla, "Hv" + EM.curEcon.df(amax), "Lv" + EM.curEcon.df(amin), "H-L" + EM.curEcon.df(adif), "mlt=" + EM.curEcon.df(mult), "fud=" + EM.curEcon.df(fudge), "hlf" + EM.curEcon.df(alhlf), "*" + EM.curEcon.df(ahlf), "ave=" + EM.curEcon.df(ave), "lims=" + EM.curEcon.df(limLow), EM.curEcon.df(limHigh), "abcdefghijklmn"));

    // derived in google sheet game tests
    for (int m : E.a2lsecs) {
      aVal = B.get(m);
      // get dVal distance from median
      dVal = aVal - ave;  // if aVal>actr:dVal>0;; aVal < actr?? dVal<0
      //     mVal = aMult * dVal;  // modified distance from center
      if (amax == amin) {// max == min
        tVal3 = tVal2 = tVal1 = tVal = 1.;  // prevent divid by 0
        set(m, tVal);
        if (History.dl > 4) {
          // list action for each calculation
          EM.curEcon.hist.add(new History(History.debuggingMinor11, "n=" + EM.curEcon.as.n + " " + titla + m, "v=" + EM.curEcon.df(aVal), "=>" + EM.curEcon.df(dVal), "=>" + EM.curEcon.df(tVal1), "=>" + EM.curEcon.df(tVal2), "=>" + EM.curEcon.df(tVal3), "=>_" + EM.curEcon.df(tVal), "ave=" + EM.curEcon.df(ave), "dV=" + EM.curEcon.df(dVal), "abcef,ghijk.lmnop.qrst"));
        }
      }
      else if (aVal <= ave) {  // aVal <= ave ,includes ave

        // tval distance from 1 is proportional to dval from ave
        // get a number above 1
        tVal1 = (-dVal / ahlf);  //frac of half v largeer => smaller
        tVal2 = (1 + (tVal1 == 1 ? 1.03 : (tVal1))); // v larger => larger
        // mult > 1 makes tVal3 larger,
        tVal3 = 1. / (tVal2 / mult);  // v larger => smaller tV3
        tVal = Math.min(limHigh, tVal3);  // some value above 1
        if (History.dl > 4) {
          // list action for each calculation
          EM.curEcon.hist.add(new History(History.debuggingMinor11, "n=" + EM.curEcon.as.n + " " + titla + m, "v=" + EM.curEcon.df(aVal), "=>" + EM.curEcon.df(dVal), "=>" + EM.curEcon.df(tVal1), "=>" + EM.curEcon.df(tVal2), "=>" + EM.curEcon.df(tVal3), "=>_" + EM.curEcon.df(tVal), "ave=" + EM.curEcon.df(ave), "dV=" + EM.curEcon.df(dVal), "abcef,ghijk.lmnop.qrst"));
          set(m, tVal);
        }
      }
      else {   // dval >= 0 aVal > ave
        //      amultr = amulthr;
        ahlf = ahlf;
        //     tVal2 = (dVal + aMult);  // reciprical value < 1.
        tVal1 = (dVal / ahlf);
        // get fraction from ave
        tVal2 = (1 + (tVal1 == 1 ? 1.03 : (tVal1)));
        // apply mult
        tVal3 = tVal2 * mult;
        //apply limit
        tVal = Math.max(limLow, tVal3);
        set(m, tVal);
        if (History.dl > 4) {
          // list action for each calculation
          EM.curEcon.hist.add(new History(History.debuggingMinor11, "n=" + EM.curEcon.as.n + " " + titla + m, "v=" + EM.curEcon.df(aVal), "=>" + EM.curEcon.df(dVal), "=>" + EM.curEcon.df(tVal1), "=>" + EM.curEcon.df(tVal2), "=>" + EM.curEcon.df(tVal3), "=>^" + EM.curEcon.df(tVal), "ave=" + EM.curEcon.df(ave), "dV=" + EM.curEcon.df(dVal), "mnopq,rst,uvwxyz"));
        }
      }
    }
    return this;
  }

  /**
   * derive a reciprocal strategic trade value using mult limited by limLow and
   * its reciprocal. The results center around 1 so that they can be multiplied
   * together and leave a result around 1 in this. mult is divided by the
   * average to keep results the same as the total values grow year by year if
   * (a-median)/(min-median) = -1.75 &lt 0, -1.75/-2.5 = 1/.7 = 1.4285 --2 &lt
   * 0, -2./-2.5= 1/1 if (a-median)/(min-median) = .7 then result = 1 + 1/.7
   *
   * @param A Input @param mult use mult/A.ave() as proportional multiplier
   *
   * e
   * r
   *
   * @param limLow lowest faction permitted for result. limHigh is its
   * reciprocal @return a new ARow with strategic values
   */
  A2Row strategicRecipValBbyMultLim(String titla, A6Row B, double mult, double alimLow) {
    Econ ec = EM.curEcon;
    double amax = B.curMax(0);
    double amin = B.curMin();
    double ave = (amax + amin) / 2.;  // ave = m
    double adif = (amax - amin); //size of a_most - a_least
    double athlf = amax - ave;  // amt > ave
    double alhlf = amin - ave;  // amt <= ave
    double dVal = 0;
    double aVal = 1;
//   double amultlr = 1.3;  // lower mult
//    double amulthr = 1.3;  // upper mult
    double fmult = mult;  // original mult
    // force mult to be > 1.
    mult = mult < 1. ? 1 / mult : mult;
    //  mult = (mult - 1.);   // amount above 1/
    double amultr = 1.;   // temp of amultlr or amulthr
    //   double aMult = mult * amulthr / ahlf; // .4/5 = .08  or -.08 if -.4
    //   double mVal = aMult * dVal;
    double tVal = 1., tVal1 = 1., tVal2 = 1., tVal3 = 1.;
    double limHigh = alimLow < 1. ? 1. / alimLow : alimLow;
    double limLow = alimLow > 1. ? 1. / alimLow : alimLow;
    // set fudge to prevent creating values beyound limits
    double fudge = (1. + limLow) * (.3 + mult);
    double ahlf = alhlf * fudge; // lower half
    ec.hist.add(new History(History.debuggingMinor11, "A2R recip " + titla, "Hv" + EM.mf(amax), "Lv" + EM.mf(amin), "H-L" + EM.mf(adif), "mlt=" + EM.mf(mult), "fud=" + EM.mf(fudge), "hlf" + EM.mf(alhlf), "*" + EM.mf(ahlf), "ave=" + EM.mf(ave), "lims=" + EM.mf(limLow), EM.mf(limHigh), "abcdefghijklmn"));

    // derived in google sheet game tests
    for (int m : E.a2lsecs) {
      aVal = B.get01(m);
      // get dVal distance from median
      dVal = aVal - ave;  // if aVal>actr:dVal>0;; aVal < actr?? dVal<0
      //     mVal = aMult * dVal;  // modified distance from center
      if (amax == amin) {// max == min
        tVal3 = tVal2 = tVal1 = tVal = 1.;  // prevent divid by 0
        set(m, tVal);
        if (History.dl > 4) {
          // list action for each calculation
          ec.hist.add(new History(History.debuggingMinor11, " " + titla + m, "v=" + EM.mf(aVal), "=>" + EM.mf(dVal), "=>" + EM.mf(tVal1), "=>" + EM.mf(tVal2), "=>" + EM.mf(tVal3), "=>_" + EM.mf(tVal), "ave=" + EM.mf(ave), "dV=" + EM.mf(dVal), "abcef,ghijk.lmnop.qrst"));
        }
      }
      else if (aVal <= ave) {  // aVal <= ave ,includes ave dval <= 0

        // tval distance from 1 is proportional to dval from ave
        // get a number above 1
        tVal1 = (-dVal / ahlf);  //frac of half v largeer => smaller
        tVal2 = (1 + (tVal1 == 1 ? 1.03 : (tVal1))); // v larger => larger
        // mult > 1 makes tVal3 larger,
        tVal3 = 1. / (tVal2 / mult);  // v larger => smaller tV3
        tVal = Math.min(limHigh, tVal3);  // some value above 1
        if (History.dl > 4) {
          // list action for each calculation
          ec.hist.add(new History(History.debuggingMinor11, " " + titla + m, "v=" + EM.mf(aVal), "=>" + EM.mf(dVal), "=>" + EM.mf(tVal1), "=>" + EM.mf(tVal2), "=>" + EM.mf(tVal3), "=>_" + EM.mf(tVal), "ave=" + EM.mf(ave), "dV=" + EM.mf(dVal), "abcef,ghijk.lmnop.qrst"));
          set(m, tVal);
        }
      }
      else {   // dval >= 0 aVal > ave
        //      amultr = amulthr;
        ahlf = ahlf;
        //     tVal2 = (dVal + aMult);  // reciprical value < 1.
        tVal1 = (dVal / ahlf);
        // get fraction from ave
        tVal2 = (1 + (tVal1 == 1 ? 1.03 : (tVal1)));
        // apply mult
        tVal3 = tVal2 * mult;
        //apply limit
        tVal = Math.max(limLow, tVal3);
        set(m, tVal);
        if (History.dl > 4) {
          // list action for each calculation
          ec.hist.add(new History(History.debuggingMinor11, "n=" + ec.as.n + " " + titla + m, "v=" + EM.mf(aVal), "=>" + EM.mf(dVal), "=>" + EM.mf(tVal1), "=>" + EM.mf(tVal2), "=>" + EM.mf(tVal3), "=>^" + EM.mf(tVal), "ave=" + EM.mf(ave), "dV=" + EM.mf(dVal), "mnopq,rst,uvwxyz"));
        }
      }
    }
    return this;
  }

  /**
   * derive a reciprocal strategic trade value using alimLow and its reciprocal.
   * The results center around 1 so that they can be multiplied together and
   * leave a result around 1 in this.
   *
   * @param titla The title for the list output lines
   * @param B the A6Row of which the first 2 rows are used
   * @param aLimLow either the highest result or its reciprical, negative
   * alimLow,means straight results
   * @return a new ARow with strategic values around 1 Recip (positive aLimLow,
   * high values small results and opposite (negative aLimLow, high values have
   * high results and opposite
   */
  A2Row strategicRecipValBbyLim(String titla, A6Row A, double aLimLow) {
    A2Row B = new A2Row(A.getRow(0), A.getRow(1));
    B.titl = A.titl; // also move the title
    B.lev = A.lev;
    return this.strategicRecipValBbyLim(titla, B, aLimLow);
  }

  /**
   * derive a strategic trade value for each of the 14 values in B
   * turn aLinLow %lt; .5 into 1.0 + aLimLow
   * turn alimLow %lt; 1.0 into the low limit, aHighLim = 1/aLowLim
   * The median either min(7)  separates hHlf above med and lHlf at or below
   * The largest value in B becomes the smallest result
   * The smailest value in B becomes the largest result
   * if the initial alimLow was negative, then the results are reversed:
   * the largest value in B becomes the largest result %lt; aHighLim
   * the smallest value in B becomes the smallest result %gt; aLowLim
   * 
   * @param titla The title for the A2Row results
   * @param B the A2Row used as input
   * @param aLimLow either the highest result or its reciprical, negative
   * alimLow,means straight results
   * @return a new ARow with strategic values around 1 Recip (positive aLimLow,
   * high values small results and opposite (negative aLimLow, high values have
   * high results and opposite
   */
  A2Row strategicRecipValBbyLim(String titla, A2Row B, double aLimLow) {
    Econ ec = EM.curEcon;
    double amax = B.curMax(0);
    double amin = B.curMin(0);
    // neg means means straight results not reciprical
    boolean straight = aLimLow < 0.0;
    // force limit positive
    aLimLow = aLimLow < 0. ? -aLimLow : aLimLow;
    // force the limit > 1.
    aLimLow = aLimLow < .5 ? 1. + aLimLow : aLimLow; // aLimLow > .5
    double aLimHigh =  (aLimLow < 1.0  ? 1/aLimLow: aLimLow);
    aLimLow = aLimLow < 1.0 ? aLimLow : 1.0 / aLimLow; // now aLimLow > 1.
    // results will be in terms of difference from 1.
    double hLimDif = aLimHigh - 1.0;  // upper limit  1.0 + hLimDif
    double lLimDif = 1.0 - aLimLow;  // as hLimDif grows => lLimDif decreases lower limit 1.0 - lLimDif
    // select lower median for recip, higher median for straight from B 
    double median = B.curMin(straight  ? 7 : 6); //median value of B
    double lHlf = median - amin; // size of lower half usually > 0, could be 0
  // straight if aSign < 0.0
    double lMult = lHlf < E.PZERO && lHlf > E.NZERO ? 0.0000001 :
        straight ? lLimDif / lHlf : -hLimDif/ lHlf; // frac of differences
    // lower limit 1.0 - lLimDif
    double hHlf = amax - median;  //  size  of high half should be >= 0
    double hMult = hHlf < E.PZERO && hHlf > E.NZERO ? 0.000001: 
        straight ? hLimDif / hHlf : -lLimDif / hHlf;
    // high limit is 1.0 + hLimDif
    double tVal = 1., tVal1 = 1., tVal2 = 1., tVal3 = 1.,mult;

    ec.hist.add(new History(History.debuggingMinor11, titla + (amax == amin ? " all 1": (!straight  ? "recipricals" : "straight") ),  "Hv" + EM.mf(amax) ,"med=" + EM.mf(median), "Lv" + EM.mf(amin),"hHlf=" + EM.mf(hHlf), "lHlf=" + EM.mf(lHlf), "lMlt=" + EM.mf(lMult), "hMlt=" + EM.mf(hMult), "lims=" + EM.mf(aLimHigh)  ,EM.mf(aLimLow), "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"));

    // process both Resource & Staff  or cargo & guests
    for (int n : E.A2SECS) {
      tVal3 = B.get(n);
      // get dVal distance from average, sign is for reciprical or straight
      tVal2 = (tVal3 - median);
     // tVal2 = aaSign * tVal3;
      //     mVal = aMult * dVal;  // modified distance from center
     
        mult = tVal2 <= E.PZERO ? lMult  : hMult;  
        tVal1 = (tVal2 *mult);  // tVal2 < 0.0 lHlf straight => tVal1 < 0.0, lMult > 0.0
                                          // tVal2 < 0.0  lHlf !straight => tVal1 > 0.0 ,  lMult < 0.0
                                          // tval2 > 0.0 hHlf straight => tVal1 > 0.0 , hMult > 0.0
                                          // tval2 > 0.0 hHlf !straight => tVal1 < 0.0, hMult < 0.0
       tVal = 1.0 + tVal1; // for lHlf tVal1 < 0.0
       set(n, tVal);
        if (History.dl > 4|| hist.size() < 2000) {  // do the one at the bottom
          // list action for each calculation
          ec.hist.add(new History(History.debuggingMinor11, "n=" + ec.as.n + " " + titla + n, 
              "v=" + EM.mf(tVal3), "-med=" + EM.mf(median),"->" + eM.mf(tVal2), "mlt=" + df(mult), 
              "=" + df(tVal1), "+1=" + df(tVal),   "<<<<<<<<"));
        }
    }// for
    return this;
  }
  
    /**
   * derive a strategic trade value for each of the 7 values in B
   * I think this is the wrong strategy, 
   * turn aLinLow %lt; .5 into 1.0 + aLimLow
   * turn alimLow %lt; 1.0 into the low limit, aHighLim = 1/aLowLim
   * The median  min(3)  separates hHlf above med and lHlf at or below
   * The largest value in B becomes the smallest result
   * The smailest value in B becomes the largest result
   * if the initial alimLow was negative, then the results are reversed:
   * the largest value in B becomes the largest result %lt; aHighLim
   * the smallest value in B becomes the smallest result %gt; aLowLim
   * 
   * @param titla The title for the ARow results
   * @param B the A2Row used as input
   * @param aLimLow either the highest result or its reciprical, negative
   * alimLow,means straight results
   * @return a new ARow with strategic values around 1 Recip (positive aLimLow,
   * high values small results and opposite (negative aLimLow, high values have
   * high results and opposite
   */
  A2Row strategicRecipValBbyLim3(String titla, A2Row B, double aLimLow) {
    A2Row rtn = new A2Row(ec,7,titla);
      rtn.A[0].strategicRecipValBbyLim("r " +titla,  B.getRow(0),  aLimLow);
      rtn.A[1].strategicRecipValBbyLim("s" +titla,  B.getRow(1),  aLimLow);
    return rtn;
  }

  /**
   * get the n'th from max value using min(E.lsecs*2-1-n)
   *
   * @param n number from max value to get
   * @return
   */
  double max(int n) {
    return min(E.lsecs * 2 - 1 - n);
  }

  /**
   * get the max value
   *
   * @return the max value
   */
  double max() {
    return min(E.lsecs * 2 - 1);
  }

  double max2() {
    return min(E.lsecs * 2 - 1 - 1);
  }

  double max3() {
    return min(E.lsecs * 2 - 1 - 2);
  }

  double min2() {
    return min(1);
  }

  double min3() {
    return min(2);
  }

  ARow maxARow() {
    return minARow(E.lsecs * 2 - 1);
  }

  /**
   * sum the values in both rows
   *
   * @return sum
   */
  double sum() {
    checkIx();
    return asum;
  }

  /**
   * sum the positive values in both rows
   *
   * @return sum of positive values
   */
  double plusSum() {
    checkIx();
    return aplusSum;
  }

  /**
   * sum the negative values in both rows
   *
   * @return sum of negative values
   */
  double negSum() {
    checkIx();
    return anegSum;
  }

  /**
   * average of the values in the 2 rows
   *
   * @return average
   */
  double ave() {
    checkIx();
    return asum * E.INVL2SECS;
  }

  /**
   * set the health or fertility fraction
   *
   * @param A  usually current  balances
   * @param B  some factor reducing the balance, use only ac and sg rows
   * @return (A-B)/A
   */
  public A2Row setFracAsubBdivByA(A6Row A, A10Row B) {
    A.addJointBalances();
    A2Row rtn = new A2Row(ec,5,"frac");
    //resum does B
    for (int m : d01) {
      for (int n : ASECS) {
          // separate each operation to localize null object errors
          double ag = A.get(m *  2 + 2,n);
        //for very small or zero ag => a very small positive value
        double xx = ag > E.NZERO && ag < E.PZERO? E.PZERO1:
                (A.get(m * 2 + 2, n)  // resource then staff 2,4
                - B.get(m, n))
                / (A.get(m * 2 + 2, n));
        if(E.debugYcalcCosts)EM.wasHere2 = " in loops m=" + m + ", n = " + n + ", A[" + (m*2 + 2) + "][" + n +  "]  get=" + EM.mf(A.get(m * 2 + 2, n) ) + "+ B[" + m + "][" + n + "]=" + EM.mf(B.get(m,n)) + ", res=" + EM.mf(xx);           
        rtn.set(m, n,xx);
      }
    }
    return rtn;
  }

  /**
   * set the health or fertility fraction
   *
   * @param A balances, working balance at 2+2*m
   * @param B costs using 0,1 rc,sg
   * @param C divisor costs using 0,1 rc,sg
   */
  public void setBalanceAsubBdivByC(A6Row A, A10Row B, A10Row C) {
    for (int m : d01) {
      for (int n : ASECS) {
        // separate each operation to localize null object errors
        set(m, n,
                (A.get(m * 2 + 2, n)
                - B.get(m, n))
                / C.get(m, n));
      }
    }
  }

  /**
   * set the health or fertility fraction, make zero divisor just small
   *
   * @param a balances, working balance at 2+2*m
   * @param b costs using 0,1 rc,sg
   * @param c divisor costs using 0,1 rc,sg
   * @return this
   */
  public A2Row setFracAsubBdivByC(ABalRows a, A10Row b, A10Row c) {
    Double cc;
    double c2;
    for (int m : d01) {
      for (int n : ASECS) {
        // separate each operation to localize null object errors
        cc = c.get(m, n);
        c2 = cc < E.PZERO || cc.isInfinite() || cc.isNaN() ? E.UNZERO : cc;
        set(m, n,
                (a.get(m * 2 + 2, n)
                - b.get(m, n))
                / c2);
      }
    }
    return this;
  }

  /**
   * set the health or fertility fraction, and save rem
   *
   * @param A balances, use working balance at 2+2*m
   * @param B costs using 0,1 rc,sg
   * @param C divisor costs using 0,1 rc,sg
   * @param rem remnant of a-b, use new A6Row(lev,title);
   * @return this (A -B)/C
   */
  public A2Row setFracAsubBdivByCnRem(A6Row A, A10Row B, A10Row C, A6Row rem) {
    double t, s;
    for (int m : d01) {
      for (int n : ASECS) {
        // separate each operation to localize null object errors
        t = C.get(m, n);
        s = rem.set(2 + 2 * m, n, A.get(m * 2 + 2, n) - B.get(m, n));// r,s - rc,sg
        rem.set(3 + 2 * m, n, A.get(3 + 2 * m, n)); //c,g wo costs
        set(m, n, t < E.PZERO ? 0.
                : s
                / C.get(m, n));
      }
    }
    return this;
  }

  public A2Row setFracAsubBnRemdivByCsubDnDif(ABalRows a, A10Row b, A6Row rem, A10Row c, A6Row d, A2Row dif) {
    for (int m : d01) {
      for (int n : ASECS) {
        // separate each operation to localize null object errors
        set(m, n,
                rem.set(2 + 2 * m, n, a.get(m * 2 + 2, n)
                        - b.get(m, n))
                / dif.set(m, n, c.get(m, n) - d.get(m, n)));
        rem.set(3 + 2 * m, n, a.get(3 + 2 * m, n)); //c,g
      }
    }
    return this;
  }

  /**
   * filter result when called by need to 0. when avail &lt; availLowLim return
   * will be searched for the highest value (this = stratVal)
   *
   * @param avail
   * @param availLowLim
   * @return each by each this if avail >= availLowLim else 0.
   */
  A2Row filterNeedByAvailable(A2Row avail, double availLowLim) {
    A2Row result = new A2Row(ec);
    for (int m : E.d2) {
      for (int n : E.alsecs) {
        result.set(m, n, avail.get(m, n) < availLowLim ? -8888. : get(m, n));
      }
    }
    return result;
  }

  /**
   * send the object to hist with preString aPre unless this.lev >= History.dl
   *
   * @param hist pointer to hist ArrayList
   * @param aPre preString value
   */
  void sendHist(ArrayList<History> hist, String aPre) {
    if (ec.clearHist() || ec.lev > ec.blev) {
      return;
    }
    String aPre1;
    aPre1 = this.aPre = ec.aPre = aPre != null ? aPre : ec.aPre;
    String titl = " " + this.titl;
    ec.lev = lev = Math.min(lev, History.auxInfo);
    hist.add(new History(aPre1, lev, "rc " + titl, A[0]));
    hist.add(new History(aPre1, lev, "sg " + titl, A[1]));

  }

  /**
   * list the object on hist with preString aPre if lev &lt; bLev &le;
   * History.dl
   *
   * @param lev
   * @param aPre
   */
  void sendHist(int lev, String aPre) {
    ec.lev = lev;
    String aPre1;
    if (ec.clearHist() || ec.lev > ec.blev) {
      return;
    }
    aPre1 = this.aPre = ec.aPre = aPre != null ? aPre : ec.aPre;
    String titl = " " + this.titl;
    lev = Math.min(ec.lev, History.auxInfo);
    hist.add(new History(aPre1, lev, " rc " + titl, A[0]));
    hist.add(new History(aPre1, lev, " sg " + titl, A[1]));

  }

  /**
   * list the object on hist with preString aPre unless bLev >= History.dl
   *
   * @param hist pointer to hist ArrayList for listing
   * @param bLev
   * @param aPre
   * @param aLev
   * @param titl0 title for row 0
   * @param titl1 title for row 1
   */
  void sendHist(ArrayList<History> hist, int bLev, String aPre, int aLev, String titl0, String titl1) {
    
    if (ec.clearHist() || (ec.lev = aLev) > (ec.blev = bLev)) {
      return;
    }
    String aPre1;
    aPre1 = this.aPre = ec.aPre = aPre != null ? aPre : ec.aPre;
    String titl = " " + this.titl;
    ec.lev = lev = Math.min(ec.lev, History.auxInfo);
    hist.add(new History(aPre1, aLev, " " + titl0, A[0]));
    hist.add(new History(aPre1, aLev, " " + titl1, A[1]));

  }

  /**
   * list the object on hist
   *
   * @param hist
   * @param bLev
   * @param aPre
   * @param titl0
   * @param titl1
   */
  void sendHist(ArrayList<History> hist, int bLev, String aPre, String titl0, String titl1) {
    sendHist(hist, bLev, aPre, ec.lev, titl0, titl1);
  }

  /**
   * list this on hist with 2 titles for the 2 rows
   *
   * @param titl0 title of first row
   * @param titl1 title of the second row
   */
  void sendHistt(String titl0, String titl1) {
    sendHist(hist, ec.blev, aPre, ec.lev, titl0, titl1);
  }

  /**
   * send this to history with rc titl and sc titl row titles
   *
   */
  void sendHist() {
    sendHist(hist, ec.blev, ec.aPre, ec.lev, "rc " + titl, "sg " + titl);
  }

  void sendHistcg() {
    sendHist(hist, ec.blev, ec.aPre, ec.lev, "c " + titl, "g " + titl);
  }

}

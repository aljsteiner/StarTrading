
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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

/**
 *
 * @author albert steiner ARow is a row of values of length E.lsecs the
 * financial sections It is used in Assets to contain some time of information
 * about financial sections ARow also contains an index of values usually from
 * min to max The sum and index are valid if setCnt == savCnt, otherwise
 * orderIndex is called setting ix and sum and savCnt to setCnt setCnt is
 * increased whenever a value in values is changed. The class also contains many
 * methods to help Assets to manipulate information about the financial sectors.
 */
public class ARow {

  // arrays have null pointers, create an error if a null pointer is used.
  //volatile protected double values[];
  volatile double values[];
  private int ix[];
  volatile private double sum = 0.;
  volatile private double plusSum = 0.;
  volatile private double negSum = 0.;
  volatile private double plusMax = 0.;
  volatile private double negMin = 0.;
  volatile private int plusCnt = 0;
  volatile private int negCnt = 0;
  volatile int setCnt = 0;
  volatile int savCnt = -10;
  private EM eM = EM.eM;
  private E eE = EM.eE;
  // the following initizlized by the constructor
  Econ ec;
  private Assets as;
  private String aPre;
  private ArrayList<History> hist; // the owner will not change

  /**
   * construct a new zero'd ARow instance
   *
   */
  public ARow(Econ ec) {
    this.ec = ec;
    as = ec.as;
    aPre = ec.aPre;
    hist = ec.getHist();
    values = new double[E.lsecs];
    ix = new int[E.lsecs];
    if (E.debugNoTerm) {
      if ((as != null)) {
        int tst = as.term;
      }
    }
    for (int i = 0; i < E.lsecs; i++) {
      values[i] = E.PZERO;
      ix[i] = i;
    }
    sum = 0.;
  }

  /**
   * find the place in the order that matches x m
   *
   * @param m index into 01values to match
   * @return matching position in min to max order
   */
  int getCurN(int m) {
    if (setCnt != savCnt) {
      makeOrderIx();
    }
    for (int p : E.alsecs) {
      if (ix[p] == m) {
        return p;
      }
    }
    return -1;
  }

  /**
   * zero fill a constructed ARow
   *
   */
  public void fill() {
    values = new double[E.lsecs];
    ix = new int[E.lsecs];
    for (int i = 0; i < E.lsecs; i++) {
      values[i] = E.PZERO;
      ix[i] = i;
    }
    sum = 0.;
  }
  NumberFormat dFrac = NumberFormat.getNumberInstance();
  NumberFormat whole = NumberFormat.getNumberInstance();
  NumberFormat dfo = dFrac;

  /**
   * create a String from a number
   *
   * @param n the number to be made strings
   * @return a string of a floating number
   */
  protected String df(double n) {
    return eM.mf(n);
  }

  String ef(double v) {
    NumberFormat exp = new DecimalFormat("0.###,###E0");
    return exp.format(v);
  }

  /**
   * create a string of a whole number
   *
   * @param n number for which any fraction will be ignored
   * @return the number with no fraction
   */
  String wh(double n) {
    whole.setMaximumFractionDigits(0);
    return whole.format(n);
  }

  /**
   * if this values have any negative, hist this with title s, throw fatal error
   *
   * @param s String describing this
   */
  public void negError(String s) {
    for (int m : E.alsecs) {
      if (get(m) < E.nzero) {
        EM.curEcon.hist.add(new History(2, "err " + s, this));
        E.myTest(true, "Negative " + get(m) + " = " + s + "[" + m + "]");
      }
    }
  }

  /**
   * test value for illegal double value using E.dTrouble issue error if
   * E.debugDouble
   *
   * @param trouble value to be tested if E.debugDouble
   * @return valid trouble
   */
  double dTrouble(Double trouble) {
    return ec.doubleTrouble(trouble, "yes");
  }

  /**
   * test value for illegal double value using E.dTrouble issue error if
   * E.debugDouble
   *
   * @param trouble value to be tested if E.debugDouble
   * @return valid trouble
   */
  double doubleTrouble(Double trouble) {
    return ec.doubleTrouble(trouble, "yes");
  }

  /**
   * test value for illegal double value using E.dTrouble issue error if
   * ec.debugDouble
   *
   * @param trouble value to be tested if E.debugDouble
   * @param cmt comment with trouble
   * @return valid trouble
   */
  double dTrouble(Double trouble, String cmt) {
    return ec.doubleTrouble(trouble, cmt);
  }

  /**
   * test value for illegal double value using E.dTrouble issue error if
   * E.debugDouble
   *
   * @param trouble value to be tested if E.debugDouble
   *
   * @return valid trouble
   */
  double doubleTrouble(Double trouble, String cmt) {
    return ec.doubleTrouble(trouble, cmt);
  }

  /**
   * return value ix of this
   *
   * @param ix index
   * @return value of values[ix]
   */
  public double get(int ix) {
    if (values == null) {
      fill();
    }
    assert ix > -1 && ix < E.LSECS : "ix value=" + ix + " out of range";
    if (E.debugDouble) {
      return doubleTrouble(values[ix]);
    }
    else {
      return values[ix];
    }
  }

  /**
   * return the values array
   *
   * @return values array
   */
  double[] getValues() {
    return values;
  }

  /**
   * increment setCnt by one
   *
   * @return setCnt++
   */
  int addCnt() {
    return setCnt++;
  }

  /**
   * zero values of this
   *
   * @return this
   */
  public ARow zero() {
    setCnt++;
    if (values == null) {
      fill();
    }
    for (int i = 0; i < E.lsecs; i++) {
      values[i] = 0.0;
    }
    return this;
  }

  /**
   * get the count of positive sectors
   *
   * @return count of positive sectors
   */
  int getPlusCount() {
    if (setCnt != savCnt) {
      makeOrderIx();
    }
    return plusCnt;
  }

  /**
   * get the count of negative sectors
   *
   * @return count of negative sectors
   */
  int getNegCount() {
    if (setCnt != savCnt) {
      makeOrderIx();
    }
    return negCnt;
  }

  /**
   * get the sum of positive sectors
   *
   * @return the positive sum
   */
  double getPlusSum() {
    if (setCnt != savCnt) {
      makeOrderIx();
    }
    return plusSum;
  }

  /**
   * get the sum of negative sectors
   *
   * @return the sum of negative sectors
   */
  double getNegSum() {
    if (setCnt != savCnt) {
      makeOrderIx();
    }
    return negSum;
  }

  /**
   * set a new value to ARow
   *
   * @param ix index of value to set
   * @param val value to set
   * @return val
   */
  public double set(int ix, double val) {
    if (values == null) {
      fill();
    }
    setCnt++;
    if (E.debugDouble) {
      values[ix] = doubleTrouble(val);
    }
    else {
      values[ix] = val;
    }
    return val;
  }

  /**
   * second set a new value to ARow using double test
   *
   * @param ix index of value to set
   * @param val value to set
   * @param titl = unused titl of calling A6Rowa
   * @param m = unused which row this is
   * @return val
   */
  public double set2(int ix, double val, String titl, int m) {
    if (values == null) {
      fill();
    }
    setCnt++;
    if (E.debugDouble) {
      values[ix] = doubleTrouble(val);
    }
    else {
      values[ix] = val;
    }
    return val;
  }

  /**
   * set value ix to val ignore cmt
   *
   * @param ix index
   * @param val value to be saved
   * @param cmt comment for this save, but comment is only for info
   * @return this with values[ix] = val
   */
  public double set(int ix, double val, String cmt) {
    if (values == null) {
      fill();
    }
    setCnt++;
    if (E.debugDouble) {
      values[ix] = doubleTrouble(val);
    }
    else {
      values[ix] = val;
    }
    return val;
  }

  /**
   * zero each value in A
   *
   * @param A
   * @return zerod A
   */
  public ARow zero(ARow A) {
    A.setCnt++;
    if (A.values == null) {
      A.fill();
    }
    for (int i = 0; i < E.lsecs; i++) {
      A.values[i] = 0.0;
    }
    return A;
  }

  /**
   * return A + B + C + D
   *
   * @param A ARow
   * @param B ARow
   * @param C ARow
   * @param D ARow
   * @return this
   */
  ARow set(ARow A, ARow B, ARow C, ARow D) {
    setCnt++;
    for (int i = 0; i < E.lsecs; i++) {
      if (E.debugDouble) {
        set(i, doubleTrouble(A.get(i)) + doubleTrouble(B.get(i)) + doubleTrouble(C.get(i)) + doubleTrouble(D.get(i)));
      }
      else {
        set(i, A.get(i) + B.get(i) + C.get(i) + D.get(i));
      }
    }
    return this;
  }

  /**
   * return A + B + C
   *
   * @param A
   * @param B
   * @param C
   * @return this
   */
  ARow set(ARow A, ARow B, ARow C) {
    double aa, bb, cc;
    setCnt++;
    for (int i = 0; i < E.lsecs; i++) {
      if (E.debugDouble) {
        aa = doubleTrouble(A.get(i));
        bb = doubleTrouble(B.get(i));
        cc = doubleTrouble(C.get(i));
      }
      else {
        aa = A.get(i);
        bb = B.get(i);
        cc = C.get(i);
      }
      set(i, aa + bb + cc);
    }
    return this;
  }

  /**
   * return A + B
   *
   * @param A an ARow
   * @param B an ARow
   */
  ARow set(ARow A, ARow B) {
    setCnt++;
    for (int i = 0; i < E.lsecs; i++) {
      set(i, doubleTrouble(A.get(i)) + doubleTrouble(B.get(i)));
    }
    return this;
  }

  /**
   * return A + v added to each member of values
   *
   * @param A
   * @param v
   * @return each value of A increased by v
   */
  ARow set(ARow A, double v) {
    setCnt++;
    for (int i = 0; i < E.lsecs; i++) {
      if (E.debugDouble) {
        set(i, doubleTrouble(A.get(i) + v));
      }
      else {
        set(i, A.get(i) + v);
      }
    }
    return this;
  }

  /**
   * set ARow to A, copying A to itself
   *
   * @param A an ARow
   * @return the save ARow with values from A
   */
  ARow set(ARow A) {
    E.myTest(A == null, "input to set is null");
    double d;
    setCnt++;
    for (int i = 0; i < E.lsecs; i++) {
      if (E.debugDouble) {
        d = doubleTrouble(A.get(i));
      }
      else {
        d = A.get(i);
      }
      set(i, d);
    }
    return this;
  }

  /**
   * set ARow to A, copying A testing a set in A6Rowa
   *
   * @param A ARow to doubleTrouble copy into the calling object row
   * @param titl ignore title of the A6Rowa
   * @param m ignore m row in A6Rowa used for this set
   * @return this
   */
  ARow set2(ARow A, String titl, int m) {
    assert A != null : "row to set is null";
    // E.myTest(A == null, "input to set is null");
    double d;
    setCnt++;
    for (int i = 0; i < E.lsecs; i++) {
      if (E.debugDouble) {
        d = ec.doubleTrouble(A.get(i), titl);
      }
      else {
        d = A.get(i);
      }
      set(i, d, titl);
    }
    return this;
  }

  /**
   * copy ARow to ret,
   *
   * @return new array with all values copied
   */
  ARow copy() {
    //  E.myTest(A == null, "input to set is null");
    ARow ret = new ARow(ec);
    // the presets in ARow() force reindex etc
    double d;
    ret.setCnt = setCnt + 1;
    for (int i = 0; i < E.lsecs; i++) {
      d = doubleTrouble(get(i));
      ret.set(i, d);
    }
    return ret;
  }

  /**
   * make a new copy of ARow with new references and the old values
   *
   * @param newEM the new EM for this copy
   * @return the new ARow with old values and new references
   */
  ARow newCopy(EM newEM) {
    ARow ret = new ARow(ec);
    double d;
    for (int i = 0; i < E.lsecs; i++) {
      d = ec.doubleTrouble(get(i), "newCopy");
      ret.set(i, d);
      ret.ix[i] = ix[i];
    }

    ret.sum = sum;
    ret.setCnt = setCnt;
    ret.savCnt = savCnt;
    ret.eM = newEM;
    ret.eE = EM.eE;
    ret.ec = eM.curEcon;
    ret.as = ec.as;
    ret.aPre = ec.aPre;
    ret.hist = ec.getHist(); // the owner will not change
    return ret;
  }

  /**
   * flip the signs of all the values in this ARow
   *
   * @return this
   */
  ARow flip() {
    for (int n : E.alsecs) {
      set(n, doubleTrouble(-get(n)));
    }
    return this;
  }

  /**
   * flip the signs of all the values in this ARow
   *
   * @return this
   */
  ARow flip(ARow prev) {
    for (int n : E.alsecs) {
      if (E.debugDouble) {
        set(n, ec.doubleTrouble(-prev.get(n), "flip"));
      }
      else {
        set(n, -prev.get(n));
      }
    }
    return this;
  }

  /**
   * set ARow to double Array first 7 values
   *
   * @param A double[]
   */
  ARow set(double[] A) {
    for (int i = 0; i < E.lsecs; i++) {
      if (E.debugDouble) {
        set(i, ec.doubleTrouble(A[i], "set"));
      }
      else {
        set(i, A[i]);
      }
    }
    return this;
  }

  /**
   * set ARow to double value A
   *
   * @param A double
   */
  ARow set(double A) {
    for (int i = 0; i < E.lsecs; i++) {
      set(i, ec.doubleTrouble(A, "set"));
    }
    return this;
  }

  /**
   * set ARows A subtract B overwrite calling ARow
   *
   * @param A ARow
   * @param B ARow subtracted
   */
  ARow setAsubB(ARow A, ARow B) {
    for (int m = 0; m < E.lsecs; m++) {
      set(m, doubleTrouble(A.get(m)) - doubleTrouble(B.get(m)));
    }
    return this;
  }

  /**
   * Get the difference between a and b for each sector check for possible
   * infinite or nan not a number input
   *
   * @param ec reference to the Econ running this difference
   * @param a the initial values
   * @param b the subtracted values
   * @return The difference a-b for each sector of a and b
   */
  ARow getAsubB(Econ ec, ARow a, ARow b) {
    ARow ret = new ARow(ec);
    for (int ix = 0; ix < E.LSECS; ix++) {
      ret.set(ix, doubleTrouble(a.get(ix)) - doubleTrouble(b.get(ix)));
    }
    return ret;
  }

  /**
   * Get the difference between a and b for each sector check for possible
   * infinite or nan not a number input
   *
   * @param a the initial values
   * @param b the subtracted values
   * @return The difference a-b for each sector of a and b
   */
  ARow getAsubB(ARow a, ARow b) {
    ARow ret = new ARow(a.getEc());
    for (int ix = 0; ix < E.LSECS; ix++) {
      ret.set(ix, doubleTrouble(a.get(ix)) - doubleTrouble(b.get(ix)));
    }
    return ret;
  }

  /**
   * Get the difference between a and v for each sector check for possible
   * infinite or nan: not a number input
   *
   * @param a the initial values
   * @param v the subtracted value
   * @return The difference a-v for each sector of a
   */
  ARow getAsubV(ARow a, double v) {
    ARow ret = new ARow(a.getEc());
    doubleTrouble(v);
    for (int ix = 0; ix < E.LSECS; ix++) {
      ret.set(ix, doubleTrouble(a.get(ix)) - v);
    }
    return ret;
  }

  /**
   * get the recorded Econ reference
   *
   * @return recorded Econ reference
   */
  Econ getEc() {
    return ec;
  }

  /**
   * return the value of setCnt
   *
   * @return setCnt
   */
  int getSetCnt() {
    return setCnt;
  }

  /**
   * set to A subtract product of B times V use with remnants that are not
   * directly SubAsset balances but a derived value check for possible infinite
   * or nan: not a number input
   *
   * @param A
   * @param B
   * @param V
   * @return A - (B*V)
   */
  ARow setAsubBmultV(ARow A, ARow B, double V) {
    for (int m : E.alsecs) {
      set(m, doubleTrouble(A.get(m)) - (doubleTrouble(B.get(m)) * doubleTrouble(V)));
    }
    return this;
  }

  /**
   * set (A-B)/C check for possible infinite or nan: not a number input
   *
   * @param A
   * @param B
   * @param C
   * @return each (A-B)/C
   */
  ARow setAsubBdivbyC(ARow A, ARow B, ARow C) {
    for (int m : E.alsecs) {
      set(m, doubleTrouble(doubleTrouble(A.get(m)) - doubleTrouble(B.get(m))) / doubleTrouble(C.get(m)));
    }
    return this;
  }

  /**
   * set sum A + (B * V) check for possible infinite or nan: not a number input
   *
   * @param A
   * @param B
   * @param V
   * @return each (B * V) + A
   */
  ARow setAaddBmultV(ARow A, ARow B, double V) {
    for (int m : E.alsecs) {
      set(m, doubleTrouble(A.get(m)) + (doubleTrouble(B.get(m)) * doubleTrouble(V)));
    }
    return this;
  }

  /**
   * subtract each A*V from this check for possible infinite or nan: not a
   * number input
   *
   * @param A
   * @param V
   * @return each this - A*V
   */
  ARow subAmultV(ARow A, double V) {
    ARow result = new ARow(ec);
    for (int m : E.alsecs) {
      result.set(m, doubleTrouble(this.get(m)) - (doubleTrouble(A.get(m) * V)));
    }
    return result;
  }

  /**
   * set instance sub B check for possible infinite or nan: not a number input
   *
   * @param B
   * @return each this-B
   */
  ARow setSubB(ARow B) {
    for (int m : E.alsecs) {
      set(m, doubleTrouble(this.get(m)) - doubleTrouble(B.get(m)));
    }
    return this;
  }

  /**
   * set A - B unless not T then set A
   *
   * @param A
   * @param B
   * @param T
   * @return A - B unless not T then A
   */
  ARow setAsubBifT(ARow A, ARow B, boolean T) {
    for (int m = 0; m < E.lsecs; m++) {
      set(m, (!T ? A.get(m) : (A.get(m) - B.get(m))));
    }
    return this;
  }

  /**
   * return this sub B without changing this
   *
   * @param B
   * @return this sub B
   */
  ARow sub(ARow B) {
    for (int m = 0; m < E.lsecs; m++) {
      set(m, get(m) - B.get(m));
    }
    return this;
  }

  /**
   * sub B from this if boolean t
   *
   * @param B sub this value
   * @param t condition
   * @return return this unless t then this - B
   */
  ARow subBifT(ARow B, boolean t) {
    if (t) {
      for (int m = 0; m < E.lsecs; m++) {
        set(m, get(m) - B.get(m));
      }
    }
    return this;
  }

  /**
   * setAmultB set ARows A multiplied by B
   *
   * @param A ARow
   * @param B ARow multiplier
   * @return A mult B
   */
  ARow setAmultB(ARow A, ARow B) {
    for (int m = 0; m < E.lsecs; m++) {
      set(m, A.get(m) * B.get(m));
    }
    return this;
  }

  /**
   * Set A * B / C check for possible infinite or nan: not a number input
   *
   * @param A
   * @param B
   * @param C
   * @return A * B / C for each set of values
   */
  ARow setAmultBdivbyC(ARow A, ARow B, ARow C) {
    for (int m : E.alsecs) {
      Double atest = C.get(m) != 0 ? doubleTrouble(B.get(m)) / doubleTrouble(C.get(m)) : 0.0;
      double bc = atest.isInfinite() || atest.isNaN() ? 0 : atest;
      set(m, doubleTrouble(A.get(m)) * bc);
    }
    return this;
  }

  /**
   * tmp = this*(B/C) for a stacked calc does not change this
   *
   * @param B
   * @param C
   * @return tmp
   */
  ARow setMultBdivbyC(ARow B, ARow C) {
    for (int m : E.alsecs) {
      Double atest = doubleTrouble(B.get(m)) / doubleTrouble(C.get(m));
      double bc = atest.isInfinite() || atest.isNaN() ? 0 : atest;
      set(m, doubleTrouble(get(m)) * bc);
    }
    return this;
  }

  /**
   * multiply this by B
   *
   * @param B
   * @return this * B
   */
  ARow setMultB(ARow B) {
    for (int m : E.alsecs) {
      set(m, doubleTrouble(get(m)) * doubleTrouble(B.get(m)));
    }
    return this;
  }

  /**
   * tmp = this*B/C
   *
   * @param B
   * @param C
   * @return this*B/C no change to this
   */
  ARow multBdivbyC(ARow B, ARow C) {
    ARow tmp = new ARow(ec);
    for (int m : E.alsecs) {
      Double bt = doubleTrouble(C.get(m)) != 0.0 ? B.get(m) / C.get(m) : 0.0;
      double bc = bt.isInfinite() || bt.isNaN() ? 0 : bt;
      tmp.set(m, doubleTrouble(get(m)) * bc);
    }
    return tmp;
  }

  /**
   * calc this * B without changing this
   *
   * @param B
   * @return this mult B
   */
  ARow mult(ARow B) {
    ARow r = new ARow(ec);
    for (int m = 0; m < E.lsecs; m++) {
      r.set(m, get(m) * B.get(m));
    }
    return r;
  }

  /**
   * set to ARow A by value V
   *
   * @param A ARow
   * @param V double
   */
  ARow setAmultV(ARow A, double V) {
    for (int secIx : E.ASECS) {
      set(secIx, A.get(secIx) * V);
    }
    return this;
  }

  ARow mult(double V) {
    for (int i = 0; i < E.lsecs; i++) {
      set(i, get(i) * V);
    }
    return this;
  }

  /**
   * setAmultVifT if not T set A
   *
   * @param A ARow
   * @param V double
   * @param T boolean
   * @return AROW if T each A * V else A
   */
  ARow setAmultVifT(ARow A, double V, boolean T) {
    for (int i = 0; i < E.lsecs; i++) {
      set(i, A.get(i) * (T ? V : 1.));
    }
    return this;
  }

  ARow multifT(double V, boolean T) {
    if (T) {
      for (int i = 0; i < E.lsecs; i++) {
        set(i, get(i) * V);
      }
    }
    return this;
  }

  /**
   * if t then return this mult B else this
   *
   * @param B
   * @param T condition
   * @return this or if T This mult B
   */
  ARow multifT(ARow B, boolean T) {
    ARow result = new ARow(ec).set(this);
    if (T) {
      for (int i = 0; i < E.lsecs; i++) {
        result.set(i, this.get(i) * B.get(i));
      }
    }
    return result;
  }

  /**
   * set multiplied by double V
   *
   * @param V double
   */
  ARow setMultV(double V) {
    for (int i = 0; i < E.lsecs; i++) {
      set(i, get(i) * V);
    }
    return this;
  }

  /**
   * set multiplied by double V if T
   *
   * @param V double
   * @param T Boolean
   * @return if(T)this * V else this
   */
  ARow setMultVifT(double V, boolean T) {
    if (T) {
      for (int i = 0; i < E.lsecs; i++) {
        set(i, get(i) * V);
      }
    }
    return this;
  }

  /**
   * set instance to this[i] - double V create fatal error if the result is lt
   * E.nzero
   *
   * @param V double
   * @return this
   */
  void setSubTo0(int i, double V) {
    E.myTest(get(i) - V < E.nzero, "%9.4f=variable goes negative, %9.4f=get(=%1.0f=i) -%9.4f=V", get(i) - V, get(i), i + 0., V);
    set(i, get(i) - V);
  }

  /**
   * make sure all values are slightly more than zero set only values < UNZERO
   * to UNZERO
   *
   * @param tit row title for the E.sysmsg about unzeroing
   * @return the unzero'd ARow
   */
  ARow unzero(String tit) {
    for (int n = 0; n < E.LSECS; n++) {
      if (get(n) < E.UNZERO) {
        set(n, E.UNZERO);
        //E.sysmsg("unzeroed %s %d",tit,n);
      }
    }
    return this;
  }

  /**
   * set to ARow A divided by corresponding entry in ARow B
   *
   * @param A ARow
   * @param B ARow divisor result set into original ARow
   * @return result
   */
  ARow setAdivbyB(ARow A, ARow B) {
    for (int m = 0; m < E.lsecs; m++) {
      Double at = A.get(m) / B.get(m);
      double ab = at.isInfinite() || at.isNaN() ? 0 : at < E.UNZERO ? E.INVZERO : at;
      set(m, ab);
    }
    return this;
  }

  /**
   * make copy of this/B illegal numbers return a 0. value
   *
   * @param B
   * @return tmp this unchanged
   */
  ARow divby(ARow B) {
    ARow tmp = new ARow(ec);
    for (int m = 0; m < E.lsecs; m++) {
      Double bt = get(m) / B.get(m);
      double bb = bt.isInfinite() || bt.isNaN() ? 0 : bt < E.UNZERO ? E.INVZERO : bt;
      tmp.set(bb);
    }
    return tmp;
  }

  /**
   * make copy of A divided by double v illegal numbers return a 0. value
   *
   * @param A input ARow
   * @param v double to divide A
   * @return each value of A divided by v
   */
  ARow setAdivbyV(ARow A, double v) {
    ARow tmp = new ARow(ec); // uses ec of this
    for (int m = 0; m < E.lsecs; m++) {
      Double at = A.get(m) / v;
      double av = at.isInfinite() || at.isNaN() ? 0 : at < E.UNZERO ? E.INVZERO : at;
      tmp.set(m, av);
    }
    return tmp;
  }

  /**
   * divide each value of this by double v illegal numbers return a 0. value
   *
   * @param v divisor of this
   * @return copy of this / v
   */
  ARow divby(double v) {
    ARow tmp = new ARow(ec);
    for (int m = 0; m < E.lsecs; m++) {
      Double at = get(m) / v;
      double av = at.isInfinite() || at.isNaN() ? 0 : at < E.UNZERO ? E.INVZERO : at;
      tmp.set(m, av);
    }
    return tmp;
  }

  /**
   * if T set this to A/B otherwise do nothing to this
   *
   * @param A ARow
   * @param B ARow
   * @param T boolean
   * @return this
   */
  ARow setAdivbyBifT(ARow A, ARow B, boolean T) {
    if (T) {
      for (int m = 0; m < E.lsecs; m++) {
        Double at = A.get(m) / B.get(m);
        double av = at.isInfinite() || at.isNaN() ? 0. : at < E.UNZERO ? E.INVZERO : at;
        set(m, av);
      }
    }
    return this;
  }

  ARow divbyBifT(ARow B, boolean T) {
    if (T) {
      for (int m = 0; m < E.lsecs; m++) {
        Double at = get(m) / B.get(m);
        double av = at.isInfinite() || at.isNaN() ? 0. : at < E.UNZERO ? E.INVZERO : at;
        set(m, av);
      }
    }
    return this;
  }

  ARow divbyVifT(double V, boolean T) {
    if (T) {
      for (int m = 0; m < E.lsecs; m++) {
        Double at = get(m) / V;
        double av = at.isInfinite() || at.isNaN() ? 0. : at < E.UNZERO ? E.INVZERO : at;
        set(m, av);
      }
    }
    return this;
  }

  /**
   * set calc min A , B/V
   *
   * @param A ARow
   * @param B ARow
   * @param V double result set ARow instance
   */
  ARow minAnBdivbyV(ARow A, ARow B, double V) {
    double t;
    for (int m = 0; m < E.lsecs; m++) {
      Double at = B.get(m) / V;
      double av = at.isInfinite() || at.isNaN() ? 0. : at < E.UNZERO ? E.INVZERO : at;
      set(m, Math.min(A.get(m), av));
    }
    return this;
  }

  /**
   *  * derive an ARow of the amounts available to swap, using only resource and
   * staff subAsset and the corresponding swapCost for each SubAsset, used for
   * INCR and DECR swaps
   *
   * @param srcFrac fraction of source source available for this swap
   * @param availFrac fraction of resource and staff available for swapCost
   * @param maxMove max move allowed, a fraction r + s sum in caller
   * @param source available &eq; -units from source * srcFrac
   * @param resource -available &eq; - resource units / adiv *availFrac
   * @param staff available &eq; -staff units / bdiv *availFrac
   * @param adiv divides resource to find a max for swap
   * @param bdiv divides staff to find a max for swap
   * @return amount available for swap, negative cannot swap
   */
  ARow setAvailableSwapxx(double srcFrac, double availFrac, double maxMove, ARow source, ARow resource, ARow staff, double adiv, double bdiv) {
    double bal, res, stf, maxM, availSrc, resrv;
    maxM = -maxMove;  // move * ..sum() up to caller
    int lev = History.aux5Info;// can change to .debuggingInformation
    if (lev < History.aux5Info) {
      hist.add(new History("sAv", lev, "source", source));
      hist.add(new History("sAv", lev, "resource", resource));
      hist.add(new History("sAv", lev, "staff", staff));
    }
    lev = History.debuggingMinor11;
    for (int n : E.alsecs) {
      bal = -source.get(n);
      resrv = (-resource.get(n) - staff.get(n)) * srcFrac;
      availSrc = bal - resrv;
      res = resource.get(n) / adiv;
      stf = -staff.get(n) * availFrac / bdiv;
      set(n, Math.min(maxM, Math.min(availSrc, Math.min(res, stf))));
      if (n == 0 || n == 2) {
        hist.add(new History("^s", lev, " availSwap n=" + n, "availSrc=" + df(availSrc), "*bf" + df(srcFrac), "=bl" + df(bal), "r" + df(resource.get(n)), "af" + df(availFrac), "ad" + df(adiv), "s" + df(stf), "bd" + df(bdiv), "sf" + df(maxM), "val" + df(get(n))));
      }
    }
    return this;
  }

  /**
   *  * derive an ARow of the amounts available to swap, using only resource and
   * staff subAsset and the corresponding swapCost for each SubAsset, used for
   * INCR and DECR swaps
   *
   * @param srcFrac fraction of source source available for this swap
   * @param availFrac fraction of resource and of staff available for swapCost
   * @param maxMove max move allowed, a fraction r + s sum in caller
   * @param source available &eq; units from source
   * @param resource available &eq; resource units / adiv *availFrac
   * @param staff available &eq; staff units / bdiv *availFrac
   * @param adiv divides resource to find a max for swap
   * @param bdiv divides staff to find a max for swap
   * @return amount available for swap, negative cannot swap
   */
  ARow setAvailableSwap2(double srcFrac, double availFrac, double maxMove, ARow source, ARow resource, ARow staff, double adiv, double bdiv) {
    double bal, res, stf, maxM, availSrc, resrv;
    maxM = -maxMove;  // move * ..sum() up to caller
    int lev = History.aux5Info;// can change to .debuggingInformation
    if (lev < History.aux5Info) {
      hist.add(new History("sAv", lev, "source", source));
      hist.add(new History("sAv", lev, "resource", resource));
      hist.add(new History("sAv", lev, "staff", staff));
    }
    lev = History.debuggingMinor11;
    for (int n : E.alsecs) {
      bal = source.get(n);
      availSrc = bal * srcFrac;
      res = resource.get(n) * availFrac / adiv;
      stf = staff.get(n) * availFrac / bdiv;
      set(n, Math.min(maxM, Math.min(availSrc, Math.min(res, stf))));
      if (n == 0 || n == 2) {
        hist.add(new History("^s", lev, " availSwap n=" + n, "availSrc=" + df(availSrc), "*bf" + df(srcFrac), "=bl" + df(bal), "r" + df(resource.get(n)), "af" + df(availFrac), "ad" + df(adiv), "s" + df(stf), "bd" + df(bdiv), "sf" + df(maxM), "val" + df(get(n))));
      }
    }
    return this;
  }

  /**
   * derive an ARow of the amounts available to swap increment, This uses
   * balances instead of needs aSrc,aStf,aRes are variables with the frac
   * applied fStf,fRes are aStf,aRes divided by (1. +resDiv),(1.+stfDiv)
   *
   * @param srcFrac fraction of source available for this swap
   * @param otherFrac fraction of resource and of staff available for swapCost
   * @param maxMov max move allowed
   * @param source source for the swap alternately r or s to set a limit
   * @param resource resource available for cost of swap
   * @param staff staff available for cost of swap
   * @param resDiv divides resource to find a max for swap
   * @param stfDiv divides staff to find a max for swap
   * @return this: amount available for swap, costs and maxMove used
   */
  ARow setAvailableSwap(double srcFrac, double otherFrac, double maxMov, ARow source, ARow resource, ARow staff, double resDiv, double stfDiv) {
    double src, res, stf, mMov, aRes, aStf, aSrc, fRes, fStf, val;
    int lev = History.aux5Info;// can change to .debuggingInformation
    if (lev < History.aux5Info) {
      hist.add(new History("s@", lev, "i source", source));
      hist.add(new History("s@", lev, "i resource", resource));
      hist.add(new History("s@", lev, "i staff", staff));
    }
    for (int n : E.alsecs) {
      double sBal = source.get(n) * srcFrac;
      res = resource.get(n) * otherFrac;
      stf = staff.get(n) * otherFrac;

      //  aSrc = src=sBal - srcResrv;
      // aStf = oBal*otherFrac;
      // calculate amount of move possible with the given cost and frac
      //fRes = (aRes=(res=resource.get(n)) * otherFrac) /(1.+ resDiv); // r to r or s to s rcost
      fRes = res / resDiv; // r to r or s to s rcost
      // fStf = (aStf=(stf=staff.get(n)) * otherFrac) /(1. + stfDiv); //  s avail / s cost divisor
      fStf = stf / stfDiv; //  s avail / s cost divisor
      set(n, (val = Math.min(maxMov, Math.min(sBal, Math.min(fRes, fStf)))));
      // list elements 0 and 2 to see how this worked
      if (n == 0 || n == 2) {
        hist.add(new History("swp", History.debuggingMinor11, " incWork n=" + n, "sBal" + df(sBal), "bf" + df(srcFrac), "src*" + df(source.get(n)), "fRes" + df(fRes), "rDiv=" + df(resDiv), "fStf" + df(fStf), "sDiv=" + df(stfDiv), "maxM=" + df(maxMov), "val=" + df(val), "<<<<<<<<<<<<"));
      }
    }
    return this;
  }

  /**
   * derive an ARow of the amounts available to swap increment, This uses
   * balances instead of needs aSrc,aStf,aRes are variables with the frac
   * applied fStf,fRes are aStf,aRes divided by (1. +resDiv),(1.+stfDiv)
   *
   * @param srcFrac fraction of source available for this swap
   * @param otherFrac fraction of resource and of staff available for swapCost
   * @param maxMov max move allowed
   * @param source source for the swap alternately r or s to set a limit
   * @param resource resource available for cost of swap
   * @param staff staff available for cost of swap
   * @param resDiv divides resource to find a max for swap
   * @param stfDiv divides staff to find a max for swap
   * @return this: amount available for swap, costs and maxMove used
   */
  ARow setAvailableSwapi(double srcFrac, double otherFrac, double maxMov, ARow source, ARow resource, ARow staff, double resDiv, double stfDiv) {
    double src, res, stf, mMov, aRes, aStf, aSrc, fRes, fStf, val;
    int lev = History.aux5Info;// can change to .debuggingInformation
    if (lev < History.aux5Info) {
      hist.add(new History("s@", lev, "i source", source));
      hist.add(new History("s@", lev, "i resource", resource));
      hist.add(new History("s@", lev, "i staff", staff));
    }

    for (int n : E.alsecs) {
      double sBal = source.get(n) * srcFrac;
      res = resource.get(n) * otherFrac;
      stf = staff.get(n) * otherFrac;

      //  aSrc = src=sBal - srcResrv;
      // aStf = oBal*otherFrac;
      // calculate amount of move possible with the given cost and frac
      //fRes = (aRes=(res=resource.get(n)) * otherFrac) /(1.+ resDiv); // r to r or s to s rcost
      fRes = res / resDiv; // r to r or s to s rcost
      // fStf = (aStf=(stf=staff.get(n)) * otherFrac) /(1. + stfDiv); //  s avail / s cost divisor
      fStf = stf / stfDiv; //  s avail / s cost divisor
      set(n, (val = Math.min(maxMov, Math.min(sBal, Math.min(fRes, fStf)))));
      // list elements 0 and 2 to see how this worked
      if (n == 0 || n == 2) {
        hist.add(new History("swp", History.debuggingMinor11, " incWork n=" + n, "sBal" + df(sBal), "bf" + df(srcFrac), "src*" + df(source.get(n)), "fRes" + df(fRes), "rDiv=" + df(resDiv), "fStf" + df(fStf), "sDiv=" + df(stfDiv), "maxM=" + df(maxMov), "val=" + df(val), "<<<<<<<<<<<<"));
      }
    }
    return this;
  }

  /**
   * OBSOLETE derive an ARow of the amounts available to swap, This uses
   * balances, mtgNeeds6 and mtggNeeds6 are extra limits, dependent on whether
   * the minimum balance in rawProspects2 is too low.
   *
   * aSrc,aStf,aRes are variables with the frac applied fStf,fRes are aStf,aRes
   * divided by (1. +resDiv),(1.+stfDiv)
   *
   * @param srcFrac fraction of source source available for this swap
   * @param otherFrac fraction of resource and staff available for swapCost
   * @param maxMov max move allowed, a fraction r + s sum in caller
   * @param source source for the swap alternately r or s to set a limit
   * @param resource resource source available for cost of swap
   * @param staff staff source available for cost of swapf
   * @param resDiv divides resource to find a max for swap
   * @param stfDiv divides staff to find a max for swap
   * @param emergMode if true, only mtgNeeds supply a max filter
   * @param mtggNeed the goal need
   * @param mtgNeed the need without goals for the partner as destination
   *
   * @return max amount available for swap, costs and move used
   */
  ARow setAvailableSwapi(double srcFrac, double otherFrac, double maxMov, ARow source, ARow resource, ARow staff, double resDiv, double stfDiv, boolean emergMode, ARow mtggNeed, ARow mtgNeed) {
    double src, res, stf, mMov, aMov, aRes, aStf, aSrc, fRes, fStf, val, gNeed = 0., ggNeed = 0., aNeed;
    mMov = maxMov;  // max Move
    int lev = History.aux5Info;// can change to .debuggingInformation
    if (lev < History.aux5Info) {
      hist.add(new History("s@", lev, "i source", source));
      hist.add(new History("s@", lev, "i resource", resource));
      hist.add(new History("s@", lev, "i staff", staff));
      hist.add(new History("s@", lev, "imtggNeed", mtggNeed));
      hist.add(new History("s@", lev, "imtgNeed", mtgNeed));
    }

    for (int n : E.alsecs) {
      aSrc = (src = source.get(n)) * srcFrac;
      // calculate amount of move possible with the given cost and frac
      fRes = (aRes = (res = resource.get(n)) * otherFrac) / (1. + resDiv); // r to r or s to s rcost
      fStf = (aStf = (stf = staff.get(n)) * otherFrac) / (1. + stfDiv); //  s avail / s cost divisor
      // make mtgNeed and mtggNeed current and goal need the partner destination
      aMov = Math.max(0., Math.min(mMov, (aNeed = emergMode ? (gNeed = mtgNeed.get(n)) : (ggNeed = mtggNeed.get(n)))));
      set(n, (val = Math.min(aMov, Math.min(aSrc, Math.min(fRes, fStf)))));
      // list elements 0 and 2 to see how this worked
      if (n == 0 || n == 2) {
        hist.add(new History("h@", History.valuesMinor7, "avail n=" + n + "aSrc=" + df(aSrc), "val=" + df(val), "bf" + df(srcFrac), "src*" + df(src), "aRes" + df(aRes), "rDiv=" + df(resDiv), "aStf" + df(aStf), "sDiv=" + df(stfDiv), "mxm=" + df(mMov), "gnd" + df(gNeed), "ggnd" + df(ggNeed)));
      }
    }
    return this;
  }

  /**
   * derive an ARow of the amounts available to swap, using only a single
   * subAsset and the corresponding swapcost for that SubAsset, for XFER this is
   * used once for resource and once for staff using the higher swapCost for
   * each subAsset
   *
   * @param srcFrac fraction of source+reserve=balwr source available for this
   * swap
   * @param availFrac fraction of resource and of staff available for swapCost
   * @param maxMove max move allowed, a fraction * sum above
   * @param source source for the swap alternately rc or sg to set a limit
   * @param negAvail available for cost of swap or swap+mov (adiv)
   * @param adiv divides subBal to find a max for swap or swap+mov
   * @return positive amount available for swap
   */
  ARow xsetAvailableSwap(String titl, double srcFrac, double availFrac, double maxMove, ARow source, ARow negAvail, double adiv) {
    double bal, avail, bAvail, avlF, sumF, v, v2 = -99, av2 = -99, av3 = -99, bal2 = -99, max = -999999., bal3;
    double bAvail2 = -9999., avail2 = -9999.;
    int maxIx = -1;

    int lev = History.dl + 5;// can change to .debuggingInformation
    if (lev < History.dl) {
      hist.add(new History("S^", lev, titl + "src", source));
      hist.add(new History("S^", lev, titl + "-avil", negAvail));
      //   hist.add(new History("sAv", lev, "e staff", staff));
    }
    lev = History.loopMinorConditionals5;  // print a sample each time
    for (int n = 0; n < E.LSECS; n++) {
      double balS = source.get(n) * srcFrac; // max possible units source of move
      bal2 = source.get(n) * availFrac; // max possible units swap cost
      bal = Math.min(balS, bal2); // min of both aource availables
      bAvail = bal / adiv;  // mov by the balances
      avail = (av3 = -negAvail.get(n) * availFrac) / adiv;  // available to mov
      set(n, Math.max(0., v = Math.min(maxMove, Math.min(avail, bAvail))));
      if (v > max) { // save the max move for this row
        maxIx = n;
        max = v;
      }
      if (n == 2) { // list the second sector with maxes after loop
        v2 = v;
        bal2 = bal;
        bAvail2 = bAvail;
        avail2 = avail;
      }
    }
    if (History.dl > lev) {
      hist.add(new History("#*", lev, titl + " v=", df(v2), "src" + df(source.get(2)), "*srF" + df(srcFrac), "=" + df(bal2), "sAvl" + df(bAvail2), "av2" + df(-negAvail.get(2)), "*af" + df(availFrac), "=" + df(-negAvail.get(2) * availFrac), "div=" + df(adiv), "=" + df(avail2), "maxM=" + df(maxMove), "maxV" + maxIx + "=" + df(max), "<<<<<<<<<<<<<<<"));
    }

    return this;
  }

  /**
   * minimize self with self or B/V
   *
   * @param B second part of min
   * @param V constant double divisor
   * @return min self or B/V
   */
  ARow minBdivbyV(ARow B, double V) {
    for (int m = 0; m < E.lsecs; m++) {
      Double at = B.get(m) / V;
      double av = at.isInfinite() || at.isNaN() ? 0. : at;
      set(m, Math.min(get(m), av));
    }
    return this;
  }

  /**
   * minimize self with B divbyC
   *
   * @param B Second parameter to minimize
   * @param C Divisor of second parameter
   * @return min self or B/C
   */
  ARow minBdivbyC(ARow B, ARow C) {
    for (int m = 0; m < E.lsecs; m++) {
      Double at = B.get(m) / C.get(m);
      double av = at.isInfinite() || at.isNaN() ? 0. : at;
      set(m, Math.min(get(m), av));
    }
    return this;
  }

  /**
   * minimize self with B/C unless T
   *
   * @param B min second parameter
   * @param C divisor
   * @param T do not change self if T false
   * @return if T min self or B/C else self
   */
  ARow minBdivbyCifT(ARow B, ARow C, boolean T) {
    if (T) {
      for (int m = 0; m < E.lsecs; m++) {
        Double at = B.get(m) / C.get(m);
        double av = at.isInfinite() || at.isNaN() ? 0. : at;
        set(m, Math.min(get(m), av));
      }
    }
    return this;
  }

  /**
   * if T get min of ARow A and ARow B/V else this unchanged
   *
   * @param A ARow
   * @param B ARow
   * @param V double
   * @param T boolean
   * @return this
   */
  ARow minAnBdivbyVifT(ARow A, ARow B, double V, boolean T) {
    if (T) {
      for (int m = 0; m < E.lsecs; m++) {
        Double at = B.get(m) / V;
        double av = at.isInfinite() || at.isNaN() ? 0. : at;
        set(m, Math.min(A.get(m), av));
      }
    }
    return this;
  }

  /**
   * if T Min this,B/V else this
   *
   * @param B ARow
   * @param V double
   * @param T boolean
   * @return if T Min this,B/V else this
   */
  ARow minBdivbyVifT(ARow B, double V, boolean T) {
    if (T) {
      for (int m = 0; m < E.lsecs; m++) {
        Double at = B.get(m) / V;
        double av = at.isInfinite() || at.isNaN() ? 0. : at;
        set(m, Math.min(get(m), av));
      }
    }
    return this;
  }

  /**
   * set if T Min(A,B/C) else this
   *
   * @param A always part of result
   * @param B possible difby c
   * @param C divisor
   * @param T condition if B divby C part of min
   * @return result
   */
  ARow setMinAnBdivbyCifT(ARow A, ARow B, ARow C, boolean T) {
    for (int m : E.alsecs) {
      Double at = B.get(m) / C.get(m);
      double av = at.isInfinite() || at.isNaN() ? 0. : at;
      set(m, T ? Math.min(A.get(m), av) : get(m));

    }
    return this;
  }

  /**
   * set the minimum of A and B for each value
   *
   * @param A
   * @param B
   * @return ARow this
   */
  ARow setMinAnB(ARow A, ARow B) {
    for (int m : E.alsecs) {
      set(m, Math.min(A.get(m), B.get(m)));
    }
    return this;
  }

  /**
   * if T set Min(A,B/V) else set A
   *
   * @param A always used in set
   * @param B second part of min divby V
   * @param V divisor
   * @param T condition
   * @return result
   */
  ARow setMinAnBdivbyVfT(ARow A, ARow B, double V, boolean T) {
    for (int m : E.alsecs) {
      set(m, T ? Math.min(A.get(m), B.get(m) / V) : A.get(m));

    }
    return this;
  }

  /**
   * if T set Min A, B-V else set A
   *
   * @param A
   * @param B
   * @param V
   * @param T condition
   * @return result
   */
  ARow setMinAnBsubVifT(ARow A, ARow B, double V, boolean T) {
    for (int m : E.alsecs) {
      set(m, T ? Math.min(A.get(m), B.get(m) - V) : A.get(m));

    }
    return this;
  }

  /**
   * if T set min A,B-C else set A
   *
   * @param A
   * @param B
   * @param C
   * @param T
   * @return
   */
  ARow setMinAnBsubCifT(ARow A, ARow B, ARow C, boolean T) {
    for (int m : E.alsecs) {
      set(m, T ? Math.min(A.get(m), B.get(m) - C.get(m)) : A.get(m));
    }
    return this;
  }

  /**
   * min(A,B-C)
   *
   * @param A
   * @param B
   * @param C
   * @return
   */
  ARow minAnBsubC(ARow A, ARow B, ARow C) {
    for (int m : E.alsecs) {
      set(m, Math.min(A.get(m), B.get(m) - C.get(m)));
    }
    return this;
  }

  /**
   * min(self,B-C)
   *
   * @param B
   * @param C
   * @return
   */
  ARow minBsubC(ARow B, ARow C) {
    for (int m : E.alsecs) {
      set(m, Math.min(get(m), B.get(m) - C.get(m)));
    }
    return this;
  }

  /**
   * set min(A,B*C)
   *
   * @param A ARow
   * @param B ARow
   * @param V double
   * @return min(A,B*V);
   */
  ARow minAnBmultV(ARow A, ARow B, double V) {
    for (int m = 0; m < E.lsecs; m++) {
      set(m, Math.min(A.get(m), B.get(m) * V));
    }
    return this;
  }

  /**
   * min this, B *V
   *
   * @param B
   * @param V
   * @return min this, B *V
   */
  ARow minBmultV(ARow B, double V) {
    for (int m = 0; m < E.lsecs; m++) {
      set(m, Math.min(get(m), B.get(m) * V));
    }
    return this;
  }

  /**
   * MinAnV
   *
   * @param A ARow
   * @param V double
   * @return this = each A*v
   */
  ARow minAnV(ARow A, double V) {
    for (int m = 0; m < E.lsecs; m++) {
      set(m, Math.min(A.get(m), V));
    }
    return this;
  }

  /**
   * minV min of this and double V
   *
   * @param V double
   * @return min this, V
   */
  ARow minV(double V) {
    for (int m = 0; m < E.lsecs; m++) {
      set(m, Math.min(get(m), V));
    }
    return this;
  }

  /**
   * add ARows A+B+C+D to ARow
   *
   * @param A an ARow
   * @param B an ARow
   * @param C an ARow
   * @param D an ARow
   */
  ARow add(ARow A, ARow B, ARow C, ARow D) {
    for (int i = 0; i < E.lsecs; i++) {
      add(i, A.get(i) + B.get(i) + C.get(i) + D.get(i));
    }
    return this;
  }

  /**
   * add ARows A+B+C to ARow
   *
   * @param A an ARow
   * @param B an ARow
   * @param C an ARow
   */
  ARow add(ARow A, ARow B, ARow C) {
    for (int i = 0; i < E.lsecs; i++) {
      add(i, A.get(i) + B.get(i) + C.get(i));
    }
    return this;
  }

  ARow add(ARow A, ARow B) {
    for (int i = 0; i < E.lsecs; i++) {
      add(i, A.get(i) + B.get(i));
    }
    return this;
  }

  /**
   * add in the maint, travel or grow routines first zero the ARow then add val
   * to the ix element of ARow
   *
   * @param ix
   * @param v
   * @return new value for ix
   */
  public double add(int ix, double v) {
    if (values == null) {
      fill();
    }
    double r;
    setCnt++;
    if (E.debugDouble) {
      r
              = values[ix]
              = ec.doubleTrouble(
                      doubleTrouble(v, "add#V")
                      + doubleTrouble(values[ix], "addVal"), "sum");
    }
    else {
      r = values[ix] += v;
    }
    return r;
  }

  /**
   * add A to ARow
   *
   * @param A an ARow
   * @return revised this ARow
   */
  ARow add(ARow A) {
    for (int i = 0; i < E.lsecs; i++) {
      add(i, A.get(i));
    }
    return this;
  }

  ARow add(double V) {
    for (int i = 0; i < E.lsecs; i++) {
      add(i, V);
    }
    return this;
  }

  ARow setAdd(ARow A, double v) {
    for (int i = 0; i < E.lsecs; i++) {
      set(A.get(i) + v);
    }
    return this;
  }

  ARow setAdd(ARow A, ARow B) {
    for (int i = 0; i < E.lsecs; i++) {
      set(i, A.get(i) + B.get(i));
    }
    return this;
  }

  /**
   * if T set A + V
   *
   * @param A ARow
   * @param V double
   * @param T boolean
   * @return if T A + V
   */
  ARow setAaddVifT(ARow A, double V, boolean T) {
    if (T) {
      for (int m = 0; m < E.lsecs; m++) {
        set(m, A.get(m) + V);
      }
    }
    return this;
  }

  /**
   * if T for each this if B < W && this > U set A+V used to add to a need, set
   * values are less than U unset values are this = 1. > .99 so may set a new
   * need value V it it is lt 0. a need
   *
   * @param V double value to add if B lt W
   * @param B ARow match this
   * @param W double B lt W
   * @param T skip add if false, do conditional add if true
   * @return this with any modifications
   */
  ARow setAaddVifgtUifBltWifT(ARow A, double V, double U, ARow B, double W, boolean T) {
    if (T) {
      for (int m = 0; m < E.lsecs; m++) {
        if ((get(m) > U) && (B.get(m) < W)) {
          set(m, A.get(m) + V);
        }
      }
    }
    return this;
  }

  /**
   * subtract double costs if a negative value
   *
   * @param B ARow double expenses for this
   * @param U normally E.nzero
   * @return if this < U this -B else this
   */
  ARow setSubBifltU(ARow B, double U) {
    for (int m = 0; m < E.lsecs; m++) {
      if (get(m) < U) {
        add(m, -B.get(m));
      }
    }

    return this;
  }

  /**
   * reduce values[ix] to maxVal if necessary
   *
   * @param ix
   * @param val max value for values[ix]
   * @return current value values[ix]
   */
  public double setMaxVal(int ix, double val) {
    if (values[ix] > val) {
      setCnt++;
      values[ix] = val;
    }
    return values[ix];
  }

  /**
   * raise values[ix] to val if necessary
   *
   * @param ix
   * @param val
   * @return current value of values[ix]
   */
  public double setMinVal(int ix, double val) {
    if (values[ix] < val) {
      values[ix] = val;
      setCnt++;
    }
    return values[ix];
  }

  /**
   * set for each A if positive A else 0 + B
   *
   * @param A use positive values
   * @param B use all values
   * @return new value of this
   */
  ARow setAposB(ARow A, ARow B) {
    for (int m : E.alsecs) {
      set(m, (A.get(m) > 0. ? A.get(m) : 0.) + B.get(m));
    }
    return this;
  }

  /*
   <Double,Double> ARow setAmapBmap(ARow A, Function<Double, Double> map1, ARow B, Function<Double, Double> map2) {
   for (int m : E.alsecs){
   Double aa = map1.apply(A);
   Double bb = map2.apply(B);
   double cc = aa;
   set(m,aa. + bb);

   }
   return this;
   }

   } */
  /**
   * limit ARow.values[ix] to min less than or eq ARow.values[ix] <= max
   *
   * @param ix
   * @param max
   * @param min
   * @return resulting values[ix] with ARow set between min and max
   */
  public double setLimVal(int ix, double min, double max) {
    return limVal(ix, min, max);
  }

  public double limVal(int ix, double min, double max) {
    if (values[ix] < min) {
      values[ix] = min;
      setCnt++;
    }
    if (values[ix] > max) {
      values[ix] = max;
      setCnt++;
    }
    return values[ix];
  }

  /**
   * set to value of A, limited by min and max A is not changed
   *
   * @param A ARow with the original values
   * @param min minimum value which may be set
   * @param max maximum value which may be set assume the ARow for this method
   * has been instantiated with new it may or may not have filled values, it is
   * left no longer valid (ordered)
   */
  public ARow limVal(ARow A, double min, double max) {
    setLimVal(A, min, max);
    return this;
  }

  /**
   * set this to each min <= a <<= max
   *
   * @param A
   * @param min
   * @param max
   * @return each a min <= a <= max
   */
  public ARow setLimVal(ARow A, double min, double max) {
    for (int m = 0; m < E.lsecs; m++) {
      set(m, A.get(m) < min ? min : A.get(m) > max ? max : A.get(m));
    }
    return this;
  }

  /**
   * set each sector of this between min and max inclusive
   *
   * @param min mins for each financial sector
   * @param max max for all financial sectors
   * @return each min <= a <= max
   */
  public ARow setLimVal(ARow min, double max) {
    double am;
    double amin;
    for (int m = 0; m < E.lsecs; m++) {
      am = get(m);
      amin = min.get(m);
      set(m, am < amin ? amin : am > max ? max : am);
    }
    return this;
  }

  /**
   * tmp = min <= tmp <= max @
   *
   *
   * @param min
   * @param max
   * @return tmp
   *
   */
  public ARow limVal(double min, double max) {
    ARow tmp = new ARow(ec);
    for (int m = 0; m < E.lsecs; m++) {
      tmp.set(m, get(m) < min ? min : get(m) > max ? max : get(m));
    }
    return tmp;
  }

  /**
   * copy current ARow to "to" ARow
   *
   * @param to must be an instantiated copy of ARow
   * @return return a copy of the calling ARow
   */
  public ARow copyto(ARow to) {
    if (values != null) {
      if (to.values == null) {
        to.fill();
      }
      for (int i = 0; i < E.lsecs; i++) {
        to.values[i] = values[i];
        to.ix[i] = ix[i];
      }
      to.sum = sum;

      to.setCnt = setCnt;
      to.savCnt = savCnt;
      //     to.filled=true;
    }
    //  oldto = to;
    return to;
  }

  /**
   * order the ix from min to max of values then set valid
   */
  ARow makeOrderIx() {
    if (setCnt != savCnt) {
      if (values == null) {
        fill();
      }
      double[] min = new double[E.lsecs];
      // int[] maxIx = new int[4];
      int[] minIx = new int[E.lsecs];
      double minC, minO;
      int minOIx, minCIx;
      negSum = plusSum = sum = 0.;
      negCnt = plusCnt = 0;
      negMin = 9999999999.;
      plusMax = -9999999999.;
      // sort entries min to max, set minIx points to entry in original array
      for (int g = 0; g < E.lsecs; g++) {
        sum += minC = values[g];
        if (minC < -0.0) {
          negSum += minC;
          negCnt++;
        }
        else {
          plusSum += minC;
          plusCnt++;
        }
        minCIx = g;
        for (int k = 0; k < g; k++) {
          if (minC < min[k]) { // currentMin < new entry in min list
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
      negMin = min[0];
      plusMax = min[E.LSECS - 1];
      savCnt = setCnt;
    }
    return this;
  }

  /**
   * order the ix from max to min of values then set valid
   */
  ARow makeMaxOrderIx() {
    if (values == null) {
      fill();
    }
    double[] max = new double[E.lsecs];
    int[] maxIx = new int[E.lsecs];
    double maxC, maxO;
    int maxOIx, maxCIx;
    sum = 0.;
    for (int g = 0; g < E.lsecs; g++) {
      sum += maxC = values[g];
      maxCIx = g;
      for (int k = 0; k < g; k++) {
        if (maxC > max[k]) {
          maxO = max[k];
          maxOIx = ix[k];
          ix[k] = maxCIx;
          maxCIx = maxOIx;
          max[k] = maxC;
          maxC = maxO;
        }
      }
      max[g] = maxC;
      ix[g] = maxCIx;
    }
    savCnt = setCnt;
    return this;
  }

  /**
   * return the n from the least index
   *
   * @param n count of mins
   * @return n't index from least min
   */
  public int minIx(int n) {
    if (setCnt != savCnt) {
      makeOrderIx();
    }
    return ix[n];
  }

  int minIx() {
    if (setCnt != savCnt) {
      makeOrderIx();
    }
    return minIx(0);
  }

  /**
   * return the n from the least index
   *
   * @param n count of mins
   * @return n't index from least min
   */
  public int minix(int n) {
    if (setCnt != savCnt) {
      makeOrderIx();
    }
    return ix[n];
  }

  /**
   * return index of the greatest value
   *
   * @return index of least value
   *
   * public int bbcc() { return minIx(0); }
   */
  public int maxIx() {
    if (setCnt != savCnt) {
      makeOrderIx();
    }
    return ix[E.lsecs - 1];
  }

  /**
   * return the nth index of the greatest value
   *
   * @param n select which max
   * @return the index of the nth greatest value
   */
  public int maxIx(int n) {
    if (setCnt != savCnt) {
      makeOrderIx();
    }
    return ix[E.lsecs - 1 - n];
  }

  /**
   * set min of 2 ARows
   *
   * @param A ARow
   * @param B ARow
   */
  public ARow min(ARow A, ARow B) {
    for (int m = 0; m < E.lsecs; m++) {
      double aa = A.get(m);
      double bb = B.get(m);
      set(m, Math.min(A.get(m), B.get(m)));
    }
    return this;
  }

  /**
   * set min of instance and ARow A
   *
   * @param A ARow
   */
  public ARow min(ARow A) {
    for (int m = 0; m < E.lsecs; m++) {
      set(m, Math.min(A.get(m), get(m)));
    }
    return this;
  }

  /**
   * if T set min of instance and AROW A
   *
   * @param A ARow
   * @param T boolean controling action if T false instance is not touched
   */
  public ARow min(ARow A, boolean T) {
    if (T) {
      for (int m = 0; m < E.lsecs; m++) {
        set(m, Math.min(A.get(m), get(m)));
      }
    }
    return this;
  }

  /**
   * set ARow instance to the min of two ARows
   *
   * @param A the first ARow for mins
   * @param B the secon Arow for mins
   * @return this an ARow each element the min of that in the 2 ARows
   */
  public ARow setMin(ARow A, ARow B) {
    for (int m = 0; m < E.lsecs; m++) {
      set(m, Math.min(A.get(m), B.get(m)));
    }
    return this;
  }

  /**
   * if T set instance to min of A or B otherwise leave self alone
   *
   * @param a ARow
   * @param b ARow
   * @param t boolean
   */
  public ARow setMinAnBifT(ARow a, ARow b, boolean t) {
    if (t) {
      for (int m = 0; m < E.lsecs; m++) {
        set(m, Math.min(a.get(m), b.get(m)));
      }
    }
    return this;
  }

  /**
   * if T set instance to min ofself A or B
   *
   * @param a ARow
   * @param b ARow
   * @param t boolean
   */
  public ARow min(ARow a, ARow b, boolean t) {
    if (t) {
      for (int m = 0; m < E.lsecs; m++) {
        set(m, Math.min(get(m), Math.min(a.get(m), b.get(m))));
      }
    }
    return this;
  }

  /**
   * set min of ARow and double
   *
   * @param A ARow
   * @param B double
   */
  public ARow min(ARow A, double B) {
    for (int m = 0; m < E.lsecs; m++) {
      set(m, Math.min(get(m), Math.min(A.get(m), B)));
    }
    return this;
  }

  /**
   * set min of self and 3 ARows
   *
   * @param A ARow
   * @param B ARow
   * @param C ARow
   */
  public ARow min(ARow A, ARow B, ARow C) {
    for (int m = 0; m < E.lsecs; m++) {
      set(m, Math.min(get(m), Math.min(A.get(m), Math.min(B.get(m), C.get(m)))));
    }
    return this;
  }

  /**
   * if t return min of self and A B C
   *
   * @param A
   * @param B
   * @param C
   * @param t
   * @return min if t else return self
   */
  public ARow min(ARow A, ARow B, ARow C, boolean t) {
    if (t) {
      for (int m = 0; m < E.lsecs; m++) {
        set(m, Math.min(get(m), Math.min(A.get(m), Math.min(B.get(m), C.get(m)))));
      }
    }
    return this;
  }

  /**
   * set min of self and A,B,C,D
   *
   * @param A
   * @param B
   * @param C
   * @param D
   * @return this
   */
  public ARow min(ARow A, ARow B, ARow C, ARow D) {
    for (int m = 0; m < E.lsecs; m++) {
      set(m, Math.min(get(m), Math.min(A.get(m), Math.min(B.get(m), Math.min(C.get(m), D.get(m))))));
    }
    return this;
  }

  /**
   * if t return set min of self and A, B, C, D for each sector
   *
   * @param A
   * @param B
   * @param C
   * @param D
   * @param t
   * @return
   */
  public ARow min(ARow A, ARow B, ARow C, ARow D, boolean t) {
    if (t) {
      for (int m = 0; m < E.lsecs; m++) {
        set(m, Math.min(get(m), Math.min(A.get(m), Math.min(B.get(m), Math.min(C.get(m), D.get(m))))));
      }
    }
    return this;
  }

  /**
   * set each sector of this row to the least of each of A,B,C
   *
   * @param A
   * @param B
   * @param C
   * @return each sector least of corresponding sector in A , B, C
   */
  public ARow setMin(ARow A, ARow B, ARow C) {
    for (int m = 0; m < E.lsecs; m++) {
      set(m, Math.min(A.get(m), Math.min(B.get(m), C.get(m))));
    }
    return this;
  }

  /**
   * return least value in row
   *
   * @return least value
   */
  double min() {
    if (setCnt != savCnt) {
      makeOrderIx();
    }
    return values[ix[0]];
  }

  /**
   * find the minimus available value, ignore values &lt =99.
   *
   * @return minimum of all values &gt -99.
   */
  double amin() {
    double rtn = 999999.;
    for (int n : E.alsecs) {
      if (values[n] > -99. && values[n] < rtn) {
        rtn = values[n];
      }
    }
    return rtn;
  }

  /**
   * find the max valid value, ignore values &lt -99.
   *
   * @return max valid value
   */
  double amax() {
    double rtn = -999999.;
    for (int n : E.alsecs) {
      if (values[n] > -99. && values[n] > rtn) {
        rtn = values[n];
      }
    }
    return rtn;
  }

  /**
   * find the average of the valid values, ignore values &lt -99.
   *
   * @return sum/cnt of valid values
   */
  double aave() {
    double rtn = 0., cnt = 0.;
    for (int n : E.alsecs) {
      if (values[n] > -99.) {
        rtn += values[n];
        cnt++;
      }
    }
    return rtn / cnt;
  }

  /**
   * find the index of the minimum valid value, ignore values &lt -99.
   *
   * @return index of minimum valid value
   */
  int aminIx() {
    double min = 999999.;
    int rtn = E.lsecs;
    for (int n : E.alsecs) {
      if (values[n] > -99. && values[n] < rtn) {
        min = values[n];
        rtn = n;
      }
    }
    return rtn;
  }

  /**
   * return the v least value in the row
   *
   * @param v 0=min value, 1 = next to min, 2= second from min
   * @return the v'th from the least value,
   */
  double min(int v) {
    if (setCnt != savCnt) {
      makeOrderIx();
    }
    return values[ix[v]];
  }

  double min2() {
    if (setCnt != savCnt) {
      makeOrderIx();
    }
    return values[ix[1]];
  }

  double min3() {
    if (setCnt != savCnt) {
      makeOrderIx();
    }
    return values[ix[2]];
  }

  /**
   * get the max of this row
   *
   * @return the max value
   */
  double max() {
    if (setCnt != savCnt) {
      makeOrderIx();
    }
    return values[ix[E.lsecs - 1]];
  }

  /**
   * get the n'th max of this row
   *
   * @param n the number below the max
   * @return the n'th max value
   */
  double max(int n) {
    if (setCnt != savCnt) {
      makeOrderIx();
    }
    return values[ix[E.lsecs - 1 - n]];
  }

  /**
   * set max of 2 ARows
   *
   * @param A ARow
   * @param B ARow
   */
  public ARow max(ARow A, ARow B) {
    for (int m = 0; m < E.lsecs; m++) {
      set(m, Math.max(A.get(m), B.get(m)));
    }
    return this;
  }

  /**
   * set max of ARow and double
   *
   * @param A ARow
   * @param B double
   */
  public ARow max(ARow A, double B) {
    for (int m = 0; m < E.lsecs; m++) {
      set(m, Math.max(A.get(m), B));
    }
    return this;
  }

  /**
   * set min of 3 ARows
   *
   * @param A ARow
   * @param B ARow
   * @param C ARow
   */
  public ARow max(ARow A, ARow B, ARow C) {
    for (int m = 0; m < E.lsecs; m++) {
      set(m, Math.max(A.get(m), Math.max(B.get(m), C.get(m))));
    }
    return this;
  }

  /**
   * max Self and B and C
   *
   * @param B ARow
   * @param C ARow
   * @return max self B C
   */
  public ARow maxSnBnC(ARow B, ARow C) {
    for (int m = 0; m < E.lsecs; m++) {
      set(m, Math.max(get(m), Math.max(B.get(m), C.get(m))));
    }
    return this;
  }

  double sum() {
    if (setCnt != savCnt) {
      makeOrderIx();
    }
    return sum;
  }

  double ave() {
    if (setCnt != savCnt) {
      makeOrderIx();
    }
    double a = 1. / E.lsecs;
    double b = sum;
    double c = sum / E.lsecs;
    double d = a * b;
    return sum / E.lsecs;
  }

  /**
   * weightedAve, give min value weight w and max value weight 1
   *
   * @param minw weight of the minimum value, increasing this decreases average
   * @param maxw weight of the maximum value, increasing this increases average
   * @return sumValues/sumWeights; become smaller as w becomes larger
   */
  double weightedAve(double minw) {
    return weightedAve(minw, 1.);
  }

  double weightedAve(double minw, double maxw) {
    double mm = max();   // forces order
    double difw = maxw - minw;  //
    double difv = mm - min(); // eg  5 max - 3 min  = 2, ww=2
    double multv = difv / difw;  // weight multiplier per unit w
    double sumWeights = 0;
    double sumValues = 0;
    double subDif = 0;
    double tempWeight = 0;
    setCnt++;
    for (int i = 0; i < E.lsecs; i++) {
      subDif = mm - values[i];  //subDif   value dif from max
      tempWeight = -subDif * multv + maxw; // at maxv subDif==0, tempWeight==maxw
      sumWeights += tempWeight;
      sumValues += values[i] * tempWeight;
    }
    return sumValues / sumWeights;
  }

  /**
   * set ARow A revalued proportionally to a newMax and newMin
   *
   * @param newMin new Minimum for the ARow
   * @param newMax new Maximum for the ARow
   * @param A The ARow that is being revalued
   */
  ARow setReValuedA(double newMin, double newMax, ARow A) {
    double oldMax = A.max();   // forces order
    double difNew = newMax - newMin;  //
    double difOld = oldMax - A.min(); // eg  5 max - 3 min  = 2, ww=2
    double multNew = difNew / difOld;  // weight multiplier per unit w
    double oldDif = 0;
    setCnt++;
    for (int i = 0; i < E.lsecs; i++) {
      oldDif = oldMax - A.values[i];  //oldDif   value dif from oldMax
      set(i, newMax - oldDif * multNew);
    }
    return this;
  }

  /**
   * set to revalued A proportionally to min and max
   *
   * @param A The ARow that is being revalued
   * @param newMin new Minimum for the ARow
   * @param newMax new Maximum for the ARow EG newMin=5.5 newMax=.5, oldMax=6
   * oldMin=1. difNew -5= .5 -5.5,difOld 5=6-1, multNew=-1 A=3, oldDif 3=6-3,
   * 3.5=.5 - 3 * -1 A=4, oldDif 2=6-4, 2.5=.5 -2*-1 A=1, oldDif 5=6-1,
   * 5.5=.5-5*-1 A=6, oldDif 0=6-6, .5 = .5 - 0*-1
   */
  ARow revalueAtoMinMax(ARow A, double newMin, double newMax) {
    double oldMax = A.max();   // forces order
    double difNew = newMax - newMin;  //
    double difOld = A.max() - A.min(); // eg  5 max - 3 min  = 2, ww=2
    double multNew = difNew / difOld;  // weight multiplier per unit w
    double oldDif = 0;
    setCnt++;
    for (int i : E.alsecs) {
      oldDif = oldMax - A.values[i];  //oldDif   value dif from oldMax
      set(i, newMax - oldDif * multNew);
    }
    return this;
  }

  /**
   * derive a strategic trade value using mult limited by limLow and its
   * reciprocal. The results center around 1 so that they can be multiplied
   * together and leave a result around 1 in this. assume 1. to 5., ctr = 3.
   * mult = 1; value 4 = 4-3 = +1, 1/ahlf=2 = .5 *mult=1 = .5 + 1 value = 1.5
   * value 2 = 2-3 = -1 -1/ahlf=2 = -.5 , neg means 1/(1+.5) = 2/3 = .66666
   * value 1 ,mult=2 = 1-3 = -2, -2/2= -1 neg means 1/((1+1)*2) = 1/4 = .25
   * value 5 ,mult=3 = 5-3 = 2, 2/2 = +1 (1+1)*mult=3 = 6.
   *
   * @param A Input
   * @param mult mult < 1.  causes wierd values, mult should be > 1.
   * @param limLow lowest faction permitted for result. limHigh is its
   * reciprocal
   * @return calling ARow new this value
   */
  ARow strategicValAbyMultLim(String atitl, ARow A, double mult, double alimLow) {
    double amax = A.max();
    double amin = A.min();
    double actr = (amax + amin) / 2.;
    double ahlf = (amax - amin) / 2.; // 1/2 diff = max,min to ctr
    double adif = (amax - amin);
    double dVal = 0;
    double aVal = 1;
    double fmult = mult;
    // force mult to be > 1.
    mult = mult < 1. ? 1 / mult : mult;
    // set mult for nominal diference of 20, adjust for other diferences
    mult = (mult - 1.);   //
    double aMult = mult / ahlf;
    double tVal = 1;
    double limHigh = alimLow < 1. ? 1. / alimLow : alimLow;
    double limLow = alimLow < 1. ? alimLow : 1. / alimLow;

    // derived in google sheet game tests
    for (int m : E.alsecs) {
      aVal = A.get(m);
      dVal = aVal - actr;  // 5-3 = 2,  1 - 3 = -2;
      if (adif < E.pzero) { // handle all the same value
        tVal = 1.;
      }
      else if (dVal > E.pzero) { // positive dif
        tVal = ((1. - dVal / ahlf) * mult) + 1.;  // some value above 1
        if (tVal > limHigh) {  // but not too high
          if (History.dl > 4) {
            StackTraceElement a0 = Thread.currentThread().getStackTrace()[1];
            EM.curEcon.hist.add(new History(5, ">>>>n" + EM.curEcon.as.n + "Err@", a0.getFileName(), wh(a0.getLineNumber()), "A.get(" + wh(m) + ")=", df(aVal) + "result=", df(tVal) + ">", "limHigh=", df(limHigh)));
          }
          //   E.myTest(true, "limHigh < A.get(" + m + ")=" + df(aVal) + " result=" + df(tVal) + "> limHigh=" + df(limHigh));
          tVal = limHigh;
        }
        // set(m, tVal);  set below
      }
      else {  // dVal < 0.
        tVal = 1. / (((1. + dVal / ahlf) * mult) + 1.);
        if (tVal < limLow) {
          if (History.dl > 4) {
            StackTraceElement a0 = Thread.currentThread().getStackTrace()[1];

            EM.curEcon.hist.add(new History(5, "n" + EM.curEcon.as.n + ">>>ERR@", a0.getFileName(), wh(a0.getLineNumber()), "A.get(" + wh(m) + ")=", df(aVal) + "result=", df(tVal) + "<", "limLow=", df(limLow)));
          }
          //    E.myTest(true, "limLow > A.get(" + m + ")=" + df(aVal) + " result=" + df(tVal) + "< limLow=" + df(limLow));
          tVal = limLow;
        }
      }
      if (History.dl > 4) {
        EM.curEcon.hist.add(new History(11, "n=" + EM.curEcon.as.n + " " + atitl + m, "v=" + df(aVal), "tV=" + df(tVal), "ctr=" + df(actr), "dV" + df(dVal), "mlt=" + df(fmult), df(mult), "hlf=" + df(ahlf), "lims=" + df(limLow), df(limHigh)));
      }
      set(m, tVal);
    }
    return this;
  }

  /**
   * derive a reciprocal strategic trade value using mult limited by limLow and
   * its reciprocal. The results center around 1 so that they can be multiplied
   * together and leave a result around 1 in this. mult is divided by the
   * average to keep results the same as the total values grow year by year if
   * (a-median)/(min-median) = -1.75 < 0, -1.75/-2.5 = 1/.7 = 1.4285 --2 < 0,
   * -2./-2.5= 1/1 if (a-median)/(min-median) = .7 then result = 1 + 1/.7 @param
   * A Input @param mult use mult/A.ave() as proportional multipli
   *
   * e
   * r
   * @param limLow lowest faction permitted for result. limHigh is its
   * reciprocal @return a new ARow with strategic values
   */
  ARow strategicRecipValAbyMultLim(String titla, ARow A, double mult, double alimLow) {
    double amax = A.max();
    double amin = A.min();
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
    EM.curEcon.hist.add(new History(History.debuggingMinor11, "n=" + EM.curEcon.as.n + " " + titla, "Hv" + df(amax), "Lv" + df(amin), "H-L" + df(adif), "mlt=" + df(mult), "fud=" + df(fudge), "hlf" + df(alhlf), "*" + df(ahlf), "ave=" + df(ave), "lims=" + df(limLow), df(limHigh), "abcdefghijklmn"));

    // derived in google sheet game tests
    for (int m : E.alsecs) {
      aVal = A.get(m);
      // get dVal distance from median
      dVal = aVal - ave;  // if aVal>actr:dVal>0;; aVal < actr?? dVal<0
      //     mVal = aMult * dVal;  // modified distance from center
      if (amax == amin) {// max == min
        tVal3 = tVal2 = tVal1 = tVal = 1.;  // prevent divid by 0
        set(m, tVal);
        if (History.dl > 4) {
          // list action for each calculation
          EM.curEcon.hist.add(new History(History.debuggingMinor11, "n=" + EM.curEcon.as.n + " " + titla + m, "v=" + df(aVal), "=>" + df(dVal), "=>" + df(tVal1), "=>" + df(tVal2), "=>" + df(tVal3), "=>_" + df(tVal), "ave=" + df(ave), "dV=" + df(dVal), "abcef,ghijk.lmnop.qrst"));
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
          EM.curEcon.hist.add(new History(History.debuggingMinor11, "n=" + EM.curEcon.as.n + " " + titla + m, "v=" + df(aVal), "=>" + df(dVal), "=>" + df(tVal1), "=>" + df(tVal2), "=>" + df(tVal3), "=>_" + df(tVal), "ave=" + df(ave), "dV=" + df(dVal), "abcef,ghijk.lmnop.qrst"));
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
          EM.curEcon.hist.add(new History(History.debuggingMinor11, "n=" + EM.curEcon.as.n + " " + titla + m, "v=" + df(aVal), "=>" + df(dVal), "=>" + df(tVal1), "=>" + df(tVal2), "=>" + df(tVal3), "=>^" + df(tVal), "ave=" + df(ave), "dV=" + df(dVal), "mnopq,rst,uvwxyz"));
        }
      }
    }
    return this;
  }

  /**
   * derive a strategic trade value for each of the 7 values in B turn aLinLow
   * %lt; .5 into 1.0 + aLimLow turn alimLow %lt; 1.0 into the low limit,
   * aHighLim = 1/aLowLim The median min(3) separates hHlf above med and lHlf at
   * or below The largest value in B becomes the smallest result The smailest
   * value in B becomes the largest result if the initial alimLow was negative,
   * then the results are reversed: the largest value in B becomes the largest
   * result %lt; aHighLim the smallest value in B becomes the smallest result
   * %gt; aLowLim
   *
   * @param titla The title for the ARow results
   * @param B the A2Row used as input
   * @param aLimLow either the highest result or its reciprical, negative
   * alimLow,means straight results
   * @return a new ARow with strategic values around 1 Recip (positive aLimLow,
   * high values small results and opposite (negative aLimLow, high values have
   * high results and opposite
   */
  ARow strategicRecipValBbyLim(String titla, ARow B, double aLimLow) {
    Econ ec = EM.curEcon;
    double amax = B.max(0);
    double amin = B.min(0);
    // neg means means straight results not reciprical
    boolean straight = aLimLow < 0.0;
    // force limit positive
    aLimLow = aLimLow < 0. ? -aLimLow : aLimLow;
    // force the limit > 1.
    aLimLow = aLimLow < .5 ? 1. + aLimLow : aLimLow; // aLimLow > .5
    double aLimHigh = (aLimLow < 1.0 ? 1 / aLimLow : aLimLow);
    aLimLow = aLimLow < 1.0 ? aLimLow : 1.0 / aLimLow; // now aLimLow < 1.
    // results will be in terms of difference from 1.
    double hLimDif = aLimHigh - 1.0;  // upper limit  1.0 + hLimDif
    double lLimDif = 1.0 - aLimLow;  // as hLimDif grows => lLimDif decreases lower limit 1.0 - lLimDif
    // select lower median for recip, higher median for straight from B 
    double median = B.min(3); //median value of B
    double lHlf = median - amin; // size of lower half usually > 0, could be 0
    // straight if aSign < 0.0
    double lMult = lHlf < E.PZERO && lHlf > E.NZERO ? 0.0000001
            : straight ? lLimDif / lHlf : -hLimDif / lHlf; // frac of differences
    // lower limit 1.0 - lLimDif
    double hHlf = amax - median;  //  size  of high half should be >= 0
    double hMult = hHlf < E.PZERO && hHlf > E.NZERO ? 0.000001
            : straight ? hLimDif / hHlf : -lLimDif / hHlf;
    // high limit is 1.0 + hLimDif
    double tVal = 1., tVal1 = 1., tVal2 = 1., tVal3 = 1., mult;

    ec.hist.add(new History(History.debuggingMinor11, titla + (amax == amin ? " all 1" : (!straight ? "recipricals" : "straight")), "Hv" + EM.mf(amax), "med=" + EM.mf(median), "Lv" + EM.mf(amin), "hHlf=" + EM.mf(hHlf), "lHlf=" + EM.mf(lHlf), "lMlt=" + EM.mf(lMult), "hMlt=" + EM.mf(hMult), "lims=" + EM.mf(aLimHigh), EM.mf(aLimLow), "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"));

    // process both Resource & Staff  or cargo & guests
    for (int n : E.ASECS) {
      tVal3 = B.get(n);
      // get dVal distance from average, sign is for reciprical or straight
      tVal2 = (tVal3 - median);
      // tVal2 = aaSign * tVal3;
      //     mVal = aMult * dVal;  // modified distance from center

      mult = tVal2 <= E.PZERO ? lMult : hMult;
      tVal1 = (tVal2 * mult);  // tVal2 < 0.0 lHlf straight => tVal1 < 0.0, lMult > 0.0
      // tVal2 < 0.0  lHlf !straight => tVal1 > 0.0 ,  lMult < 0.0
      // tval2 > 0.0 hHlf straight => tVal1 > 0.0 , hMult > 0.0
      // tval2 > 0.0 hHlf !straight => tVal1 < 0.0, hMult < 0.0
      tVal = 1.0 + tVal1; // for lHlf tVal1 < 0.0
      set(n, tVal);
      if (History.dl > 4 || hist.size() < 2000) {  // do the one at the bottom
        // list action for each calculation
        ec.hist.add(new History(History.debuggingMinor11, "n=" + ec.as.n + " " + titla + n,
                                "v=" + EM.mf(tVal3), "-med=" + EM.mf(median), "->" + eM.mf(tVal2), "mlt=" + df(mult),
                                "=" + df(tVal1), "+1=" + df(tVal), (tVal > 1.0 ? "< " + eM.mf(aLimHigh) : ">" + eM.mf(aLimLow)), "<<<<<<<<<<<<<<<<<<<<"));
      }
    }// for
    return this;
  }

  /**
   * softens the call to revalueAtoMinMax by possibly making the distance
   * between max and min smaller. When maxNew &gt minNew than min and max
   * parameter are unchanged unless A.min() or A.max() is between minNew and
   * maxNew, then the A.min() or A.max() values are used for newMin or newMax.
   * <p>
   * When maxNew &lt minNew then if A.min() or A.max() is between maxNew and
   * minNew, newMin becomes A.max() and/or newMax becomes A.min()
   */
  ARow setSoftRevalueAtoMinMax(ARow A, double minNew, double maxNew
  ) {
    double reMax, reMin;
    if (maxNew > minNew) {
      if (A.min() < maxNew && A.min() > minNew) {
        reMin = A.min();
      }
      else if (A.min() > maxNew) {
        // make reMin larger than minNew by the proportion that A.max > minNew
        double maxFrac = (maxNew - minNew) / (A.max() - minNew);
        reMin = (A.min() - minNew) * maxFrac + minNew;
        // if A.min < minNew raise it to minNew
      }
      else {
        reMin = minNew;
      }
      if (A.max() < maxNew && A.max() > minNew) {
        reMax = A.max();
      }
      else {
        reMax = maxNew;
      }
      return revalueAtoMinMax(A, reMin, reMax);
    }
    else {  //maxNew < minNew
      // maxNew=2, minNew = 6, A.min() = 3, ,reMax=3;
      if (A.min() < minNew && A.min() > maxNew) {
        reMax = A.min();
      }
      else if (A.min() > minNew) {
        double maxFrac = (minNew - maxNew) / (A.max() - maxNew);
        reMax = (A.min() - maxNew) * maxFrac + maxNew;
      }
      else {
        reMax = maxNew;
      }
      if (A.max() < minNew && A.max() > maxNew) {
        reMin = A.max();
      }
      else {
        reMin = minNew;
      }
      return revalueAtoMinMax(A, reMin, reMax);
    }
  }

  /**
   * invert ARow
   *
   * @param A source
   * @return inversion of A
   */
  ARow invertA(ARow A
  ) {
    for (int m = 0; m < E.lsecs; m++) {
      set(m, 1. / A.get(m));
    }
    return this;
  }

  /**
   * invert this ARow
   *
   * @return inversion of this ARow
   */
  ARow invert() {
    for (int m = 0; m < E.lsecs; m++) {
      set(m, 1. / get(m));
    }
    return this;
  }

  /**
   * limit Move by the limit ARow, set need to 1. if move < 0. move is the ARow
   * using this method @param need @param limit
   */
  ARow resetMove(ARow need, ARow limit
  ) {
    for (int m = 0; m < E.lsecs; m++) {
      set(m, Math.min(get(m), limit.get(m)));
      if (get(m) < E.pzero) {
        need.set(m, 1.);
      }
    }
    return this;
  }

  /**
   * limit Move by the limit ARow, set need to 1. if move < least tmove is the
   * ARow using this method @param need previous value of temp need
   *
   * @
   * param limit one of the swap or xfer ARows
   * @param least the least amount of move to still have tneed set
   *
   */
  ARow resetMove(ARow need, ARow limit, double least
  ) {
    for (int m = 0; m < E.lsecs; m++) {
      set(m, Math.min(get(m), limit.get(m)));
      if (get(m) < least) {
        need.set(m, 1.);
      }
    }
    return this;
  }

  /**
   * limit tMove by the swpLimit ARow, set need to 1. if move < least tmove is
   * the ARow using this method @param need pre value of oldNeed unless Mov
   *
   * e < least @
   *
   * param swpLimit one of the swap or xfer ARows @param oldneed need before
   * modification @param least the least amount of move to still have tneed set
   *
   */
  ARow setTMove(ARow need, ARow swpLimit, ARow oldNeed, ARow src, double least
  ) {
    for (int m = 0; m < E.lsecs; m++) {
      need.set(m, get(m) < least ? 1. : oldNeed.get(m));
      set(m, oldNeed.get(m) < E.nzero ? Math.min(src.get(m), swpLimit.get(m)) : -1.);

    }
    return this;
  }

  void aaa(int bbb, int c
  ) {

  }

  ARow setAelseWifBltV(ARow A, double W, ARow B, double V
  ) {
    for (int m = 0; m < E.lsecs; m++) {
      set(m, B.get(m) < V ? W : A.get(m));
    }
    return this;
  }

  /**
   * used in swaps to set tXneed to 1. if move B is too small
   *
   * @param V double conditional value to be set
   * @param B ARow move to be tested
   * @param W double move must be >= or need set off
   * @return if B < W set this to V
   */
  ARow setVifBltW(double V, ARow B, double W
  ) {
    for (int m = 0; m < E.lsecs; m++) {
      if (B.get(m) < W) {
        set(m, V);
      }
    }
    return this;
  }

  /**
   * add value to ix sector of this instance of cargos from ix sector of source
   *
   * @param ix index to the sector to be added from source
   * @param mov the amount to be added
   * @param source the other cargos as source
   * @return a reference to this instance of cargos
   */
  ARow addCargoValue(int ix, double mov, ARow source
  ) {
    E.myTest(source.get(ix) - mov < E.NZERO, "Error negative sourc resulte%1d %7.2f after source%1d %7.2f -mov %7.2f", ix, source.get(ix) - mov, ix, source.get(ix), mov);

    E.myTest(this.get(ix) + mov < E.nzero, "error negative result after add mov = %7.2f + this%1d  %7.2f= result%1d %7.2f", mov, ix, get(ix), ix, get(ix) + mov);
    source.add(ix, -mov);
    add(ix, mov);
    return this;
  }

  ARow addGuestsValue(int ix, double mov, double[] myGrade, ARow source, double[] sGrade, int downGrade
  ) {
    double remMov = mov;
    //9/9/15 skip either 0 source or 0 move value, avaoid infinite or NaN
    if (source.get(ix) != 0. && mov != 0.) {
      double tmov = mov * E.lgrades / (source.get(ix) * (E.lgrades - 2)); // fraction of augmented move per staff
      double avmov = mov * E.lgrades / (source.get(ix) * (E.lgrades - 5)); // augmented a mov
      double amov = 0;

      int ii, k = 0, kt = 0, kmax = 4 * E.lgrades;
      for (ii = 0; ii < kmax && (remMov > E.pzero); ii++) {
        k = ii % E.lgrades;
        E.myTest(sGrade[k] < E.NZERO, "Error round %2d, guests grade%2d %7.2f negative", ii, k, sGrade[k]);

        amov = tmov * sGrade[k];
        amov = Math.max(avmov, amov); // increase a small tail
        amov = Math.min(amov, sGrade[k]); // prevent neg
        amov = Math.min(amov, remMov);
        amov = Math.max(amov, 0.); // force not negative
        //   if (amov > remMov) {
        ///     amov = remMov;
        //   }
        E.myTest(sGrade[k] - amov < E.NZERO, "Error round%2d source guest%1d %7.2f grade%2d %7.2f will be neg after subtract amov %7.2f giving %7.2f, mov=%7.2f, remnant=%7.2f, ", ii, ix, source.get(ix), k, sGrade[k], amov, sGrade[k] - amov, mov, remMov);

        sGrade[k] -= amov;
        kt = k - downGrade > 0 ? k - downGrade : k;
        E.myTest(myGrade[kt] < E.NZERO, "Error round%2d source this%1d %7.2f negative grade%2d %7.2f, mov %7.2f remMov %7.2f", ii, ix, this.get(ix), k, myGrade[kt], mov, remMov);
        E.myTest(myGrade[kt] + amov < E.NZERO, "Error round%2d source this%1d %7.2f negative grade%2d %7.2f + amov %7.2f = %7.2f, mov %7.2f remMov %7.2f", ii, ix, this.get(ix), k, myGrade[kt], amov, myGrade[kt] + amov, mov, remMov);
        myGrade[kt] += amov;
        remMov -= amov;
      }
      double mySum = 0., sSum = 0.;
      for (int m = 0; m < E.lgrades; m++) {
        mySum += myGrade[m];
        sSum += sGrade[m];
      }
      set(ix, mySum);
      source.set(ix, sSum);
      // finally test for error
      E.myTest(remMov < E.NZERO || remMov > E.PZERO, "Error remMov %7.2f not 0.0 at this.get(%1d) %7.2f, mov %7.2f", remMov, ix, this.get(ix), mov);

    } // end not zero source or mov
    return this;
  } // end addGuestValue

  /**
   * apply conditions to creating a History
   *
   * @param hist the array containing history
   * @param bLev The level limit, any level beyond this prevents creation of
   * History, as always History.dl is the ultimate limit. No History can be
   * creater above History.dl
   * @param aPre The History prefix
   * @param lev The level of the History
   * @param www This is a variable size array of parameters all of String
   */
  void send(ArrayList<History> hist, int bLev, String aPre, int lev, String... www) {
    if (bLev <= History.dl && lev <= bLev) {
      if (aPre == null) {
        aPre = this.aPre;
      }
      else {
        this.aPre = aPre;
        ec.aPre = aPre;
        if (as != null) {
          as.aPre = aPre;
        }
      }
      hist.add(new History(aPre, lev, www));
    }
  }

  /**
   * apply conditions to creating a History
   *
   * @param hist the array containing history
   * @param bLev The level limit, any level beyond this prevents creation of
   * History, as always History.dl is the ultimate limit. No History can be
   * creater above History.dl
   * @param aPre The History prefix
   * @param lev The level of the History
   * @param title title of the History
   * @param m This is a variable size array of parameters all of String
   */
  void send(ArrayList<History> hist, int bLev, String aPre, int lev, String title, ARow m) {
    if (bLev <= History.dl && lev < bLev) {
      hist.add(new History(aPre, lev, title, m));
    }
  }

  /**
   * apply conditions to creating a History
   *
   * @param hist the array containing history
   * @param fracd fraction digits usually more than 2
   * @param bLev The level limit, any level beyond this prevents creation of
   * History, as always History.dl is the ultimate limit. No History can be
   * creater above History.dl
   * @param aPre The History prefix
   * @param lev The level of the History
   * @param title title of the History
   * @param m This is a variable size array of parameters all of String
   */
  void send(ArrayList<History> hist, int fracd, int bLev, String aPre, int lev, String title, ARow m) {
    if (bLev <= History.dl && lev < bLev) {
      hist.add(new History(aPre, fracd, lev, title, m));
    }
  }
}

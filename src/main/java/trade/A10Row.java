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
public class A10Row extends A6Rowa {

  static int[] dA1 = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
  static int[] dA = dA1;
  static final int[] IA = dA;
  static int[] dA2 = {2, 3, 4, 5, 6, 7, 8, 9};
  static final int[] I29 = dA2;
  static final int[] I09 = dA1;
  static int[] d29 = dA2;
  // static int lA = 10, lev4 = 4, lev5 = 5, lev6 = 6, lev7 = 7, lev8 = 8, lev9 = 9, lev10 = 10;
  // ARow[] A = new ARow[lA];
  // reqCosts for r,c,s,g   or rHealth sHealth rFertility sFertility
  // double sum[] = {0., 0., 0.}, plusSum[] = {0., 0., 0.}, negSum[] = {0., 0., 0.};
  // double minSum[] = {0., 0., 0.}, minSum2[] = {0., 0., 0.};
  // assume only cost type
//  int[] aCnt = {-11, -11, -11, -11, -11, -11, -11, -11, -11, -11};
//  int[] x1 = new int[E.l2secs];
//  int[] x2 = new int[E.l2secs];
  // int[] x3 = new int[E.l2secs];
  int[] x4 = new int[E.l2secs];
  int[] x5 = new int[E.l2secs];
  int[][] iix1 = {x1, x2, x3, x4, x5};
  int[] dResumsa = {0, 1};
  int[] mResum1a = {2, 3, 4, 5};
  int[] mResum2a = {6, 7, 8, 9};
  int[][] mResuma = {mResum1a, mResum2a};
  // Econ ec = EM.curEcon;
  // ArrayList<History> hist = ec.getHist(); // the owner will not change

  /**
   * The no parameter constructor
   */
  public A10Row(Econ ec) {
    super(ec,10, tcost, History.informationMinor9, "unset");
    lev = History.informationMinor9;
    titl = "unset";
    iix = iix1;
    lA = 10;
    dA = dA1;
    dResums = dResumsa;
    mResum = mResuma;
  }

  /**
   * constructor for new costs object 0RC=2+4,1SG=3+5,2r-r,3r - s,4s -r,5s-s
   *
   * @param alev level for listing this row array
   * @param atitl title of the row array
   */
  public A10Row(Econ ec,int alev, String atitl) {
    super(ec,10, tcost, alev, atitl);
    iix = iix1;
    lA = 10;
    dA = dA1;
    balances = false;
    costs = true;
    dResums = dResumsa;
    mResum = mResuma;
  }

  /**
   * set to all the values in A10Row B
   *
   * @param B
   * @return the new this
   */
  A10Row set(A10Row B) {
    lev = B.lev;
    titl = B.titl;
    blev = B.blev;
    balances = B.balances;
    double v=1.;
    for (int m : dA) {
      //     aCnt[m]++;// also raise cnt in A10; no for a reoder
      for (int n : ASECS) {
        v = B.get(m,n);
        set(m,n, v);
      }
    }
    return this;
  }
  /** copy an A2Row into the r & s locations with appropriate rc and sg
   * 
   * @param b  an input A2
   * @return   the entire instance of A10Row
   */
  A10Row set(A2Row b){
    zero();
    A[0] = b.A[0].copy();
    A[2] = b.A[0].copy();
    A[1] = b.A[1].copy();
    A[6] = b.A[1].copy();
    return this;
  }

  /**
   * copy A10Row object, copy each by each of calling A10Row there is no change
   * to the calling instance b is a new object lev,titl,balances,costs,blev are
   * all copied as well as all A[] values
   *
   * @return new object copy new references for the values
   */
  public A10Row copy10() {
    return copy10(lev,titl);
  }

  /**
   * copy A10Row object, copy each by each of calling A10Row there is no change
   * to the calling instance b is a new object lev,titl,balances,costs,blev are
   * all copied as well as all A[] values
   *
   * @param lev the display level to set for this A10Row
   * @return new object copy new references for the values
   */
  
  public A10Row copy(int lev){
    return copy10(lev,titl);
  }
  /**
   * copy A10Row object, copy each by each of calling A10Row there is no change
   * to the calling instance b is a new object lev,titl,balances,costs,blev are
   * all copied as well as all A[] values
   * rows rc,sg are resumed so they may not match the original
   *
   * @param alev the display level to set for this A10Row
   * @param aTitl new titl for the copy
   * @return new object copy new references for the values
   */
  public A10Row copy10(int alev, String atitl) {
    A10Row rtn = new A10Row(ec);
    rtn.lev = alev;
    rtn.titl = atitl;
    rtn.balances = balances;
    rtn.costs = costs;
    rtn.blev = blev;
    Double t;
    for (int m:I09) {
      for (int n : ASECS) {
         t= get(m, n);
        rtn.set(m, n, t);
      }
    }
    return rtn;
  }
  
 
  

  /**
   * add to row m, sector n value val
   *
   * @param m
   * @param n
   * @param val
   * @return return the result in A[m].get(n)
   */
  @Override
  double add(int m, int n, double val) {
    return A[m].add(n, val);
  }

  /**
   * add val to sector mm in the 2 rows
   *
   * @param mm row = mm/E.lsecs, sector = mm%E.lsecs
   * @param val value to be store
   * @return add(row,sector,val)
   */
  double add(int mm, double val) {
    return this.add(Math.floorDiv(mm, E.lsecs), mm % E.lsecs, val);
  }

  /**
   * add A10Rows A , B, C
   *
   * @param A
   * @param B
   * @param C
   * @return this the sum of A,B,C
   */
  A10Row add(A10Row A, A10Row B, A10Row C) {
    for (int n = 0; n < 4; n++) {
      this.getRow(2 + n).add(A.getRow(2 + n), B.getRow(2 + n), C.getRow(2 + n));
      this.getRow(6 + n).add(A.getRow(6 + n), B.getRow(6 + n), C.getRow(6 + n));
    }
    this.getRow(0).add(this.getRow(2), this.getRow(3), this.getRow(4), this.getRow(5));
    this.getRow(1).add(this.getRow(6), this.getRow(7), this.getRow(8), this.getRow(9));
    return this;
  }

  /**
   * find the n from the min value of rows 0,1
   *
   * @param x index generated from some other object
   * @return the min n'th index
   */
  int findMinIx(int x) {
    checkIx(0);
    for (int m : E.A2SECS) {
      if (x == iix[0][m]) {
        return m;
      }
    }
    return L2SECS - 1;

  }

  @Override
  protected Object clone() throws CloneNotSupportedException {
    return super.clone(); //To change body of generated methods, choose Tools | Templates.
  }

  /**
   * get the value of rows 0,1 by set m = m%2
   *
   * @param m The row %2 to be gotten
   * @param n The value in the requested row
   * @return m % 2,n
   */
  public double gett(int m, int n) {
    resum(m % 2);
    return get(m % 2, n);
  }

  /**
   * get working from working rows m % 2
   *
   * @param m row m%2 *4 +2 the r rows
   * @param n sector
   * @return return row(m%2 *4 +2), sector n
   */
  public double gett1(int m, int n) {
    return get((m % 2) * 4 + 2, n); // the r rows
  }

  /**
   * get value from row m%2*4+3 the c rows
   *
   * @param m row selector
   * @param n sector selector
   * @return row m%2*4+3 sector n
   */
  public double gett2(int m, int n) {
    return get((m % 2) * 4 + 3, n); // c rows
  }

  /**
   * get the s rows sector n
   *
   * @param m row(m%2*4+4) the s row
   * @param n sector
   * @return get row(m%2*4+4) sector n
   */
  public double gett3(int m, int n) {
    return get((m % 2) * 4 + 4, n); // s
  }

  /**
   * get the g rows sector n
   *
   * @param m row(m%2*4+5) the g row
   * @param n sector
   * @return get row(m%2*4+5) sector n
   */
  public double gett4(int m, int n) {
    return get((m % 2) * 4 + 5, n); // g
  }

  /**
   * set object copy from 8 costs, and convert staff work to staff balance
   * values set balances false and costs true
   *
   * @param alev used by sendHist as send if History.dl &gt; alev, lev = alev
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
  public A10Row setCopyCosts(int alev, String aTitl, ARow RR, ARow RS, ARow CR, ARow CS, ARow SR, ARow SS, ARow GR, ARow GS, ARow sBal, ARow sWork, double phe, double years) {
    balances = false;
    costs = true;
    lev = alev;
    titl = aTitl;
    phe = phe < PZERO ? 1. : phe;
    years = years < PZERO ? 1. : years;
    for (int n : E.alsecs) {
      double sw = sWork.get(n);
      sw = sw < PZERO ? 1 : sw;
      set(2, n, (RR.get(n)) * phe * years);
      set(3, n, CR.get(n) * phe * years);
      set(4, n, SR.get(n) * phe * years);
      set(5, n, GR.get(n) * phe * years);
      set(6, n, RS.get(n) / sw * phe * years);
      set(7, n, CS.get(n) / sw * phe * years);
      set(8, n, SS.get(n) / sw * phe * years);
      set(9, n, GS.get(n) / sw * phe * years);

    }
    return this;
  }

  /**
   * set object copy from 8 costs, and convert staff work to staff balance
   * values
   *
   * @param alev used by sendHist as send if History.dl &gt; alev, lev = alev
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
  public A10Row setCopyCosts(int alev, String aTitl, ARow RR, ARow RS, ARow CR, ARow CS, ARow SR, ARow SS, ARow GR, ARow GS, ARow sBal, ARow sWork, double phe) {

    return this.setCopyCosts(alev, aTitl, RR, RS, CR, CS, SR, SS, GR, GS, sBal, sWork, phe, 1.);
  }

  /**
   * set object copy from 8 costs, and convert staff work to staff balance
   * values
   *
   * @param alev used by sendHist as send if History.dl &gt; alev, lev = alev
   * @param atitl title for all sendHist rows
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
  public A10Row setCopyCosts(int alev, String atitl, ARow RR, ARow RS, ARow CR, ARow CS, ARow SR, ARow SS, ARow GR, ARow GS, ARow sBal, ARow sWork) {

    return this.setCopyCosts(alev, atitl, RR, RS, CR, CS, SR, SS, GR, GS, sBal, sWork, 1., 1.);
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
   * subtract each B times V from the this calling object
   *
   * @param B  the source to be subtracted
   * @param V  multiplier
   * @return each this - each B * V
   */
  public A10Row setSubBmultV(A10Row B, double V) {
    for (int m : dA1) {
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
   * set to Min of each by each B,C
   *
   * @param B
   * @param C
   * @return min each by each B,C, 0 = min(2,4),1=min(3,5)
   */
  public A10Row setMin(A10Row B, A10Row C) {
    double b = 1., c = 1.;
    for (int m : dA1) {
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
   * set to Max of each by each A, B
   *
   * @param A
   * @param B
   * @return
   */
  public A10Row setMax(A10Row A, A10Row B) {
    double b = 1., c = 1., a = 1.;
    for (int m : d29) {
      for (int n : ASECS) {
        // do the rr,rs,sr,ss sets first
        a = A.get(m, n);
        b = B.get(m, n);

        set(m, n, a > b ? a : b);
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
  public A10Row setMin(A10Row B, A10Row C, A10Row D) {
    double b = 1., c = 1., d = 1.;
    for (int m : dA2) {
      for (int n : E.alsecs) {
        // do the rr,rs,sr,ss sets first
        b = B.get(m, n);
        c = C.get(m, n);
        d = D.get(m, n);
        set(m, n, b < c ? b : c < d ? c : d);
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
  public A10Row setAmultB(A10Row A, A10Row B) {
    // mult each member set of 2 rows each A by corresponding B
    for (int m : d29) {
      for (int n : ASECS) {
        // separate each operation to localize null object errors
        set(m, n,
                A.get(m, n)
                * B.get(m, n));
      }
    }
    return this;
  }

  /** set multiplying this instance by a fertility
   * 
   * @param a a raw cost
   * @param f a fertility
   * @return this
   */
  public A10Row setAmultF(A10Row a,A2Row f){
    for(int n:ASECS){
      for(int m:A01){
        for(int mm:A03){
        set(2+4*m+mm,n,a.get(2+4*m+mm,n)*f.get(m,n));
        }
      }
    }
    return this;
  }

  /**
   * subtract each value in C from the corresponding value in B
   *
   * @param B
   * @return
   */
  public A10Row setBsubC(A10Row B,A10Row C) {
    // mult each member set of 2 rows each A by corresponding B
    for (int m : d29) {
      for (int n : ASECS) {
        // separate each operation to localize null object errors
        set(m, n, B.get(m, n) - C.get(m, n));
      }
    }
    return this;
  }

  /**
   * set object to each a sector mult by V
   *
   * @param a  A10Row to multiply
   * @param v multiplier for each sector
   * @return  each a sector mult by v
   */
  public A10Row setAmultV(A10Row a, double v) {
    // mult each member set of 2 rows each A by corresponding B
    Double d;
    for (int m : d29) {
      for (int n : ASECS) {
       d= a.get(m, n);
       E.myTestDouble(d,"a.get(m,n)","in setAmultV m=%d,n=%d,d=%s",m,n,String.valueOf(d));
        // separate each operation to localize null object errors
        set(m, n,
                d
                * v);
      }
    }
    return this;
  }

  /**
   * divide each value of each row in aA by corresponding elements in B
   *
   * @param A
   * @param B
   * @return each A each element divided by corresponding B
   */
  public A10Row setAdivByB(A10Row A, A10Row B) {
    // mult each member set of 2 rows each A by corresponding B
    for (int m : d29) {
      for (int n : ASECS) {
        // separate each operation to localize null object errors
        set(m, n,
                A.get(m, n)
                / B.get(m, n));
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
  public A10Row setAdd(A10Row a, A10Row b) {
    for (int m : IA) {
      for (int n : ASECS ) {
        this.set(m, n, a.get(m, n) + b.get(m, n));
      }
    }
    return this;
  }

  /**
   * get internal ARow n
   *
   * @param m
   * @return
   */
  public ARow getRow(int n) {
    return A[n];
  }

  /**
   * sum the designated row
   *
   * @param row
   * @return
   */
  @Override
  double rowSum(int row) {
    return this.A[row].sum();
  }

  /**
   * list percents for designated rows against balances, note that r,c,s,g have
   * both r and s cost components
   *
   * @param hist
   * @param blev
   * @param lev
   * @param aPre
   * @param title
   * @param balances
   * @param grows
   * @param reqM
   * @param reqG
   * @param maint
   * @param travel
   * @param growthC
   * @param
   */
  void sendPercent8(ArrayList<History> hist, int blev, int lev, String aPre, String title, A6Row balances, A6Row grows, A10Row reqM, A10Row reqG, A10Row maint, A10Row travel, A10Row rawGC, A10Row growthC, A10Row MTGC) {
    if (ec.clearHist()) {
      return;
    }
    int rb = 0, rr = 0, rg = 0;
    String rs = " rc.rc/rc ";
    if (blev < History.dl && lev <= blev && ec.myEconCnt< 10) {
      hist.add(new History(aPre, lev, rs + title, "qM" + ec.df(reqM.rowSum(rr) * 100. / balances.rowSum(rb)), "qG" + ec.df(reqG.rowSum(rr) * 100. / balances.rowSum(rb)), "m" + ec.df(maint.rowSum(rr) * 100. / balances.rowSum(rb)), "t" + ec.df(travel.rowSum(rr) * 100. / balances.rowSum(rb)), "gC" + ec.df(rawGC.rowSum(rr) * 100. / balances.rowSum(rb)), "mtg" + ec.df(MTGC.rowSum(rr) * 100. / balances.rowSum(rb)), "Gs" + ec.df(grows.rowSum(rg) * 100. / balances.rowSum(rb)), "remQm" + ec.df((balances.rowSum(rb) - reqM.rowSum(rr)) * 100. / balances.rowSum(rb)), "remQg" + ec.df((balances.rowSum(rb) - reqG.rowSum(rr)) * 100. / balances.rowSum(rb)),"mtgRem" + ec.df((balances.rowSum(rb)- MTGC.rowSum(rr))*100./balances.rowSum(rb))));
    }
    if (blev < History.dl && lev <= blev && ec.myEconCnt< 10) {
      rb = 1;
      rr = 1;
      rg = 1;
      rs = " sg.sg/sg ";
         hist.add(new History(aPre, lev, rs + title, "qM" + ec.df(reqM.rowSum(rr) * 100. / balances.rowSum(rb)), "qG" + ec.df(reqG.rowSum(rr) * 100. / balances.rowSum(rb)), "m" + ec.df(maint.rowSum(rr) * 100. / balances.rowSum(rb)), "t" + ec.df(travel.rowSum(rr) * 100. / balances.rowSum(rb)), "gC" + ec.df(rawGC.rowSum(rr) * 100. / balances.rowSum(rb)), "mtg" + ec.df(MTGC.rowSum(rr) * 100. / balances.rowSum(rb)), "Gs" + ec.df(grows.rowSum(rg) * 100. / balances.rowSum(rb)), "remQm" + ec.df((balances.rowSum(rb) - reqM.rowSum(rr)) * 100. / balances.rowSum(rb)), "remQg" + ec.df((balances.rowSum(rb) - reqG.rowSum(rr)) * 100. / balances.rowSum(rb)),"mtgRem" + ec.df((balances.rowSum(rb)- MTGC.rowSum(rr))*100./balances.rowSum(rb))));
    }
    if (blev < History.dl && lev <= blev && ec.myEconCnt< 10) {
      rb = 2;
      rr = 2;
      rg = 2;
      rs = " r.r/r ";
          hist.add(new History(aPre, lev, rs + title, "qM" + ec.df(reqM.rowSum(rr) * 100. / balances.rowSum(rb)), "qG" + ec.df(reqG.rowSum(rr) * 100. / balances.rowSum(rb)), "m" + ec.df(maint.rowSum(rr) * 100. / balances.rowSum(rb)), "t" + ec.df(travel.rowSum(rr) * 100. / balances.rowSum(rb)), "gC" + ec.df(rawGC.rowSum(rr) * 100. / balances.rowSum(rb)), "mtg" + ec.df(MTGC.rowSum(rr) * 100. / balances.rowSum(rb)), "Gs" + ec.df(grows.rowSum(rg) * 100. / balances.rowSum(rb)), "remQm" + ec.df((balances.rowSum(rb) - reqM.rowSum(rr)) * 100. / balances.rowSum(rb)), "remQg" + ec.df((balances.rowSum(rb) - reqG.rowSum(rr)) * 100. / balances.rowSum(rb)),"mtgRem" + ec.df((balances.rowSum(rb)- MTGC.rowSum(rr))*100./balances.rowSum(rb))));
      rr = 3;
      rs = " c.r/r ";
      rg = 3;
      rb = 2;
         hist.add(new History(aPre, lev, rs + title, "qM" + ec.df(reqM.rowSum(rr) * 100. / balances.rowSum(rb)), "qG" + ec.df(reqG.rowSum(rr) * 100. / balances.rowSum(rb)), "m" + ec.df(maint.rowSum(rr) * 100. / balances.rowSum(rb)), "t" + ec.df(travel.rowSum(rr) * 100. / balances.rowSum(rb)), "gC" + ec.df(rawGC.rowSum(rr) * 100. / balances.rowSum(rb)), "mtg" + ec.df(MTGC.rowSum(rr) * 100. / balances.rowSum(rb)), "Gs" + ec.df(grows.rowSum(rg) * 100. / balances.rowSum(rb)), "remQm" + ec.df((balances.rowSum(rb) - reqM.rowSum(rr)) * 100. / balances.rowSum(rb)), "remQg" + ec.df((balances.rowSum(rb) - reqG.rowSum(rr)) * 100. / balances.rowSum(rb)),"mtgRem" + ec.df((balances.rowSum(rb)- MTGC.rowSum(rr))*100./balances.rowSum(rb))));
      rr = 3;
      rs = " c.r/c ";
      rg = 3;
      rb = 3;
         hist.add(new History(aPre, lev, rs + title, "qM" + ec.df(reqM.rowSum(rr) * 100. / balances.rowSum(rb)), "qG" + ec.df(reqG.rowSum(rr) * 100. / balances.rowSum(rb)), "m" + ec.df(maint.rowSum(rr) * 100. / balances.rowSum(rb)), "t" + ec.df(travel.rowSum(rr) * 100. / balances.rowSum(rb)), "gC" + ec.df(rawGC.rowSum(rr) * 100. / balances.rowSum(rb)), "mtg" + ec.df(MTGC.rowSum(rr) * 100. / balances.rowSum(rb)), "Gs" + ec.df(grows.rowSum(rg) * 100. / balances.rowSum(rb)), "remQm" + ec.df((balances.rowSum(rb) - reqM.rowSum(rr)) * 100. / balances.rowSum(rb)), "remQg" + ec.df((balances.rowSum(rb) - reqG.rowSum(rr)) * 100. / balances.rowSum(rb)),"mtgRem" + ec.df((balances.rowSum(rb)- MTGC.rowSum(rr))*100./balances.rowSum(rb))));
      rr = 4;
      rs = " s.r/s ";
      rg = 4;
      rb = 4;
         hist.add(new History(aPre, lev, rs + title, "qM" + ec.df(reqM.rowSum(rr) * 100. / balances.rowSum(rb)), "qG" + ec.df(reqG.rowSum(rr) * 100. / balances.rowSum(rb)), "m" + ec.df(maint.rowSum(rr) * 100. / balances.rowSum(rb)), "t" + ec.df(travel.rowSum(rr) * 100. / balances.rowSum(rb)), "gC" + ec.df(rawGC.rowSum(rr) * 100. / balances.rowSum(rb)), "mtg" + ec.df(MTGC.rowSum(rr) * 100. / balances.rowSum(rb)), "Gs" + ec.df(grows.rowSum(rg) * 100. / balances.rowSum(rb)), "remQm" + ec.df((balances.rowSum(rb) - reqM.rowSum(rr)) * 100. / balances.rowSum(rb)), "remQg" + ec.df((balances.rowSum(rb) - reqG.rowSum(rr)) * 100. / balances.rowSum(rb)),"mtgRem" + ec.df((balances.rowSum(rb)- MTGC.rowSum(rr))*100./balances.rowSum(rb))));
      rr = 5;
      rs = " g.r/g ";
      rg = 5;
      rb = 5;
         hist.add(new History(aPre, lev, rs + title, "qM" + ec.df(reqM.rowSum(rr) * 100. / balances.rowSum(rb)), "qG" + ec.df(reqG.rowSum(rr) * 100. / balances.rowSum(rb)), "m" + ec.df(maint.rowSum(rr) * 100. / balances.rowSum(rb)), "t" + ec.df(travel.rowSum(rr) * 100. / balances.rowSum(rb)), "gC" + ec.df(rawGC.rowSum(rr) * 100. / balances.rowSum(rb)), "mtg" + ec.df(MTGC.rowSum(rr) * 100. / balances.rowSum(rb)), "Gs" + ec.df(grows.rowSum(rg) * 100. / balances.rowSum(rb)), "remQm" + ec.df((balances.rowSum(rb) - reqM.rowSum(rr)) * 100. / balances.rowSum(rb)), "remQg" + ec.df((balances.rowSum(rb) - reqG.rowSum(rr)) * 100. / balances.rowSum(rb)),"mtgRem" + ec.df((balances.rowSum(rb)- MTGC.rowSum(rr))*100./balances.rowSum(rb))));
      rb = 4;
      rr = 6;
      rg = 2;
      rs = " r.s/s ";
         hist.add(new History(aPre, lev, rs + title, "qM" + ec.df(reqM.rowSum(rr) * 100. / balances.rowSum(rb)), "qG" + ec.df(reqG.rowSum(rr) * 100. / balances.rowSum(rb)), "m" + ec.df(maint.rowSum(rr) * 100. / balances.rowSum(rb)), "t" + ec.df(travel.rowSum(rr) * 100. / balances.rowSum(rb)), "gC" + ec.df(rawGC.rowSum(rr) * 100. / balances.rowSum(rb)), "mtg" + ec.df(MTGC.rowSum(rr) * 100. / balances.rowSum(rb)), "Gs" + ec.df(grows.rowSum(rg) * 100. / balances.rowSum(rb)), "remQm" + ec.df((balances.rowSum(rb) - reqM.rowSum(rr)) * 100. / balances.rowSum(rb)), "remQg" + ec.df((balances.rowSum(rb) - reqG.rowSum(rr)) * 100. / balances.rowSum(rb)),"mtgRem" + ec.df((balances.rowSum(rb)- MTGC.rowSum(rr))*100./balances.rowSum(rb))));
      rb = 4;
      rr = 7;
      rg = 3;
      rs = " c.s/s ";
          hist.add(new History(aPre, lev, rs + title, "qM" + ec.df(reqM.rowSum(rr) * 100. / balances.rowSum(rb)), "qG" + ec.df(reqG.rowSum(rr) * 100. / balances.rowSum(rb)), "m" + ec.df(maint.rowSum(rr) * 100. / balances.rowSum(rb)), "t" + ec.df(travel.rowSum(rr) * 100. / balances.rowSum(rb)), "gC" + ec.df(rawGC.rowSum(rr) * 100. / balances.rowSum(rb)), "mtg" + ec.df(MTGC.rowSum(rr) * 100. / balances.rowSum(rb)), "Gs" + ec.df(grows.rowSum(rg) * 100. / balances.rowSum(rb)), "remQm" + ec.df((balances.rowSum(rb) - reqM.rowSum(rr)) * 100. / balances.rowSum(rb)), "remQg" + ec.df((balances.rowSum(rb) - reqG.rowSum(rr)) * 100. / balances.rowSum(rb)),"mtgRem" + ec.df((balances.rowSum(rb)- MTGC.rowSum(rr))*100./balances.rowSum(rb))));
      rb = 4;
      rr = 8;
      rg = 4;
      rs = " s.s/s ";
         hist.add(new History(aPre, lev, rs + title, "qM" + ec.df(reqM.rowSum(rr) * 100. / balances.rowSum(rb)), "qG" + ec.df(reqG.rowSum(rr) * 100. / balances.rowSum(rb)), "m" + ec.df(maint.rowSum(rr) * 100. / balances.rowSum(rb)), "t" + ec.df(travel.rowSum(rr) * 100. / balances.rowSum(rb)), "gC" + ec.df(rawGC.rowSum(rr) * 100. / balances.rowSum(rb)), "mtg" + ec.df(MTGC.rowSum(rr) * 100. / balances.rowSum(rb)), "Gs" + ec.df(grows.rowSum(rg) * 100. / balances.rowSum(rb)), "remQm" + ec.df((balances.rowSum(rb) - reqM.rowSum(rr)) * 100. / balances.rowSum(rb)), "remQg" + ec.df((balances.rowSum(rb) - reqG.rowSum(rr)) * 100. / balances.rowSum(rb)),"mtgRem" + ec.df((balances.rowSum(rb)- MTGC.rowSum(rr))*100./balances.rowSum(rb))));
      rb = 4;
      rr = 9;
      rg = 5;
      rs = " g.s/s ";
         hist.add(new History(aPre, lev, rs + title, "qM" + ec.df(reqM.rowSum(rr) * 100. / balances.rowSum(rb)), "qG" + ec.df(reqG.rowSum(rr) * 100. / balances.rowSum(rb)), "m" + ec.df(maint.rowSum(rr) * 100. / balances.rowSum(rb)), "t" + ec.df(travel.rowSum(rr) * 100. / balances.rowSum(rb)), "gC" + ec.df(rawGC.rowSum(rr) * 100. / balances.rowSum(rb)), "mtg" + ec.df(MTGC.rowSum(rr) * 100. / balances.rowSum(rb)), "Gs" + ec.df(grows.rowSum(rg) * 100. / balances.rowSum(rb)), "remQm" + ec.df((balances.rowSum(rb) - reqM.rowSum(rr)) * 100. / balances.rowSum(rb)), "remQg" + ec.df((balances.rowSum(rb) - reqG.rowSum(rr)) * 100. / balances.rowSum(rb)),"mtgRem" + ec.df((balances.rowSum(rb)- MTGC.rowSum(rr))*100./balances.rowSum(rb))));
      rb = 5;
      rr = 9;
      rg = 5;
      rs = " g.s/g ";
         hist.add(new History(aPre, lev, rs + title, "qM" + ec.df(reqM.rowSum(rr) * 100. / balances.rowSum(rb)), "qG" + ec.df(reqG.rowSum(rr) * 100. / balances.rowSum(rb)), "m" + ec.df(maint.rowSum(rr) * 100. / balances.rowSum(rb)), "t" + ec.df(travel.rowSum(rr) * 100. / balances.rowSum(rb)), "gC" + ec.df(rawGC.rowSum(rr) * 100. / balances.rowSum(rb)), "mtg" + ec.df(MTGC.rowSum(rr) * 100. / balances.rowSum(rb)), "Gs" + ec.df(grows.rowSum(rg) * 100. / balances.rowSum(rb)), "remQm" + ec.df((balances.rowSum(rb) - reqM.rowSum(rr)) * 100. / balances.rowSum(rb)), "remQg" + ec.df((balances.rowSum(rb) - reqG.rowSum(rr)) * 100. / balances.rowSum(rb)),"mtgRem" + ec.df((balances.rowSum(rb)- MTGC.rowSum(rr))*100./balances.rowSum(rb))));
      rb = 0;
      rr = 2;
      rg = 2;
      rs = " r.r/rc ";
          hist.add(new History(aPre, lev, rs + title, "qM" + ec.df(reqM.rowSum(rr) * 100. / balances.rowSum(rb)), "qG" + ec.df(reqG.rowSum(rr) * 100. / balances.rowSum(rb)), "m" + ec.df(maint.rowSum(rr) * 100. / balances.rowSum(rb)), "t" + ec.df(travel.rowSum(rr) * 100. / balances.rowSum(rb)), "gC" + ec.df(rawGC.rowSum(rr) * 100. / balances.rowSum(rb)), "mtg" + ec.df(MTGC.rowSum(rr) * 100. / balances.rowSum(rb)), "Gs" + ec.df(grows.rowSum(rg) * 100. / balances.rowSum(rb)), "remQm" + ec.df((balances.rowSum(rb) - reqM.rowSum(rr)) * 100. / balances.rowSum(rb)), "remQg" + ec.df((balances.rowSum(rb) - reqG.rowSum(rr)) * 100. / balances.rowSum(rb)),"mtgRem" + ec.df((balances.rowSum(rb)- MTGC.rowSum(rr))*100./balances.rowSum(rb))));
      rb = 0;
      rr = 4;
      rg = 4;
      rs = " s.r/rc ";
         hist.add(new History(aPre, lev, rs + title, "qM" + ec.df(reqM.rowSum(rr) * 100. / balances.rowSum(rb)), "qG" + ec.df(reqG.rowSum(rr) * 100. / balances.rowSum(rb)), "m" + ec.df(maint.rowSum(rr) * 100. / balances.rowSum(rb)), "t" + ec.df(travel.rowSum(rr) * 100. / balances.rowSum(rb)), "gC" + ec.df(rawGC.rowSum(rr) * 100. / balances.rowSum(rb)), "mtg" + ec.df(MTGC.rowSum(rr) * 100. / balances.rowSum(rb)), "Gs" + ec.df(grows.rowSum(rg) * 100. / balances.rowSum(rb)), "remQm" + ec.df((balances.rowSum(rb) - reqM.rowSum(rr)) * 100. / balances.rowSum(rb)), "remQg" + ec.df((balances.rowSum(rb) - reqG.rowSum(rr)) * 100. / balances.rowSum(rb)),"mtgRem" + ec.df((balances.rowSum(rb)- MTGC.rowSum(rr))*100./balances.rowSum(rb))));
      rb = 0;
      rr = 0;
      rg = 0;
      rs = " rc/rc ";
         hist.add(new History(aPre, lev, rs + title, "qM" + ec.df(reqM.rowSum(rr) * 100. / balances.rowSum(rb)), "qG" + ec.df(reqG.rowSum(rr) * 100. / balances.rowSum(rb)), "m" + ec.df(maint.rowSum(rr) * 100. / balances.rowSum(rb)), "t" + ec.df(travel.rowSum(rr) * 100. / balances.rowSum(rb)), "gC" + ec.df(rawGC.rowSum(rr) * 100. / balances.rowSum(rb)), "mtg" + ec.df(MTGC.rowSum(rr) * 100. / balances.rowSum(rb)), "Gs" + ec.df(grows.rowSum(rg) * 100. / balances.rowSum(rb)), "remQm" + ec.df((balances.rowSum(rb) - reqM.rowSum(rr)) * 100. / balances.rowSum(rb)), "remQg" + ec.df((balances.rowSum(rb) - reqG.rowSum(rr)) * 100. / balances.rowSum(rb)),"mtgRem" + ec.df((balances.rowSum(rb)- MTGC.rowSum(rr))*100./balances.rowSum(rb))));
      rb = 1;
      rr = 1;
      rg = 1;
      rs = " sg/sg ";
         hist.add(new History(aPre, lev, rs + title, "qM" + ec.df(reqM.rowSum(rr) * 100. / balances.rowSum(rb)), "qG" + ec.df(reqG.rowSum(rr) * 100. / balances.rowSum(rb)), "m" + ec.df(maint.rowSum(rr) * 100. / balances.rowSum(rb)), "t" + ec.df(travel.rowSum(rr) * 100. / balances.rowSum(rb)), "gC" + ec.df(rawGC.rowSum(rr) * 100. / balances.rowSum(rb)), "mtg" + ec.df(MTGC.rowSum(rr) * 100. / balances.rowSum(rb)), "Gs" + ec.df(grows.rowSum(rg) * 100. / balances.rowSum(rb)), "remQm" + ec.df((balances.rowSum(rb) - reqM.rowSum(rr)) * 100. / balances.rowSum(rb)), "remQg" + ec.df((balances.rowSum(rb) - reqG.rowSum(rr)) * 100. / balances.rowSum(rb)),"mtgRem" + ec.df((balances.rowSum(rb)- MTGC.rowSum(rr))*100./balances.rowSum(rb))));
      rb = 1;
      rr = 4;
      rg = 4;
      rs = " s.s/sg ";
         hist.add(new History(aPre, lev, rs + title, "qM" + ec.df(reqM.rowSum(rr) * 100. / balances.rowSum(rb)), "qG" + ec.df(reqG.rowSum(rr) * 100. / balances.rowSum(rb)), "m" + ec.df(maint.rowSum(rr) * 100. / balances.rowSum(rb)), "t" + ec.df(travel.rowSum(rr) * 100. / balances.rowSum(rb)), "gC" + ec.df(rawGC.rowSum(rr) * 100. / balances.rowSum(rb)), "mtg" + ec.df(MTGC.rowSum(rr) * 100. / balances.rowSum(rb)), "Gs" + ec.df(grows.rowSum(rg) * 100. / balances.rowSum(rb)), "remQm" + ec.df((balances.rowSum(rb) - reqM.rowSum(rr)) * 100. / balances.rowSum(rb)), "remQg" + ec.df((balances.rowSum(rb) - reqG.rowSum(rr)) * 100. / balances.rowSum(rb)),"mtgRem" + ec.df((balances.rowSum(rb)- MTGC.rowSum(rr))*100./balances.rowSum(rb))));
      rb = 1;
      rr = 6;
      rg = 2;
      rs = " r.s/sg ";
         hist.add(new History(aPre, lev, rs + title, "qM" + ec.df(reqM.rowSum(rr) * 100. / balances.rowSum(rb)), "qG" + ec.df(reqG.rowSum(rr) * 100. / balances.rowSum(rb)), "m" + ec.df(maint.rowSum(rr) * 100. / balances.rowSum(rb)), "t" + ec.df(travel.rowSum(rr) * 100. / balances.rowSum(rb)), "gC" + ec.df(rawGC.rowSum(rr) * 100. / balances.rowSum(rb)), "mtg" + ec.df(MTGC.rowSum(rr) * 100. / balances.rowSum(rb)), "Gs" + ec.df(grows.rowSum(rg) * 100. / balances.rowSum(rb)), "remQm" + ec.df((balances.rowSum(rb) - reqM.rowSum(rr)) * 100. / balances.rowSum(rb)), "remQg" + ec.df((balances.rowSum(rb) - reqG.rowSum(rr)) * 100. / balances.rowSum(rb)),"mtgRem" + ec.df((balances.rowSum(rb)- MTGC.rowSum(rr))*100./balances.rowSum(rb))));
    }
  }

  /**
   * percents in relation to rc and sg
   *
   * @obsolete functions moved to just sendPercents, no output here
   * @param hist
   * @param blev
   * @param lev
   * @param aPre
   * @param title
   * @param balances
   * @param grows
   * @param reqM
   * @param reqG
   * @param maint
   * @param travel
   * @param rawGC
   * @param growthC
   * @param MTGC
   */
  void sendPercent2(ArrayList<History> hist, int blev, int lev, String aPre, String title, A6Row balances, A6Row grows, A10Row reqM, A10Row reqG, A10Row maint, A10Row travel, A10Row rawGC, A10Row growthC, A10Row MTGC) {
    if (blev < History.dl && lev <= blev && ec.myEconCnt< 10) {
      int rb = 2, rr = 2, rg = 2;
      String rs = " a ";

    }
  }

  /**
   * send 10 rows to the hist log do not list if econCnt &gt; E.maxEconHist do
   * not list if econCnt &gt; 10 do not list if History.dl &gt; blev and blev
   * &ge lev
   *
   * @param lev the level of this log
   * @param blev the max level listed * @param aPre the prefix
   * @param titl the title
   */
  void sendHist(int lev, String aPre, String titl) {
    this.sendHist(hist, blev, aPre, lev, titl);
  }

  /**
   * send 10 rows to the hist log do not list if econCnt &gt; E.maxEconHist do
   * not list if econCnt &gt; 10 do not list if History.dl &gt; blev and blev
   * &ge lev
   *
   * @param lev the level of this log
   * @param blev the max level listed * @param aPre the prefix
   */
  @Override
  void sendHist(int lev, String aPre) {
    this.sendHist(hist, blev, aPre, lev, titl);
  }

  /**
   * send 10 rows to the hist log do not list if econCnt &gt; E.maxEconHist do
   * not list if econCnt &gt; 10 do not list if History.dl &gt; blev and blev
   * &ge lev
   *
   * @param lev the level of this log
   * @param blev the max level listed
   * @param aPre the prefix
   * @param titl the title
   */
  void sendHist(int blev, int lev, String aPre, String titl) {
    sendHist(hist, blev, aPre, lev, titl);
  }
  /** another version of sendHist
   * 
   * @param blev limit level
   * @param apre  prefix
   * @param lev   level
   * @param titl  title
   */
  void sendHist(int blev,String apre,int lev,String titl){
    sendHist(hist,blev,apre,lev,titl);
  }

  /**
   * send 10 rows to the hist log 
   * do not list if econCnt &gt; E.maxEconHist 
   * do not list if econCnt &gt; 10 
   * do not list if History.dl &lt; blev and blev &le lev
   *
   * @param hist the log array
   * @param lev the level of this log
   * @param aPre the prefix
   * @param blev the max level listed
   * @param titl the title
   */
  void sendHist(ArrayList<History> hist, int blev, String aPre, int lev, String titl) {

    if (History.dl > blev && lev <= blev) {
      balances = false;
      lev = Math.min(lev, History.informationMinor9);
      resum(0);
      resum(1);
      hist.add(new History(aPre, lev, " RC " + titl, A[0]));
      hist.add(new History(aPre, lev, " SG " + titl, A[1]));
      hist.add(new History(aPre, lev, (" r.r " + titl), A[2]));
      hist.add(new History(aPre, lev, (" c.r " + titl), A[3]));
      hist.add(new History(aPre, lev, (" s.r " + titl), A[4]));
      hist.add(new History(aPre, lev, (" g.r " + titl), A[5]));
      hist.add(new History(aPre, lev, (" r.s " + titl), A[6]));
      hist.add(new History(aPre, lev, (" c.s " + titl), A[7]));
      hist.add(new History(aPre, lev, (" s.s " + titl), A[8]));
      hist.add(new History(aPre, lev, (" g.s " + titl), A[9]));
    }
  }

  void sendHist(ArrayList<History> hist, String aPre) {
    sendHist(hist, blev, aPre, lev, titl);
  }

  /**
   * send to history the 0 & 1 rows
   *
   * @param bLev list only if bLev is greater or equal to lev
   * @param aPre pre characters
   * @param lev listing level
   * @param tit0
   * @param tit1
   */
  void sendHist01(int bLev, String aPre, int lev, String tit0, String tit1) {

    lev = Math.min(lev, History.informationMinor9);
    if (blev >= lev) {
      resum(0);
      resum(1);
      hist.add(new History(aPre, lev, " " + tit0, A[0]));
      hist.add(new History(aPre, lev, " " + tit1, A[1]));
    }
  }
  
  /** list a donot group
   * 
   * @param group group number 0 = incr, 1 = decr, 2 = xfer
   * @param bLev  list only if bLev is greater or equal to lev
   * @param aPre  prefix characters in the listing
   * @param lev   level of the listing
   */
   void sendDoNot(int group,int bLev, String aPre, int lev) {
    lev = Math.min(lev, History.informationMinor9);
    if ( bLev >= lev) {
      hist.add(new History(aPre, lev, " doNot" + group*2, A[group*2]));
      hist.add(new History(aPre, lev, " doNot" + group*2+1, A[group*2+1]));
    }
  }

  void sendHist() {
    sendHist(hist, blev, "#H", lev, titl);
  }
  
  /** zero this object
   * 
   * @return zero'd object
   */
  @Override
  public A10Row zero(){
      for (int m = 0; m < 10; m++) {

      for (int n : ASECS) {
        A[m].set(n,0.0);
      }
    }
    return this;
  }
  /**
   * age the doNot object
   *
   * @return this
   */
  public A10Row age() {
    for (int m : dA) {
      for (int n : E.alsecs) {
        if (A[m].get(n) > E.pzero) {
          A[m].add(n,-1); 
        }
      }
    }
    return this;
  }

  /**
   * set doNot for the index nIx to nYears years
   *
   * @param group  Swap type 0=incr, 1=decr,2=xchg
   * @param ixSrc 0=resource, 1=staff
   * @param srcIx  index of source row
   * @param nYears  number of years avoid, 100=forever
   * @return this
   */
  public A10Row setDoNot(int group, int ixSrc, int srcIx, double nYears) {
    A[group * 2 + ixSrc].set(srcIx%LSECS,nYears);
    dResums[0] = 99;
    dResums[1] = 99; // disable resums
    return this;
  }
  
   /**
   * set doNot for the index nIx to nYears years
   *
   * @param group  Swap type 0=incr, 1=decr,2=xchg
   * @param needIx  index a sector in the 2 rows 0-13
   * @param nYears  number of years avoid, 100=forever
   * @return this
   */
  public A10Row setDoNot(int group,  int needIx, double nYears) {
    A[group * 2 + needIx/LSECS].set(needIx%LSECS,nYears);
    dResums[0] = 99;
    dResums[1] = 99; // disable resums
    return this;
  }

  /**
   * test whether some doNots are still active
   *
   * @return number of doNot's set
   */
  public int isDoNot() {
    int doNots = 0;
    for (int m : dA1) {
      for (int n : E.alsecs) {
        if (get(m,n) > PZERO) {
          doNots += get(m,n);
        }
      }
    }
    return doNots;
  }

  /**
   * filter avail set very negative any value for which the doNot has positive
   * years left
   *
   * @param group 0,1,2,3,4 incr, delr, xfer of doNot
   * @param avail
   * @return avail with doNots negated
   */
  A2Row filterByDoNot(int group, A2Row avail) {
    return filterByDoNot(group,avail,-9999.);
  }
  
    /**
   * filter avail set to flag any value for which the doNot has positive
   * years left  The invoking object has nonzero counts where flags should be set
   * first row nominally for resource, second row for staff
   *
   * @param group 0,1,2,3,4 incr, delr, of doNot
   * @param avail  source file to have flags set, rtn is a copy
   * @param flag value set if donot.
   * @return copy of avail with doNots set to flag
   */
  A2Row filterByDoNot(int group, A2Row avail,double flag) {
    A2Row rtn = new A2Row(avail.ec);
    rtn = avail.newCopy(eM);
    for (int m=0;m<2;m++) {
      for (int n=0;n< E.lsecs;n++) {
        // if doNot.get > 0, substitute flag value
        if (get(m + 2 * group,n) > PZERO) {
          rtn.set(m,n,flag);
         // rtn.A[m].setCnt++;
        }
      }
    }
    return rtn;
  }

    /**
   * filter avail set to flag any value for which the doNot has positive
   * years left 
   * use by XFER treat both rows 0,1 with the same flags, since they may be 
   * either resource, reversed resource, staff or reversed staff files
   * with the amount of possible move of resource or staff.
   *
   * @param group 0,1,2,3,4 incr, delr, xfer of doNot
   * @param row  row of filter file to use for setting flags
   * @param avail  source file to filter
   * @param flag value set if donot in the calling file.
   * @return copy of avail with doNots set to flag
   */
  A2Row filterByDoNot(int group,int row, A2Row avail,double flag) {
    A2Row rtn;
    rtn = avail.newCopy(eM);
    for (int m=0;m<2;m++) {
      for (int n=0;n< E.lsecs;n++) {
        // if doNot.get > 0, substitute flag value
        if (get(row + 2 * group,n) > PZERO) {
          rtn.set(m,n,flag);
        }
      }
    }
    return rtn;
  }
  /**
   * check if any doNot is set
   *
   * @return true is yes, else false
   */
  boolean checkDoNot() {
    for (int m : dA) {
      for (int n : E.alsecs) {
        if (get(m,n) > PZERO) {
          return true;
        }
      }
    }
    return false;
  }

/** get boolean value of whether doNot is set for ix
 * 
 * @param group 0,1,2,3,4 incr, delr, xfer of doNot
 * @param ix  index in group
 * @return true if element still has a count
 */
boolean getDoNot(int group,int ix){
  return get(group*2 + ix/LSECS,ix%LSECS)> 0;
}
} // class
